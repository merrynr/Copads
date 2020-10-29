package Server;

import java.io.File;
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
    private Map<Integer, String> hashMap;
    // The queue to keep track of & send outgoing messages
    private Queue<String> msgQueue;

    // The timer for this node
    private Timer timer;
    // The number of votes the node has received for the current term
    private int voteCount;
    // The current term of voting
    private int term;
    // Whether the node has voted for the current term
    private boolean voted;
    // The current state of the node
    private STATE state;

    // The list of all the peers in the cluster
    private ArrayList<String> peerList;
    // The name of the current leader of the cluster, or null if during election
    private String leader;
    // The totalNodes number of nodes in this cluster
    private final int totalNodes = 3; //FIXME: still need to change to 5 & add 2 more peers in docker-compose
    // The name of this node
    private String name;


    /* Node constructor: initialize new node */ //OK
    public Node() {
        log = new File("src/main/resources/log.txt");
        hashMap = new HashMap<>();
        msgQueue = new LinkedList<>();

        timer = new Timer(this);
        voteCount = 0;
        term = 0;
        voted = false;
        state = STATE.FOLLOWER;

        peerList = new ArrayList();
        leader = null;
        name = System.getenv("HOSTNAME");

        //hard-code peerlist portion (fix me at the end)
        for (int i = 1; i <= totalNodes; i++) {
            String next = "peer" + i;
            if(!next.equals(name))
                peerList.add(next);
        }

        System.out.println("I am " + name); //~Print~//
    }

    public void resetTimer() {
        timer.interrupt();
        timer = new Timer(this);
        timer.start();
    }

    //ELECTION
    /* Method to create new election thread and start election process */
    public void startElection() {

    }

    //Methods (synchronization on msgQueue might require Server.Node to be a Thread...hmm):



    //STATIC HELPER METHODS:
    public static int genRandomNum(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }



    //MESSAGES
    public void addMessage(String message) {
        synchronized (msgQueue) {
            msgQueue.add(message);
        }
    }

    public void processMessage() {}


    public STATE getNodeState() {
        return this.state;
    }
}