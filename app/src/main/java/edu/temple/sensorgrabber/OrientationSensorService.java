
package edu.temple.sensorgrabber;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Surface;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by sam rizer on 5/20/2015.
 *
 * Heavily borrowed from this guy on StackExchange for the math behind azimuth, pitch, and roll accuracy.
 *
 * References:
 *      THE MATH: http://math.stackexchange.com/questions/381649/whats-the-best-3d-angular-co-ordinate-system-for-working-with-smartphone-apps/382048#382048
 *      THE CODE: http://stackoverflow.com/questions/16317599/android-compass-that-can-compensate-for-tilt-and-pitch/16386066#16386066
 *
 **/
public class OrientationSensorService extends Service implements SensorEventListener {

    SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss");
    Calendar calTime = Calendar.getInstance();
    String currentTime;

    public final static int SENSOR_UNAVALIABLE = -1;
    int seconds;

    SensorManager m_sm;
    SensorEventListener m_parent;
    Activity m_activity;

    // raw inputs from Android sensors
    float m_Norm_Gravity;           // length of raw gravity vector received in onSensorChanged(...).  should be about 10
    float[] m_NormGravityVector;    // Normalised gravity vector, (i.e. length of this vector is 1), which points straight up into space
    float m_Norm_MagField;          // length of raw magnetic field vector received in onSensorChanged(...).
    float[] m_NormMagFieldValues;   // Normalised magnetic field vector, (i.e. length of this vector is 1)

    // accuracy specifications. SENSOR_UNAVAILABLE if unknown, otherwise SensorManager.SENSOR_STATUS_UNRELIABLE, SENSOR_STATUS_ACCURACY_LOW, SENSOR_STATUS_ACCURACY_MEDIUM or SENSOR_STATUS_ACCURACY_HIGH
    int m_GravityAccuracy;          // accuracy of gravity sensor
    int m_MagneticFieldAccuracy;    // accuracy of magnetic field sensor

    // values calculated once gravity and magnetic field vectors are available
    float[] m_NormEastVector;       // normalised cross product of raw gravity vector with magnetic field values, points east
    float[] m_NormNorthVector;      // Normalised vector pointing to magnetic north
    boolean m_OrientationOK;        // set true if m_azimuth_radians and m_pitch_radians have successfully been calculated following a call to onSensorChanged(...)
    float m_azimuth_radians;        // angle of the device from magnetic north
    float m_pitch_radians;          // tilt angle of the device from the horizontal.  m_pitch_radians = 0 if the device if flat, m_pitch_radians = Math.PI/2 means the device is upright.
    float m_pitch_axis_radians;     // angle which defines the axis for the rotation m_pitch_radians

    boolean bothSensorsHaveValues = false;

    /*
     * CONSTRUCTORS
     */
    public OrientationSensorService() {

    }

    public OrientationSensorService(SensorManager sm, SensorEventListener parent) {
        m_sm = sm;
        m_parent = parent;
        m_activity = null;
        m_NormGravityVector = m_NormMagFieldValues = null;
        m_NormEastVector = new float[3]; //x, y, z
        m_NormNorthVector = new float[3];
        m_OrientationOK = false;
    }

    /*
     * SENSOR METHODS
     */
    public int Register(Activity activity,  int sensorSpeed) {
        //m_sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        m_activity = activity;
        m_NormGravityVector = new float[3];
        m_NormMagFieldValues = new float[3];
       // m_NormEastVector = new float[3]; //x, y, z
       // m_NormNorthVector = new float[3];
        m_OrientationOK = false;
        int count = 0;

        Sensor SensorGravity = m_sm.getDefaultSensor(Sensor.TYPE_GRAVITY);
        if (SensorGravity != null) {
            m_sm.registerListener(this, SensorGravity, sensorSpeed);
            m_GravityAccuracy = SensorManager.SENSOR_STATUS_ACCURACY_HIGH;
            count++;
        } else {
            m_GravityAccuracy = SENSOR_UNAVALIABLE;
        }

        Sensor SensorMagField = m_sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if (SensorMagField != null) {
            m_sm.registerListener(this, SensorMagField, sensorSpeed);
            m_MagneticFieldAccuracy = SensorManager.SENSOR_STATUS_ACCURACY_HIGH;
            count++;
        } else {
            m_MagneticFieldAccuracy = SENSOR_UNAVALIABLE;
        }

        return count;
    }

