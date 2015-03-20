package edu.temple.sensorgrabber;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {

    //For some reason our activitydata class is causing problems in the service. I am going to attempt to debug it here.
    private ActivityData nachos = new ActivityData();

    private BroadcastReceiver BReceiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            //put here whatever you want your activity to do with the intent received
            Bundle args = intent.getExtras();
            String azimuth = args.getString("azimuth");
            String pitch = args.getString("pitch");
            String roll = args.getString("roll");

            Log.d("Sensor Values:", azimuth + "," + pitch + "," + roll);

//            Toast toast = new Toast(context);
//            toast.setDuration(Toast.LENGTH_LONG);
//            toast.setText("Sensor Values:" + azimuth + "," + pitch + "," + roll);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.v("MainActivity:","Started");
        LocalBroadcastManager.getInstance(this).registerReceiver(BReceiver, new IntentFilter("orientationValues"));

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

        Intent intent = new Intent( getApplicationContext(), InfoSensorService.class);

        switch(view.getId()){
            case R.id.buttonStart:
                Log.v("MainActivity:","Start Service Button Hit");
                startService(intent);
                //nachos.addXYZData(12345l, 0f, 1f, 2f, "nachos");
                break;

            case R.id.buttonStop:
                Log.v("MainActivity:","Stop Service Button Hit");
                stopService(intent);
                break;


        }
    }

}
