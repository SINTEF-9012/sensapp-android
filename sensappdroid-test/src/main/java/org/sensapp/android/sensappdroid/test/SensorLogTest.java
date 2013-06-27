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
package org.sensapp.android.sensappdroid.test;

import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;
import org.sensapp.android.sensappdroid.clientsamples.sensorlogger.AbstractSensor;
import org.sensapp.android.sensappdroid.clientsamples.sensorlogger.BatterySensor;
import org.sensapp.android.sensappdroid.contract.SensAppContract;

/**
 * Created with IntelliJ IDEA.
 * User: Jonathan
 * Date: 27/06/13
 * Time: 14:49
 * To change this template use File | Settings | File Templates.
 */
public class SensorLogTest extends AndroidTestCase{

    private int countTestSensors(){
        Cursor cursor = getContext().getContentResolver().query(Uri.parse(SensAppContract.Sensor.CONTENT_URI + "/composite/test"), null, null, null, null);
        assertNotNull(cursor);
        int count  = 0;
        while (cursor.moveToNext()) {
            count ++;
        }
        cursor.close();
        return count;
    }

    public void testInsertSensor() {
        assertEquals(0, countTestSensors());
        AbstractSensor as = new BatterySensor("test");
        as.registerInSensApp(getContext(), R.drawable.ic_launcher);
        assertEquals(1, countTestSensors());
    }

    public void testSensorMethods(){
        AbstractSensor as = null;
        assert(as == null);
        as = new BatterySensor("test");
        assert(as.getName() == "Battery");
        assert(as.getFullName().contains("Battery"));
        assert(false);
    }
}
