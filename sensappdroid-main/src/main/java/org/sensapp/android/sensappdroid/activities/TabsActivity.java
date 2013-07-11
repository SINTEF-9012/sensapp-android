package org.sensapp.android.sensappdroid.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import org.sensapp.android.sensappdroid.R;
import org.sensapp.android.sensappdroid.contract.SensAppContract;
import org.sensapp.android.sensappdroid.fragments.CompositeListFragment;
import org.sensapp.android.sensappdroid.fragments.CompositeListFragment.OnCompositeSelectedListener;
import org.sensapp.android.sensappdroid.fragments.GraphsListFragment;
import org.sensapp.android.sensappdroid.fragments.GraphsListFragment.OnGraphSelectedListener;
import org.sensapp.android.sensappdroid.fragments.MeasureListFragment;
import org.sensapp.android.sensappdroid.fragments.MeasureListFragment.OnMeasureSelectedListener;
import org.sensapp.android.sensappdroid.fragments.SensorListFragment;
import org.sensapp.android.sensappdroid.fragments.SensorListFragment.OnSensorSelectedListener;

import java.util.ArrayList;
import java.util.List;

public class TabsActivity extends FragmentActivity implements OnCompositeSelectedListener, OnSensorSelectedListener, OnMeasureSelectedListener, OnGraphSelectedListener{

    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;
    PagerTitleStrip mPagerTitle;
    final static int NB_FRAGMENTS = 4;
    final static List<Fragment> fragmentList = new ArrayList<Fragment>(){{
        add(new CompositeListFragment());
        add(new SensorListFragment());
        add(new GraphsListFragment());
        add(new MeasureListFragment());
    }};
    final static List<String> fragmentNames = new ArrayList<String>(){{
        add("Composites");
        add("Sensors");
        add("Graphs");
        add("Measures");
    }};
    final static int COMPOSITES = 0;
    final static int SENSORS = 1;
    final static int GRAPHS = 2;
    final static int MEASURES = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the app.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mPagerTitle = (PagerTitleStrip) findViewById(R.id.pager_title_strip);

        for(int i=0; i< mPagerTitle.getChildCount(); i++){
            if (mPagerTitle.getChildAt(i) instanceof TextView){
                ((TextView)mPagerTitle.getChildAt(i)).setTextSize(20);
                ((TextView)mPagerTitle.getChildAt(i)).setHeight(40);
                ((TextView)mPagerTitle.getChildAt(i)).setWidth(125);
                ((TextView)mPagerTitle.getChildAt(i)).setGravity(Gravity.CENTER);
                final int finalI = i;
                ((TextView)mPagerTitle.getChildAt(i)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int current = mViewPager.getCurrentItem();
                        int clicked = finalI;
                        if(clicked == 0 && current > 0){
                            mViewPager.setCurrentItem(current-1);
                        }
                        else if(clicked == 2 && current < NB_FRAGMENTS){
                            mViewPager.setCurrentItem(current+1);
                        }
                    }
                });
            }
        }

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {
            }

            @Override
            public void onPageSelected(int i) {
                for(int j=0; j<mPagerTitle.getChildCount(); j++){
                    final int clicked = j;
                    final int current = i;
                    ((TextView)mPagerTitle.getChildAt(j)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(clicked == 0 && current > 0){
                                mViewPager.setCurrentItem(current-1);
                            }
                            else if(clicked == 2 && current < NB_FRAGMENTS){
                                mViewPager.setCurrentItem(current+1);
                            }
                        }
                    });
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });

        mViewPager.setAdapter(mSectionsPagerAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        return fragmentList.get(mViewPager.getCurrentItem()).onContextItemSelected(item);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = new Fragment();
            switch (position) {
                case COMPOSITES:
                    return fragmentList.get(COMPOSITES);
                case SENSORS:
                    return fragmentList.get(SENSORS);
                case GRAPHS:
                    return fragmentList.get(GRAPHS);
                case MEASURES:
                    return fragmentList.get(MEASURES);
                default:
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return NB_FRAGMENTS;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case COMPOSITES:
                    return fragmentNames.get(COMPOSITES);
                case SENSORS:
                    return fragmentNames.get(SENSORS);
                case GRAPHS:
                    return fragmentNames.get(GRAPHS);
                case MEASURES:
                    return fragmentNames.get(MEASURES);
            }
            return null;
        }
    }

    @Override
	public void onMeasureSelected(Uri uri) {
		Intent i = new Intent(this, MeasureActivity.class);
		i.setData(uri);
		startActivity(i);
	}

	@Override
	public void onSensorSelected(Uri uri) {
		Intent i = new Intent(this, MeasuresActivity.class);
		i.setData(Uri.parse(SensAppContract.Measure.CONTENT_URI + "/" + uri.getLastPathSegment()));
		startActivity(i);
	}

	@Override
	public void onCompositeSelected(Uri uri) {
		Intent i = new Intent(getApplicationContext(), CompositeActivity.class);
		i.setData(Uri.parse(SensAppContract.Sensor.CONTENT_URI + "/composite/" + uri.getLastPathSegment()));
		startActivity(i);
	}


    @Override
    public void onGraphSelected(Uri uri) {
        Intent i = new Intent(getApplicationContext(), GraphDisplayerActivity.class);
        i.setData(uri);
        startActivity(i);
    }


}