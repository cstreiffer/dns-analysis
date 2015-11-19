package util.hdfs;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class HDFSManager {
	
	private BufferedWriter fileOutput;
	
	private static final String HDFS = "hdfs://ip-172-31-54-40.ec2.internal:9000";
	private static final String FILE = "hdfs://ip-172-31-54-40.ec2.internal:9000/user/christopherstreiffer/big-supah-output.txt";
	
	private static final HDFSManager myManager = new HDFSManager();
	
	private HDFSManager() {
		fileOutput = null;
        try{
            Configuration config = new Configuration();
            URI uri = new URI(HDFS);
            FileSystem hdfs = FileSystem.get(uri, config);
            Path path = new Path(FILE);
            FSDataOutputStream myStream = hdfs.append(path);
            fileOutput = new BufferedWriter(new OutputStreamWriter(myStream));
            System.out.println(fileOutput);
        } catch(Exception e){
            System.out.println("File not found");
            e.printStackTrace();
        }
	}
	
	public static BufferedWriter getFileWriter() {
		return myManager.fileOutput;
	}

}
