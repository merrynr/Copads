package Server;

/**
 * Timer thread subclass: gens random time from 3 to 5 s and countdown. Alerts Server.Node to change status(es) at 0
 * */ //OK
public class Timer extends Thread {

    Node node;
    int countdown;

    public Timer(Node current) {
        node = current;
        countdown = Node.genRandomNum(3000, 5000); //between 3 to 5 s
    }

    public void run() {
        boolean isInterrupted = false;
        try {
            Thread.sleep(countdown);
        } catch (InterruptedException e) {
            isInterrupted = true;
        }

        if(!isInterrupted) {
            System.out.println("starting election...");
            node.startElection();
        }
    }
}
