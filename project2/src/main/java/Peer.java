public class Peer {

    private Host host;

    private String name;
    private int number;
    private Timer timer;


    public Peer(Host current, String peerName) {
        host = current;
        name = peerName;
        number = 0;
        timer = new Timer();
        timer.start();
    }


    public void set_number(int number) { this.number = number; }

    public int get_number() { return number; }


    public void resetTimer() {
        timer.interrupt();
        timer = new Timer();
        timer.start();
    }

    /** Timer thread subclass */
    private class Timer extends Thread {

        public void run() {
            boolean isInterupted = false;
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                isInterupted = true;
            }

            if(!isInterupted) {
                String downMsg = "Down " + name + " " + host.getSendCount();
                host.addMessage(downMsg);
                host.removePeer(name); //delete this
            }
        }
    }
}
