package Client;

import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Client {

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

        Scanner reader = new Scanner(System.in);
        String cmd;
        String msg;

        while (true) {
            System.out.println("Enter a cmd... (A -> change to the hash-map value, \nB-> query hash-map values.)");
            cmd = reader.nextLine();
            switch (cmd) {
                case "A":
                    System.out.println("Enter <key> <value> <peer>");
                    String[] arr = reader.nextLine().split(" ");

                    msg = name + "/" + arr[2] + "/LOG_REQUEST/" + arr[0] + "/ " + arr[1];
                    System.out.println(msg);
                    try {
                        Unicast.sendMsg(msg, arr[2]);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case "B":
                    System.out.println("Enter which peer: (1 through 5)");
                    cmd = reader.nextLine();
                    int num = Integer.parseInt(cmd);
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

    private String pickRandom() {
        return null;
    }

}
