package edu.temple.sensorgrabber;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.File;
import java.io.FileOutputStream;


public class MainActivity extends ActionBarActivity {


    AlarmManager scheduler;
    Intent intentSchedule;
    PendingIntent scheduledIntent;

    //Set how many MS between each attempted update.
    final int repeatMS = 1000;

    private BroadcastReceiver BReceiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            //put here whatever you want your activity to do with the intent received
            Bundle args = intent.getExtras();
            String time = args.getString("time");
            String azimuth = args.getString("azimuth");
            String pitch = args.getString("pitch");
            String roll = args.getString("roll");

            Log.v("Sensor Values:", azimuth + "," + pitch + "," + roll);
            setTextValues(time, azimuth,pitch,roll);
        }
    };

    private BroadcastReceiver FileBReceiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            //put here whatever you want your activity to do with the intent received
            Bundle args = intent.getExtras();
            //String fileName = args.getString("filename");

            //sendFileViaEmail(fileName);


        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.v("MainActivity:","Started");
        LocalBroadcastManager.getInstance(this).registerReceiver(BReceiver, new IntentFilter("orientationValues"));
        LocalBroadcastManager.getInstance(this).registerReceiver(FileBReceiver, new IntentFilter("fileDone"));

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(BReceiver);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClick(View view){

        Intent intent = new Intent( getApplicationContext(), DataStorageService.class);

        switch(view.getId()){
//            case R.id.buttonStart:
//                Log.v("MainActivity:","Start Service Button Hit");
//                //startService(intent);
//                //nachos.addXYZData(12345l, 0f, 1f, 2f, "nachos");
//                startAutoCheckOfSensors();
//
//                //Start the DataStorageService which is meant to listen to broadcasts and store them.
//                startService(intent);
//
//                //Check to see if the service is running. If so let the user know.
//                //if (isMyServiceRunning(DataStorageService.class)) {
//                    //Let the user know.
//
//                //}
//
//                break;
//
//            case R.id.buttonStop:
//                Log.v("MainActivity:","Stop Service Button Hit");
//                stopAutoCheckOfSensors();
//
//                //Stop the DataStorageService which should hopefully write to a file.
//                stopService(intent);
//                break;

            case R.id.toggleButtonRecord:
                boolean on = ((ToggleButton) view).isChecked();

                if (on) {
                    startAutoCheckOfSensors();
                    startService(intent);
                    ((ToggleButton) view).setText("Stop Recording");


                } else {
                    stopService(intent);
                    ((ToggleButton) view).setText("Press To Record");
                }




        }
    }

    void startAutoCheckOfSensors(){
        scheduler = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        intentSchedule = new Intent(getApplicationContext(), InfoSensorService.class );
        //PendingIntent scheduledIntent = PendingIntent.getService(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        scheduledIntent = PendingIntent.getService(getApplicationContext(), 0, intentSchedule, 0);
        scheduler.setRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime(), repeatMS, scheduledIntent );
    }

    void stopAutoCheckOfSensors(){
        scheduler.cancel(scheduledIntent);
    }

    void setTextValues(String time, String azimuth, String pitch, String roll){
        TextView timeTextView = (TextView) findViewById(R.id.textTime);
        TextView azimuthTextView = (TextView) findViewById(R.id.textAzimuth);
        TextView pitchTextView = (TextView) findViewById(R.id.textPitch);
        TextView rollTextView = (TextView) findViewById(R.id.textRoll);

        timeTextView.setText(time);
        azimuthTextView.setText(azimuth);
        pitchTextView.setText(pitch);
        rollTextView.setText(roll);


    }

    void sendFileViaEmail(String filename){

        File sdCard = Environment.getExternalStorageDirectory();
        File directory = new File (sdCard.getAbsolutePath() + "/sensorGrabber");
        File file = new File(directory, filename);


        //Get all files?
        File[] files = getFilesDir().listFiles();
        //FileOutputStream fOut = openFileOutput(filename, Context.MODE);
        //File file = new File(getFilesDir()+"/"+filename);

        //Uri U = Uri.fromFile(file);
        //Intent i = new Intent(Intent.ACTION_SEND);
        //Intent i = Intent.createChooser(i, "Send Mail");
        //i.setType("application/csv");
        //i.putExtra(Intent.EXTRA_SUBJECT, "Captured Data");
        //i.putExtra(Intent.EXTRA_TEXT, "This is captured data.");
        //i.putExtra(Intent.EXTRA_STREAM, U);
        //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //startActivity(Intent.createChooser(i, "Send Mail"));
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
