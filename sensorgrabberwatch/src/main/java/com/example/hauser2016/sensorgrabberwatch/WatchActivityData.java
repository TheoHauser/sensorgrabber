package com.example.hauser2016.sensorgrabberwatch;

import java.util.ArrayList;

/**
 * Created by hauser2016 on 6/9/15.
 */
public class WatchActivityData {

    //Default size for the ArrayList to store the data before putting it to a file.
    //It should add more when it runs out of space, not entirely sure how this will effect things and I will
    //have to test it out.

    private static int defaultSize = 10000;
    int secondIncrement = 1;

    //Declare Vector to store our data.
    ArrayList<String> activityVector = new ArrayList<String>(defaultSize);


    //Constructor to create the header.
    WatchActivityData(){

    }

    //Add X,Y,Z data along with Time to the temp array.
    public void addXYZData(String timeData, float xData, float yData, float zData, String name){

        //Combine all the inputs into a string to add to the list.
        String tempString = null;
        tempString = timeData + "," + Float.toString(xData) + "," + Float.toString(yData) + "," + Float.toString(zData) + "," + name;
        activityVector.add(tempString);

    }

    //New method to add both our angles and our acceleration.
    public void addAngleAccelData(String timeData, float xAngle, float yAngle, float zAngle, float xAccel, float yAccel, float zAccel){

        String tempString = null;
        tempString  = timeData + "," +
                Integer.toString(secondIncrement)+","+
                Float.toString(xAngle) + "," +
                Float.toString(yAngle) + "," +
                Float.toString(zAngle) + "," +
                Float.toString(xAccel) + "," +
                Float.toString(yAccel) + "," +
                Float.toString(zAccel) + "," ;


        activityVector.add(tempString);
        secondIncrement++;


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


