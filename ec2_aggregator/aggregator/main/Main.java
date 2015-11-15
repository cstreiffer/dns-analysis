package aggregator.main;

import java.util.List;
import java.util.Map.Entry;

import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;

import util.sqs.SQSFactory;
import util.sqs.SimpleQueue;

public class Main {
	
	public static void main(String...args) {
		SimpleQueue myQueue = SQSFactory.getSimpleQueue();
		
		ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(myQueue.getSqsUrl());
		
		List<Message> messages = myQueue.getSqs().receiveMessage(receiveMessageRequest).getMessages();
		
        for (Message message : messages) {
            System.out.println("  Message");
            System.out.println("    MessageId:     " + message.getMessageId());
            System.out.println("    ReceiptHandle: " + message.getReceiptHandle());
            System.out.println("    MD5OfBody:     " + message.getMD5OfBody());
            System.out.println("    Body:          " + message.getBody());
            for (Entry<String, String> entry : message.getAttributes().entrySet()) {
                System.out.println("  Attribute");
                System.out.println("    Name:  " + entry.getKey());
                System.out.println("    Value: " + entry.getValue());
            }
            myQueue.getSqs().deleteMessage(myQueue.getSqsUrl(), message.getReceiptHandle());
        }   
	}
}
