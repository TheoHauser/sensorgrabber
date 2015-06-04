package edu.temple.sensorgrabber;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.File;
import java.io.FileOutputStream;
import android.support.v4.app.DialogFragment;

public class MainActivity extends ActionBarActivity{

    AlarmManager scheduler;
    Intent intentSchedule;
    PendingIntent scheduledIntent;

    //Set how many MS between each attempted update.
    final int repeatMS = 500;

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
            String fileName = args.getString("filename");

            AlertDialog s = askToSave(fileName);
            s.show();


            //sendFileViaEmail(fileName);


        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.v("MainActivity:", "Started");
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

    public void onClickRecord(View view) {

        Intent intent = new Intent( getApplicationContext(), DataStorageService.class);

        switch (view.getId()) {

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

    public void OnClickViewRecordings(View view) {
        Intent intent = new Intent(this, ViewRecordingsActivity.class);
        startActivity(intent);
    }

    void startAutoCheckOfSensors() {
        scheduler = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        intentSchedule = new Intent(getApplicationContext(), InfoSensorService.class );
        //PendingIntent scheduledIntent = PendingIntent.getService(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        scheduledIntent = PendingIntent.getService(getApplicationContext(), 0, intentSchedule, 0);
        scheduler.setRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime(), repeatMS, scheduledIntent );
    }

    void stopAutoCheckOfSensors(){
        scheduler.cancel(scheduledIntent);
    }

    void setTextValues(String time, String azimuth, String pitch, String roll) {
        TextView timeTextView = (TextView) findViewById(R.id.textTime);
        TextView azimuthTextView = (TextView) findViewById(R.id.textAzimuth);
        TextView pitchTextView = (TextView) findViewById(R.id.textPitch);
        TextView rollTextView = (TextView) findViewById(R.id.textRoll);

        timeTextView.setText(time);
        azimuthTextView.setText(azimuth);
        pitchTextView.setText(pitch);
        rollTextView.setText(roll);


    }

    void sendFileViaEmail(String filename) {

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

    AlertDialog askToSave(String fileName){
        final String f = fileName;
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Save sensor data?")
                    .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            CharSequence ch = "File " + f + " saved in \n/sensorGrabber/";
                            Toast saved = Toast.makeText(getApplicationContext(), ch, Toast.LENGTH_LONG);
                            saved.show();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            File sdCard = Environment.getExternalStorageDirectory();
                            File directory = new File (sdCard.getAbsolutePath() + "/sensorGrabber");
                            File file = new File(directory, f);
                            boolean deleted = file.delete();

                            CharSequence c = "Save Cancelled";
                            CharSequence fail = "Cancel Failed, File "+ f+ " saved in /sensorGrabber";
                            if(deleted){
                                Toast delete = Toast.makeText(getApplicationContext(),c,Toast.LENGTH_SHORT);
                                delete.show();
                            }
                            else{
                                Toast failed = Toast.makeText(getApplicationContext(),fail, Toast.LENGTH_LONG);
                                failed.show();
                            }
                        }
                    });
        AlertDialog s = builder.create();
        return s;


    }
}
