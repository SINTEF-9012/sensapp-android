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