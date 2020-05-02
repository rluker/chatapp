/**
 * Rachel Luker && Jake McCord
 * Client for the chatapp.
 */ 

import java.net.*;
import java.time.Instant;
import java.io.*;
import java.util.Date;

public class ChatClient
{
	public static final int DEFAULT_PORT = 7331;
	
	public static void main(String[] args) throws IOException {
		
		BufferedReader fromServer = null;		
		BufferedOutputStream toServer = null;	
		Socket socket = null;		
		String status = null;
		String date = null;
		String username = null;
		String newLine = "\r\n\r\n";
		String sStatus = null;
		String sDate = null;
		String sMessage = null;	
		
		try {
			if (args.length == 2) {
				socket = new Socket(args[0], DEFAULT_PORT);
				toServer = new BufferedOutputStream(socket.getOutputStream());
				fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				// header info
				status = "status: 200 \r\n";
				date = "date: " + Instant.now().toString() + "\r\n"; // need to format to UTC
				username = args[1] + "\r\n"; // get username request from args
				newLine = "\r\n\r\n";

				String header = status + date + username + newLine;

				// send request header to the server
				toServer.write(header.getBytes());
				toServer.flush();
			} else {
				System.err.println("Usage: java ChatClient <i.e. localhost> <i.e. name>");
				System.exit(0);
			}

			String line = fromServer.readLine();
			if (line.contains("201")) { // request was accepted, accepted join
				// If we are approved, we would enter and start the chat screen.
				sStatus = line;
				sDate = fromServer.readLine();
				fromServer.readLine();
				fromServer.readLine();

				System.out.println("_____REQUEST APPROVED!!!");

				ChatScreen win = new ChatScreen(socket, args[1]);

				Thread ReaderThread = new Thread(new ReaderThread(socket, win));
				ReaderThread.start();
			} else if (line.contains("301")) { // MOVE THIS server response/message
				sStatus = line;
				sDate = fromServer.readLine();
				sMessage = fromServer.readLine();
				fromServer.readLine();
				fromServer.readLine();
			} else if (line.contains("401")) { // unsuccessful join
				System.out.println("_____YOU SHALL NOT JOIN!!!");
				sStatus = line;
				sDate = fromServer.readLine();
				sMessage = fromServer.readLine();
				fromServer.readLine();
				fromServer.readLine();
			} else if (line.contains("404")) {  // MOVE THIS user does not exist
				sStatus = line;
				sDate = fromServer.readLine();
				sMessage = fromServer.readLine();
				fromServer.readLine();
				fromServer.readLine();
			}
		}
		catch (IOException ioe) {
			System.err.println(ioe);
		}
	}
}
