/**
 * A chatroom server listening on port 7331. 
 *
 * @author - Rachel Luker && Jake McCord
 */

import java.net.*;
import java.time.Instant;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.Vector;

public class  ChatServer
{
	public static final int DEFAULT_PORT = 7331;

    // Vector that contains messages coming to the broadcast thread
    // This doesn't go in this class....

	// construct a thread pool for concurrency	
	private static final Executor exec = Executors.newCachedThreadPool();

	public static void main(final String[] args) throws IOException {
		ServerSocket socket = null;
		Vector<String> vector = new Vector<>();
		Map<String, OutputStream> sockets = new HashMap<>();
		Runnable broadcast = new BroadcastThread(sockets, vector);
		exec.execute(broadcast);

		try {
			// establish the socket
			if (args.length != 0) {
				System.err.println("Usage: java ChatServer");
				System.exit(0);
			} else {
				socket = new ServerSocket(DEFAULT_PORT);
				System.out.println("Server bound at port " + socket.getLocalPort() + "\n");
			}

			while (true) {
				/**
				 * now listen for connections and service the connection in a separate thread.
				 */
				Socket client = socket.accept();
				Runnable task = new Connection(client, sockets, vector);

				// Runnable task = new Connection(socket.accept());
				exec.execute(task);
			}
		} catch (IOException ioe) {
			System.err.println(ioe);
		} finally {
			if (socket != null)
				socket.close();
		}
	}
}

/**
 * This class handles the meat of the server.
 */
class Handler {
	/**
	 * this method is invoked by a separate thread
	 */
	public void process(Socket client,Map<String, OutputStream> sockets, Vector<String> vector) throws java.io.IOException {
		BufferedReader fromClient = null;
		PrintWriter toClient = null;
		String status = null;
		String date = null;
		String user = null;
		String toUser = null;
		String content = null;


		try {
			fromClient = new BufferedReader(new InputStreamReader(client.getInputStream()));
			toClient = new PrintWriter(client.getOutputStream(), true);
			StringBuilder str = new StringBuilder();
			StringBuilder response = new StringBuilder();

			String line;
			String newLine = "\r\n\r\n";
			while ((line = fromClient.readLine()) != null) {
				if (line.contains("200")) {
					response = new StringBuilder();
					System.out.println("_____Received 200 status.");
					status = line;
					date = fromClient.readLine();
					user = fromClient.readLine();
					fromClient.readLine();
					fromClient.readLine();

					if (!sockets.keySet().contains(user)) { // ACCEPTED
						sockets.put(user, client.getOutputStream());
						response.append("status: 201 \r\n");
						response.append("date: " + Instant.now().toString() + "\r\n");
						response.append(newLine);

						vector.add("["+user.substring(user.indexOf(" ") + 1) + " has joined the chat.]" + "\r\n");
					} else { // YOU SHALL NOT JOIN
						response.append("status: 401 \r\n");
						response.append("date: " + Instant.now().toString() + "\r\n");
						response.append(newLine);
					}

					toClient.println(response.toString());

				} else if (line.contains("202")) { // general message
					response = new StringBuilder();
					System.out.println("_____Received 202 status.");
					status = line;
					date = fromClient.readLine();
					user = fromClient.readLine();
					content = fromClient.readLine();
					fromClient.readLine();
					fromClient.readLine();

					// grab the content line, this is the message, add to vector
					vector.add(user.substring(user.indexOf(" ") + 1) + ": " + content + "\r\n");

				} else if (line.contains("203")) { // private message
					System.out.println("_____Received 203 status.");
					status = line;
					date = fromClient.readLine();
					user = fromClient.readLine();
					user = user.substring(user.indexOf(" ") + 1);
					toUser = fromClient.readLine();
					toUser = toUser.substring(toUser.indexOf(" ") + 1);
					content = fromClient.readLine();
					fromClient.readLine();
					fromClient.readLine();

					if( sockets.containsKey(toUser)) { // does the toUser exist?
						// can send directly to the outputStream
						System.out.println("_____User exists: " + toUser);
						if(content.indexOf(" ") > -1) {
							content = content.substring(content.indexOf(" ")+1);
						}
						toClient.println(user + " [PM@" +toUser + "]: " + content);
						toClient.flush();

						PrintWriter pm = new PrintWriter(sockets.get(toUser), true);
						pm.println(user + " [PM]: " + content);
						pm.flush();
					} else {
						// send that back to the user
						System.out.println("_____User does not exist: " + toUser);
						toClient.println(">> Whoops! User [" + toUser + "] is not in this chat!");
						toClient.flush();
					}


				} else if (line.contains("300")) { // leaving/exiting chat
					// remove the user from the socket map
					System.out.println("_____Received 300 status.");
					sockets.remove(user.substring(user.indexOf(" ") + 1));
					vector.add("["+user.substring(user.indexOf(" ") + 1)+" has left the chat.]" + "\r\n");
				}

				if(sockets.keySet().size() <= 0) {
					System.exit(0);
				}
			}

		} catch (final IOException ioe) {
			System.err.println(ioe);
		}
	}

}

class Connection implements Runnable {
	private final Socket client;
	private Map<String, OutputStream> sockets;
	private static Handler handler = new Handler();
	Vector<String> vector;

	public Connection(Socket client, Map<String, OutputStream> sockets, Vector<String> vector) {
		this.client = client;
		this.sockets = sockets;
		this.vector = vector;
	}

	/**
	 * This method runs in a separate thread.
	 */
	public void run() {
		try {
			// System.out.println("Check 2 - Connection");
			handler.process(client, sockets, vector);
		} catch (final java.io.IOException ioe) {
			System.err.println(ioe);
		}
	}
}