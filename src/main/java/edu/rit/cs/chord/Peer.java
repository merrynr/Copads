package edu.rit.cs.chord;

public class Peer {
    private int Id;
    private int predecessorId;
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
}
