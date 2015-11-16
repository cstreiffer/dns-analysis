package listener.server;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Logger;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.sqs.model.SendMessageRequest;

import util.sqs.SQSFactory;
import util.sqs.SimpleQueue;

public class EC2ListenServerThread extends Thread {

	private Socket server;
	private SimpleQueue sqs;
	private DataInputStream inputStream;

	private boolean listening = false;



	public EC2ListenServerThread(Socket server) {
		this.server = server;
		sqs = SQSFactory.getSimpleQueue();
		handShake();
	}
	
	public void run() {
		//System.out.println("Just connected to " + server.getRemoteSocketAddress());
		while(listening) {
			String input;
			try {
				if((input = inputStream.readUTF()) != null) {
					System.out.println(input);
					listening = (input.equalsIgnoreCase("quit") == false);

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
		try {
			inputStream.close();
			server.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
