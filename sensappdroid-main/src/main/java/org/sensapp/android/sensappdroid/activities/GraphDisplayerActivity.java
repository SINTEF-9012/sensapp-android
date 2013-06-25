package org.sensapp.android.sensappdroid.activities;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import org.sensapp.android.sensappdroid.R;
import org.sensapp.android.sensappdroid.contract.SensAppContract;
import org.sensapp.android.sensappdroid.graph.GraphAdapter;
import org.sensapp.android.sensappdroid.graph.GraphBaseView;
import org.sensapp.android.sensappdroid.graph.GraphBuffer;
import org.sensapp.android.sensappdroid.graph.GraphWrapper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Jonathan
 * Date: 19/06/13
 * Time: 16:21
 * To change this template use File | Settings | File Templates.
 */
public class GraphDisplayerActivity extends Activity {
    private String graphName="GRAPH";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.graph_displayer);
        graphName = getIntent().getData().getLastPathSegment();
        setTitle(graphName);

        GraphAdapter adapter;
        List<GraphWrapper> gwl = new ArrayList<GraphWrapper>();

        //Init sensor names
        Cursor cursorSensorNames = getContentResolver().query(Uri.parse(SensAppContract.GraphSensor.CONTENT_URI + "/graph/" + graphName), null, null, null, null);
        String[] sensorNames = new String[cursorSensorNames.getCount()];
        for(int i=0; cursorSensorNames.moveToNext(); i++)
            sensorNames[i] = cursorSensorNames.getString(cursorSensorNames.getColumnIndex(SensAppContract.GraphSensor.SENSOR));

        Cursor cursor;
        for(String name : sensorNames){
            cursor = getContentResolver().query(Uri.parse(SensAppContract.Measure.CONTENT_URI + "/" + name), null, null, null, null);
            addGraphToWrapperList(gwl, cursor);
        }
        /*Cursor cursor = getContentResolver().query(Uri.parse(SensAppContract.Measure.CONTENT_URI + "/" + "Android_Tab_AccelerometerX"), null, null, null, null);
        addGraphToWrapperList(gwl, cursor);

        cursor = getContentResolver().query(Uri.parse(SensAppContract.Measure.CONTENT_URI + "/" + "Android_Tab_AccelerometerY"), null, null, null, null);
        addGraphToWrapperList(gwl, cursor);*/


        adapter = new GraphAdapter(getApplicationContext(), gwl);
        ListView list = (ListView) findViewById(R.id.list_graph_displayer);
        list.setAdapter(adapter);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.composite_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.manage_sensors:
                //CompositeListFragment.ManageCompositeDialogFragment.newInstance("test").show(getSupportFragmentManager(), "manage_sensor");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addGraphToWrapperList(List<GraphWrapper> gwl, Cursor cursor){
        GraphBuffer buffer = new GraphBuffer();
        //Cursor cursor = getContentResolver().query(Uri.parse(SensAppContract.Measure.CONTENT_URI + "/" + "Android_Tab_AccelerometerY"), null, null, null, null);
        for (cursor.moveToFirst() ; !cursor.isAfterLast() ; cursor.moveToNext()) {
            buffer.insertData(cursor.getFloat(cursor.getColumnIndex(SensAppContract.Measure.VALUE)));
        }

        GraphWrapper wrapper = new GraphWrapper(buffer);
        wrapper.setGraphOptions(Color.BLUE, 500, GraphBaseView.LINECHART, -2, 2, "Y");
        wrapper.setPrinterParameters(false, false, true);

        gwl.add(wrapper);
    }
}