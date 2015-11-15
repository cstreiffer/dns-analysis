package util.sqs;

public class SQSFactory {
	
	public static SimpleQueue getSimpleQueue() {
		return new SimpleQueue();
	}
}
