package listener.server;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;

import org.apache.hadoop.conf.Configuration;

import com.google.common.io.Closeables;

public class EC2ListenServerThread extends Thread {

	private Socket server;
	private Configuration config;
	private BufferedWriter fileOutput;
	private static final String FILE = "files/output.txt";
	
	
	public EC2ListenServerThread(Socket server) {
		this.server = server;
		this.config = new Configuration();
		try {
			File file = new File(FILE);
			FileWriter fw = new FileWriter(file);
			fileOutput = new BufferedWriter(fw);
		} catch (IOException e) {
			System.out.println("Didn't work");
		}
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
					// Store this data in a file
					System.out.println("Writing to textfile: " + input);
					fileOutput.write(input);
					fileOutput.flush();
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
