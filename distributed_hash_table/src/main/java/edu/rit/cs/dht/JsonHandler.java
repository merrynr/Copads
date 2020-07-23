package edu.rit.cs.dht;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Error;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import com.thetransactioncompany.jsonrpc2.server.MessageContext;
import com.thetransactioncompany.jsonrpc2.server.RequestHandler;

import java.util.Map;

public class JsonHandler {

	 public static class MiniServerHandler implements RequestHandler {
	     // Reports the method names of the handled requests
		public String[] handledRequests() {
		    return new String[]{"online", "offline", "ispeeronline"};
		}

		// Processes the requests
		public JSONRPC2Response process(JSONRPC2Request req, MessageContext ctx) {
			Map<String, Object> myParams = req.getNamedParams();
			Object id = myParams.get("id");
		    if (req.getMethod().equals("online")) {
				Object ip = myParams.get("ip");
				Object port = myParams.get("port");
				MiniServer.addPeer((Integer)id, ip.toString(), port.toString());
				return new JSONRPC2Response(true, req.getID());
	         } else if (req.getMethod().equals("offline")) {
	         	MiniServer.removePeer((Integer)id);
				return new JSONRPC2Response(true, req.getID());
	         } else if (req.getMethod().equals("ispeeronline")) {
				return new JSONRPC2Response(MiniServer.isPeerOnline((Integer)id), req.getID());
			} else {
		        // Method name not supported
				return new JSONRPC2Response(JSONRPC2Error.METHOD_NOT_FOUND, req.getID());
	         }
	     }
	 }
}
