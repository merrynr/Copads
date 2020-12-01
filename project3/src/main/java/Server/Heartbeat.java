package Server;

import java.util.HashSet;
import java.util.Set;

/**
 * Heartbeat generator: a counter thread that generates heartbeat messages and adds them to the msgQueue
 */
class Heartbeat extends Thread {
    Node node;
    Set<String> sendList;
    Set<String> receiveList;

    public Heartbeat(Node current) {
        node = current;

        sendList = node.getSendList();
        receiveList = new HashSet<>();
    }


    /**
     * Run method for heartbeat thread
     * Adds message to message queue to send heartbeat message to all nodes
     */
    public void run() {

        while (node.getNodeState() == Node.STATE.LEADER) {
            node.addMessage(node.getNodeName() + "/ALL/HEARTBEAT/" + node.getTerm() + "/" + node.getSequence());

            if (node.getRequest()) {
                String msg;
                node.incrSeq();

                if(!(receiveList.size() > sendList.size())) {
                    //not majority -> send LOG_APPEND
                    node.write(node.getSequence(), "LOG_APPEND/" + node.getKey() + "/" + node.getVal());

                    node.addMessage(node.getNodeName() + "/ALL/LOG_APPEND/" + node.getSequence() + "/" + node.getKey() + "/" + node.getVal());
                } else {
                    //majority confirmed -> send LOG_COMMIT
                    node.write(node.getSequence(), "LOG_COMMIT");
                    node.commit();

                    node.addMessage(node.getNodeName() + "/ALL/" + "LOG_COMMIT/" + node.getSequence());

                    sendList = node.getSendList();
                    receiveList = new HashSet<>();
                    receiveList.add(node.getNodeName());
                }
            }

            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void addLogResponse(String nodeName) {
        if(sendList.contains(nodeName))
            sendList.remove(nodeName);

        receiveList.add(nodeName);
    }
}