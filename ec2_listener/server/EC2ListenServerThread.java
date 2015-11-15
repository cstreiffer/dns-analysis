package server;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

//import org.apache.log4j.Logger;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSAsyncClient;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.twitter.chill.config.Config;

public class EC2ListenServerThread extends Thread {

	private Socket server;
	private boolean listening = false;
	private DataInputStream inputStream;

	private static AmazonSQS sqs;
	private static String sqsUrl;
	//private static Logger logger = Logger.getLogger(EC2ListenServerThread.class); 
	
	static {
		initialize();
	}

	public EC2ListenServerThread(Socket server) {
		this.server = server;
		handShake();
	}
	
	public void run() {
		//logger.trace("Just connected to " + server.getRemoteSocketAddress());
		System.out.println("Just connected to " + server.getRemoteSocketAddress());
		while(listening) {
			String input;
			try {
				if((input = inputStream.readUTF()) != null) {
					System.out.println(input);
					sqs.sendMessage(new SendMessageRequest(sqsUrl, input));
				}
				inputStream.
			} catch (AmazonClientException | IOException e) {
				//logger.error(e);	
				e.printStackTrace();
			}
		}
	}
	
	private void handShake() {
		// Method to test whether connection is trustworthy
		try {
			inputStream = new DataInputStream(server.getInputStream());
			listening = true;
		} catch (IOException e) {
			// Do something here
		}
	}
	
	private static void initialize() {
        AWSCredentials credentials = null;
        try {
            credentials = new ProfileCredentialsProvider().getCredentials();
        } catch (Exception e) {
            throw new AmazonClientException(
                    "Cannot load the credentials from the credential profiles file. " +
                    "Please make sure that your credentials file is at the correct " +
                    "location (~/.aws/credentials), and is in valid format.",
                    e);
        }
        
        sqs = new AmazonSQSAsyncClient(credentials);
        Region usEast1 = Region.getRegion(Regions.US_EAST_1);
        sqs.setRegion(usEast1);
        
        try {
            CreateQueueRequest createQueueRequest = new CreateQueueRequest("MyQueue");
            sqsUrl = sqs.createQueue(createQueueRequest).getQueueUrl();
            
        } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which means your request made it " +
                    "to Amazon SQS, but was rejected with an error response for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which means the client encountered " +
                    "a serious internal problem while trying to communicate with SQS, such as not " +
                    "being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }
	}
}
