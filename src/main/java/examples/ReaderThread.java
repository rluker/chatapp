/**
 * This thread is passed a socket that it reads from. Whenever it gets input
 * it writes it to the ChatScreen text area using the displayMessage() method.
 * 
 * EVERY CLIENT WILL HAVE ITS OWN READER THREAD.
 * 
 * 
 * Notes from assignment:
 * The client application must be concerned with both reading data from the user and 
 * reading data from the socket. Reading data from the user is handled by the usual event 
 * mechanisms of implementing the actionPerformed() method from the ActionListener interface. 
 * Reading data from the socket is not as straightforward. There are numerous ways of handling it, 
 * but I suggest the simplest approach is to create a separate thread for each client 
 * which continually reads from the socket, displaying data in the display area whenever a 
 * message from the server arrives.
 */

import java.io.*;
import java.net.*;
import javax.swing.*;

public class ReaderThread implements Runnable
{
	Socket server;
	BufferedReader fromServer;
	PrintWriter toServer = null;
	ChatScreen screen;

	public ReaderThread(Socket server, ChatScreen screen) {
		this.server = server;
		this.screen = screen;
	}

	public void run() {
		try {
			fromServer = new BufferedReader(new InputStreamReader(server.getInputStream()));
			toServer = new PrintWriter(server.getOutputStream(), true);

			String line;
			while ((line = fromServer.readLine()) != null) {
				screen.displayMessage(line);
			}

			screen.displayExit();
			System.out.println("Reader thread exited.");
			screen.exit();
			toServer.println("status: 300\r\n".getBytes());
		}
		catch (IOException ioe) { System.out.println(ioe); }

	}
}
