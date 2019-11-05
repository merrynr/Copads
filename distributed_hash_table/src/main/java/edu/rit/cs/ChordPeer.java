package edu.rit.cs;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2Session;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2SessionException;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

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
    enum STATUS {
        ONLINE, OFFLINE;
    }

    private int nodeID;
    private int predecessorId;  // Only needed if you want to record predecessor as well.
    private int successorId;

    private int requestID = -1;
    private String ipAddress=Config.DEFAULT_PEER_IP;
    private String port;

    public ChordPeer(int nodeID) {
        this.nodeID = nodeID;
        this.port = Config.DEFAULT_PORT_PREFIX + String.valueOf(nodeID);
    }

    public ChordPeer(int nodeID, String ipAddress, String port){
        this.nodeID = nodeID;
        this.ipAddress = ipAddress;
        this.port = port;
        startPeer();
    }

    public void startPeer() {
        tellMiniServer(STATUS.ONLINE);
    }

    public void stopPeer() {
        tellMiniServer(STATUS.OFFLINE);
    }


    public boolean isPeerOnline(int peerID) {
        // Creating a new session to a JSON-RPC 2.0 web service at a specified URL
        // The JSON-RPC 2.0 server URL
        URL serverURL = null;

        try {
            serverURL = new URL(Config.SERVER_IP+":"+Config.SERVER_PORT);
        } catch (MalformedURLException e) {
            // handle exception...
        }

        // Create new JSON-RPC 2.0 client session
        JSONRPC2Session mySession = new JSONRPC2Session(serverURL);

        // Construct new request
        requestID++;
        JSONRPC2Request request = new JSONRPC2Request("ispeeronline", requestID);
        Map<String, Object> myParams = new HashMap<String, Object>();
        myParams.put("id", peerID);
        request.setNamedParams(myParams);

        // Send request
        JSONRPC2Response response = null;
        try {
            response = mySession.send(request);
        } catch (JSONRPC2SessionException e) {
            System.err.println(e.getMessage());
            // handle exception...
        }

        // Print response result / error
        if (response.indicatesSuccess()) {
            return (Boolean)response.getResult();
        } else {
            System.out.println(response.getError().getMessage());
            return false;
        }
    }

    private void tellMiniServer(STATUS status) {
        // Creating a new session to a JSON-RPC 2.0 web service at a specified URL
        // The JSON-RPC 2.0 server URL
        URL serverURL = null;

        try {
            serverURL = new URL(Config.SERVER_IP+":"+Config.SERVER_PORT);
        } catch (MalformedURLException e) {
            // handle exception...
        }

        // Create new JSON-RPC 2.0 client session
        JSONRPC2Session mySession = new JSONRPC2Session(serverURL);

        // Construct new request
        JSONRPC2Request request;
        requestID++;
        Map<String, Object> myParams = new HashMap<String, Object>();
        myParams.put("id", this.nodeID);
        switch (status) {
            case ONLINE:
                request = new JSONRPC2Request("online", requestID);
                myParams.put("ip", this.ipAddress);
                myParams.put("port", this.port);
                break;
            case OFFLINE:
            default:
                request = new JSONRPC2Request("offline", requestID);
                break;
        }
        request.setNamedParams(myParams);

        // Send request
        JSONRPC2Response response = null;
        try {
            response = mySession.send(request);
        } catch (JSONRPC2SessionException e) {
            System.err.println(e.getMessage());
            // handle exception...
        }

        // Print response result / error
        if (response.indicatesSuccess()) {
            if((Boolean)response.getResult())
                System.out.println("Successful!!!");
            else
                System.out.println("Failed!!!");
        } else
            System.out.println(response.getError().getMessage());
    }






    public int getNodeID() {
        return nodeID;
    }

    public void setNodeID(int nodeID) {
        this.nodeID = nodeID;
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
