public class Heartbeat extends Thread{

    Host host;

    public Heartbeat(Host current) {
        host = current;
    }

    public void run() {
        while(true) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String liveMsg = "Live " + host.getHostName() + " " + host.getSendCount();
            host.addMessage(liveMsg);
        }
    }
}
