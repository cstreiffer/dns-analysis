package listener.server;

import java.io.IOException;
import java.net.ServerSocket;

import listener.server.exceptions.EC2ListenServerException;

public class EC2ListenServer {
	
	private Integer port;
	private ServerSocket serverSocket;
	
	//private Logger logger = Logger.getLogger(EC2ListenServer.class);
	
	public EC2ListenServer(Integer port) {
		this.port = port;
	}
	
	public void run() {		
		try {
			openSocketConnection();			
			waitAndListen();				
		} catch(EC2ListenServerException e) {
			e.printStackTrace();
			try {
				closeSocketConnection();
			} catch(EC2ListenServerException i) {
				//logger.error(i);
				i.printStackTrace();
			}
//			run();
		}
	}
	
	private void openSocketConnection() throws EC2ListenServerException {
		try {
			serverSocket = new ServerSocket(port);
		} catch(IOException e) {
			throw new EC2ListenServerException();
		}
	}
	
	private void closeSocketConnection() throws EC2ListenServerException {
		try {
			if(serverSocket != null) {
				serverSocket.close();
			}
		} catch(IOException e) {
			throw new EC2ListenServerException();
		}
	}
	
	private void waitAndListen() {
		while(true) {
			try {
				new EC2ListenServerThread(serverSocket.accept()).start();
			} catch (IOException e) {
				//logger.error(e);
				e.printStackTrace();
			}
		}
	}
}
