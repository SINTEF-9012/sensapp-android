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
package org.sensapp.android.sensappdroid.activities;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.TextView;
import org.sensapp.android.sensappdroid.R;
import org.sensapp.android.sensappdroid.contract.SensAppContract;
import org.sensapp.android.sensappdroid.fragments.GraphsListFragment.OnGraphSelectedListener;
import org.sensapp.android.sensappdroid.graph.*;

/**
 * Created with IntelliJ IDEA.
 * User: Jonathan
 * Date: 14/06/13
 * Time: 12:27
 * To change this template use File | Settings | File Templates.
 */
public class GraphActivity extends Activity {

    private final static String TAG = GraphActivity.class.getSimpleName();

    private TextView tvSensorName;
    private GraphDetailsView graph1;
    private GraphDetailsView graph2;
    private GraphDetailsView graph3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "__ON_CREATE__");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_accgyro_view);
        tvSensorName = (TextView) findViewById(R.id.tv_sensor_name);
        tvSensorName.setText("Accelerometers");
        graph1 =  (GraphDetailsView) findViewById(R.id.gv_sensor_graph1);
        graph2 =  (GraphDetailsView) findViewById(R.id.gv_sensor_graph2);
        graph3 =  (GraphDetailsView) findViewById(R.id.gv_sensor_graph3);

        GraphBuffer bufferAX = new GraphBuffer();
        Cursor cursor = getContentResolver().query(Uri.parse(SensAppContract.Measure.CONTENT_URI + "/" + "Android_Tab_AccelerometerX"), null, null, null, null);
        while(!cursor.isLast()){
            bufferAX.insertData(cursor.getFloat(cursor.getColumnIndex(SensAppContract.Measure.VALUE)));
            cursor.moveToNext();
        }
        GraphWrapper wrapper1 = new GraphWrapper(bufferAX);
        wrapper1.setGraphOptions(Color.YELLOW, 300, GraphBaseView.LINECHART, -300, 300, "Lateral");
        wrapper1.setPrinterParameters(true, false, false);
        graph1.registerWrapper(wrapper1);

        GraphWrapper wrapper2 = new GraphWrapper(bufferAX);
        wrapper2.setGraphOptions(Color.YELLOW, 300, GraphBaseView.LINECHART, -300, 300, "Longitudinal");
        wrapper2.setPrinterParameters(true, false, false);
        graph2.registerWrapper(wrapper2);

        GraphWrapper wrapper3 = new GraphWrapper(bufferAX);
        wrapper3.setGraphOptions(Color.YELLOW, 300, GraphBaseView.LINECHART, 0, 600, "Vertical");
        wrapper3.setPrinterParameters(true, false, false);
        graph3.registerWrapper(wrapper3);
    }
}
