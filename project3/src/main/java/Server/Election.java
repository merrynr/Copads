package Server;

import java.util.HashSet;
import java.util.Set;

/**
 * Election: a thread that oversees the election process when node is a candidate
 */
public class Election extends Thread {

    Node node;
    Set<String> sendList;
    Set<String> receiveList;


    public Election(Node current) {
        node = current;
        sendList = node.getSendList();
        System.out.println("copied sendlist: " + sendList);
        receiveList = new HashSet<>();
    }

    /**
     * Run method for Election thread
     * Adds vote-request messages to message queue every 0.5 s until new term or majority votes received
     */
    public void run() {
        while(node.getNodeState() == Node.STATE.CANDIDATE) {
            System.out.println(receiveList.size() + "/" + sendList.size());
            if(receiveList.size() < sendList.size()) {

                for(String peer: sendList) {
                    node.addMessage(node.getNodeName() + "/" + peer + "/VOTE_REQUEST/" + node.getTerm());
                }

                try{
                    Thread.sleep(500);
                }
                catch(Exception e){
                    System.out.println("This shouldn't happen: Election thread unable to sleep.");
                }
            } else {
                node.stopTimer();
                node.startHeartbeat();
            }
        }
    }

    public void addVote(String nodeName) {
        if(sendList.contains(nodeName))
            sendList.remove(nodeName);

        receiveList.add(nodeName);
    }
}
