package listener.main;

import listener.server.EC2ListenServer;

public class Main {
	
	  public static void main(String[] args) {
		  
		  // Create instance of EC2ListenServer
		  EC2ListenServer myServer = new EC2ListenServer();
		  
		  // Run that instance
		  myServer.run();
		  
	  }
}