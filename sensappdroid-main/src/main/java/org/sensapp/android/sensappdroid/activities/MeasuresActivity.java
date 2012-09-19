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
import org.sensapp.android.sensappdroid.fragments.MeasureListFragment.OnMeasureSelectedListener;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class MeasuresActivity extends FragmentActivity implements OnMeasureSelectedListener {
	
	private String sensorName;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.measures);
        sensorName = getIntent().getData().getLastPathSegment();
        setTitle(sensorName);
    }

	@Override
	public void onMeasureSelected(Uri uri) {
		Intent i = new Intent(this, MeasureActivity.class);
		i.setData(uri);
		startActivity(i);
	}
}