package listener.server;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;

import com.google.common.io.Closeables;

public class EC2ListenServerThread extends Thread {

	private Socket server;
	private BufferedWriter myWriter;
	
	private static final String MNT_FILE="/mnt/temp/dns_queries.txt";
	
	public EC2ListenServerThread(Socket server) throws IOException {
		this.server = server;
		this.myWriter = new BufferedWriter(new FileWriter(new File(MNT_FILE)));
	}
	
	public void run() {
		try {
			System.out.println("Just connected to " + server.getRemoteSocketAddress());
			DataInputStream reader = null;
			DataOutputStream writer = null;
			try {
				reader = new DataInputStream(server.getInputStream());
				writer = new DataOutputStream(server.getOutputStream());			
				writer.writeUTF("Connected to: " + server.getLocalSocketAddress());
				
				String input = "";
				while(server.isConnected() && (input = reader.readUTF()) != null) {
					System.out.println("Writing to the stream: " + input);
					myWriter.write(input);
					myWriter.flush();					
				}
		         
			} finally {
				Closeables.close(writer, true);
				Closeables.close(reader, true);
				Closeables.close(server, true);
			}
		} catch(ConnectException ce) {
		      System.err.println("Could not connect: " + ce);
	    } catch(Throwable t) {
		      System.err.println("Error receiving data: " + t);
	    }
	}
}
