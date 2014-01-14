package org.sensapp.android.sensappdroid.api;

import android.os.AsyncTask;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created with IntelliJ IDEA.
 * User: Dell
 * Date: 19.12.13
 * Time: 18:42
 * To change this template use File | Settings | File Templates.
 */
public final class HttpGetRequest extends AsyncTask<String, Void, Void> {


    private JSONObject result = null;
    @Override
    protected Void doInBackground(String... strings) {
        for (String url:strings){
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse httpResponse = httpclient.execute(new HttpGet(url));
                InputStream inputStream = httpResponse.getEntity().getContent();

                BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
                String line = "";
                String str = "";
                while((line = bufferedReader.readLine()) != null)
                    str += line;

                inputStream.close();

                result = new JSONObject(str);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public JSONObject getResult() {
        return result;
    }
}
