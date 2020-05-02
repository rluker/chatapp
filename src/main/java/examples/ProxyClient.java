/**
 * RACHEL LUKER - 09/17/2019
 * Client that sends an IP Name to a server, and echos the IP Name returned.
 */

import java.net.*;
import java.io.*;

public class ProxyClient {

    // the default port
    public static final int PORT = 8080;
    public static final int BUFFER_SIZE = 2048;

    
    // this could be replaced with an IP address or IP name
	public static final String host = "localhost";

    public static void main(String[] args) throws java.io.IOException {
        byte[] buffer = new byte[BUFFER_SIZE];

        BufferedInputStream fromServer = null;
        Socket server = null;
        PrintWriter toServer = null;	

		if (args.length != 1) {
			System.err.println("Usage: java ProxyClient <Server Request>");
			System.exit(0);
		}

        // String host = args[0];

		try {
            // create socket and connect to default port 
			server = new Socket(host, PORT);
			
            // fromServer = new BufferedReader(new InputStreamReader(server.getInputStream()));
            // toServer = new PrintWriter(server.getOutputStream(),true);
            fromServer = new BufferedInputStream(server.getInputStream());

            int read = 0;
            while ((read = fromServer.read(buffer)) != -1) {
				toClient.write(buffer, 0, read);
				toClient.flush();
            }
            server.close();
		}
		catch (IOException ioe) {
			System.err.println(ioe);
        }
        finally {
            // Close any streams
            if(server != null) {
                server.close();
            }
            if (fromServer!= null) {
                fromServer.close();
            }
            if (toServer!= null) {
                toServer.close();
            }
        }
	}

}