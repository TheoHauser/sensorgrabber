package com.example.hauser2016.sensorgrabberwatch;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by hauser2016 on 6/9/15.
 */
public class WatchInfoSensorService extends Service implements SensorEventListener {
//Prep objects for the sensor and manager.
private SensorManager sensorManager = null;
private Sensor sensorAccelerometer = null;
private Sensor sensorMagnetic = null;
private Sensor sensorGeoRotationVector = null;

//Arrays to hold sensor values.
private float R[] = new float[9];
private float I[] = new float[9];
float[] mAccelerometerValues = null;
float[] mMagneticValues = null;
float orientation[] = new float[3];
float orientation1[] = new float[3];

//testing rotation vector
float[] rotationMatrix = null;

//get time
SimpleDateFormat time = new SimpleDateFormat("HHmmss");
Calendar calTime = Calendar.getInstance();
long ogTime = calTime.getTimeInMillis();

String currentTime;
int seconds;


boolean bothSensorsHaveValues = false;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        prepareSensors();
        //ogTime = calTime.getTimeInMillis();
        return START_STICKY;
        //return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void sendBroadcast(){
        Intent intent = new Intent ("orientationValues"); //put the same message as in the filter you used in the activity when registering the receiver
        intent.putExtra("time", String.valueOf(currentTime));
        intent.putExtra("azimuth", String.valueOf(orientation[0]));
        intent.putExtra("pitch", String.valueOf(orientation[1]));
        intent.putExtra("roll", String.valueOf(orientation[2]));
        intent.putExtra("seconds", String.valueOf(seconds));
        //adding extra stuff for the accelerometer values.
        intent.putExtra("xAccel", String.valueOf(mAccelerometerValues[0]));
        intent.putExtra("yAccel", String.valueOf(mAccelerometerValues[1]));
        intent.putExtra("zAccel", String.valueOf(mAccelerometerValues[2]));


        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }


    //********** Sensor Methods **********//

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

//        //Grab the sensor information from the sensor that is callin this method.
//        if(sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
//            mAccelerometerValues = sensorEvent.values;
//
//        if(sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
//            mMagneticValues = sensorEvent.values;

        switch (sensorEvent.sensor.getType()) {

            case Sensor.TYPE_ROTATION_VECTOR:
                rotationMatrix=new float[16];
                SensorManager.getRotationMatrixFromVector(rotationMatrix, sensorEvent.values);
                SensorManager.remapCoordinateSystem(rotationMatrix, SensorManager.AXIS_X, SensorManager.AXIS_Z, rotationMatrix);
                SensorManager.getOrientation(rotationMatrix, orientation);
            case Sensor.TYPE_ACCELEROMETER:
                mAccelerometerValues = sensorEvent.values;

        }

        currentTime = time.format(calTime.getTime());

        //Check to see if both sensors have values.
        if(mAccelerometerValues != null && rotationMatrix !=null){
            //Attempt to grab the rotation matrix.
//            boolean success = SensorManager.getRotationMatrix(R, I, mAccelerometerValues, mMagneticValues);
            //Check to see if we were successful in grabbing the rotation matrix.
            //If we are indeed successful then we will use that info to grab our orientation.
            //          if(success){
//                SensorManager.getOrientation(R,orientation1);
//                currentTime = time.format(calTime.getTime());
//                //seconds = (ogTime - calTime.getTimeInMillis())/1000;
            //        }

            //We have our values, now we should prep the intent that will send them back.
            sendBroadcast();

            //We have sent the information back to the Activity. So now let us unregister the sensor listener.
            sensorManager.unregisterListener(this);

            //let us now stop the service.
            stopSelf();


        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void prepareSensors() {
        //Register the sensorManager and both the accelerometer and magnetic sensor.
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //sensorMagnetic = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensorGeoRotationVector = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        //Register listeners.
        sensorManager.registerListener(this, sensorAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        //sensorManager.registerListener(this, sensorMagnetic, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, sensorGeoRotationVector, SensorManager.SENSOR_DELAY_FASTEST);
    }


}

