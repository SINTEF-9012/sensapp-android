package org.sensapp.android.sensappdroid.activities;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import org.sensapp.android.sensappdroid.R;
import org.sensapp.android.sensappdroid.contract.SensAppContract;
import org.sensapp.android.sensappdroid.fragments.GraphsListFragment;
import org.sensapp.android.sensappdroid.fragments.ManageGraphSensorFragment;
import org.sensapp.android.sensappdroid.graph.GraphAdapter;
import org.sensapp.android.sensappdroid.graph.GraphBaseView;
import org.sensapp.android.sensappdroid.graph.GraphBuffer;
import org.sensapp.android.sensappdroid.graph.GraphWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Jonathan
 * Date: 19/06/13
 * Time: 16:21
 * To change this template use File | Settings | File Templates.
 */
public class GraphDisplayerActivity extends FragmentActivity {
    private String graphName="GRAPH";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.graph_displayer);
        graphName = getIntent().getData().getLastPathSegment();
        setTitle(graphName);

        displayGraphs();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.composite_menu, menu);
        return true;
    }

    private Integer displayGraphs(){
        GraphAdapter adapter;
        List<GraphWrapper> gwl = new ArrayList<GraphWrapper>();

        //Init sensor names
        Cursor cursorSensors = getContentResolver().query(Uri.parse(SensAppContract.GraphSensor.CONTENT_URI + "/graph/" + graphName), null, null, null, null);
        String[] sensorNames = new String[cursorSensors.getCount()];
        String[] graphTitles = new String[cursorSensors.getCount()];
        int[] graphStyles = new int[cursorSensors.getCount()];
        int[] graphColors = new int[cursorSensors.getCount()];
        for(int i=0; cursorSensors.moveToNext(); i++){
            sensorNames[i] = cursorSensors.getString(cursorSensors.getColumnIndex(SensAppContract.GraphSensor.SENSOR));
            graphTitles[i] = cursorSensors.getString(cursorSensors.getColumnIndex(SensAppContract.GraphSensor.TITLE));
            graphStyles[i] = cursorSensors.getInt(cursorSensors.getColumnIndex(SensAppContract.GraphSensor.STYLE));
            graphColors[i] = cursorSensors.getInt(cursorSensors.getColumnIndex(SensAppContract.GraphSensor.COLOR));
        }

        Cursor cursor;
        for(int i=0; i<sensorNames.length; i++){
            cursor = getContentResolver().query(Uri.parse(SensAppContract.Measure.CONTENT_URI + "/" + sensorNames[i]), null, null, null, null);
            addGraphToWrapperList(gwl, cursor, graphTitles[i], graphStyles[i], graphColors[i]);
        }

        adapter = new GraphAdapter(getApplicationContext(), gwl);
        ListView list = (ListView) findViewById(R.id.list_graph_displayer);
        list.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        return 1;
    }

    @Override
    public void onResume(){
        super.onResume();
        displayGraphs();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.manage_sensors:
                Callable<Integer> function = new Callable<Integer>(){
                    public Integer call(){
                        return displayGraphs();
                    }
                };

                ManageGraphSensorFragment.newInstance(graphName).show(getSupportFragmentManager(), "ManageGraphDialog", function);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addGraphToWrapperList(List<GraphWrapper> gwl, Cursor cursor, String title, int style, int color){
        GraphBuffer buffer = new GraphBuffer();
        //Cursor cursor = getContentResolver().query(Uri.parse(SensAppContract.Measure.CONTENT_URI + "/" + "Android_Tab_AccelerometerY"), null, null, null, null);
        for (cursor.moveToFirst() ; !cursor.isAfterLast() ; cursor.moveToNext()) {
            buffer.insertData(cursor.getFloat(cursor.getColumnIndex(SensAppContract.Measure.VALUE)));
        }

        GraphWrapper wrapper = new GraphWrapper(buffer);
        wrapper.setGraphOptions(color, 500, style, title);
        wrapper.setPrinterParameters(true, false, true);

        gwl.add(wrapper);
    }
}