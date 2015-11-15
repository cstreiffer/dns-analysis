package util.sqs;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSAsyncClient;
import com.amazonaws.services.sqs.model.CreateQueueRequest;

import util.credentials.CredentialManager;

public class SimpleQueue {
	
	private AmazonSQS sqs;
	private String sqsUrl;
	private String sqsName = "myQueue";

	SimpleQueue() {
		sqs = new AmazonSQSAsyncClient(CredentialManager.getCredentials());
        Region usEast1 = Region.getRegion(Regions.US_EAST_1);
        sqs.setRegion(usEast1);
        
        CreateQueueRequest createQueueRequest = new CreateQueueRequest(sqsName);
        sqsUrl = sqs.createQueue(createQueueRequest).getQueueUrl();
	}
	
	public AmazonSQS getSqs() {
		return sqs;
	}
	
	public String getSqsUrl() {
		return sqsUrl;
	}
}
