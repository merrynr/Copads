package Client;

import java.util.Scanner;

public class Client {

    public static void main(String args[]) {

        System.out.println("Client program started...");

        Scanner reader = new Scanner(System.in);
        String cmd;

        while (true) {
            System.out.println("Enter a message make a change to the hash-map value: ");
            cmd = reader.nextLine();

            System.out.println("Echo: " + cmd);//~Print~// FIXME

        }
    }

    private String pickRandom() {
        return null;
    }

}
