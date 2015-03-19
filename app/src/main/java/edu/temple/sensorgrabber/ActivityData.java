package edu.temple.sensorgrabber;

import java.util.ArrayList;
import java.util.Vector;

import android.os.Bundle;

public class ActivityData {
	
	//Default size for the ArrayList to store the data before putting it to a file.
	//It should add more when it runs out of space, not entirely sure how this will effect things and I will
	//have to test it out.
	
	private static int defaultSize = 10000;
	
	//Declare Vector to store our data.
	ArrayList<String> activityVector = new ArrayList<String>(defaultSize);
	
	
	//Constructor to create the header.
	ActivityData(){
	
	}
		
	//Add X,Y,Z data along with Time to the temp array.
	public void addXYZData(long timeData, float xData, float yData, float zData, String name){
		
		//Combine all the inputs into a string to add to the list.
		String tempString = new String();
		tempString = Long.toString(timeData) + "," + Float.toString(xData) + "," + Float.toString(yData) + "," + Float.toString(zData) + "," + name;
		activityVector.add(tempString);
		
	}
	
	//Get time, X, Y, Z, name data from the activityVector.
	public String pullXYZData(int line){
		
		String returnString = new String();
		returnString = activityVector.get(line);
				
		return returnString;
		
		
	}
	
	//Return the number of items stored.
	public int returnSize(){
		
		int size = activityVector.size();
		return size;
		
	}
	
	
	
}
