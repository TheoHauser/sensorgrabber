package edu.temple.sensorgrabber;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by sam rizer on 5/13/2015.
 */
public class ViewRecordingsActivity extends ActionBarActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewrecordings);

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
}

/*

    Here's the link I found for this: http://stackoverflow.com/questions/8906471/how-to-display-list-of-folders-and-files-using-androids-expandable-listview

    public class customListAdapter extends BaseExpandableListAdapter {

    private File folder1;
    private File folder2;

    private String[] groups = {};
    private String[][] children = {};

    public customListAdapter() {
        // Sample data set.  children[i] contains the children (String[]) for groups[i].
        folder1 = new File (Environment.getExternalStorageDirectory(),"/Folder1");
        folder2 = new File (Environment.getExternalStorageDirectory(),"/Folder2");

        String[] fileList1 = folder1.list();
        String[] fileList2 = folder2.list();

        Arrays.sort(fileList1);
        Arrays.sort(fileList2);

        groups = new String[] { "Folder1" , "Folder2" };
        children = new String[][] { fileList1, fileList2 };
    }//constructor


    public Object getChild(int groupPosition, int childPosition) {
        return children[groupPosition][childPosition];
    }

    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    public int getChildrenCount(int groupPosition) {
        return children[groupPosition].length;
    }

    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                                View convertView, ViewGroup parent) {

        TextView textView = new TextView(this);
        textView.setBackgroundColor(Color.BLACK);
        textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        textView.setPadding(100, 5, 0, 5);
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(23);
        textView.setId(1000);

        textView.setText(getChild(groupPosition, childPosition).toString());
        return textView;
    }//getChildView

    public Object getGroup(int groupPosition) {
        return groups[groupPosition];
    }

    public int getGroupCount() {
        return groups.length;
    }

    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
            ViewGroup parent) {
        TextView textView = new TextView(this);
        textView.setBackgroundColor(Color.WHITE);
        textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        textView.setPadding(100, 0, 0, 0);
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(25);
        textView.setText(getGroup(groupPosition).toString());

        return textView;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public boolean hasStableIds() {
        return true;
    }

}//customListAdapter
 */
