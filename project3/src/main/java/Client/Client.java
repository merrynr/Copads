package Client;

import java.io.IOException;
import java.util.HashSet;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;

public class Client {

    public static Queue<String> msgQueue;
    public static Unicast unicast;
    public static String name;
    public static Set<String> nodeList;
    public static final int totalNodes = 5;

    public static void main(String args[]) {

        Set<String> nodeList = new HashSet<>();
        Unicast unicast = new Unicast();
        unicast.start();

        String name = System.getenv("HOSTNAME");

        for (int i = 1; i <= 5; i++) {
            String next = "peer" + i;
            nodeList.add(next);
        }
        System.out.println("Client program started...");
        unicast = new Unicast();
        unicast.start();

        for (int i = 1; i <= totalNodes; i++) {
            String next = "peer" + i;
            nodeList.add(next);
        }

        Scanner reader = new Scanner(System.in);
        String cmd;
        String msg;
        int num;

        while (true) {
            System.out.println("Enter R for Request or Q for QUERY");
            cmd = reader.nextLine();
            switch (cmd) {
                case "R":
                    System.out.println("Enter <key> <value> <1 through 5>");
                    String[] arr = reader.nextLine().split(" ");

                    num = Integer.parseInt(arr[1]);

                    if (num > 0 && num < 6) {
                        msg = name + "/" + arr[2] + "/LOG_REQUEST/" + arr[0] + "/peer" + num;
                        System.out.println(msg);

                        try {
                            Unicast.sendMsg(msg, arr[2]);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("Peer # out of range. Please try again");
                    }

                    break;
                case "Q":
                    System.out.println("Enter which peer: <1 through 5>");
                    cmd = reader.nextLine();
                    num = Integer.parseInt(cmd);
                    if (num > 0 && num < 6) {
                        cmd = "peer" + num;

                        msg = name + "/" + cmd + "/LOG_QUERY";
                        System.out.println(msg);

                        try {
                            Unicast.sendMsg(msg, cmd);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                default:
                    System.out.println("Cannot understand command. Please try again.");
            }
        }
    }
}