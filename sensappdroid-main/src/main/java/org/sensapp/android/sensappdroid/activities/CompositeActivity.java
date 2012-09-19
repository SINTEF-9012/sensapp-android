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

import org.sensapp.android.sensappdroid.R;
import org.sensapp.android.sensappdroid.contract.SensAppContract;
import org.sensapp.android.sensappdroid.fragments.CompositeListFragment.ManageCompositeDialogFragment;
import org.sensapp.android.sensappdroid.fragments.SensorListFragment.OnSensorSelectedListener;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class CompositeActivity extends FragmentActivity implements OnSensorSelectedListener {
	
	private static final String TAG = CompositeActivity.class.getSimpleName();

	private String compositeName;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.d(TAG, "__ON_CREATE__");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensors);
        compositeName = getIntent().getData().getLastPathSegment();
        setTitle(compositeName);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.composite_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.manage_sensors:
			ManageCompositeDialogFragment.newInstance(compositeName).show(getSupportFragmentManager(), "manage_sensor");
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onSensorSelected(Uri uri) {
		Intent i = new Intent(this, MeasuresActivity.class);
		i.setData(Uri.parse(SensAppContract.Measure.CONTENT_URI + "/" + uri.getLastPathSegment()));
		startActivity(i);
	}
}