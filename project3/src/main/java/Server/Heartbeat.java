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
     * Run method for hearthbeat thread
     * Adds message to message queue to send heartbeat message to all nodes
     */
    public void run() {

        while (node.getNodeState() == Node.STATE.LEADER) {
            node.addMessage(node.getName() + "/ALL/HEARTBEAT");
        }

        try {
            sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}