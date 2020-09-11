import wordcount.WordCount_Seq_Improved;

import java.net.*;
import java.io.*;

public class Server {

    /**
     * Server main process function listens for initial connection from any incoming clients
     * and creates a serivce thread to handle each one
     */
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

    private Socket requestSocket;
    private Socket returnSocket;

    private String clientIP;
    private int clientPort;

    private DataInputStream in;
    private DataOutputStream out;

    public Service(Socket aClientSocket) {
        requestSocket = aClientSocket;
        this.start();
    }

    /**
     * Thread function to give service to a single client.
     * Eg:
     * establish initial connection with client, read received
     * data-set file, preform word_count on it, format results,
     * and finally send it back to the client as a file
     */
    public void run() {
        try {
            //establish request_connection streams

            in = new DataInputStream(requestSocket.getInputStream());

            String data = in.readUTF();
            System.out.println("Received port# from client: \"" + data + "\""); /*~*/

            clientIP = requestSocket.getInetAddress().getHostAddress();
            clientPort = Integer.parseInt(data);

            //read inputStream + write to file
            String inputFile = "src/main/resources/" + clientIP + "_Input";
            Transfer.receiveAsFile(requestSocket, inputFile);
            System.out.println("Data recieved in " + inputFile);

            //close request_connection when done.
            in.close();
            requestSocket.close();

            //handle word count + format response
            //IMPORTANT NOTE: affr.csv is too large to handle & causes outOfMemoryError on heap space
            String outputFile = "src/main/resources/" + clientIP + "_Output";
            WordCount_Seq_Improved.countWords(inputFile, outputFile);
            System.out.println("Finished writing results to: " + outputFile);

            //establish return_connection
            returnSocket = new Socket(clientIP, clientPort);
            out = new DataOutputStream(returnSocket.getOutputStream());

            //send back results
            Transfer.sendAsFile(returnSocket, outputFile);
            System.out.println("Sent result-file back to: " + clientIP);

            //close request_connection when done.
            out.close();
            returnSocket.close();

            //delete files related to this particular client
            File infile = new File(inputFile);
            File outfile = new File(outputFile);
            infile.delete();
            outfile.delete();


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
