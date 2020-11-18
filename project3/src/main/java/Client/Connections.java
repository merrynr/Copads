package Client;


import Server.Node;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.*;

/** Classes for managing incoming connections from other peers (mainly) */

class Unicast extends Thread {

    private static int port = 9092;
    ServerSocket listenerSocket;

    /** Server.Unicast server */
    public void run() {
        try {
            listenerSocket = new ServerSocket(port);
            System.out.println("connection server started ...\n"); //~Print~//
            while (true) {
                Socket peerSocket = listenerSocket.accept();
                UnicastConnection c = new UnicastConnection(peerSocket);
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

    /** Client's Unicast listener */
    public void run() {
        try {
            String message = in.readUTF();

            System.out.println(message);

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