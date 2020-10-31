package Server;

import java.util.HashSet;
import java.util.Set;

public class LogReplication extends Thread{

    Node node;
    Set<String> sendList;
    Set<String> receiveList;

    public LogReplication(Node current) {
        node = current;

        sendList = node.getSendList();
        receiveList = new HashSet<>();
    }

    public void run() {
        String message;
        while (node.getNodeState() == Node.STATE.LEADER) {
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
