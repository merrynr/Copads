package edu.rit.cs.gossip;

import java.io.File;

// interface defined based on this paper, https://www.distributed-systems.net/my-data/papers/2007.osr.pdf
public interface Peer {

    /**
     * Select a neighboring peer to send a message
     * @return Selected peer
     */
    public Peer selectPeer();

    /**
     * Select a message to be sent
     * @return Selected message
     */
    public String selectToSend();

    /**
     * Send a selected message to a selected peer
     * @param peer Selected peer
     * @param msg Selected message
     */
    public void sendTo(Peer peer, String msg);

    /**
     * Receive a message from a peer
     * @param peer A peer who sent a message
     * @param msg The message content
     */
    public void receiveFrom(Peer peer, String msg);

    /**
     * Receive a message from any peer
     * @param msg The message content
     */
    public void receiveFromAny(String msg);

    /**
     * Determine whether to cache the message to a file
     * @param file Local cache
     * @param msg The message content
     */
    public void selectToKeep(File file, String msg);

    /**
     * Process the data from a local cache
     * @param file The file containing multiple messages
     */
    public void processData(File file);

}
