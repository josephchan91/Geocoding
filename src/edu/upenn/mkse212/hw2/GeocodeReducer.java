package edu.upenn.mkse212.hw2;

import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.io.*;

import edu.upenn.mkse212.URLGeocodeWritable;

public class GeocodeReducer extends Reducer<Text, URLGeocodeWritable, Text, Text> {

  /* TODO: Your reducer code here */
	public void reduce(Text key, Iterable<URLGeocodeWritable> values, Context context) 
	throws java.io.IOException, InterruptedException{
		
		boolean keyIsRelatedToCities = false;
		double[] coords = new double[2];
		
		// We only take the coordinate of the first found geocode (in case a category has >1 geocodes)
		for (URLGeocodeWritable value : values) {
			if(value.getName().toString().equals("GEOCODE")) {
				// Value represents a geocode, must be related. Get coordinates
				keyIsRelatedToCities = true;
				coords[0] = value.getLatitude();
				coords[1] = value.getLongitude();
				break;
			}
		}
		
		// If no geocode was found, the category is not related to the cities
		if(!keyIsRelatedToCities) return;
		String coordsToString = "(" + coords[0] + "," + coords[1] + ")";
		
		// Iterate over values and emit for each image
		for (URLGeocodeWritable value : values) {
			if(!value.getName().toString().equals("GEOCODE")) {
				// Value represents an image, emit
				String keyAndURL = key.toString() + "\t" + value.getName().toString();
				context.write(new Text(coordsToString), new Text(keyAndURL));
			}
		}
	}
  
}
