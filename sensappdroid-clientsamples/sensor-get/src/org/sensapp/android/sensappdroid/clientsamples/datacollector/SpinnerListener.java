package org.sensapp.android.sensappdroid.clientsamples.datacollector;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sensapp.android.sensappdroid.api.SensAppHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Dell
 * Date: 17.12.13
 * Time: 15:32
 * To change this template use File | Settings | File Templates.
 */
public class SpinnerListener implements AdapterView.OnItemSelectedListener{
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
         if (adapterView.getId() ==  R.id.spinner ){
             Spinner spinner1 =  (Spinner) ((View) adapterView.getParent()).findViewById(R.id.spinner1) ;
             Spinner spinner2 =  (Spinner) ((View) adapterView.getParent()).findViewById(R.id.spinner2) ;
             Button button =  (Button) ((View) adapterView.getParent()).findViewById(R.id.button) ;

             // check from and to
             String sensor = ((TextView) view).getText().toString();
             JSONObject result = SensAppHelper.getMeasurement(adapterView.getContext(),sensor , null, null, "demo.sensapp.org");
             addTimeToSpinner((Context) adapterView.getContext(), result, spinner1, spinner2);

             spinner1.setEnabled(true);
             spinner2.setEnabled(true);
             spinner2.setSelection(spinner2.getLastVisiblePosition());
             button.setEnabled(true);


         }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void addTimeToSpinner(Context context, JSONObject json, Spinner s1, Spinner s2){
        List<String> list = new ArrayList<String>();

        try {
            Long basetime = json.getLong("bt");
            JSONArray e = json.getJSONArray("e");

            for(int i = 0; i < e.length(); i++){
                Long time =  Long.valueOf(e.getJSONObject(i).getString("t"));
                if(basetime == null){
                       list.add(time.toString());

                } else {
                    Long sum = time + basetime;
                    list.add(sum.toString());
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        if(list.isEmpty()){
           list.add("null");
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s1.setAdapter(dataAdapter);
        s2.setAdapter(dataAdapter);


    }
}
