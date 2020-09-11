import java.net.*;
import java.io.*;

public class Client {

    public static void main(String args[]) {

        String filename = "src/main/resources/" + args[0];

        String serverIP = args[1];
        int serverPort = 6789;

        int listenerPort = 6789;

        //create client-side connection listener
        try {
            ServerSocket listenerSocket = new ServerSocket(listenerPort);
            System.out.println("Listener Initialized...");

            // create request & send to server
            Request newConnection = new Request(filename, serverIP, serverPort,
                    listenerSocket.getLocalPort());

            System.out.println("Waiting for reply from server...");
            Socket replySocket = listenerSocket.accept();
            DataInputStream in = new DataInputStream(replySocket.getInputStream());

            String data = in.readUTF();
            System.out.println("Received data from server, \"" + data + "\".");

            listenerSocket.close();
            in.close();

        } catch (IOException e) {
            System.out.println("Listen :" + e.getMessage());
        }
    }

}

class Request extends Thread {
    Socket requestSocket;

    String serverIP;
    int serverPort;

    int localPort;

    DataOutputStream out;

    String filename;

    public Request(String filename, String serverIP, int serverPort, int localPort){
        this.serverIP = serverIP;
        this.serverPort = 6789;

        this.localPort = localPort;
        this.filename = filename;
        this.start();
    }

    public void run() {
        //establish request_connection
        try {
            requestSocket = new Socket(serverIP, serverPort);
            out = new DataOutputStream(requestSocket.getOutputStream());

            String addr = Integer.toString(localPort);
            out.writeUTF(addr);
            System.out.println("Sent: " + addr + "\n");

            //read csv file and send contents to server
            Transfer.sendAsFile(requestSocket, filename);
            out.close();

            System.out.println("Sent: " + addr + "\n");

        } catch (UnknownHostException e) {
            System.out.println("Socket:" + e.getMessage());
        } catch (EOFException e) {
            System.out.println("EOF:" + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO:" + e.getMessage());
        } finally {
            if (requestSocket != null)
                try {
                    requestSocket.close();
                } catch (IOException e) {
                    System.out.println("Failed to close requestSocket: " + e.getMessage());
                }
        }
    }
}