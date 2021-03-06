/**
 * Copyright (C) 2012 SINTEF <fabien@fleurey.com>
 *
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE, Version 3, 29 June 2007;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sensapp.android.sensappdroid.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import org.sensapp.android.sensappdroid.contract.SensAppContract;
import org.sensapp.android.sensappdroid.graph.GraphBaseView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Jonathan
 * Date: 20/06/13
 * Time: 10:28
 * To change this template use File | Settings | File Templates.
 */
public class ManageGraphSensorFragment extends DialogFragment {

    private static final String GRAPH_NAME = "graph_name";
    private static final String GRAPH_ID = "graph_id";
    private List<String> sensorsAdded = new ArrayList<String>();
    private List<String> sensorsRemoved = new ArrayList<String>();
    private Cursor cursor;
    private Callable<Integer> toExec;

    public static ManageGraphSensorFragment newInstance(String graphName, long id) {
        ManageGraphSensorFragment frag = new ManageGraphSensorFragment();
        Bundle args = new Bundle();
        args.putString(GRAPH_NAME, graphName);
        args.putLong(GRAPH_ID, id);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final String graphName = getArguments().getString(GRAPH_NAME);
        final Long graphID = getArguments().getLong(GRAPH_ID);
        cursor = getActivity().getContentResolver().query(SensAppContract.Sensor.CONTENT_URI, null, null, null, SensAppContract.Sensor.NAME + " ASC");
        Cursor cursorGraphSensor = getActivity().getContentResolver().query(Uri.parse(SensAppContract.GraphSensor.CONTENT_URI + "/graph/" + graphID), null, null, null, SensAppContract.GraphSensor.SENSOR + " ASC");
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
            if(sensorStatus[i])
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
                            values.put(SensAppContract.GraphSensor.MAX, Integer.MAX_VALUE);
                            values.put(SensAppContract.GraphSensor.MIN, Integer.MIN_VALUE);
                            values.put(SensAppContract.GraphSensor.GRAPH, graphID);
                            values.put(SensAppContract.GraphSensor.SENSOR, name);
                            getActivity().getContentResolver().insert(SensAppContract.GraphSensor.CONTENT_URI, values);
                            values.clear();
                        }
                        for (String name : sensorsRemoved) {
                            String where = SensAppContract.GraphSensor.SENSOR + " = \"" + name + "\" AND " + SensAppContract.GraphSensor.GRAPH + " = " + graphID;
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
