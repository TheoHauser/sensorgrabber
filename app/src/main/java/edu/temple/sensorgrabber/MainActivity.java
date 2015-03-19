package edu.temple.sensorgrabber;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class MainActivity extends ActionBarActivity {

    //For some reason our activitydata class is causing problems in the service. I am going to attempt to debug it here.
    private ActivityData nachos = new ActivityData();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.v("MainActivity:","Started");

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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

        Intent intent = new Intent( getApplicationContext(), SensorDataService.class);

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
