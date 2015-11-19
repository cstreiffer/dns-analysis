package listener.server;

import java.io.IOException;
import java.net.ConnectException;
import java.net.ServerSocket;

import com.google.common.io.Closeables;

public class EC2ListenServer {
	
	private Integer port;
	private ServerSocket serverSocket;
		
	public EC2ListenServer(Integer port) {
		this.port = port;
	}
	
	public void run() {	
		try {
			serverSocket = null;
			try {
				serverSocket = new ServerSocket(port);
				waitAndListen();
			} finally {
				Closeables.close(serverSocket, true);
			}
		} catch(ConnectException ce) {
		      System.err.println("Could not connect: " + ce);
	    } catch(Throwable t) {
		      System.err.println("Error receiving data: " + t);
	    }
		run();
	}
		
	private void waitAndListen() {
		while(true) {
			try {
				new EC2ListenServerThread(serverSocket.accept()).start();
			} catch (IOException e) {
				System.out.println("And here we are.");
				if(serverSocket.isClosed()) {
					break;
				}
			}
		}
	}
}
