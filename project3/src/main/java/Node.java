import java.io.File;
import java.util.Map;

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


    private class Timer extends Thread {
    }

    private class Heartbeat extends Thread {
    }

}