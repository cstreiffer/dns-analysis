package listener.manager;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.BlockingQueue;

public class FileManager {

	private static final String DIR = "/mnt/temp/%s.txt";
	private static final String PROPERTIES = "ec2_listener/configuration/configuration.properties";
	private static final String CONFIGURATION = "configuration/configuration";

	private static FileWriter fw;
	private static Map<String, MntFileWriter> myMap;
	private static final FileManager myManager = new FileManager();

	private FileManager() {
		try {
			fw = new FileWriter(PROPERTIES, true);
			myMap = new HashMap<String, MntFileWriter>();
			load();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static FileManager getInstance() {
		return myManager;
	}

	public BlockingQueue<String> getBlockingQueue(String identifier) throws IOException {
		if(myMap.containsKey(identifier)) {
			return myMap.get(identifier).getBlockingQueue();
		} else {
			String file = createFile();
			MntFileWriter toAdd = new MntFileWriter(file);
			toAdd.start();
			myMap.put(identifier, toAdd);
			fw.write("\n" + identifier + "=" + file + "\n");
			fw.flush();
			return toAdd.getBlockingQueue();
		}
	}
	
	public void close() throws IOException {
		fw.close();
	}

	private String createFile() {
		Date date = new Date() ;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		return String.format(DIR, dateFormat.format(date));
	}
	
	private void load() throws IOException {
		ResourceBundle props = ResourceBundle.getBundle(CONFIGURATION);
		for(String s : props.keySet()) {
			MntFileWriter toAdd = new MntFileWriter(props.getString(s));
			toAdd.start();
			myMap.put(s, toAdd);
		}
	}
}
