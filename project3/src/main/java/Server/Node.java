package Server;

import java.io.IOException;
import java.util.*;

/***
 * Class representing one of our nodes
 */
public class Node extends Thread {

    /**
     * Enum representing the possible states of a node
     */
    public enum STATE {LEADER, FOLLOWER, CANDIDATE}


    // The message log
    private ArrayList<String> log;
    // The key-value pair map
    private Map<String, String> hashMap;

    private int sequence; //sequence # of the last log append/commit message
    private boolean request;
    private String key;
    private String val;

    // The queue to keep track of & send outgoing messages
    private final Queue<String> msgQueue;
    // The queue to keep track of log requests

    // The timer for this node
    private Timer timer;
    // The current term of voting
    private int term;
    // Whether the node has receiveList for the current term
    private boolean voted;
    // The current state of the node
    private STATE state;

    // The list of all the nodes in the cluster
    private Set<String> nodeList;
    // The name of the current leader of the cluster, or null if during election
    private String leader;
    // The totalNodes number of nodes in this cluster
    private final int totalNodes = 5;

    // The name of this node
    private String name;

    private Heartbeat heartbeat = null;
    private Election election = null;
    private LogReplication logReplication = null;


    /* Node constructor: initialize new node */ //OK
    public Node() {
        log = new ArrayList<>();

        //while log

        hashMap = new HashMap<>();
        sequence = -1;
        request = false;
        key = null;
        val = null;

        msgQueue = new LinkedList<>();

        term = 0;
        voted = false;
        state = STATE.FOLLOWER;

        nodeList = new HashSet<>();
        leader = null;

        name = System.getenv("HOSTNAME");

        for (int i = 1; i <= totalNodes; i++) {
            String next = "peer" + i;
            nodeList.add(next);
        }

        timer = new Timer(this);
        timer.start();

        System.out.println("I am " + name); //~Print~//
        System.out.println("\tstate - " + state); //~Print~//

    }

