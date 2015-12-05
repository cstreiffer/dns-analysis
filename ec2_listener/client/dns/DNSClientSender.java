package client.dns;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;

import com.google.common.io.Closeables;

public class DNSClientSender {
	
	private static final String IP_ADDRESS = "52.3.122.56";
	//private static final String IP_ADDRESS = "localhost";

	private static final String DNS_FILE = "files/dns_queries.txt";
	private static final String DONE = "done";
	private static final Integer PORT = 5055;
	
	public static void main(String[] args) throws InterruptedException {
		try {
			BufferedReader reader = null;
			PrintWriter writer = null;
			Socket server = null;
			BufferedReader textReader = null;
			try {
				server = new Socket(IP_ADDRESS, PORT);				
				reader = new BufferedReader(new InputStreamReader(server.getInputStream()));			
			    writer = new PrintWriter(server.getOutputStream(), true);
				System.out.println("Starting connection from: " + server.getRemoteSocketAddress());
				
				FileReader fr = new FileReader(DNS_FILE);
				textReader = new BufferedReader(fr);
				
				String json_data = "";
				String thisLine = "";
				while ((thisLine = textReader.readLine()) != null) {
					System.out.println(thisLine);
					json_data = thisLine;
					writer.println(json_data);
				}
				writer.println(DONE);
				
				while((thisLine = reader.readLine()) != null){
					if(thisLine.equals(DONE)) {
						break;
					}
				}
				
			} finally {
				System.out.println("Ending connection from: " + server.getRemoteSocketAddress());
				Closeables.close(textReader, true);
				Closeables.close(writer, true);
				Closeables.close(reader, true);
				Closeables.close(server, true);
			}
		} catch (ConnectException ce) {
			System.err.println("Could not connect: " + ce);
		} catch (Throwable t) {
			System.err.println("Error receiving data: " + t);
		}
	}
}