package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.*;

/** Classes for managing incoming connections from other peers (mainly) */

class Unicast extends Thread {

    private Node node;
    private static int port;

    ServerSocket listenerSocket;

    public Unicast(Node current) {

        node = current;
        port = 9092;
    }

    /** Server.Unicast server */
    public void run() {
        try {
            listenerSocket = new ServerSocket(port);
            System.out.println("connection srver started ...\n"); //~Print~//
            while (true) {
                Socket peerSocket = listenerSocket.accept();
                UnicastConnection c = new UnicastConnection(node, peerSocket);
                c.start();
            }
        } catch (IOException e) {
            System.out.println("Listen :" + e.getMessage());
        }
    }

    public static void sendMsg(String message, String peerName) throws IOException{
        Socket senderSocket = new Socket(peerName, port);
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

    Node node;

    private DataInputStream in;
    private Socket peerSocket;

    public UnicastConnection(Node current, Socket aPeerSocket) {
        node = current;
        try {
            peerSocket = aPeerSocket;
            in = new DataInputStream(peerSocket.getInputStream());
        } catch (IOException e) {
            System.out.println("Connection:" + e.getMessage());
        }
    }

    /** Server.Unicast listener */
    public void run() {
        try {
            String message = in.readUTF();

            node.addMessage(message);

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