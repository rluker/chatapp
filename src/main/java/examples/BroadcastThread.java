
/**
 * 
 * THE SERVER WILL HAVE A BROADCAST THREAD THAT IS READING FROM THE VECTOR or BlockingQueue(QUEUE)
 * TO GRAB ANY NEW MESSAGES.
 */

import java.io.*;
import java.net.*;
import javax.swing.*;
import java.lang.*;
import java.util.*;

public class BroadcastThread implements Runnable
{
    // Vector that contains messages coming to the broadcast thread
    // I think this goes in here....?
    public Vector<String> vector;
    public Map<String, OutputStream> sockets;

    public BroadcastThread(Map<String, OutputStream> sockets, Vector<String> vector) {
        this.vector = vector;
        this.sockets = sockets;
    }

    public void run(){
        while (true) {
            // sleep for 1/10th of a second
            try { Thread.sleep(100); } catch (InterruptedException ignore) { }

            /**
             * check if there are any messages in the Vector. If so, remove them
             * and broadcast the messages to the chatroom
             */

             while (!vector.isEmpty()) {
                String message = vector.remove(0);

                if (message.startsWith(">>pm:")) { // end with \r\n
                    //grab the username, send to that outputStream
                    try { 
                        String username = null;;
                        sockets.get(username).write(message.getBytes()); 
                    } catch (IOException e) { }
                } else { // broadcast to everyone
                    for (OutputStream outputStream : sockets.values()) {
                        try {
                            outputStream.write(message.getBytes()); 
                            outputStream.flush();
                        } catch (IOException e) { }
                    }
                }

             }

        }
    }
} 