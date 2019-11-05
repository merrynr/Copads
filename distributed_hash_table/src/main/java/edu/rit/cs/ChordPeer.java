package edu.rit.cs;

import java.io.File;

class FingerTable {
    private int index;
    private int idealSuccessor;
    private int actualSuccessor;
    private String ipAddress;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIdealSuccessor() {
        return idealSuccessor;
    }

    public void setIdealSuccessor(int idealSuccessor) {
        this.idealSuccessor = idealSuccessor;
    }

    public int getActualSuccessor() {
        return actualSuccessor;
    }

    public void setActualSuccessor(int actualSuccessor) {
        this.actualSuccessor = actualSuccessor;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
}


public class ChordPeer implements Peer{
    private int Id;
    private int predecessorId;  // Only needed if you want to record predecessor as well.
    private int successorId;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getPredecessorId() {
        return predecessorId;
    }

    public void setPredecessorId(int predecessorId) {
        this.predecessorId = predecessorId;
    }

    public int getSuccessorId() {
        return successorId;
    }

    public void setSuccessorId(int successorId) {
        this.successorId = successorId;
    }

    public String insert(File file) { return ""; }

    public File lookup(String hashCode) { return null; }

}
