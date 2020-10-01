import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Host {

    String hostname;
    Map<String, Peer> peerList;

    public Host() {
        hostname = System.getenv("HOSTNAME");
        peerList = Collections.synchronizedMap(new HashMap<>());
    }


    public static void main(String args[]) {

        Host host = new Host();

        Broadcast broadcastServer = new Broadcast(host);
        broadcastServer.start();

        Unicast unicastServer = new Unicast(host);
        unicastServer.start();

        try{
            broadcastServer.join();
            unicastServer.join();

        } catch (InterruptedException e) {
            System.out.println("Interrupted :" + e.getMessage());
        }
    }

}


