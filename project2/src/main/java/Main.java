/**
 * Application class - starts broadcast, unicast, host, heartbeat, and maxnumber threads
 */
public class Main {

    public static void main(String args[]) {

        //central server/manager
        Host host = new Host();
        host.start();

        Broadcast broadcastServer = new Broadcast(host);
        broadcastServer.start();

        Unicast unicastServer = new Unicast(host);
        unicastServer.start();

        Heartbeat heartbeat = new Heartbeat(host);
        heartbeat.start();

        MaxNumber maxNumber = new MaxNumber(host);
        maxNumber.start();

        try{
            broadcastServer.join();
            unicastServer.join();

            heartbeat.join();
            maxNumber.join();
            host.join();

        } catch (InterruptedException e) {
            System.out.println("Interrupted :" + e.getMessage());
        }
    }
}
