package listener.manager;

import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MntFileWriter extends Thread {
	
	private BlockingQueue<String> myQueue;
	private FileWriter fw;
	
	public MntFileWriter(String fileName) throws IOException {
		myQueue = new LinkedBlockingQueue<String>();
		fw = new FileWriter(fileName, true);
	}
	
	public BlockingQueue<String> getBlockingQueue() {
		return myQueue;
	}
	
	public void run() {
		while (true) {
			try {
				String message = myQueue.take();
				System.out.println("MNT: " + message);
				fw.write(message + "\n");
				fw.flush();
			} catch (InterruptedException | IOException e) {}
		}
	}
}
