package client.dns;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.net.ConnectException;
import java.net.Socket;

import com.google.common.io.Closeables;

public class DNSClientSender {
	
	private static final String IP_ADDRESS = "localhost";
	private static final String DNS_FILE = "files/dns_queries.json";
	
	public static void main(String[] args) throws InterruptedException {
		try {
			int port = 5055;
			DataOutputStream writer = null;
			DataInputStream reader = null;
			Socket client = null;
			BufferedReader textReader = null;
			try {
				client = new Socket(IP_ADDRESS, port);				
				writer = new DataOutputStream(client.getOutputStream());
				reader = new DataInputStream(client.getInputStream());

				FileReader fr = new FileReader(DNS_FILE);
				textReader = new BufferedReader(fr);
				
				String json_data = "";
				String thisLine = "";
				while ((thisLine = textReader.readLine()) != null) {
					json_data = thisLine;
					writer.writeUTF(json_data+"\n");
				}
				
			} finally {
				Closeables.close(textReader, true);
				Closeables.close(writer, true);
				Closeables.close(reader, true);
				Closeables.close(client, true);
			}
		} catch (ConnectException ce) {
			System.err.println("Could not connect: " + ce);
		} catch (Throwable t) {
			System.err.println("Error receiving data: " + t);
		}
	}
}