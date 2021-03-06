package com.example.hauser2016.sensorgrabberwatch;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by hauser2016 on 6/9/15.
 */
public class WatchDataStorageService extends Service {
    //Where we are going to store the stuff we need.
    WatchActivityData storedActivityData = new WatchActivityData();
    SimpleDateFormat dateTime = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
    Calendar calTime = Calendar.getInstance();
    String fileName =  dateTime.format(calTime.getTime()) + ".csv";
    File file = createFile();
    String nameOfCapture = "Test";
    private final static String TAG = "WatchDataStorageService";


    @Override
    public void onCreate() {
        //Register our broadcast receiver.
        super.onCreate();

        Log.d(TAG, "service started");

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("orientationValues"));

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //TODO:do i change this?


        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        //Put this in here. It seems like maybe Android was killing this before I got the chance to? Weird...
        try{
            unregisterReceiver(receiver);
        }catch (Exception e){
            e.printStackTrace();
        }

        //Now we have to write all the data to the file system.
        writeToFile();
        //data is written to a file. Now send the file name.
        sendFileName();

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public File createFile(){
        File sdCard = Environment.getExternalStorageDirectory();
        File directory = new File (sdCard, "/sensorGrabber");
        directory.mkdirs();
        File file = new File(directory, fileName);
        return file;
    }


    public void writeToFile(){
        //Create the output file to write to.
        try{
            //Commented out the above in an attempt to write somewhere public so i can send the email.
            //FileOutputStream fOut = openFileOutput(fileName, Context.MODE_APPEND);
            FileOutputStream fOut = new FileOutputStream(file, true);
            PrintWriter pWriter = new PrintWriter(fOut);

            //Grab size of storedData object.
            int sizeOfStoredData = storedActivityData.returnSize();
            String currentLineToWrite;

            for(int i = 0; i < sizeOfStoredData; i++) {
                currentLineToWrite = storedActivityData.pullXYZData(i);
                pWriter.printf("%s\n", currentLineToWrite);
            }

            pWriter.close();
            Log.v("something", "osmething");



        } catch(Exception e){
            e.printStackTrace();
        }
    }

    void sendFileName(){
        Log.d("sendFileName", fileName);
        Intent intent = new Intent ("fileDone"); //put the same message as in the filter you used in the activity when registering the receiver
        intent.putExtra("filename", String.valueOf(fileName));
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    //Handle broadcast stuff.
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle args = intent.getExtras();
            String time = args.getString("time");
            String azimuth = args.getString("azimuth");
            String pitch = args.getString("pitch");
            String roll = args.getString("roll");
            String seconds = args.getString("seconds");

            //I have added some more data.
            String xAccel = args.getString("xAccel");
            String yAccel = args.getString("yAccel");
            String zAccel = args.getString("zAccel");

            Log.i("azi", azimuth);


            //Temporary storage.
            //storedActivityData.addXYZData(Long.valueOf(time), Float.valueOf(azimuth), Float.valueOf(pitch), Float.valueOf(roll), nameOfCapture);


            //Now that I am logging more information I am going to use the addAngleAccelData() method instead of the addXYZData() method.
            storedActivityData.addAngleAccelData(time,Float.valueOf(azimuth), Float.valueOf(pitch), Float.valueOf(roll),
                    Float.valueOf(xAccel),Float.valueOf(yAccel),Float.valueOf(zAccel));
        }

    };



}


