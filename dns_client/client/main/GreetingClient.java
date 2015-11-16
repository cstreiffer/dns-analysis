package client.main;

import java.net.*;
import java.io.*;

public class GreetingClient
{
   public static void main(String [] args)
   {

      int port = 5055;
      try
      {
         Socket client = new Socket("ec2-52-5-126-181.compute-1.amazonaws.com", port);
         OutputStream outToServer = client.getOutputStream();
         DataOutputStream out = new DataOutputStream(outToServer);
         for(int i = 0; i < 10; i++) {
        	 out.writeUTF("Hello from " + client.getLocalSocketAddress() + ": " + i);
         }
         client.close();
      }catch(IOException e)
      {
         e.printStackTrace();
      }
   }
}