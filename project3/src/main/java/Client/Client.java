package Client;

import java.util.Queue;
import java.util.Scanner;
import java.util.Set;

public class Client {

    public static Queue<String> msgQueue;

    public static void main(String args[]) {

        System.out.println("Client program started...");

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

    private String pickRandom() {
        return null;
    }

    //MOVE TO THREADED CONNECTION
    private static void sendMessage(String message) {

        //SEND TO FIRST NODE IN NODELIST
            //IF GOOD RESPONSE, BREAK
            //IF RECEIVE NO LEADER, PRINT FAILURE
            //IF NO RESPONSE, LOOP
    }

}
