package edu.temple.sensorgrabber;

import android.app.Service;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.content.*;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

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

    private ActivityData storedData;

    //Time Handling
    private int waitTimeMS = 500;
    private Boolean waitedLongEnough; //Set this if the time has elapsed.
    private Long lastUpdatedTime = 0l;
    private Long currentUpdatedTime = 0l;

    String nameSession = "TestName";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        android.os.Debug.waitForDebugger();
        Log.v("SensorDataService","Service Started");
        //Start the service.
        //Register the sensorManager and both the accelerometer and magnetic sensor.
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorMagnetic = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        //Register listeners.
        sensorManager.registerListener(this, sensorAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, sensorMagnetic, SensorManager.SENSOR_DELAY_FASTEST);

        //Grab time.
        lastUpdatedTime = System.currentTimeMillis();

        //return super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        android.os.Debug.waitForDebugger();


        Log.v("SensorDataService:","Values changed!");
        float azimuth;

        //Do the time stuff.
        currentUpdatedTime = System.currentTimeMillis();

        if(sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER )
            mAccelerometerValues = sensorEvent.values;
        if(sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            mMagneticValues = sensorEvent.values;

        android.os.Debug.waitForDebugger();
        Log.v("derp:","derp");
        android.os.Debug.waitForDebugger();
        if((currentUpdatedTime - lastUpdatedTime) < waitTimeMS ) {

            if (mAccelerometerValues != null && mMagneticValues != null) {
                float R[] = new float[9];
                float I[] = new float[9];

                //Attempt to grab the rotation matrix.
                android.os.Debug.waitForDebugger();
                boolean success = SensorManager.getRotationMatrix(R, I, mAccelerometerValues, mMagneticValues);

                if (success) {
                    lastUpdatedTime = System.currentTimeMillis();

                    float orientation[] = new float[3];
                    SensorManager.getOrientation(R, orientation);
                    android.os.Debug.waitForDebugger();

                    Log.v("AZIMUTH:", String.valueOf(orientation[0]) + "Taken at:" + lastUpdatedTime.toString());
                    //somehow return orientation[0] back to another thread.
                    android.os.Debug.waitForDebugger();

                    azimuth = orientation[0];
                    //Here is where you should add the stored data to the ActivityData object.
                    storedData.addXYZData(lastUpdatedTime, orientation[0], orientation[1], orientation[2], nameSession);
                }

            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}

