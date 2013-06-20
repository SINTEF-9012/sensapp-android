package org.sensapp.android.sensappdroid.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.DialogFragment;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import org.sensapp.android.sensappdroid.contract.SensAppContract;
import org.sensapp.android.sensappdroid.graph.GraphBaseView;

import java.util.ArrayList;
import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Jonathan
 * Date: 20/06/13
 * Time: 10:28
 * To change this template use File | Settings | File Templates.
 */
public class ManageGraphSensorFragment extends DialogFragment {

    private static final String GRAPH_NAME = "composite_name";
    private ArrayList<String> sensorsAdded = new ArrayList<String>();
    private ArrayList<String> sensorsRemoved = new ArrayList<String>();
    private Cursor cursor;
    private Callable<Integer> toExec;

    public static ManageGraphSensorFragment newInstance(String graphName) {
        ManageGraphSensorFragment frag = new ManageGraphSensorFragment();
        Bundle args = new Bundle();
        args.putString(GRAPH_NAME, graphName);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final String graphName = getArguments().getString(GRAPH_NAME);
        cursor = getActivity().getContentResolver().query(SensAppContract.Sensor.CONTENT_URI, null, null, null, SensAppContract.Sensor.NAME + " ASC");
        Cursor cursorGraphSensor = getActivity().getContentResolver().query(Uri.parse(SensAppContract.GraphSensor.CONTENT_URI + "/graph/" + graphName), null, null, null, SensAppContract.GraphSensor.SENSOR + " ASC");
        String[] sensorNames = new String[cursor.getCount()];
        boolean[] sensorStatus = new boolean[cursor.getCount()];
        cursorGraphSensor.moveToFirst();
        int columnIDSensorName = cursor.getColumnIndexOrThrow(SensAppContract.Sensor.NAME);
        int columnIDGraphSensor = cursorGraphSensor.getColumnIndexOrThrow(SensAppContract.GraphSensor.SENSOR);
        for (int i = 0 ; cursor.moveToNext() ; i ++) {
            //Init sensorNames and put sensorStatus to true if the sensor is in the graph
            sensorNames[i] = cursor.getString(columnIDSensorName);
            if(!cursorGraphSensor.isAfterLast() && cursor.getString(columnIDSensorName).equals(cursorGraphSensor.getString(columnIDGraphSensor))){
                sensorStatus[i] = true;
            }
            else
                sensorStatus[i] = false;
            if(sensorStatus[i] == true)
                cursorGraphSensor.moveToNext();

        }
        //Make and display the Dialog
        return new AlertDialog.Builder(getActivity())
                .setTitle("Add sensors to the graph " + graphName)
                .setMultiChoiceItems(sensorNames, sensorStatus,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                cursor.moveToPosition(which);
                                String sensorName = cursor.getString(cursor.getColumnIndexOrThrow(SensAppContract.Sensor.NAME));
                                if (isChecked) {
                                    sensorsRemoved.remove(sensorName);
                                    sensorsAdded.add(sensorName);
                                } else {
                                    sensorsAdded.remove(sensorName);
                                    sensorsRemoved.add(sensorName);
                                }
                            }
                        })
                .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        ContentValues values = new ContentValues();
                        for (String name : sensorsAdded) {
                            values.put(SensAppContract.GraphSensor.TITLE, name);
                            values.put(SensAppContract.GraphSensor.STYLE, GraphBaseView.LINECHART);
                            values.put(SensAppContract.GraphSensor.COLOR, Color.BLUE);
                            values.put(SensAppContract.GraphSensor.MAX, 3);
                            values.put(SensAppContract.GraphSensor.MIN, 3);
                            values.put(SensAppContract.GraphSensor.GRAPH, graphName);
                            values.put(SensAppContract.GraphSensor.SENSOR, name);
                            getActivity().getContentResolver().insert(SensAppContract.GraphSensor.CONTENT_URI, values);
                            values.clear();
                        }
                        for (String name : sensorsRemoved) {
                            String where = SensAppContract.GraphSensor.SENSOR + " = \"" + name + "\" AND " + SensAppContract.GraphSensor.GRAPH + " = \"" + graphName + "\"";
                            getActivity().getContentResolver().delete(SensAppContract.GraphSensor.CONTENT_URI, where, null);
                        }
                        cursor.close();
                    }
                }).create();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        if (cursor != null) {
            cursor.close();
        }
    }


    public void show(FragmentManager fragmentManager, String manageGraphDialog, Callable<Integer> func) {
        super.show(fragmentManager, manageGraphDialog);
            toExec = func;
    }

    @Override
    public void onStop(){
        try {
            toExec.call();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onStop();
    }
}
