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
        while (node.getNodeState() == Node.STATE.LEADER) {
            if (node.getRequest()) {
                if(!(receiveList.size() > sendList.size())) {
                    //not majority -> send LOG_APPEND
                    node.addMessage(node.getNodeName() + "/ALL/LOG_APPEND/" + node.getSequence()+1 + "/" +
                                    node.getKey() + "/" + node.getVal());
                } else {
                    //majority confirmed -> send LOG_COMMIT
                    node.commit();
                    node.addMessage(node.getNodeName() + "/ALL/LOG_COMMIT/" + node.getSequence()); //(if u move this before, sequence+1)

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
