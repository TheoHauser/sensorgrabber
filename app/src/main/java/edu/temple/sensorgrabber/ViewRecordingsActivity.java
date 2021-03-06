package edu.temple.sensorgrabber;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.net.Uri;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by sam rizer on 5/13/2015.
 */
public class ViewRecordingsActivity extends ListActivity {

    File c[];

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewrecordings);

        int j = 0;
        String dir = Environment.getExternalStorageDirectory() + "/sensorGrabber";
        setTitle(dir);
        File fdir = new File(dir);
        c = fdir.listFiles();
        String fileList[] = new String[c.length];
        List values = new ArrayList();

        for(File f : fdir.listFiles()){
            fileList[j] = f.getAbsolutePath();
            fileList[j] = fileList[j].substring(fileList[j].lastIndexOf('/')+1, fileList[j].length()-4);
            values.add(fileList[j]);
            j++;
        }

        ArrayAdapter adapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_2, android.R.id.text1, values);
        setListAdapter(adapter);

        final Button switchact =(Button)findViewById(R.id.buttonReturn);
        switchact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        Log.v("ViewRecordingsActivity:", "Started");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        String filename = c[position].toString();
        File f = new File(filename);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.fromFile(f));
        startActivity(intent);
    }

}

