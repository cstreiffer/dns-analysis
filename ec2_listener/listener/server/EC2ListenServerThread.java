package listener.server;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;

import com.google.common.io.Closeables;

public class EC2ListenServerThread extends Thread {

	private Socket server;
	private FileWriter myWriter;
	
	private static final String MNT_FILE = "/mnt/temp/dns_queries.txt";
	//private static final String MNT_FILE = "output.txt";
	private static final String DONE = "done";
	
	public EC2ListenServerThread(Socket server) throws IOException {
		this.server = server;
		this.myWriter = new FileWriter(MNT_FILE, true);
	}
	
	public void run() {
		try {
			System.out.println("Just connected to " + server.getRemoteSocketAddress());
			BufferedReader reader = null;
			PrintWriter writer = null;

			try {
				reader = new BufferedReader(new InputStreamReader(server.getInputStream()));			
			    writer = new PrintWriter(server.getOutputStream(), true);

				String input = "";
				while((input = reader.readLine()) != null) {
					if(input.equals(DONE)) {
						break;
					} else {
						myWriter.write(String.format("%s\n", input));
						myWriter.flush();
					}
				}		        
				writer.println(DONE);

			} finally {
				System.out.println("Exiting connection: " + server.getRemoteSocketAddress());
				Closeables.close(writer, true);
				Closeables.close(reader, true);
				Closeables.close(server, true);
				Closeables.close(myWriter, true);
			}
		} catch(ConnectException ce) {
		      System.err.println("Could not connect: " + ce);
	    } catch(Throwable t) {
		      System.err.println("Error receiving data: " + t);
	    }
	}
}
