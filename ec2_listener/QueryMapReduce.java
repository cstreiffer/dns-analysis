import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaDoubleRDD;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.mllib.regression.LinearRegressionModel;
import org.apache.spark.mllib.regression.LinearRegressionWithSGD;

import scala.Tuple2;

public final class QueryMapReduce {
  private static final Pattern SPACE = Pattern.compile(" ");

  public static void main(String[] args) throws Exception {

    if (args.length < 1) {
      System.err.println("Usage: JavaWordCount <file>");
      System.exit(1);
    }
    FileReader reader = new FileReader("counter.txt");
    BufferedReader readbuff = new BufferedReader(reader);
    int counter = Integer.parseInt(readbuff.readLine());
    readbuff.close();
    
    java.io.File file = new java.io.File("");
    File p = new File(file.getAbsolutePath()+"/query_mr_results"+counter);
    if(p.exists()){
	    for (File f : p.listFiles()) f.delete();
	    p.delete();
    }
    
    File p_domip = new File(file.getAbsolutePath()+"/query_mr_domip_results"+counter);
	if(p_domip.exists()){
    	for (File f : p_domip.listFiles()) f.delete();
	    p_domip.delete();
	}
    
    File p_time = new File(file.getAbsolutePath()+"/query_mr_time_results"+counter);
    if(p_time.exists()){
	    for (File f : p_time.listFiles()) f.delete();
	    p_time.delete();
    }
	    
    SparkConf sparkConf = new SparkConf().setAppName("QueryMapReduce");
    JavaSparkContext ctx = new JavaSparkContext(sparkConf);
    JavaRDD<String> lines = ctx.textFile(args[0], 1);

    JavaRDD<String> words = lines.flatMap(new FlatMapFunction<String, String>() {
      @Override
      public Iterable<String> call(String s) throws NumberFormatException {
    	  List<String> query_list = Arrays.asList(s.split("\n"));
    	  StringBuilder sb = new StringBuilder();

    	  for(String q_s:query_list){
    		  if(q_s.split(",").length>0){
	    		  String domain = q_s.split(",")[0];
	    		  String ip_addr = q_s.split(",")[1];
	    		  String time = q_s.split(",")[2];
	    		  String type = q_s.split(",")[3];
	    		  String MAC = q_s.split(",")[4];
	        	  int time_val = Integer.parseInt(time.split("\\.")[0]);
	        	  time_val = time_val - (time_val%1800);
	        	  time = Integer.toString(time_val);
	        	  sb.append(domain+" ");
	        	  sb.append(ip_addr+" ");
	        	  //sb.append(type+" ");
	        	 // sb.append(MAC+" ");
	        	  sb.append(time);
	        	  sb.append("\n");   
    		  }
    	  }
    	  
        return Arrays.asList(sb.toString().split("\n"));
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
    counts.saveAsTextFile("query_mr_results"+counter);


      JavaPairRDD<String, Integer> time_ones = words.mapToPair(new PairFunction<String, String, Integer>() {
        @Override
        public Tuple2<String, Integer> call(String s) {
          return new Tuple2<String, Integer>(s.split("\\s")[2], 1);
        }
      });

      JavaPairRDD<String, Integer> time_counts = time_ones.reduceByKey(new Function2<Integer, Integer, Integer>() {
        @Override
        public Integer call(Integer i1, Integer i2) {
          return i1 + i2;
        }
      });
      time_counts.saveAsTextFile("query_mr_time_results"+counter);

        JavaPairRDD<String, Integer> domip_ones = words.mapToPair(new PairFunction<String, String, Integer>() {
          @Override
          public Tuple2<String, Integer> call(String s) {
            return new Tuple2<String, Integer>((s.split("\\s")[0]+", "+s.split("\\s")[1]), 1);
          }
        });

        JavaPairRDD<String, Integer> domip_counts = domip_ones.reduceByKey(new Function2<Integer, Integer, Integer>() {
          @Override
          public Integer call(Integer i1, Integer i2) {
            return i1 + i2;
          }
        });
        domip_counts.saveAsTextFile("query_mr_domip_results"+counter);
        
        if(counter%5==0){
        	counter=1;
        }else{
        	counter+=1;
        }

        
        
        
        
        
		JavaRDD<LabeledPoint> parsedData = time_counts.map(new Function<Tuple2<String,Integer>, LabeledPoint>() {
			@Override
			public LabeledPoint call(Tuple2<String, Integer> tuple) {
				double time = Double.parseDouble(tuple._1());
				double count = (double) tuple._2();
				//String[] parts = line.split(" ");
				//String[] features = parts[1].split(" ");
				//double[] v = new double[features.length];
				//for (int i = 0; i < features.length - 1; i++)
				//	v[i] = Double.parseDouble(features[i]);
				//return new LabeledPoint(Double.parseDouble(parts[0]), Vectors.dense(v));
				double[] counts = new double[2];
				counts[0] = 0;
				counts[1] = count;
				return new LabeledPoint(time, Vectors.dense(counts));
			}
		});
		parsedData.cache();

		// Building the model
		int numIterations = 100;
		final LinearRegressionModel model = LinearRegressionWithSGD.train(JavaRDD.toRDD(parsedData), numIterations);

		// Evaluate model on training examples and compute training error
		JavaRDD<Tuple2<Double, Double>> valuesAndPreds = parsedData
				.map(new Function<LabeledPoint, Tuple2<Double, Double>>() {
					public Tuple2<Double, Double> call(LabeledPoint point) {
						double prediction = model.predict(point.features());
						return new Tuple2<Double, Double>(prediction, point.label());
					}
				});
		double MSE = new JavaDoubleRDD(valuesAndPreds.map(new Function<Tuple2<Double, Double>, Object>() {
			public Object call(Tuple2<Double, Double> pair) {
				return Math.pow(pair._1() - pair._2(), 2.0);
			}
		}).rdd()).mean();
		System.out.println("training Mean Squared Error = " + MSE);
        
        
        
        
        
        
        
        List<Tuple2<String, Integer>> output = counts.collect();
        for (Tuple2<?,?> tuple : output) {
        	System.out.println(tuple._1() + ": " + tuple._2());
        }
        System.out.println(file.getAbsolutePath());
        FileWriter fw = new FileWriter(file.getAbsolutePath()+"/counter.txt");
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(Integer.toString(counter));
        bw.close();
        ctx.stop();
  }
}