import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.*;

/** Classes for managing incoming connections from other peers (mainly) */

class Broadcast extends Thread {

    private Host host;
    private static int port;

    private DatagramSocket listenerSocket;
    int bufLen;
    private byte[] listenerBuf;

    private String initMsg;
    private String recMsg;

    public Broadcast(Host current) {

        host = current;
        port = 9091;

        initMsg = "Add " + host.getHostName();
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
                if (!msgArgs[1].equals(host.getHostName())) {

                    //New Peer
                    host.addPeer(msgArgs[1]);
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Broadcast sender */
    private static void broadcast (String message) throws IOException {
        DatagramSocket senderSocket = senderSocket = new DatagramSocket();
            senderSocket.setBroadcast(true);

            byte[] senderBuf = message.getBytes();

            DatagramPacket packet = new DatagramPacket(senderBuf, senderBuf.length,
                    InetAddress.getByName("255.255.255.255"), port);
            senderSocket.send(packet);

            if (senderSocket != null) senderSocket.close();
    }
}


class Unicast extends Thread {

    private Host host;
    private static int port;

    ServerSocket listenerSocket;

    public Unicast(Host current) {

        host = current;
        port = 9092;
    }

    /** Unicast server */
    public void run() {
        try {
            listenerSocket = new ServerSocket(port);
            System.out.println("Unicast Server started ..."); //~*TestPrint*~//
            while (true) {
                Socket peerSocket = listenerSocket.accept();
                UnicastConnection c = new UnicastConnection(host, peerSocket);
                c.start();
            }
        } catch (IOException e) {
            System.out.println("Listen :" + e.getMessage());
        }
    }

    public static void unicast (String message, String name) throws IOException{
        Socket senderSocket = new Socket(name, port);
        DataOutputStream out = new DataOutputStream(senderSocket.getOutputStream());

        out.writeUTF(message);
        out.flush();

        out.close();

        if (senderSocket != null) {
            try {
                senderSocket.close();
            } catch (IOException e) {
                System.out.println("close:" + e.getMessage());
            }
        }

    }
}


class UnicastConnection extends Thread {

    Host host;

    private DataInputStream in;
    private Socket peerSocket;

    public UnicastConnection(Host current, Socket aPeerSocket) {
        host = current;
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
            String message = in.readUTF();
            host.addMessage(message);
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