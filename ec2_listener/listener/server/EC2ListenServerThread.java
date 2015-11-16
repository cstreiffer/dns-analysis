package listener.server;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Logger;

import util.sqs.SQSFactory;
import util.sqs.SimpleQueue;
//import org.apache.log4j.Logger;

public class EC2ListenServerThread extends Thread {

	private Socket server;
	private SimpleQueue sqs;
	private DataInputStream inputStream;

	private boolean listening = false;


	private static Logger logger = Logger.getLogger(EC2ListenServerThread.class); 

	public EC2ListenServerThread(Socket server) {
		this.server = server;
		sqs = SQSFactory.getSimpleQueue();
		handShake();
	}
	
	public void run() {
		logger.trace("Just connected to " + server.getRemoteSocketAddress());
		//System.out.println("Just connected to " + server.getRemoteSocketAddress());
		while(listening) {
			String input;
			try {
				if((input = inputStream.readUTF()) != null) {
					System.out.println(input);
					sqs.getSqs().sendMessage(new SendMessageRequest(sqs.getSqsUrl(), input));
				}
			} catch (AmazonClientException e) {
				// ??
			} catch (EOFException e) {
				e.printStackTrace();
			} catch (IOException e) {
				// ??
			}
		}
	}
	
	private void handShake() {
		// Method to test whether connection is trustworthy
		try {
			inputStream = new DataInputStream(server.getInputStream());
			listening = true;
		} catch (IOException e) {
			// Do something here
		}
	}
}
