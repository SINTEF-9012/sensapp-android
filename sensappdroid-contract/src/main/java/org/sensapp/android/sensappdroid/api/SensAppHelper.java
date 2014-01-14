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
package org.sensapp.android.sensappdroid.api;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.util.concurrent.ExecutionException;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
import org.sensapp.android.sensappdroid.contract.SensAppContract;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources.NotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;

public class SensAppHelper {

    private SensAppHelper() {
    }

    private static ContentValues buildMeasure(String sensor, String value, long basetime, long time) throws IllegalArgumentException {
        if (sensor == null) {
            throw new IllegalArgumentException("The sensor is null");
        } else if (value == null) {
            throw new IllegalArgumentException("The value is null");
        }
        ContentValues values = new ContentValues();
        values.put(SensAppContract.Measure.SENSOR, sensor);
        values.put(SensAppContract.Measure.VALUE, value);
        values.put(SensAppContract.Measure.BASETIME, basetime);
        values.put(SensAppContract.Measure.TIME, time);
        return values;
    }

    /**
     * Returns all sensors available in the local DB
     *
     * @param context the context used for the context resolver
     * @return  Cursor object containing sensor names
     */
    public static Cursor getSensors(Context context) {

        Cursor c = context.getContentResolver().query(SensAppContract.Sensor.CONTENT_URI, new String[]{SensAppContract.Sensor.NAME}, null, null, null);
        return c;
    }

