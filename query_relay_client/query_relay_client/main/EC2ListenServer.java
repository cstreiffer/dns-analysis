package query_relay_client.main;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class EC2ListenServer extends Thread {

	private ServerSocket serverSocket;

	public EC2ListenServer(int port) throws IOException {
		serverSocket = new ServerSocket(port);
	}

	public void run() {
		while(true) {
			try {
				System.out.println("Waiting for client on port " +
						serverSocket.getLocalPort() + "...");
				Socket server = serverSocket.accept();
				System.out.println("Just connected to "
						+ server.getRemoteSocketAddress());
				DataInputStream in =
						new DataInputStream(server.getInputStream());
				System.out.println(in.readUTF());
				DataOutputStream out = new DataOutputStream(server.getOutputStream());
				int val = 0;
				while(true) {
					out.writeUTF(String.format("Fuccckkkaaa You: %d", val++));
				}
			} catch(SocketTimeoutException s) {
				System.out.println("Socket timed out!");
				break;
			} catch(IOException e) {
				e.printStackTrace();
				break;
			}
		}
	}
	
	public static void main(String [] args) {
		int port = 5055;
		try {
			Thread t = new EC2ListenServer(port);
			t.start();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}
