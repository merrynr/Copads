package edu.rit.cs.dht;

//The JSON-RPC 2.0 Base classes that define the
//JSON-RPC 2.0 protocol messages

import com.thetransactioncompany.jsonrpc2.JSONRPC2ParseException;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import com.thetransactioncompany.jsonrpc2.server.Dispatcher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

class PeerInfo {
	private String ipAddress;
	private String port;
	public PeerInfo(String ipAddress, String port){
		this.ipAddress = ipAddress;
		this.port = port;
	}

	public String getIpAddress(){ return this.ipAddress; }
	public String getPort(){ return this.port; }
}

public class MiniServer {
	private static Map<Integer, PeerInfo> onlinePeers = new HashMap<Integer, PeerInfo>();

	public static void addPeer(int peerID, String ipAddres, String port) {
		onlinePeers.put(peerID, new PeerInfo(ipAddres, port));
	}

	public static void removePeer(int peerID) {
		onlinePeers.remove(peerID);
	}

	public static boolean isPeerOnline(int peerID) {
		return onlinePeers.containsKey(peerID);
	}

	/**
	 * The port that the server listens on.
	 */
	private static final int PORT = Integer.parseInt(Config.SERVER_PORT);

	/**
	 * A handler thread class.  Handlers are spawned from the listening
	 * loop and are responsible for a dealing with a single client
	 * and broadcasting its messages.
	 */
	private static class Handler extends Thread {
		private String name;
		private Socket socket;
		private BufferedReader in;
		private PrintWriter out;
		private Dispatcher dispatcher;

		/**
		 * Constructs a handler thread, squirreling away the socket.
		 * All the interesting work is done in the run method.
		 */
		public Handler(Socket socket) {
			this.socket = socket;

			// Create a new JSON-RPC 2.0 request dispatcher
			this.dispatcher =  new Dispatcher();

			// Register the "echo", "getDate" and "getTime" handlers with it
			dispatcher.register(new JsonHandler.MiniServerHandler());
		}

		/**
		 * Services this thread's client by repeatedly requesting a
		 * screen name until a unique one has been submitted, then
		 * acknowledges the name and registers the output stream for
		 * the client in a global set, then repeatedly gets inputs and
		 * broadcasts them.
		 */
		public void run() {
			try {
				// Create character streams for the socket.
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out = new PrintWriter(socket.getOutputStream(), true);

				// read request
				String line;
				line = in.readLine();
				//System.out.println(line);
				StringBuilder raw = new StringBuilder();
				raw.append("" + line);
				boolean isPost = line.startsWith("POST");
				int contentLength = 0;
				while (!(line = in.readLine()).equals("")) {
					//System.out.println(line);
					raw.append('\n' + line);
					if (isPost) {
						final String contentHeader = "Content-Length: ";
						if (line.startsWith(contentHeader)) {
							contentLength = Integer.parseInt(line.substring(contentHeader.length()));
						}
					}
				}
				StringBuilder body = new StringBuilder();
				if (isPost) {
					int c = 0;
					for (int i = 0; i < contentLength; i++) {
						c = in.read();
						body.append((char) c);
					}
				}

				System.out.println(body.toString());
				JSONRPC2Request request = JSONRPC2Request.parse(body.toString());
				JSONRPC2Response resp = dispatcher.process(request, null);


				// send response
				out.write("HTTP/1.1 200 OK\r\n");
				out.write("Content-Type: application/json\r\n");
				out.write("\r\n");
				out.write(resp.toJSONString());
				// do not in.close();
				out.flush();
				out.close();
				socket.close();
			} catch (IOException e) {
				System.out.println(e);
			} catch (JSONRPC2ParseException e) {
				e.printStackTrace();
			} finally {
				try {
					socket.close();
				} catch (IOException e) {
				}
			}
		}
	}


	public static void main(String[] args) throws Exception {

		System.out.println("The server is running.");
		ServerSocket listener = new ServerSocket(PORT);
		try {
			while (true) {
				new Handler(listener.accept()).start();
			}
		} finally {
			listener.close();
		}
	}


}