    public void Unregister() {
        m_activity = null;
        m_NormGravityVector = m_NormMagFieldValues = null;
        m_OrientationOK = false;
        m_sm.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent evnt) {
        int sensorType = evnt.sensor.getType();
        switch(sensorType) {
            case Sensor.TYPE_GRAVITY:
                if (m_NormGravityVector == null)
                    m_NormGravityVector = new float[3];
                System.arraycopy(evnt.values, 0, m_NormGravityVector, 0, m_NormGravityVector.length);
                m_Norm_Gravity = (float) Math.sqrt(m_NormGravityVector[0] * m_NormGravityVector[0] + m_NormGravityVector[1] * m_NormGravityVector[1] + m_NormGravityVector[2] * m_NormGravityVector[2]);
                for (int i = 0; i < m_NormGravityVector.length; i++)
                    m_NormGravityVector[i] /= m_Norm_Gravity;
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                if (m_NormMagFieldValues == null)
                    m_NormMagFieldValues = new float[3];
                System.arraycopy(evnt.values, 0 , m_NormMagFieldValues, 0, m_NormMagFieldValues.length);
                m_Norm_MagField = (float) Math.sqrt(m_NormMagFieldValues[0] * m_NormMagFieldValues[0] + m_NormMagFieldValues[1] * m_NormMagFieldValues[1] + m_NormMagFieldValues[2] * m_NormMagFieldValues[2]);
                for (int i = 0; i < m_NormMagFieldValues.length; i++)
                    m_NormMagFieldValues[i] /= m_Norm_MagField;
                break;
        }

        if (m_NormGravityVector != null && m_NormMagFieldValues != null) {
            //Calculates the horizontal vector that points due east
            float East_x = m_NormMagFieldValues[1]*m_NormGravityVector[2] - m_NormMagFieldValues[2]*m_NormGravityVector[1];
            float East_y = m_NormMagFieldValues[2]*m_NormGravityVector[0] - m_NormMagFieldValues[0]*m_NormGravityVector[2];
            float East_z = m_NormMagFieldValues[0]*m_NormGravityVector[1] - m_NormMagFieldValues[1]*m_NormGravityVector[0];
            float norm_East = (float) Math.sqrt(East_x*East_x + East_y*East_y + East_z*East_z);

            if (m_Norm_Gravity * m_Norm_MagField * norm_East < 0.1f) { //Values are typically > 100
                m_OrientationOK = false; //In free-fall (or space, or magnetic north)
            } else {
                m_NormEastVector[0] = East_x / norm_East;
                m_NormEastVector[1] = East_y / norm_East;
                m_NormEastVector[2] = East_z / norm_East;

                float M_dot_G = (m_NormGravityVector[0]*m_NormMagFieldValues[0] + m_NormGravityVector[1]*m_NormMagFieldValues[1] + m_NormGravityVector[2]*m_NormMagFieldValues[2]);//calculates horizontal vector pointing due north
                float North_x = m_NormMagFieldValues[0] - m_NormGravityVector[0] * M_dot_G;
                float North_y = m_NormMagFieldValues[1] - m_NormGravityVector[1] * M_dot_G;
                float North_z = m_NormMagFieldValues[2] - m_NormGravityVector[2] * M_dot_G;
                float norm_North = (float) Math.sqrt(North_x*North_x + North_y*North_y + North_z*North_z);

                m_NormNorthVector[0] = North_x / norm_North;
                m_NormNorthVector[1] = North_y / norm_North;
                m_NormNorthVector[2] = North_z / norm_North;

                //takes into account the rotation of the device away from its normal position
                int rotation = m_activity.getWindowManager().getDefaultDisplay().getRotation();
                float screen_adjustment = 0;

                switch(rotation) {
                    case Surface.ROTATION_0:
                        screen_adjustment = 0;
                        break;
                    case Surface.ROTATION_90:
                        screen_adjustment = (float) Math.PI/2;
                        break;
                    case Surface.ROTATION_180:
                        screen_adjustment = (float) Math.PI;
                        break;
                    case Surface.ROTATION_270:
                        screen_adjustment = (float) Math.PI/2;
                        break;
                }

                //calculates all the required angles for the rotation matrix
                //Here's where the math gets a little nutty
                float sin = m_NormEastVector[1] - m_NormNorthVector[0];
                float cos = m_NormEastVector[0] + m_NormNorthVector[1];

                m_azimuth_radians = (float) (sin != 0 &&cos != 0 ? Math.atan2(sin, cos) : 0);
                m_pitch_radians = (float) Math.acos(m_NormGravityVector[2]);
                sin = -m_NormEastVector[1] - m_NormNorthVector[0];
                cos = m_NormEastVector[0] - m_NormNorthVector[1];
                float aximuth_plus_two_pitch_axis_radians = (float) (sin != 0 && cos != 0 ? Math.atan2(sin, cos) : 0);
                m_pitch_axis_radians = (float) (aximuth_plus_two_pitch_axis_radians - m_azimuth_radians) / 2;
                m_azimuth_radians += screen_adjustment;
                m_pitch_axis_radians += screen_adjustment;
                m_OrientationOK= true;
            }

           // bothSensorsHaveValues = true;

           // if (bothSensorsHaveValues) {

           // }
        }
        if (m_parent != null)
            m_parent.onSensorChanged(evnt);

        if (bothSensorsHaveValues) {
            currentTime = time.format(calTime.getTime());
            sendBroadcast();
            Unregister();
            stopSelf();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        int SensorType = sensor.getType();
        switch(SensorType) {
            case Sensor.TYPE_GRAVITY:
                m_GravityAccuracy = accuracy;
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                m_MagneticFieldAccuracy = accuracy;
                break;
        }
    }

    /*
     * BROADCAST METHODS
     */

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        //Register(ActivityManager., SensorManager.SENSOR_DELAY_FASTEST);
        return START_STICKY;
    }

    private void sendBroadcast() {
        Intent intent = new Intent ("orientationValues"); //put the same message as in the filter you used in the activity when registering the receiver
        intent.putExtra("time", String.valueOf(currentTime));
        intent.putExtra("azimuth", String.valueOf(m_azimuth_radians));
        intent.putExtra("pitch", String.valueOf(m_pitch_radians));
        intent.putExtra("roll", String.valueOf(m_pitch_axis_radians));
        intent.putExtra("seconds", String.valueOf(seconds));
        //adding extra stuff for the accelerometer values.
        intent.putExtra("xAccel", String.valueOf(m_NormGravityVector[0]));
        intent.putExtra("yAccel", String.valueOf(m_NormGravityVector[1]));
        intent.putExtra("zAccel", String.valueOf(m_NormGravityVector[2]));


        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

}
