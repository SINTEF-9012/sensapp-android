package org.sensapp.android.sensappdroid.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.*;
import android.widget.*;
import org.sensapp.android.sensappdroid.R;
import org.sensapp.android.sensappdroid.contract.SensAppContract;
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
    private long graphID=0;

    public static class GraphSensorOptionsDialogFragment extends DialogFragment implements AdapterView.OnItemSelectedListener {

        private static final String GRAPHSENSOR_ID = "graphsensorID";
        private static final String GRAPHSENSOR_NAME = "graphsensorName";
        private int styleSelected = -1;
        private int colorSelected = -1;
        private Callable<Integer> toExec;

        public static GraphSensorOptionsDialogFragment newInstance(long graphID, String name, Callable<Integer> function) {
            GraphSensorOptionsDialogFragment frag = new GraphSensorOptionsDialogFragment();
            frag.toExec = function;
            Bundle args = new Bundle();
            args.putLong(GRAPHSENSOR_ID, graphID);
            args.putString(GRAPHSENSOR_NAME, name);
            frag.setArguments(args);
            return frag;
        }

        private void initColorArray(ArrayList<String> strings){
            strings.add("Edit color");
            strings.add("Blue");
            strings.add("Cyan");
            strings.add("Dark gray");
            strings.add("Gray");
            strings.add("Green");
            strings.add("Light gray");
            strings.add("Magenta");
            strings.add("Red");
            strings.add("Yellow");
        }

        private void initStyleArray( ArrayList<String> strings){
            strings.add("Edit style");
            strings.add("Linechart");
            strings.add("Barchart");
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final long graphSensorID = getArguments().getLong(GRAPHSENSOR_ID);
            final String graphSensorName = getArguments().getString(GRAPHSENSOR_NAME);

            View dialogOptionGraphSensor = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_option_graph_sensor, null);
            Spinner color = (Spinner)dialogOptionGraphSensor.findViewById(R.id.graph_sensor_color);
            Spinner style = (Spinner)dialogOptionGraphSensor.findViewById(R.id.graph_sensor_style);
            final EditText title = (EditText) dialogOptionGraphSensor.findViewById(R.id.graph_sensor_title);
            final EditText min = (EditText) dialogOptionGraphSensor.findViewById(R.id.graph_sensor_lowest);
            final EditText max = (EditText) dialogOptionGraphSensor.findViewById(R.id.graph_sensor_highest);

            ArrayList<String> colorStrings = new ArrayList<String>();
            initColorArray(colorStrings);
            ArrayList<String> styleStrings = new ArrayList<String>();
            initStyleArray(styleStrings);

            ArrayAdapter<String> adapterColor = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_item, colorStrings);
            adapterColor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            color.setAdapter(adapterColor);
            color.setOnItemSelectedListener(this);

            ArrayAdapter<String> adapterStyle = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_item, styleStrings);
            adapterStyle.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            style.setAdapter(adapterStyle);
            style.setOnItemSelectedListener(this);

            return new AlertDialog.Builder(getActivity())
                    .setTitle("Edit chart options for " + graphSensorName)
                    .setView(dialogOptionGraphSensor)
                    .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            ContentValues values = new ContentValues();
                            if(!(title.getText().length() == 0))
                                values.put(SensAppContract.GraphSensor.TITLE, title.getText().toString());
                            if(!(min.getText().length() == 0))
                                values.put(SensAppContract.GraphSensor.MIN, Integer.parseInt(min.getText().toString()));
                            if(!(max.getText().length() == 0))
                                values.put(SensAppContract.GraphSensor.MAX, Integer.parseInt(max.getText().toString()));
                            if(colorSelected != -1)
                                values.put(SensAppContract.GraphSensor.COLOR, colorSelected);
                            if(styleSelected != -1)
                                values.put(SensAppContract.GraphSensor.STYLE, styleSelected);

                            if(values.size()!=0)
                                getActivity().getContentResolver().update(SensAppContract.GraphSensor.CONTENT_URI, values, SensAppContract.GraphSensor.ID + " = " + graphSensorID, null);
                        }
                    }).create();
        }

        public void onStop(){
            try {
                toExec.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
            super.onStop();
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long l) {
            String item = (String)parent.getItemAtPosition(pos);
            if(item.equals("Barchart")) {
                styleSelected = GraphBaseView.BARCHART;
                return;
            }
            if(item.equals("Linechart")){
                styleSelected = GraphBaseView.LINECHART;
                return;
            }
            //getColorFromString return -1 if the String is not a color
            //so it works also for the "non style" choice.
            colorSelected = getColorFromString(item);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            return;
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.graph_displayer);

        List<String> data = getIntent().getData().getPathSegments();
        graphName = data.get(data.size()-1);
        graphID = Long.parseLong(data.get(data.size() - 2));

        setTitle(graphName);

        ListView list = (ListView) findViewById(R.id.list_graph_displayer);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                GraphWrapper gw = (GraphWrapper)adapterView.getItemAtPosition(i);
                Callable<Integer> refreshGraphs = new Callable<Integer>(){
                    public Integer call(){
                        return displayGraphs();
                    }
                };
                GraphSensorOptionsDialogFragment.newInstance(gw.getID(), gw.getName(), refreshGraphs).show(getSupportFragmentManager(), "newOptionEditor");
            }
        });


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
        Cursor cursorSensors = getContentResolver().query(Uri.parse(SensAppContract.GraphSensor.CONTENT_URI + "/graph/" + graphID), null, null, null, null);
        String[] sensorNames = new String[cursorSensors.getCount()];
        String[] graphTitles = new String[cursorSensors.getCount()];
        int[] graphStyles = new int[cursorSensors.getCount()];
        int[] graphColors = new int[cursorSensors.getCount()];
        long[] graphSensorIDs = new long[cursorSensors.getCount()];
        for(int i=0; cursorSensors.moveToNext(); i++){
            graphSensorIDs[i] = cursorSensors.getLong(cursorSensors.getColumnIndex((SensAppContract.GraphSensor.ID)));
            sensorNames[i] = cursorSensors.getString(cursorSensors.getColumnIndex(SensAppContract.GraphSensor.SENSOR));
            graphTitles[i] = cursorSensors.getString(cursorSensors.getColumnIndex(SensAppContract.GraphSensor.TITLE));
            graphStyles[i] = cursorSensors.getInt(cursorSensors.getColumnIndex(SensAppContract.GraphSensor.STYLE));
            graphColors[i] = cursorSensors.getInt(cursorSensors.getColumnIndex(SensAppContract.GraphSensor.COLOR));
        }

        Cursor cursor;
        for(int i=0; i<sensorNames.length; i++){
            cursor = getContentResolver().query(Uri.parse(SensAppContract.Measure.CONTENT_URI + "/" + sensorNames[i]), null, null, null, null);
            addGraphToWrapperList(gwl, cursor, graphTitles[i], graphStyles[i], graphColors[i], graphSensorIDs[i]);
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
                Callable<Integer> refresh = new Callable<Integer>(){
                    public Integer call(){
                        return displayGraphs();
                    }
                };
                ManageGraphSensorFragment.newInstance(graphName, graphID).show(getSupportFragmentManager(), "ManageGraphDialog", refresh);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addGraphToWrapperList(List<GraphWrapper> gwl, Cursor cursor, String title, int style, int color, long graphSensorID){
        GraphBuffer buffer = new GraphBuffer();
        //Cursor cursor = getContentResolver().query(Uri.parse(SensAppContract.Measure.CONTENT_URI + "/" + "Android_Tab_AccelerometerY"), null, null, null, null);
        for (cursor.moveToFirst() ; !cursor.isAfterLast() ; cursor.moveToNext()) {
            buffer.insertData(cursor.getFloat(cursor.getColumnIndex(SensAppContract.Measure.VALUE)));
        }

        GraphWrapper wrapper = new GraphWrapper(graphSensorID, buffer);
        wrapper.setGraphOptions(color, 500, style, title);
        wrapper.setPrinterParameters(true, false, true);

        gwl.add(wrapper);
    }

    static public int getColorFromString(String color){
        if(color.equals("Blue"))
            return Color.BLUE;
        if(color.equals("Cyan"))
            return Color.CYAN;
        if(color.equals("Dark gray"))
            return Color.DKGRAY;
        if(color.equals("Gray"))
            return Color.GRAY;
        if(color.equals("Green"))
            return Color.GREEN;
        if(color.equals("Light gray"))
            return Color.LTGRAY;
        if(color.equals("Magenta"))
            return Color.MAGENTA;
        if(color.equals("Red"))
            return Color.RED;
        if(color.equals("Yellow"))
            return Color.YELLOW;
        return -1;
    }
}