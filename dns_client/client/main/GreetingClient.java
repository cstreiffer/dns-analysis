package client.main;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class GreetingClient
{
   public static void main(String [] args) throws InterruptedException
   {

      int port = 5055;
      try
      {
         Socket client = new Socket("ec2-52-5-126-181.compute-1.amazonaws.com", port);
         OutputStream outToServer = client.getOutputStream();
         DataOutputStream out = new DataOutputStream(outToServer);
         DataInputStream in =
					new DataInputStream(client.getInputStream());
        out.writeUTF("Hello from " + client.getLocalSocketAddress());

         BufferedReader in_text = new BufferedReader(new InputStreamReader(System.in));
         String line = "";

         while (line.equalsIgnoreCase("quit") == false) {
        	 System.out.print("> ");
        	 line = in_text.readLine();
        	 out.writeUTF(line);
         }
         in_text.close();
         in.close();
         out.close();
         outToServer.close();
         client.close();
      }catch(IOException e)
      {
         e.printStackTrace();
      }
   }
}