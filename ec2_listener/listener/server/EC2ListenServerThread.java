package listener.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

import listener.manager.FileManager;


public class EC2ListenServerThread extends Thread {
	
	private Socket connection;
	private BlockingQueue<String> receivedMessageQueue;
	private PrintWriter outputStream;
	private BufferedReader inputStream;
	private Boolean running ;

	private static final String CLOSE = "closingConnection";
	private static final String HELLO_HELLO = "oooBabyBaby";

	public EC2ListenServerThread(Socket connection) throws IOException {
		this.connection = connection;
		outputStream = new PrintWriter(new OutputStreamWriter(connection.getOutputStream()));
		inputStream = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		handshake();
		running = true;
		System.out.println("Starting connection from: " + connection.getRemoteSocketAddress());
	}
	
	public void run() {
		
		try {
			while (running) {
				String message = inputStream.readLine();
				if(message.equals(CLOSE)) {
					break;
				} else {
					receivedMessageQueue.put(message);
				}
			} 
		} catch (InterruptedException | IOException e) {
			//Eats it
		}
	}
		
	private void handshake() throws IOException {
		String identifier = inputStream.readLine();
		receivedMessageQueue = FileManager.getInstance().getBlockingQueue(identifier);
		outputStream.println(HELLO_HELLO);
		outputStream.flush();
	}
	
	public void close() {
		try {
			running = false;
			outputStream.println(CLOSE);	
			outputStream.close();
			inputStream.close();
			connection.close();			
		} catch (IOException e) {}
	}
}