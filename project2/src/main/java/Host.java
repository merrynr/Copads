import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Host {

    String hostname;
    Map<String, Peer> peerList;

    public Host() {
        hostname = System.getenv("HOSTNAME");
        //System.out.println("My name is: " + hostname);

        peerList = Collections.synchronizedMap(new HashMap<>());
    }



    public static void main(String args[]) {

        Host host = new Host();

        Broadcast broadcastServer = new Broadcast(host);
        broadcastServer.start();

        //TODO: Create Unicast thread

        try{
            broadcastServer.join();
            //unicastServer.join();

        } catch (InterruptedException e) {
            System.out.println("Interrupted :" + e.getMessage());
        }
    }

}


class Broadcast extends Thread {

    private Host host;
    private int port;

    private static DatagramSocket receiverSocket; //receiverSocket
    int bufLen;
    private byte[] receiveBuf; //receiverBuf

    private String initMsg;
    private String recMsg;

    public Broadcast(Host current) {

        host = current;
        port = 9091;

        initMsg = "Add " + host.hostname;
        bufLen = 256;
        receiveBuf = new byte[bufLen];
    }

    /** Broadcast receiver */
    public void run() {

        //System.out.println("Running Broadcast server of " + host.hostname);


        try {
            receiverSocket = new DatagramSocket(port, InetAddress.getByName("0.0.0.0"));
            receiverSocket.setBroadcast(true);
            broadcast(initMsg);


            while (true) {
                //System.out.println("next");
                DatagramPacket packet = new DatagramPacket(receiveBuf, receiveBuf.length);
                receiverSocket.receive(packet);
                System.out.println("received a packet");
                recMsg = new String(packet.getData()).trim();

                String msgArgs[] = recMsg.split(" ");
                if (!msgArgs[1].equals(host.hostname)) {

                    //New Peer
                    if (!host.peerList.containsKey(msgArgs[1])) {

                        System.out.println(recMsg);
                        Peer newPeer = new Peer();
                        host.peerList.put(msgArgs[1],newPeer);
                        System.out.println("alive=" + newPeer.get_alive()); //Change this to timestamp later
                    } else {
                        //Peer shutdown and restarted
                        Peer aPeer = host.peerList.get(msgArgs[1]);
                        aPeer.set_alive(true);
                        aPeer.set_number(1);
                        System.out.println("test_number=" + aPeer.get_number());
                    }
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Broadcast sender */
    private void broadcast(String message) throws IOException {
        DatagramSocket senderSocket = new DatagramSocket();
        senderSocket.setBroadcast(true);

        byte[] senderBuf = message.getBytes();

        DatagramPacket packet = new DatagramPacket(senderBuf, senderBuf.length,
                InetAddress.getByName("255.255.255.255"), port);
        senderSocket.send(packet);
        System.out.println("packet sent");
        senderSocket.close();
    }
}


class Unicast extends Thread {

    private Host host;
    private int port;

    public Unicast(Host current) {

        host = current;
        port = 9091;

    }

    public void run() {

    }
}