package Server;

import Server.Node;

public class Main {
    public static void main(String args[]) {

        Node node = new Node();
        node.start();
        Unicast unicast = new Unicast(node);
        unicast.start();

        try{
            unicast.join();
            node.join();

        } catch (InterruptedException e) {
            System.out.println("Interrupted :" + e.getMessage());
        }
    }
}
