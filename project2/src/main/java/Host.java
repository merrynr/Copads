import java.io.IOException;
import java.util.*;

public class Host extends Thread{

    private String hostName;
    private Map<String, Peer> peerList;
    private Queue<String> messageQueue;

    private final double sendSize = 0.5; //percentage of peerList to send to
    private final int sendCount = 2;

    /** Host Constructor */
    public Host() {
        hostName = System.getenv("HOSTNAME");
        peerList = new HashMap<>();
        messageQueue = new LinkedList<>();

        System.out.println("I am " + hostName); //~*TestPrint*~//
    }

    /** Gossip manager */
    public void run() {
        while(true) {
            synchronized (messageQueue) {
                if(messageQueue.isEmpty()) {
                    try {
                        messageQueue.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    String aMsg = messageQueue.remove();
                    processMessage(aMsg);
                }
            }
        }
    }

    private void processMessage(String message) {
        String msgArgs[] = message.split(" ");

        String cmd = msgArgs[0];
        String sender = msgArgs[1];
        int forwardCountdown = Integer.parseInt(msgArgs[2]);

        //execute command & modify message if not from the host itself
        printPeerList();
        if(!sender.equals(hostName)) {
            switch(cmd) {
                case "Live":
                    System.out.println("got live msg from someone else"); //~*TestPrint*~//
                    if(peerList.containsKey(sender)) {
                        peerList.get(sender).resetTimer();
                    } else {

                        System.out.println("peerList added " + sender); //~*TestPrint*~//
                        addPeer(sender);
                    }
                    break;
                case "Down":
                    if(peerList.containsKey(sender)) {
                        peerList.remove(sender);
                    }
                    break;
            }
        }

        if(forwardCountdown > 0 ) {
            System.out.println(message);
            msgArgs[2] = Integer.toString(forwardCountdown - 1);
            message = String.join(" ", msgArgs);

            for (String peer : selectPeers()) {
                try {
                    System.out.println(); //~*TestPrint*~//
                    Unicast.unicast(message, peer);
                } catch (IOException e) {/*peer is down, timer will eventually remove it*/}
            }
        }
    }

    //Accessors
    public String getHostName() {
        return hostName;
    }

    public int getSendCount() {
        return sendCount;
    }

    //Methods for peerList
    public void addPeer(String peerName) {
        if(!peerList.containsKey(peerName)) {
            Peer newPeer = new Peer(this, peerName);
            peerList.put(peerName, newPeer);
        }
    }

    public void removePeer(String peerName) {
        if(peerList.containsKey(peerName)) {
            peerList.remove(peerName);
        }
    }

    private ArrayList<String> selectPeers() {
        ArrayList<String> allPeers = new ArrayList<>(peerList.keySet());
        ArrayList<String> forwardPeers = new ArrayList<>();

        int absSendSize;

        //add peers to the forward list
        if(!allPeers.isEmpty()) {
            absSendSize = (int)Math.round(allPeers.size() * sendSize);

            if(absSendSize == 0)
                absSendSize++;

            System.out.println("\tforward targets:"); //~*TestPrint*~//

            Collections.shuffle(allPeers);
            for (int i = 0; i < absSendSize; i++) {
                forwardPeers.add(allPeers.get(i));
                System.out.println("\t" + allPeers.get(i)); //~*TestPrint*~//
            }
        }

        return forwardPeers;
    }

    public void printPeerList() {
        System.out.println("PeerList: ");
        for (String key : peerList.keySet())
            System.out.println("\t" + key);
    }


    //Methods for messageQueue
    public void addMessage(String message) {
        synchronized (messageQueue) {
            messageQueue.add(message);
            messageQueue.notifyAll();
        }

    }

}


