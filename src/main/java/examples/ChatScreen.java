/**
 * This program is a rudimentary demonstration of Swing GUI programming.
 * Note, the default layout manager for JFrames is the border layout. This
 * enables us to position containers using the coordinates South and Center.
 *
 * Usage:
 *	java ChatScreen
 *
 * When the user enters text in the textfield, it is displayed backwards 
 * in the display area.
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.io.*;
import java.net.*;
import java.time.Instant;


public class ChatScreen extends JFrame implements ActionListener, KeyListener
{
	private JButton sendButton;
	private JButton exitButton;
	private JTextField sendText;
	private JTextArea displayArea;
	private Socket socket;
	private OutputStream outputStream;
	private String currentUser;

	public ChatScreen( Socket socket, String currentUser ) {
		this.socket = socket;
		this.currentUser = currentUser;
		try {this.outputStream = socket.getOutputStream(); } catch (IOException e ) { }

		/**
		 * a panel used for placing components
		 */
		JPanel p = new JPanel();

		Border etched = BorderFactory.createEtchedBorder();
		Border titled = BorderFactory.createTitledBorder(etched, "Enter Message Here ...");
		p.setBorder(titled);

		/**
		 * set up all the components
		 */
		sendText = new JTextField(30);
		sendButton = new JButton("Send");
		exitButton = new JButton("Exit");

		/**
		 * register the listeners for the different button clicks
		 */
		sendText.addKeyListener(this);
		sendButton.addActionListener(this);
		exitButton.addActionListener(this);

		/**
		 * add the components to the panel
		 */
		p.add(sendText);
		p.add(sendButton);
		p.add(exitButton);

		/**
		 * add the panel to the "south" end of the container
		 */
		getContentPane().add(p,"South");

		/**
		 * add the text area for displaying output. Associate
		 * a scrollbar with this text area. Note we add the scrollpane
		 * to the container, not the text area
		 */
		displayArea = new JTextArea(15,40);
		displayArea.setEditable(false);
		displayArea.setFont(new Font("SansSerif", Font.PLAIN, 14));

		JScrollPane scrollPane = new JScrollPane(displayArea);
		getContentPane().add(scrollPane,"Center");

		/**
		 * set the title and size of the frame
		 */
		setTitle("GUI Demo");
		pack();

		setVisible(true);
		sendText.requestFocus();

		/** anonymous inner class to handle window closing events */
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				String newline = "\r\n\r\n";
				String status = "status: 300"  + "\r\n";
				String date = "date: " + Instant.now().toString() + "\r\n"; // need to format to UTC
				String from = "from: " + currentUser  + "\r\n";
				String header = status + date + from + newline;

				try {
					outputStream.write(header.getBytes());
					outputStream.flush();
				} catch (IOException e) {}

				System.exit(0);
			}
		} );

	}

	/**
	 * Displays a message
	 */
	public void displayMessage(String message) {
		displayArea.append(message + "\n");
	}

	/**
	 * Displays a join message
	 */
	public void displayJoin() {
		displayArea.append("[" + currentUser + " has joined the chat.]"+ "\n");
	}

	/**
	 * Displays an exit message
	 */
	public void displayExit() {
		displayArea.append("[" + currentUser + " has left the chat.]"+ "\n");
	}

	public void exit() {
		System.exit(0);
	}

	/**
	 * This gets the text the user entered and outputs it
	 * in the display area.
	 */
	public void packageGmMessageText() {
		String message = sendText.getText().trim();

		String header = null;;
		String newline = "\r\n\r\n";
		String status = "status: 202"  + "\r\n";
		String date = "date: " + Instant.now().toString() + "\r\n"; // need to format to UTC
		String from = "from: " + currentUser  + "\r\n";
		String toUser = "";
		String content = "";

		for (int i = 0; i < message.length(); i++) {
			content += message.charAt(i);
		}
		content += "\r\n";

		if(content.contains("@")) {
			status = "status: 203"  + "\r\n";
			if (content.indexOf(" ") > -1) {
				toUser = "to: " + content.substring(content.indexOf("@")+1, content.indexOf(" ")) + "\r\n";
			}
		}

		header = status + date + from + toUser + content + newline;

		try {
			outputStream.write(header.getBytes());
			outputStream.flush();
		} catch (IOException e) {}

		sendText.setText("");
		sendText.requestFocus();
	}


	/**
	 * This method responds to action events .... i.e. button clicks
	 * and fulfills the contract of the ActionListener interface.
	 */
	public void actionPerformed(ActionEvent evt) {
		Object source = evt.getSource();

		if (source == sendButton) 
			packageGmMessageText();
		else if (source == exitButton) {
			String newline = "\r\n\r\n";
			String status = "status: 300"  + "\r\n";
			String date = "date: " + Instant.now().toString() + "\r\n"; // need to format to UTC
			String from = "from: " + currentUser  + "\r\n";
			String header = status + date + from + newline;

			try {  
				outputStream.write(header.getBytes());
				outputStream.flush();
			} catch (IOException e) {}

			System.exit(0);
		}
	}

	/**
	 * These methods responds to keystroke events and fulfills
	 * the contract of the KeyListener interface.
	 */

	/**
	 * This is invoked when the user presses
	 * the ENTER key.
	 */
	public void keyPressed(KeyEvent e) { 
		if (e.getKeyCode() == KeyEvent.VK_ENTER)
		packageGmMessageText();
	}

	/** Not implemented */
	public void keyReleased(KeyEvent e) { }

	/** Not implemented */
	public void keyTyped(KeyEvent e) {  }


	// public static void main(String[] args) {
	// 	try {
	// 		System.out.println("Check - The Chat Screen Class - Part 1");
	// 		Socket annoying = new Socket(args[0], 7331);
	// 		ChatScreen win = new ChatScreen();
	// 		win.displayMessage("My name is " + args[1]);

	// 		System.out.println("Check - The Chat Screen Class");
	// 		Thread ReaderThread = new Thread(new ReaderThread(annoying, win));

	// 		ReaderThread.start();
	// 	}
	// 	catch (UnknownHostException uhe) { System.out.println(uhe); }
	// 	catch (IOException ioe) { System.out.println(ioe); }


	// }
}
