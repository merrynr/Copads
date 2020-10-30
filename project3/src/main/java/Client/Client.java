package Client;

import java.util.Queue;
import java.util.Scanner;
import java.util.Set;

public class Client {

    public static Queue<String> msgQueue;
    public static Unicast unicast;
    public static String name = System.getenv("HOSTNAME");

    public static void main(String args[]) {

        System.out.println("Client program started...");
        unicast = new Unicast();
        unicast.start();

        Scanner reader = new Scanner(System.in);
        String cmd;

        while (true) {
            System.out.println("Enter QUERY or a KEY/VALUE pair to add");
            cmd = reader.nextLine();

            if (cmd.equals("QUERY")) {
                synchronized (msgQueue) {
                    msgQueue.add("LOG_QUERY");
                }
            } else {
                synchronized (msgQueue) {
                    msgQueue.add("LOG_REQUEST/" + cmd);
                }
            }
        }
    }

    public static void addMessage(String message) {
        synchronized (msgQueue) {
            msgQueue.add(message);
        }
    }

    //MOVE TO THREADED CONNECTION
    private static void processMessage(String message) {

        String[] splitMessage = message.split("/");
        switch (splitMessage[1]) {
            case "LOG_QUERY":

                break;

            case "LOG_REQUEST":

                break;
        }


        //SEND TO FIRST NODE IN NODELIST
            //IF GOOD RESPONSE, BREAK
            //IF RECEIVE NO LEADER, PRINT FAILURE
            //IF NO RESPONSE, LOOP
    }



}
