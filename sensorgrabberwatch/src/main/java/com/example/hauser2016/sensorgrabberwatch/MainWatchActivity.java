package com.example.hauser2016.sensorgrabberwatch;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.File;

public class MainWatchActivity extends Activity {

    AlarmManager scheduler;
    Intent intentSchedule;
    PendingIntent scheduledIntent;
    private TextView mTextView;

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
            setTextValues(time, azimuth, pitch, roll);
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
        setContentView(R.layout.activity_main_watch);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                TextView mTextView = (TextView) stub.findViewById(R.id.text);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(BReceiver);
    }


    public void onClickRecord(View view) {

        Intent intent = new Intent( getApplicationContext(), WatchDataStorageService.class);

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

    void startAutoCheckOfSensors() {
        scheduler = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        intentSchedule = new Intent(getApplicationContext(), WatchInfoSensorService.class );
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
        AlertDialog.Builder builder = new AlertDialog.Builder(MainWatchActivity.this);
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
                        File directory = new File(sdCard.getAbsolutePath() + "/sensorGrabberWatch");
                        File file = new File(directory, f);
                        boolean deleted = file.delete();

                        CharSequence c = "Save Cancelled";
                        CharSequence fail = "Cancel Failed, File " + f + " saved in /sensorGrabberWatch";
                        if (deleted) {
                            Toast delete = Toast.makeText(getApplicationContext(), c, Toast.LENGTH_SHORT);
                            delete.show();
                        } else {
                            Toast failed = Toast.makeText(getApplicationContext(), fail, Toast.LENGTH_LONG);
                            failed.show();
                        }
                    }
                });
        AlertDialog s = builder.create();
        return s;


    }
}