    /**
     * Get measurements from the local DB and the server and returns merged results.
     *
     * @param context the context used for the context resolver
     * @param sensor name of the sensor to get data
     * @param from timestamp restriction
     * @param to timestamp restriction
     * @param serverUrl server, which stores the data for the requested sensor
     * @return
     */
    public static JSONObject getMeasurement(Context context, String sensor, Long from, Long to, String serverUrl) {
        HttpGetRequest req = null;
        boolean webDataAvailable = false;
        JSONObject webData = new JSONObject();

        if (serverUrl != null) {
            req = getFromWeb(sensor, from, to, serverUrl);
            try {
                req.get();
                webData = req.getResult();
                if (webData != null && webData.has("e")) {
                    webDataAvailable = true;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        Cursor c = getFromLocalDB(context, sensor, from, to);
        JSONObject result = generateJson(c, webDataAvailable);

        if (webDataAvailable) {
            if (!result.has("e")) {
                return webData;
            }
            result = mergeData(result, webData);
        }
        return result;
    }

    private static JSONObject generateJson(Cursor c, boolean filter) {
        JSONObject jObj = new JSONObject();

        if (c.moveToFirst()) {

            int indexSensor = c.getColumnIndex(SensAppContract.Measure.SENSOR);
            int indexBasetime = c.getColumnIndex(SensAppContract.Measure.BASETIME);
            int indexValue = c.getColumnIndex(SensAppContract.Measure.VALUE);
            int indexTime = c.getColumnIndex(SensAppContract.Measure.TIME);
            int indexUploaded = c.getColumnIndex(SensAppContract.Measure.UPLOADED);

            if (indexSensor != -1 && indexBasetime != -1 && indexTime != -1 && indexValue != -1) {
                try {
                    jObj.put("bn", c.getString(indexSensor));
                    jObj.put("bt", c.getLong(indexBasetime));
                    if (c.getInt(indexUploaded) == 0 || filter) {
                        JSONObject set = new JSONObject("{" + "v" + ":" + c.getLong(indexValue) + "," + "t:" + c.getLong(indexTime) + "}");
                        jObj.accumulate("e", set);
                    }
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            } else {
                return null;
            }

            do {

                try {
                    if (c.getInt(indexUploaded) == 0 || filter) {
                        JSONObject set = new JSONObject("{" + "v" + ":" + c.getLong(indexValue) + "," + "t:" + c.getLong(indexTime) + "}");
                        jObj.accumulate("e", set);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } while (c.moveToNext());
        }
        return jObj;
    }


    private static JSONObject mergeData(JSONObject localData, JSONObject webData) {
        JSONObject merged = webData;
        if (!webData.has("e")) {
            return localData;
        }


        try {
            Long btWeb = webData.getLong("bt");
            Long btLoc = localData.getLong("bt");
            Long dif = btWeb - btLoc;

            JSONArray eLoc = localData.getJSONArray("e");
            for (int i = 0; i < eLoc.length(); i++) {
                JSONObject entry = eLoc.getJSONObject(i);
                entry.wait(100);
                Long time = Long.getLong(entry.getString("t"));
                Long updatedTime = time - dif;
                entry.put("t", updatedTime.toString());
                merged.accumulate("e", entry);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return merged;
    }

    /**
     * Get measurements from the server
     *
     *
     * @param sensor name of the sensor to get data
     * @param from timestamp restriction
     * @param to timestamp restriction
     * @param serverUrl server, which stores the data for the requested sensor
     * @return
     */
    public static HttpGetRequest getFromWeb(String sensor, Long from, Long to, String serverUrl) {
        JSONObject result = null;
        HttpGetRequest req = new HttpGetRequest();
        String reqUrl = "http://" + serverUrl + "/sensapp/databases/raw/data/" + sensor;
        if (from != null && to != null) {
            reqUrl += "?from=" + from + "&to=" + to;
        }
        req.execute(reqUrl);

        return req;
    }

    /**
     * Get measurements from local DB.
     *
     *
     * @param context the context used for the context resolver
     * @param sensor name of the sensor to get data
     * @param from timestamp restriction
     * @param to timestamp restriction
     * @return Cursor object containing requested data
     */
    public static Cursor getFromLocalDB(Context context, String sensor, Long from, Long to) {
        Uri uri = SensAppContract.Measure.CONTENT_URI;
        String[] projection = {SensAppContract.Measure.SENSOR, SensAppContract.Measure.VALUE, SensAppContract.Measure.BASETIME, SensAppContract.Measure.TIME, SensAppContract.Measure.UPLOADED};
        String selection = SensAppContract.Measure.SENSOR + "= '" + sensor + "'";

        if (from != null) {
            selection = selection + " AND " + (SensAppContract.Measure.BASETIME + "+" + SensAppContract.Measure.TIME) + ">=" + from;
        }

        if (to != null) {
            selection = selection + " AND " + (SensAppContract.Measure.BASETIME + "+" + SensAppContract.Measure.TIME) + "<=" + to;
        }

        // query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder);
        Cursor c = context.getContentResolver().query(uri, projection, selection, null, null);

        return c;
    }


    /**
     * Insert measure with default basetime and time.
     * Same as
     *
     * @param context the context used for the content resolver
     * @param sensor  the name of the associated sensor
     * @param value   the value of the measure
     * @return the uri of the newly inserted measure, the measure id is -1 in case of failure
     * @throws IllegalArgumentException
     * @link insertMeasure(Context context, String sensor, int value, long basetime, long time)
     * with basetime = 0 and current time.
     * @see insertMeasure(Context context, String sensor, int value)
     * @see insertMeasure(Context context, String sensor, float value)
     */
    public static Uri insertMeasure(Context context, String sensor, String value) throws IllegalArgumentException {
        ContentValues values = buildMeasure(sensor, value, 0, System.currentTimeMillis() / 1000);
        InsertionManager.storeMeasure(context, values);
        return null;
    }

    public static Uri insertMeasure(Context context, String sensor, int value) throws IllegalArgumentException {
        return insertMeasure(context, sensor, String.valueOf(value));
    }

    public static Uri insertMeasure(Context context, String sensor, float value) throws IllegalArgumentException {
        return insertMeasure(context, sensor, String.valueOf(value));
    }

    public static Uri insertMeasure(Context context, String sensor, String value, long basetime, long time) throws IllegalArgumentException {
        ContentValues values = buildMeasure(sensor, value, basetime, time);
        InsertionManager.storeMeasure(context, values);
        return null;
    }

    public static Uri insertMeasure(Context context, String sensor, int value, long basetime, long time) throws IllegalArgumentException {
        return insertMeasure(context, sensor, String.valueOf(value), basetime, time);
    }

    public static Uri insertMeasure(Context context, String sensor, float value, long basetime, long time) throws IllegalArgumentException {
        return insertMeasure(context, sensor, String.valueOf(value), basetime, time);
    }

    public static Uri insertMeasureForced(Context context, String sensor, String value) throws IllegalArgumentException {
        ContentValues values = buildMeasure(sensor, value, 0, System.currentTimeMillis() / 1000);
        return context.getContentResolver().insert(SensAppContract.Measure.CONTENT_URI, values);
    }

    public static Uri insertMeasureForced(Context context, String sensor, String value, long basetime, long time) throws IllegalArgumentException {
        ContentValues values = buildMeasure(sensor, value, basetime, time);
        return context.getContentResolver().insert(SensAppContract.Measure.CONTENT_URI, values);
    }

    public static boolean isSensAppInstalled(Context context) {
        try {
            context.getPackageManager().getApplicationInfo("org.sensapp.android.sensappdroid", 0);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
        return true;
    }

    public static Dialog getInstallationDialog(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("SensApp application is needed. Would you like install it now?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Uri uri = Uri.parse("market://details?id=" + "org.sensapp.android.sensappdroid");
                try {
                    context.startActivity(new Intent(Intent.ACTION_VIEW, uri));
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        return builder.create();
    }

    private static ContentValues buildSensor(Context context, String name, String description, SensAppUnit unit, SensAppBackend backend, SensAppTemplate template, Drawable icon) throws IllegalArgumentException {
        if (context == null) {
            throw new IllegalArgumentException("The context is null");
        } else if (name == null) {
            throw new IllegalArgumentException("The sensor name is null");
        } else if (isSensorRegistered(context, name)) {
            return null;
        } else if (unit == null) {
            throw new IllegalArgumentException("The sensor unit is null");
        } else if (backend == null) {
            throw new IllegalArgumentException("The sensor backend is null");
        } else if (template == null) {
            throw new IllegalArgumentException("The sensor template is null");
        }
        ContentValues values = new ContentValues();
        values.put(SensAppContract.Sensor.NAME, name);
        if (description == null) {
            values.put(SensAppContract.Sensor.DESCRIPTION, "");
        } else {
            values.put(SensAppContract.Sensor.DESCRIPTION, description);
        }
        values.put(SensAppContract.Sensor.UNIT, unit.getIANAUnit());
        values.put(SensAppContract.Sensor.BACKEND, backend.getBackend());
        values.put(SensAppContract.Sensor.TEMPLATE, template.getTemplate());
        if (icon != null) {
            Bitmap bitmap = ((BitmapDrawable) icon).getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            values.put(SensAppContract.Sensor.ICON, stream.toByteArray());
        }
        return values;
    }

    public static Uri registerSensor(Context context, String name, String description, SensAppUnit unit, SensAppBackend backend, SensAppTemplate template, Drawable icon) throws IllegalArgumentException {
        ContentValues values = buildSensor(context, name, description, unit, backend, template, icon);
        if (values == null) {
            return null;
        }
        return context.getContentResolver().insert(SensAppContract.Sensor.CONTENT_URI, values);
    }

    public static Uri registerSensor(Context context, String name, String description, SensAppUnit unit, SensAppBackend backend, SensAppTemplate template, int iconResourceId) throws IllegalArgumentException, NotFoundException {
        Drawable icon = context.getResources().getDrawable(iconResourceId);
        return registerSensor(context, name, description, unit, backend, template, icon);
    }

    public static Uri registerNumericalSensor(Context context, String name, String description, SensAppUnit unit, Drawable icon) throws IllegalArgumentException {
        return registerSensor(context, name, description, unit, SensAppBackend.raw, SensAppTemplate.NUMERICAL, icon);
    }

    public static Uri registerStringSensor(Context context, String name, String description, SensAppUnit unit, Drawable icon) throws IllegalArgumentException {
        return registerSensor(context, name, description, unit, SensAppBackend.raw, SensAppTemplate.STRING, icon);
    }

    public static Uri registerNumericalSensor(Context context, String name, String description, SensAppUnit unit, int iconResourceId) throws IllegalArgumentException, NotFoundException {
        Drawable icon = context.getResources().getDrawable(iconResourceId);
        return registerSensor(context, name, description, unit, SensAppBackend.raw, SensAppTemplate.NUMERICAL, icon);
    }

    public static Uri registerStringSensor(Context context, String name, String description, SensAppUnit unit, int iconResourceId) throws IllegalArgumentException, NotFoundException {
        Drawable icon = context.getResources().getDrawable(iconResourceId);
        return registerSensor(context, name, description, unit, SensAppBackend.raw, SensAppTemplate.STRING, icon);
    }

    public static boolean renameSensor(Context context, String name, String newName) {
        ContentValues values = new ContentValues();
        values.put(SensAppContract.Sensor.NAME, newName);
        context.getContentResolver().update(Uri.parse(SensAppContract.Sensor.CONTENT_URI + "/" + name), values, null, null);
        values.clear();
        values.put(SensAppContract.Measure.SENSOR, newName);
        context.getContentResolver().update(Uri.parse(SensAppContract.Measure.CONTENT_URI + "/" + name), values, null, null);
        return true;
    }

    public static boolean isSensorRegistered(Context context, String name) {
        Cursor c = context.getContentResolver().query(Uri.parse(SensAppContract.Sensor.CONTENT_URI + "/" + name), new String[]{SensAppContract.Sensor.NAME}, null, null, null);
        boolean isRegistered = false;
        if (c != null) {
            isRegistered = c.getCount() > 0;
            c.close();
        }
        return isRegistered;
    }
}