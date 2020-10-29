package Server;

/**
 * Heartbeat generator: a counter thread that generates heartbeat messages and adds them to the msgQueue
 */
class Heartbeat extends Thread {
    Node node;

    public Heartbeat(Node current) {
        node = current;
    }

    public void run() {
    }
}