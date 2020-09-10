import java.net.*;
import java.io.*;

public class Server {

    public static void main(String args[]) {
        int listenerPort = 6789;

        //create server-side connection listener
        try {
            ServerSocket listenerSocket = new ServerSocket(listenerPort);
            System.out.println("Server is running and accepting client connections...");

            while (true) {
                Socket serviceSocket = listenerSocket.accept(); //create for each incoming client
                System.out.println("Incoming client received - creating new service thread.");
                Service newConnection = new Service(serviceSocket); //create corresponding thread for each client
            }
        } catch (IOException e) {
            System.out.println("Listen :" + e.getMessage());
        }
    }
}

class Service extends Thread {

    Socket requestSocket;
    Socket returnSocket;

    String clientIP;
    int clientPort;

    DataInputStream in;
    DataOutputStream out;

    public Service(Socket aClientSocket) {
        requestSocket = aClientSocket;
        this.start();
    }

    public void run() {
        try {
            //establish request_connection streams
            //then read input & close request_connection as soon as finished reading.
            in = new DataInputStream(requestSocket.getInputStream());

            String data = in.readUTF();
            System.out.println("Received data from client: \"" + data + "\""); /*~*/

            clientIP = clientIP = requestSocket.getInetAddress().getHostAddress();
            clientPort = Integer.parseInt(data);
            System.out.println(clientIP); /*~*/
            System.out.println(clientPort); /*~*/

            //TODO: handle word count + format response
            requestSocket.close();
            in.close();

            //establish return_connection
            returnSocket = new Socket(clientIP, clientPort);
            out = new DataOutputStream(returnSocket.getOutputStream());

            String message = "This is a test response from the server, did you get it?"; /*~*/
            out.writeUTF(message); /*~*/

            System.out.println("Sent: " + "\"" + message + "\"."); /*~*/


        } catch (EOFException e) {
            System.out.println("EOF:" + e.getMessage());
        } catch (UnknownHostException e) {
            System.out.println("Sock:" + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO:" + e.getMessage());
        } finally {
            if (requestSocket != null)
                try {
                    requestSocket.close();
                } catch (IOException e) {
                    System.out.println("Failed to close requestSocket.");
                }
            if (returnSocket != null)
                try {
                    returnSocket.close();
                } catch (IOException e) {
                    System.out.println("Failed to close returnSocket.");
                }
        }
    }
}
