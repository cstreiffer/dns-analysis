// File Name GreetingClient.java
package main;

import java.net.*;
import java.io.*;

public class GreetingClient extends Thread
{
	private String identifier;
	private int port;
	
	public GreetingClient(String identifier, int port) {
		this.identifier = identifier;
		this.port = port;
	}
	
	public void run() {
      String serverName = "";
      try
      {
         System.out.println("Connecting to " + serverName +
		 " on port " + port);
         Socket client = new Socket(InetAddress.getByName("52.5.126.181"), port);
         System.out.println("Just connected to " 
		 + client.getRemoteSocketAddress() + identifier);
         OutputStream outToServer = client.getOutputStream();
         DataOutputStream out = new DataOutputStream(outToServer);
         out.writeUTF("Hello from "
                      + client.getLocalSocketAddress());
         InputStream inFromServer = client.getInputStream();
         DataInputStream in =
                 new DataInputStream(inFromServer);
         while(true){
        	 System.out.println(String.format("Server says:%s on %s ", in.readUTF(), identifier));
         try {
			sleep(1000l);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
         }

      }catch(IOException e)
      {
         e.printStackTrace();
      }
	}
	
   public static void main(String [] args)
   {
	   Thread t1 = new GreetingClient("Server 1", 5055);
//	   Thread t2 = new GreetingClient("Server 2", 5055);
	   
	   t1.start();
	//   t2.start();
	   
   }
}