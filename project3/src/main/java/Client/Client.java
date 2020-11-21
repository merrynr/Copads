package Client;

import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Client {

    public static Unicast unicast;
    public static String name;
    public static Set<String> nodeList;
    public static final int totalNodes = 5;

    public static void main(String args[]) {

        nodeList = new HashSet<>();
        unicast = new Unicast();
        unicast.start();

        name = System.getenv("HOSTNAME");

        for (int i = 1; i <= totalNodes; i++) {
            String next = "peer" + i;
            nodeList.add(next);
        }

        System.out.println("Client program started...");

        Scanner reader = new Scanner(System.in);
        String cmd;
        String[] arr;
        String msg;
        int num;

        while (true) {
            System.out.println("Enter R for Request or Q for QUERY");
            cmd = reader.nextLine();
            switch (cmd) {
                case "R":
                    System.out.println("Enter <key> <value> <1 through 5>\n" +
                            "To delete an entry, type DELETE as the value.");
                    arr = reader.nextLine().split(" ");
                    num = Integer.parseInt(arr[2]);

                    if (num > 0 && num < 6) {
                        msg = name + "/peer" + arr[2] + "/LOG_REQUEST/" + arr[0] + "/" + arr[1];
                        System.out.println(msg);

                        try {
                            Unicast.sendMsg(msg, "peer" + arr[2]);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("Peer # out of range. Please try again");
                    }

                    break;
                case "Q":
                    System.out.println("Enter which peer: <1 through 5> <optional: key>");
                    arr = reader.nextLine().split(" ");
                    num = Integer.parseInt(arr[0]);

                    if (num > 0 && num < 6) {
                        msg = name + "/" + "peer" + arr[0] + "/LOG_QUERY";
                        if(arr.length == 2)
                            msg = msg + "/" + arr[1];
                        System.out.println(msg);

                        try {
                            Unicast.sendMsg(msg, "peer" + arr[0]);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                default:
                    System.out.println("Could not understand command");
            }
        }
    }
}