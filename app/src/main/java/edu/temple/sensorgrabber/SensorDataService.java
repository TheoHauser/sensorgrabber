package edu.temple.sensorgrabber;

import android.app.Service;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.content.*;
import android.net.Uri;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by vmartin on 3/18/15.
 */
//TODO finish ability to write to file
//TODO: check how stopService works... So that you can write code to save the data when the service stop button is hit.
    
public class SensorDataService extends Service implements SensorEventListener {



    private SensorManager sensorManager = null;
    private Sensor sensorAccelerometer = null;
    private Sensor sensorMagnetic = null;

    float[] mAccelerometerValues = null;
    float[] mMagneticValues = null;

    private ActivityData storedData = new ActivityData();

    //Time Handling
    private int waitTimeMS = 500;
    private Boolean waitedLongEnough; //Set this if the time has elapsed.
    private Long lastUpdatedTime = 0l;
    private Long currentUpdatedTime = 0l;
    private Long elapsedTime = 0l;

    int totalCount = 0;
    //date
    Date today = new Date();

    String nameSession = "TestName";

    //Declare the date object for time stamping.
    String fileNameOutput = Long.toString(today.getTime()) + ".csv";
    File sdCard = Environment.getExternalStorageDirectory();
    File directory = new File (sdCard.getAbsolutePath() + "/sensorgrabber");
    File file = new File(directory, fileNameOutput);


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onDestroy() {

        //Unregister the sensors.
        destroySensors();

        //Write the data to a csv file.
        writeDataToFile();

        //super.onDestroy();
    }

    private void destroySensors() {
        Log.v("SensorDataServer:", "Unregister Listeners for Sensors");
        sensorManager.unregisterListener(this);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        android.os.Debug.waitForDebugger();
        storedData.addXYZData(12345l, 1f, 2f, 3f, "nachos");
        Log.v("SensorDataService","Service Started");
        //Start the service.
        prepareSensors();

        //Grab time.
        lastUpdatedTime = System.currentTimeMillis();

        //return super.onStartCommand(intent, flags, startId);
        try{
            openAndTagFileWithName();
        } catch(Exception e){
            Log.v("PROBLEM HOLE","PROBLEM");
            e.printStackTrace();
        };

        return START_STICKY;
    }

    private void prepareSensors() {
        //Register the sensorManager and both the accelerometer and magnetic sensor.
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorMagnetic = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        //Register listeners.
        sensorManager.registerListener(this, sensorAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, sensorMagnetic, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        android.os.Debug.waitForDebugger();


       // Log.v("SensorDataService:","Values changed!");
        float azimuth;

        //Do the time stuff.
        currentUpdatedTime = System.currentTimeMillis();
       // android.os.Debug.waitForDebugger();
        elapsedTime = currentUpdatedTime - lastUpdatedTime;

        if((elapsedTime) >= waitTimeMS ) {

            if(sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER )
                mAccelerometerValues = sensorEvent.values;
            if(sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
                mMagneticValues = sensorEvent.values;

            //android.os.Debug.waitForDebugger();
          //  Log.v("derp:","derp");
           // android.os.Debug.waitForDebugger();

                if (mAccelerometerValues != null && mMagneticValues != null) {
                    float R[] = new float[9];
                    float I[] = new float[9];

                    //Attempt to grab the rotation matrix.
                    android.os.Debug.waitForDebugger();
                    boolean success = SensorManager.getRotationMatrix(R, I, mAccelerometerValues, mMagneticValues);

                    if (success) {

                        float orientation[] = new float[3];
                        SensorManager.getOrientation(R, orientation);
                        android.os.Debug.waitForDebugger();

//                        Log.v("AZIMUTH:", String.valueOf(orientation[0]) + "Taken at:" + lastUpdatedTime.toString());
                        //somehow return orientation[0] back to another thread.
  //                      android.os.Debug.waitForDebugger();

                        azimuth = orientation[0];

                        //Here is where you should add the stored data to the ActivityData object.
                        storedData.addXYZData(lastUpdatedTime, orientation[0], orientation[1], orientation[2], nameSession);
                        lastUpdatedTime = currentUpdatedTime;
    //                    Log.v("SensorDataService:", "hopefully just updated.");

                    }

                }
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void writeDataToFile() {


        //Create the output file to write to.
        try{
            FileOutputStream fOut = openFileOutput(fileNameOutput, Context.MODE_APPEND);
            PrintWriter pWriter = new PrintWriter(fOut);



            //Grab size of storedData object.
            int sizeOfStoredData = storedData.returnSize();
            String currentLineToWrite;

            for(int i = 0; i <= sizeOfStoredData; i++){
                currentLineToWrite = storedData.pullXYZData(i);
                pWriter.printf("%s\n", currentLineToWrite);

            }

            pWriter.close();



        } catch(Exception e){
            e.printStackTrace();
        }

        //Tell me I'm done and a cool guy.
        Toast toast = Toast.makeText(this, "File written?", Toast.LENGTH_LONG);
        Log.d("SensorDataService", "File should be written.");
        sendCapturedDataViaEmail();


    }

    public void openAndTagFileWithName() throws FileNotFoundException
    {
        //FileOutputStream fOut = new FileOutputStream(file,true);
        FileOutputStream fOut = openFileOutput(fileNameOutput, Context.MODE_APPEND);
        PrintWriter pWriter = new PrintWriter(fOut);
        pWriter.printf("%s\n", nameSession);
        pWriter.close();

    }

    private void sendCapturedDataViaEmail(){
        Uri U = Uri.fromFile(file);
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("application/csv");
        i.putExtra(Intent.EXTRA_SUBJECT, "Captured Data");
        i.putExtra(Intent.EXTRA_TEXT, "This is captured data.");
        i.putExtra(Intent.EXTRA_STREAM, U);
        startActivity(Intent.createChooser(i, "Send Mail"));

    }
}

