package edu.rit.cs.rpc;

//The Client sessions package
import com.thetransactioncompany.jsonrpc2.client.*;

//The Base package for representing JSON-RPC 2.0 messages
import com.thetransactioncompany.jsonrpc2.*;

//The JSON Smart package for JSON encoding/decoding (optional)
import net.minidev.json.*;

//For creating URLs
import java.net.*;
import java.util.HashMap;
import java.util.Map;


public class JsonRPCClient {


	public static void main(String[] args) {


		// Creating a new session to a JSON-RPC 2.0 web service at a specified URL
		// The JSON-RPC 2.0 server URL
		URL serverURL = null;

		try {
			serverURL = new URL("http://127.0.0.1:8080");

		} catch (MalformedURLException e) {
		// handle exception...
		}

		// Create new JSON-RPC 2.0 client session
		JSONRPC2Session mySession = new JSONRPC2Session(serverURL);


		// Once the client session object is created, you can use to send a series
		// of JSON-RPC 2.0 requests and notifications to it.

		// Sending an example "getTime" request:
		// Construct new request
		String method = "getDate";
		int requestID = 0;
		JSONRPC2Request request = new JSONRPC2Request(method, requestID);
		if(method=="echo") {
			Map<String, Object> myParams = new HashMap<String, Object>();
			myParams.put("hello", "->world?");
			request.setNamedParams(myParams);
		}
		// Send request
		JSONRPC2Response response = null;

		try {
			response = mySession.send(request);

		} catch (JSONRPC2SessionException e) {

		System.err.println(e.getMessage());
		// handle exception...
		}

		// Print response result / error
		if (response.indicatesSuccess())
			System.out.println(response.getResult());
		else
			System.out.println(response.getError().getMessage());

	}
}