import scala.Tuple2;

import org.apache.spark.api.java.*;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.mllib.regression.LinearRegressionModel;
import org.apache.spark.mllib.regression.LinearRegressionWithSGD;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;

public class LinearRegression {
	
	static {
		Logger.getLogger("org").setLevel(Level.WARN);
		Logger.getLogger("akka").setLevel(Level.WARN);
	}
	

	public static void main(String[] args) {
		SparkConf conf = new SparkConf().setAppName("Linear Regression Example");
		JavaSparkContext sc = new JavaSparkContext(conf);

		// Load and parse the data
		String path1 = "/Users/christopherstreiffer/Desktop/ECE558/goodPaulHuntingAJauntIntoDNS/TimeCounts1";
		String path2 = "/Users/christopherstreiffer/Desktop/ECE558/goodPaulHuntingAJauntIntoDNS/TimeCounts2";
		String path3 = "/Users/christopherstreiffer/Desktop/ECE558/goodPaulHuntingAJauntIntoDNS/TimeCounts3";
		JavaRDD<String> data1 = sc.textFile(path1);
		JavaRDD<String> data2 = sc.textFile(path2);
		JavaRDD<String> data3 = sc.textFile(path3);
		
		JavaRDD<String> words = data1.union(data2).union(data3);
		JavaPairRDD<String, Double> ones = words.mapToPair(new PairFunction<String, String, Double>() {
			@Override
			public Tuple2<String, Double> call(String s) {
				String val = s.replace("(", "").replace(")", "");
				String[] ret = val.split(",");
				return new Tuple2<String, Double>(ret[0], Double.parseDouble(ret[1]));
			}
		});
		
		JavaPairRDD<String, Iterable<Double>> data = ones.groupByKey();
		JavaPairRDD<String, Iterable<Double>> sortedData = data.sortByKey();
		JavaRDD<LabeledPoint> parsedData = sortedData.map(new Function<Tuple2<String,Iterable<Double>>,LabeledPoint>() {
			@Override
			public LabeledPoint call(Tuple2<String, Iterable<Double>> v1) throws Exception {
				String val1 = v1._1;			
				Iterator<Double> iter = v1._2.iterator();				
				List<Double> myList = new ArrayList<Double>();
				
				System.out.println("Value: " + val1);
				while(iter.hasNext()) {
					Double toAdd = iter.next();
					myList.add(toAdd);
				}
				
				Double[] myDouble = myList.toArray(new Double[myList.size()]);				
				double[] ret = new double[myDouble.length];
				for(int i=0; i < ret.length; i++) {
					ret[i] = myDouble[i];
				}
				return new LabeledPoint(Double.parseDouble(val1), Vectors.dense(ret));
			}
			
		});
	
		parsedData.cache();
		
//		JavaRDD<LabeledPoint> parsedData = data.map(new Function<String, LabeledPoint>() {
//			public LabeledPoint call(String line) {
//				String[] parts = line.split(",");
//				String[] features = parts[1].split(" ");
//				double[] v = new double[features.length];
//				for (int i = 0; i < features.length - 1; i++)
//					v[i] = Double.parseDouble(features[i]);
//				return new LabeledPoint(Double.parseDouble(parts[0]), Vectors.dense(v));
//			}
//		});


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
				System.out.println("Pair 1: " + pair._1() + " Pair 2: " + pair._2());
				return Math.pow(pair._1() - pair._2(), 2.0);
			}
		}).rdd()).mean();
		System.out.println("training Mean Squared Error = " + MSE);

		// Save and load model
		// model.save(sc.sc(), "myModelPath");
		// LinearRegressionModel sameModel = LinearRegressionModel.load(sc.sc(),
		// "myModelPath");
	}

}
