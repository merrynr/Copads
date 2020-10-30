package Server;

/**
 * Heartbeat generator: a counter thread that generates heartbeat messages and adds them to the msgQueue
 */
class Heartbeat extends Thread {
    Node node;

    public Heartbeat(Node current) {
        node = current;
    }


    /**
     * Run method for heartbeat thread
     * Adds message to message queue to send heartbeat message to all nodes
     */
    public void run() {

        while (node.getNodeState() == Node.STATE.LEADER) {
            node.addMessage(node.getNodeName() + "/ALL/HEARTBEAT/" + node.getTerm());

            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


}