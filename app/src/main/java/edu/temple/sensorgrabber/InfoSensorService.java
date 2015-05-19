package edu.temple.sensorgrabber;

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
 * Created by vmartin on 3/19/15.
 */
public class InfoSensorService extends Service implements SensorEventListener {

    //Prep objects for the sensor and manager.
    private SensorManager sensorManager = null;
    private Sensor sensorAccelerometer = null;
    private Sensor sensorMagnetic = null;

    //Arrays to hold sensor values.
    float[] mAccelerometerValues = null;
    float[] mMagneticValues = null;
    float orientation[] = new float[3];

    //who knows if this works
    SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss");
    Calendar calTime = Calendar.getInstance();

    String currentTime;


    boolean bothSensorsHaveValues = false;




    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        prepareSensors();
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
        //adding extra stuff for the accelerometer values.
        intent.putExtra("xAccel", String.valueOf(mAccelerometerValues[0]));
        intent.putExtra("yAccel", String.valueOf(mAccelerometerValues[1]));
        intent.putExtra("zAccel", String.valueOf(mAccelerometerValues[2]));


        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }


    //********** Sensor Methods **********//

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        //Grab the sensor information from the sensor that is callin this method.
        if(sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            mAccelerometerValues = sensorEvent.values;

        if(sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            mMagneticValues = sensorEvent.values;

        //Check to see if both sensors have values.
        if(mAccelerometerValues != null && mMagneticValues !=null)
            bothSensorsHaveValues = true;

        //Both sensors have values so now we have to use those values to get our azimuth.
        if(bothSensorsHaveValues) {
            float R[] = new float[9];
            float I[] = new float[9];

            //Attempt to grab the rotation matrix.
            boolean success = SensorManager.getRotationMatrix(R, I, mAccelerometerValues, mMagneticValues);

            //Check to see if we were successful in grabbing the rotation matrix.
            //If we are indeed successful then we will use that info to grab our orientation.
            if(success){
                SensorManager.getOrientation(R,orientation);
                currentTime = time.format(calTime.getTime());
            }

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
        sensorMagnetic = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        //Register listeners.
        sensorManager.registerListener(this, sensorAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, sensorMagnetic, SensorManager.SENSOR_DELAY_FASTEST);
    }


}
