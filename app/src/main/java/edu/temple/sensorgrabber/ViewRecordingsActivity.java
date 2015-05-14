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

import org.xml.sax.Parser;
import org.xmlpull.v1.XmlPullParser;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by sam rizer on 5/13/2015.
 */
public class ViewRecordingsActivity extends ListActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewrecordings);

        int j = 0;
        String dir = Environment.getExternalStorageDirectory() + "/sensorGrabber";
        setTitle(dir);
        File fdir = new File(dir);
        String c[] = fdir.list();
        String fileList[] = new String[c.length];
        List values = new ArrayList();

        for(File f : fdir.listFiles()){
            fileList[j] = f.getAbsolutePath();
            values.add(f);
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
        String filename = (String) getListAdapter().getItem(position).toString();
    }

}

/*

    Here's the link I found for this: http://stackoverflow.com/questions/8906471/how-to-display-list-of-folders-and-files-using-androids-expandable-listview
*/
