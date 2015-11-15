package util.sqs;

public class SQSFactory {
	
	public static SimpleQueue getSimpleQueue(String name) {
		return new SimpleQueue();
	}
}
