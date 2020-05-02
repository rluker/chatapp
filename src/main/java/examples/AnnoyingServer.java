/**
 * An annoying server listening on port 6007. 
 *
 * @author - Greg Gagne.
 */

import java.net.*;
import java.io.*;
import java.util.concurrent.*;

public class  AnnoyingServer
{
	public static final int DEFAULT_PORT = 7331;

	// construct a thread pool for concurrency	
	private static final Executor exec = Executors.newCachedThreadPool();

	public static void main(String[] args) throws IOException {
		ServerSocket sock = null;

		try {
			// establish the socket
			sock = new ServerSocket(DEFAULT_PORT);

			while (true) {
				/**
				 * now listen for connections
				 * and service the connection in a separate thread.
				 */
				Runnable task = new Connection(sock.accept());
				exec.execute(task);
			}
		}
		catch (IOException ioe) { System.err.println(ioe); }
		finally {
			if (sock != null)
				sock.close();
		}
	}
}

class Handler 
{

	/**
	 * this method is invoked by a separate thread
	 */
	public void process(Socket client) throws java.io.IOException {
		DataOutputStream toClient = null;
		int count = 0;

		try {
			toClient = new DataOutputStream(client.getOutputStream());

			while (true) {
				String message = "[" + count + "]\n";	
				toClient.writeBytes(message);
				
				try {
					Thread.sleep(5000);
				}
				catch (InterruptedException ie) { }

				count++;
			}
		}
		catch (IOException ioe) {
			System.err.println(ioe);
		}
		finally {
			// close streams and socket
			if (toClient != null)
				toClient.close();
		}
	}
}

class Connection implements Runnable
{
	private Socket	client;
	private static Handler handler = new Handler();

	public Connection(Socket client) {
		this.client = client;
	}

	/**
	 * This method runs in a separate thread.
	 */	
	public void run() { 
		try {
			handler.process(client);
		}
		catch (java.io.IOException ioe) {
			System.err.println(ioe);
		}
	}
}
