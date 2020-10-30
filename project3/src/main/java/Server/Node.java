package Server;

import java.io.File;
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


    // The message log TODO: Type
    private File log;
    // The key-value pair map TODO: k/v types
    private Map<String, String> hashMap;
    // The queue to keep track of & send outgoing messages
    private Queue<String> msgQueue;

    // The timer for this node
    private Timer timer;
    // The number of votes the node has received for the current term
    private int voteCount; //(Keeping for appendlog votes for now)
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
    private final int totalNodes = 3; //FIXME: still need to change to 5 & add 2 more peers in docker-compose

    // The name of this node
    private String name;

    private Heartbeat heartbeat;
    private Election election;


    /* Node constructor: initialize new node */ //OK
    public Node() {
        log = new File("src/main/resources/log.txt");
        hashMap = new HashMap<>();
        msgQueue = new LinkedList<>();

        voteCount = 0;
        term = 0;
        voted = false;
        state = STATE.FOLLOWER;

        nodeList = new HashSet<String>();
        leader = null;

        name = System.getenv("HOSTNAME");

        //hard-code peerlist portion (fix me at the end)
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
    /* Method to create new election thread and start election process */
    public void startHeartbeat() {
        state = STATE.LEADER;
        leader = name;

        if (heartbeat != null && heartbeat.isAlive()) { //This block should hopefully never run!
            heartbeat.interrupt();
        }

        heartbeat = new Heartbeat(this);
        heartbeat.start();

        System.out.println("BECAME LEADER.");
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

            //SEND MESSAGE TO ALL OTHER NODES
            if (splitMessage[1].equals("ALL")) {

                for (String peer : nodeList) {
                    if(!peer.equals(name)) {

                        splitMessage[1] = peer;
                        String outmsg = String.join("/", splitMessage);
                        System.out.println("Sending: " + outmsg);

                        try {
                            Unicast.sendMsg(outmsg, peer);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {    //SEND TO 1 NODE
                try {
                    Unicast.sendMsg(message, splitMessage[1]);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } else {    //MESSAGE BEING RECEIVED BY NODE
            System.out.println("Received: " + message);

            if (splitMessage[2].equals("HEARTBEAT")) {

                //RESOLVE TERM INCONSISTENCIES
                int msgTerm = Integer.parseInt(splitMessage[3]); //(writing them separately 4 now, since log may be different)

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

            } else if (splitMessage[2].equals("VOTE_REQUEST")) {

                //RESOLVE TERM INCONSISTENCIES
                int msgTerm = Integer.parseInt(splitMessage[3]);

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

                //VOTE_RESPONSE
                if (!voted) {
                    addMessage( splitMessage[1] + "/" + splitMessage[0] + "/VOTE_RESPONSE/" + msgTerm);
                    voted = true;
                }

            } else if (splitMessage[2].equals("VOTE_RESPONSE")) {
                election.addVote(splitMessage[0]);
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

    /* create a copy of a set of the other peers to send messages to*/
    public HashSet<String> getSendList() {
        HashSet<String> sendList = new HashSet<>(nodeList);
        sendList.remove(name);
        return sendList;
    }
}