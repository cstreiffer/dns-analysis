package aggregator.main;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;

import scala.Tuple2;

public class JavaCustomReceiver {
	private static final Pattern SPACE = Pattern.compile(" ");
	private static final String ACCESS_KEY = "AKIAJGCZSSNNFIQUDNMQ";
	private static final String SECRET_KEY = "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";

	static {
		Logger.getLogger("org").setLevel(Level.WARN);
		Logger.getLogger("akka").setLevel(Level.WARN);
	}

	public static void main(String[] args) {

		SparkConf conf = new SparkConf().setAppName("APPNAME").setMaster("local[3]");
		JavaSparkContext javaSparkContext = new JavaSparkContext(conf);

		javaSparkContext.hadoopConfiguration().set("fs.s3.awsAccessKeyId", ACCESS_KEY);
		javaSparkContext.hadoopConfiguration().set("fs.s3.awsSecretAccessKey", SECRET_KEY);
		//javaSparkContext.hadoopConfiguration().set("fs.s3.impl", "org.apache.hadoop.fs.s3native.NativeS3FileSystem");

		JavaRDD<String> lines = javaSparkContext.textFile("s3a://cs-514/temp/sample.txt");

		JavaRDD<String> words = lines.flatMap(new FlatMapFunction<String, String>() {
			@Override
			public Iterable<String> call(String s) {
				return Arrays.asList(SPACE.split(s));
			}
		});

		JavaPairRDD<String, Integer> ones = words.mapToPair(new PairFunction<String, String, Integer>() {
			@Override
			public Tuple2<String, Integer> call(String s) {
				return new Tuple2<String, Integer>(s, 1);
			}
		});

		JavaPairRDD<String, Integer> counts = ones.reduceByKey(new Function2<Integer, Integer, Integer>() {
			@Override
			public Integer call(Integer i1, Integer i2) {
				return i1 + i2;
			}
		});

		List<Tuple2<String, Integer>> output = counts.collect();
		for (Tuple2<?, ?> tuple : output) {
			System.out.println(tuple._1() + ": " + tuple._2());
		}
		javaSparkContext.stop();
	}
}