    @Override
    public void run() {
        while(true) {
            synchronized (msgQueue) {
                if(msgQueue.isEmpty()) {
                    try {
                        msgQueue.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    String amsg = msgQueue.remove();
                    processMessage(amsg);
                }
            }
        }
    }

    //TIMER (redundant, but ended up actually using all 3 in different cases... Maybe there is a better way)
    public void resetTimer() {
        stopTimer();
        startTimer();
    }

    public void stopTimer() {
        if (timer.isAlive())
            timer.interrupt();
    }

    private void startTimer() {
        timer = new Timer(this);
        timer.start();
    }

    //ELECTION
    /* Method to create new election thread and start election process */
    public void startElection() {
        timer = new Timer(this);
        timer.start();

        state = STATE.CANDIDATE;
        term++;


        if (election != null && election.isAlive()) {
            election.interrupt();
        }


        election = new Election(this);
        election.start();

        System.out.println("BECAME CANDIDATE."); //~Print~//

        election.addVote(name);
        voted = true;
    }

    //HEARTBEAT
    /* Method to create new heartbeat thread and start heartbeats when current node is the leader */
    public void startHeartbeat() {
        state = STATE.LEADER;
        leader = name;

        if (heartbeat != null && heartbeat.isAlive()) { //This block should hopefully never run!
            heartbeat.interrupt();
        }

        heartbeat = new Heartbeat(this);
        heartbeat.start();

        System.out.println("BECAME LEADER."); //~Print~//
    }

    //LOG_REPLICATION
    /* Method to create new election thread and start election process */
    public void startLogReplication() {
        if (state.equals(STATE.LEADER)) {

            if (logReplication != null && logReplication.isAlive()) { //This block should hopefully never run!
                logReplication.interrupt();
            }

            logReplication = new LogReplication(this);
            logReplication.start();

            System.out.println("\tstarted log replication"); //~Print~//
        }
    }

    /* Method to temporarily store current data on this node */
    public void append(boolean r, String k, String v) {
        request = r;
        key = k;
        val = v;
    }
    /* Method to commit current data on this node */
    public void commit() {
        if (val.equals("DELETE")) {
            if (hashMap.containsKey(key)) {
                hashMap.remove(key);
            }
        } else {
            hashMap.put(key, val);
        }
        request = false;
    }

    /* Method to write log append/commit messages to log*/
    public void write(int seq, String msg) {
        log.add(seq, msg);
    }

    /* Method to query & return a requested entry */
    private String query(String key) {
        if (hashMap.containsKey(key))
            return hashMap.get(key);
        else
            return "nonexistent entry: \'" + key + "\'";
    }



    //MESSAGES
    public void addMessage(String message) {
        synchronized (msgQueue) {
            msgQueue.add(message);
            msgQueue.notifyAll();
        }
    }

    public void processMessage(String message) {

        //PROCESS MESSAGE
        String[] splitMessage = message.split("/");

        //MESSAGE BEING SENT BY NODE
        if (splitMessage[0].equals(this.name)) {
            System.out.println("Sending: " + message); //~Print~//

            //SEND MESSAGE TO ALL OTHER NODES
            if (splitMessage[1].equals("ALL")) {

                for (String peer : nodeList) {
                    if(!peer.equals(name)) {

                        splitMessage[1] = peer;
                        String outmsg = String.join("/", splitMessage);

                        try {
                            Unicast.sendMsg(outmsg, peer);
                        } catch (IOException e) {
                            //This means the peer we are sending to is down. This is ok, just continue to the next one
                        }
                    }
                }
            } else {    //SEND TO 1 NODE
                try {
                    Unicast.sendMsg(message, splitMessage[1]);
                } catch (IOException e) {
                    //This means the peer we are sending to is down. This is ok, just continue to the next one
                }
            }

        } else {    //MESSAGE BEING RECEIVED BY NODE
            System.out.println("Received: " + message); //~Print~//
            int msgTerm;
            int seq;

            switch (splitMessage[2]) {
                case "HEARTBEAT":
                    //RESOLVE TERM INCONSISTENCIES
                    msgTerm = Integer.parseInt(splitMessage[3]); //(writing them separately 4 now, since log may be different)

                    if (msgTerm > term) {
                        term = msgTerm;
                        voted = true;
                    }
                    if (!state.equals(STATE.FOLLOWER)) {
                        state = STATE.FOLLOWER;
                    }
                    if (leader != splitMessage[0]) {
                        leader = splitMessage[0];
                    }

                    //HEARTBEAT
                    resetTimer();
                    break;

                case "LOG_REQUEST": //(Message format: Sender/Receiver/LOG_REQUEST/Key/Value)
                    if (state.equals(STATE.LEADER) && !request) {
                        //BEGIN LOG_REPLICATION (Leader and not busy)
                        append(true, splitMessage[3], splitMessage[4]);
                        logReplication.addLogResponse(name);

                    } else if (leader != null) {
                        //FORWARD TO LEADER (Not Leader)
                        splitMessage[0] = name;
                        splitMessage[1] = leader;
                        addMessage(String.join("/", splitMessage));

                    } else {
                        //REJECT CLIENT REQUEST (Leader busy, or election occurring)
                        addMessage( splitMessage[1] + "/" + splitMessage[0] + "/TO_CLIENT/Unable to process your " +
                                "request - Server busy. Please try again in a few moments.");
                    }
                    break;

                case "LOG_QUERY": //(Message format: Sender/Receiver/LOG_QUERY /(optional-key)
                    if (splitMessage.length == 4) {
                        addMessage(splitMessage[1] + "/" + splitMessage[0] + "/TO_CLIENT/" + query(splitMessage[3]));
                    }
                    else {
                        addMessage(splitMessage[1] + "/" + splitMessage[0] + "/TO_CLIENT/" + hashMap.toString());
                    }
                    break;

                case "LOG_APPEND":
                    seq = Integer.parseInt(splitMessage[3]);
                    // (Message format:  Sender/Receiver/LOG_APPEND/Sequence/Key/Value)
                    if (state.equals(STATE.FOLLOWER)){
                        //PROCEED WITH CURRENT REPLICATION (node caught up)
                        if (sequence == seq-1) {
                            append(true, splitMessage[4], splitMessage[5]);

                            write(seq, splitMessage[2] + "/" + String.join("/", Arrays.copyOfRange(splitMessage, 4, 6)));
                            incrSeq();

                            addMessage(splitMessage[1] + "/" + splitMessage[0] + "/LOG_RESPONSE");
                        } else {
                            //ASK LEADER for LOG_SYNC (node is behind)
                            addMessage(splitMessage[1] + "/" + splitMessage[0] + "/LOG_SYNC_REQUEST/" + sequence);
                        }
                    }
                    break;

                case "LOG_RESPONSE":
                    if (state.equals(STATE.LEADER)){
                        logReplication.addLogResponse(splitMessage[0]);
                    }
                    break;

                case "LOG_COMMIT":
                    seq = Integer.parseInt(splitMessage[3]);

                    if (state.equals(STATE.FOLLOWER)){
                        if (sequence == seq-1) {
                            //PROCEED WITH CURRENT COMMIT (node caught up)
                            commit();

                            write(seq, splitMessage[2]);
                            incrSeq();
                        } else {
                            //ASK LEADER for LOG_SYNC (node is behind)
                            addMessage(splitMessage[1] + "/" + splitMessage[0] + "/LOG_SYNC_REQUEST/" + sequence);
                        }
                    }
                    break;

                case "LOG_SYNC_REQUEST":
                    if (state.equals(STATE.LEADER)){
                        String[] parselm;
                        for(int i = Integer.parseInt(splitMessage[3])+1; i < log.size(); i++) {
                            parselm = log.get(i).split("/");
                            String retMsg = splitMessage[1] + "/" + splitMessage[0] + "/LOG_SYNC_EXECUTE" + "/" + parselm[0] + "/" + i;

                            if(parselm[0].equals("LOG_APPEND")) {
                                retMsg = retMsg + "/" + parselm[1] + "/" + parselm[2];
                            }
                            addMessage(retMsg);
                        }
                    }
                    break;

                case "LOG_SYNC_EXECUTE":
                    seq = Integer.parseInt(splitMessage[4]);
                    if  (splitMessage[3].equals("LOG_APPEND")) {
                        append(true, splitMessage[5], splitMessage[6]);
                        write(seq, splitMessage[3] + "/" + String.join("/", Arrays.copyOfRange(splitMessage, 5, 7)));
                        incrSeq();
                    } else {
                        commit();
                        write(seq, splitMessage[3]);
                        incrSeq();
                    }
                    break;

                case "VOTE_REQUEST":
                    //RESOLVE TERM INCONSISTENCIES
                    msgTerm = Integer.parseInt(splitMessage[3]);

                    if (msgTerm > term) {
                        term = msgTerm;
                        voted = false;

                        if (state.equals(STATE.LEADER)) {
                            state = STATE.FOLLOWER;
                            heartbeat.interrupt();
                            timer.start();
                        }
                    }
                    if (leader != null) {
                        leader = null;
                    }

                    //RESPOND
                    if (!voted) {
                        addMessage( splitMessage[1] + "/" + splitMessage[0] + "/VOTE_RESPONSE/" + msgTerm);
                        voted = true;
                    }
                    break;

                case "VOTE_RESPONSE":
                    election.addVote(splitMessage[0]);
                    break;
            }
        }
    }

    //STATIC HELPER METHODS
    public static int genRandomNum(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }

    //ACCESSORS & (MUTATORS)
    public String getNodeName() {
        return name;
    }

    public STATE getNodeState() {
        return this.state;
    }

    public int getTerm() {
        return this.term;
    }

    public boolean getRequest() {
        return this.request;
    }

    public String getKey() {
        return this.key;
    }

    public String getVal() {
        return this.val;
    }

    public int getSequence() {
        return this.sequence;
    }

    public void incrSeq() {
        sequence++;
    }

    /* create a copy of a set of the other peers to send messages to */
    public HashSet<String> getSendList() {
        HashSet<String> sendList = new HashSet<>(nodeList);
        sendList.remove(name);
        return sendList;
    }
}