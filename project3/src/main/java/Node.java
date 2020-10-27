import java.io.File;
import java.util.*;

/***
 * Class representing one of our nodes
 */
public class Node {

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
    // The total number of nodes in this cluster
    private final int total = 3; //FIXME: still need to change to 5 & add 2 more peers in docker-compose
    // The name of this node
    private String name;


    public Node() {
        //log = new File(); //TODO: need to deal with filepath
        hashMap = new HashMap<>();
        msgQueue = new LinkedList<>();

        timer = new Timer();
        voteCount = 0;
        term = 0;
        voted = false;
        state = STATE.FOLLOWER;

        peerList = new ArrayList();
        name = System.getenv("HOSTNAME");

        //hard-code peerlist portion (fix me at the end)
        for (int i = 1; i <= total; i++) {
            String next = "peer" + i;
            if(!next.equals(name))
                peerList.add(next);
        }

        System.out.println("I am " + name); //~Print~//
    }

    //Methods (synchronization on msgQueue might require Node to be a Thread...hmm):
    //private void processMessage(String message) {}


    /**
     * Timer thread subclass: gens random time from 3 to 5 s and countdown. Alerts Node to change status(es) at 0
     * */
    private class Timer extends Thread {
        //TODO: (might be able to reuse project 2's with some mods like the randomization?)
    }

    /**
     * Heartbeat generator: a counter thread that generates heartbeat messages and adds them to the msgQueue
     */
    private class Heartbeat extends Thread {
        //TODO: (could also choose to reuse project 2's heartbeat, but with 0.5 s...?)
    }

}