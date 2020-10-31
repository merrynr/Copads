package Client;

import java.io.IOException;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;

public class Client {

    public static Queue<String> msgQueue;
    public static Unicast unicast;
    public static MessagePrinter messagePrinter;
    public static String name = System.getenv("HOSTNAME");
    public static Set<String> nodeList;
    public static final int totalNodes = 5;

    public static void main(String args[]) {

        System.out.println("Client program started...");
        unicast = new Unicast();
        unicast.start();
        messagePrinter = new MessagePrinter();
        messagePrinter.start();

        for (int i = 1; i <= totalNodes; i++) {
            String next = "peer" + i;
            nodeList.add(next);
        }

        Scanner reader = new Scanner(System.in);
        String cmd;

        while (true) {
            System.out.println("Enter QUERY or a KEY/VALUE pair to add");
            cmd = reader.nextLine();
            boolean sent = false;

            if (cmd.equals("QUERY")) {

                for (String node : nodeList) {
                    try {
                        Unicast.sendMsg(Client.name + "/" + node + "/QUERY", node);
                        sent = true;
                    } catch (IOException e) {
                        continue;
                    }

                    if (sent) {
                        break;
                    }
                }

                if (!sent) {
                    System.out.println("No valid nodes found");
                }

            } else {
//
                String[] splitInput = cmd.split("/");
                for (String node : nodeList) {
                    try {
                        Unicast.sendMsg(Client.name + "/" + node + "/LOG_REQUEST" + splitInput[0] + "/" + splitInput[1], node);
                        sent = true;
                    } catch (IOException e) {
                        continue;
                    }
                }

                if (!sent) {
                    System.out.println("No valid nodes found");
                }
            }
        }
    }

    public static void addMessage(String message) {
        synchronized (msgQueue) {
            msgQueue.add(message);
        }
    }


    public static class MessagePrinter extends Thread {

        public void run() {
            String message;

            if (msgQueue.isEmpty()) {
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                synchronized (msgQueue) {
                    message = msgQueue.remove();
                }

                String[] splitMessage = message.split("/");
                System.out.println(splitMessage[3]);
            }
        }
    }


}
