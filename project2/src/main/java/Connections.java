import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.*;

/** Classes for managing incoming connections from other peers (mainly) */

class Broadcast extends Thread {

    private Host host;
    private int port;

    private DatagramSocket listenerSocket;
    int bufLen;
    private byte[] listenerBuf;

    private String initMsg;
    private String recMsg;

    public Broadcast(Host current) {

        host = current;
        port = 9091;

        initMsg = "Add " + host.hostname;
        bufLen = 256;
        listenerBuf = new byte[bufLen];
    }

    /** Broadcast listener */
    public void run() {

        try {
            listenerSocket = new DatagramSocket(port, InetAddress.getByName("0.0.0.0"));
            listenerSocket.setBroadcast(true);
            broadcast(initMsg);


            while (true) {
                DatagramPacket packet = new DatagramPacket(listenerBuf, listenerBuf.length);
                listenerSocket.receive(packet);
                recMsg = new String(packet.getData()).trim();

                String msgArgs[] = recMsg.split(" ");
                if (!msgArgs[1].equals(host.hostname)) {

                    //New Peer FIXME: refactor code
                    if (!host.peerList.containsKey(msgArgs[1])) {

                        System.out.println(recMsg);
                        Peer newPeer = new Peer();
                        host.peerList.put(msgArgs[1],newPeer);
                        //System.out.println("alive=" + newPeer.get_alive()); //Change this to timestamp later
                    } else {
                        //Peer shutdown and restarted
                        Peer aPeer = host.peerList.get(msgArgs[1]);
                        aPeer.set_alive(true);
                        aPeer.set_number(1);
                        //System.out.println("test_number=" + aPeer.get_number());
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

    ServerSocket listenerSocket;

    public Unicast(Host current) {

        host = current;
        port = 9092;
    }

    /** Unicast server */
    public void run() {
        try {
            listenerSocket = new ServerSocket(port);
            System.out.println("Unicast Server is running and accepting client connections..."); //FIXME//
            while (true) {
                Socket peerSocket = listenerSocket.accept();
                UnicastConnection c = new UnicastConnection(peerSocket);
                c.start();
            }
        } catch (IOException e) {
            System.out.println("Listen :" + e.getMessage());
        }
    }
}


class UnicastConnection extends Thread {

    private DataInputStream in;
    private Socket peerSocket;

    public UnicastConnection(Socket aPeerSocket) {
        try {
            peerSocket = aPeerSocket;
            in = new DataInputStream(peerSocket.getInputStream());
        } catch (IOException e) {
            System.out.println("Connection:" + e.getMessage());
        }
    }

    /** Unicast listener */
    public void run() {
        try {
            String data = in.readUTF();
            System.out.println("Received message: " + data); //FIXME//

            //TODO: store in queue for processing/forwarding

        } catch (EOFException e) {
            System.out.println("EOF:" + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO:" + e.getMessage());
        } finally {
            try {
                peerSocket.close();
            } catch (IOException e) {/*close failed*/}
        }
    }
}