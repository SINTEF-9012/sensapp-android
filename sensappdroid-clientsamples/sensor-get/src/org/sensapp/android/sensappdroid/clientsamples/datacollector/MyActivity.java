package org.sensapp.android.sensappdroid.clientsamples.datacollector;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.sensapp.android.sensappdroid.api.SensAppHelper;
import org.sensapp.android.sensappdroid.api.SensAppUnit;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MyActivity extends Activity {
    /**
     * Called when the activity is first created.
     */

    TableLayout tl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        tl = (TableLayout) findViewById(R.id.tabL) ;
        Spinner spinner = (Spinner) findViewById(R.id.spinner);

        // disable spinner from/to and button
        Spinner spinner1 =  (Spinner) findViewById(R.id.spinner1) ;
        spinner1.setEnabled(false);

        Spinner spinner2 =  (Spinner) findViewById(R.id.spinner2) ;
        spinner2.setEnabled(false);

        Button button =  (Button) findViewById(R.id.button) ;
        button.setEnabled(false);

        // add listener
        spinner.setOnItemSelectedListener(new SpinnerListener());

        // Check if SensApp is installed.
        if (!SensAppHelper.isSensAppInstalled(getApplicationContext())) {
            // If not suggest to install and return.
            SensAppHelper.getInstallationDialog(MyActivity.this).show();
            return;
        }

        // get available Sensors
        Cursor sensors = SensAppHelper.getSensors(getApplicationContext());
        addSensorsToSpinner(sensors);
    }

    public void executeQuery(View view){
        tl.removeAllViews();
        String sensor = ((Spinner) findViewById(R.id.spinner)).getSelectedItem().toString();
        String from = ((Spinner) findViewById(R.id.spinner1)).getSelectedItem().toString();
        String to = ((Spinner) findViewById(R.id.spinner2)).getSelectedItem().toString();
        Long fromVal = null;
        Long toVal = null;
        if(!from.equals("null")){
            fromVal = Long.valueOf(from);
            toVal = Long.valueOf(to);
        }

        JSONObject result = SensAppHelper.getMeasurement(getApplicationContext(), sensor, fromVal, toVal, "demo.sensapp.org");

        if(result != null){
           printToScreen(result);
        }
    }

    private void addSensorsToSpinner(Cursor sensors){
         Spinner spinner = (Spinner) findViewById(R.id.spinner);

        List<String> list = new ArrayList<String>();
        if (sensors.moveToFirst()) {
         do{
             list.add(sensors.getString(0));
         }while(sensors.moveToNext());
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }

    private void printToScreen(JSONObject json) {
    //    String[] colums = c.getColumnNames();
        TableRow tr_head = new TableRow(this);


           TextView columnSensor = new TextView(this);
           columnSensor.setPadding(5, 5, 10, 5);
           columnSensor.setText("Sensor");
           tr_head.addView(columnSensor);

        TextView columnBasetime = new TextView(this);
        columnBasetime.setPadding(5, 5, 10, 5);
        columnBasetime.setText("Basetime");
        tr_head.addView(columnBasetime);

        TextView columnTime = new TextView(this);
        columnTime.setPadding(5, 5, 10, 5);
        columnTime.setText("Time");
        tr_head.addView(columnTime);

        TextView columnValue = new TextView(this);
        columnValue.setPadding(5, 5, 10, 5);
        columnValue.setText("Value");
        tr_head.addView(columnValue);

        tl.addView(tr_head);

        try {
            JSONArray eArray = json.getJSONArray("e");
            for(int i = 0; i < eArray.length(); i++){
               JSONObject entry = eArray.getJSONObject(i);
               TableRow line = new TableRow(this);

                TextView fieldBn = new TextView(this);
                fieldBn.setPadding(5, 5, 10, 5);
                fieldBn.setText(json.getString("bn"));
                line.addView(fieldBn);

                TextView fieldBt = new TextView(this);
                fieldBt.setPadding(5, 5, 10, 5);
                fieldBt.setText(json.getString("bt"));
                line.addView(fieldBt);

                TextView fieldT = new TextView(this);
                fieldT.setPadding(5, 5, 10, 5);
                fieldT.setText(eArray.getJSONObject(i).getString("t"));
                line.addView(fieldT);

                TextView fieldV = new TextView(this);
                fieldV.setPadding(5, 5, 10, 5);
                fieldV.setText(eArray.getJSONObject(i).getString("v"));
                line.addView(fieldV);

                tl.addView(line);
            }

 /*       if (c.moveToFirst()) {
            do {
                TableRow line = new TableRow(this);
                for (String s : colums) {
                    TextView field = new TextView(this);
                    field.setPadding(5, 5, 10, 5);
                    field.setText(c.getString(c.getColumnIndex(s)));
                    line.addView(field);
                }
                tl.addView(line);
            } while (c.moveToNext());
        }  */
        } catch (JSONException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
