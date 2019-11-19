/**
 * RACHEL LUKER 10/20/2019 MIDTERM 
 * Client sends a phrase to a server and is returned the phrase in reverse.
 */

import java.io.*;
import java.net.*;
import java.util.*;

public class Handler 
{
	public static final int BUFFER_SIZE = 256;
	
	public void process(Socket client) throws java.io.IOException {
		BufferedReader fromClient = null;
		PrintWriter toClient = null;
		
		try {

			fromClient = new BufferedReader(new InputStreamReader(client.getInputStream()));
			toClient = new PrintWriter(client.getOutputStream(), true);

			String reverso = fromClient.readLine();
			String[] delims = reverso.split("\\s+");
			List<String> words = new ArrayList<String>();
			for(String s : delims) {
				words.add(s);
			}
			Collections.reverse(words);
			String reversed = String.join(" ", words);
			toClient.println(reversed + "\r\n");
   		}
		catch (IOException ioe) {
			System.err.println(ioe);
		}
		finally {
			if (client != null)
				client.close();
			if (fromClient != null)
				fromClient.close();
			if (toClient != null)
				toClient.close();
		}
	}
}
