package org.sensapp.android.sensappdroid.activities;

import java.util.HashMap;

import org.sensapp.android.sensappdroid.R;
import org.sensapp.android.sensappdroid.contract.SensAppContract;
import org.sensapp.android.sensappdroid.fragments.CompositeListFragment;
import org.sensapp.android.sensappdroid.fragments.CompositeListFragment.OnCompositeSelectedListener;
import org.sensapp.android.sensappdroid.fragments.MeasureListFragment;
import org.sensapp.android.sensappdroid.fragments.MeasureListFragment.OnMeasureSelectedListener;
import org.sensapp.android.sensappdroid.fragments.SensorListFragment;
import org.sensapp.android.sensappdroid.fragments.SensorListFragment.OnSensorSelectedListener;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TabHost;

public class TabsActivity extends Activity implements OnCompositeSelectedListener, OnSensorSelectedListener, OnMeasureSelectedListener {
    
	private TabHost tabHost;
    private TabManager tabManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_tabs);
        
        tabHost = (TabHost) findViewById(android.R.id.tabhost);
        tabHost.setup();

        tabManager = new TabManager(this, tabHost, R.id.realtabcontent);

        tabManager.addTab(tabHost.newTabSpec("Composites").setIndicator("Composites"), CompositeListFragment.class, null);
        tabManager.addTab(tabHost.newTabSpec("Sensors").setIndicator("Sensors"), SensorListFragment.class, null);
        tabManager.addTab(tabHost.newTabSpec("Measures").setIndicator("Measures"), MeasureListFragment.class, null);

        if (savedInstanceState != null) {
            tabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("tab", tabHost.getCurrentTabTag());
    }

    public static class TabManager implements TabHost.OnTabChangeListener {
        
    	private final Activity tabActivity;
        private final TabHost tabHost;
        private final int containerId;
        private final HashMap<String, TabInfo> tabs = new HashMap<String, TabInfo>();
        private TabInfo lastTab;

        static final class TabInfo {
            private final String tag;
            private final Class<?> clss;
            private final Bundle args;
            private Fragment fragment;
            TabInfo(String tag, Class<?> clss, Bundle args) {
                this.tag = tag;
                this.clss = clss;
                this.args = args;
            }
        }

        static class DummyTabFactory implements TabHost.TabContentFactory {
        	private final Context context;
            public DummyTabFactory(Context context) {
                this.context = context;
            }
            @Override
            public View createTabContent(String tag) {
                View v = new View(context);
                v.setMinimumWidth(0);
                v.setMinimumHeight(0);
                return v;
            }
        }

        public TabManager(Activity activity, TabHost tabHost, int containerId) {
            this.tabActivity = activity;
            this.tabHost = tabHost;
            this.containerId = containerId;
            this.tabHost.setOnTabChangedListener(this);
        }

        public void addTab(TabHost.TabSpec tabSpec, Class<?> clss, Bundle args) {
            tabSpec.setContent(new DummyTabFactory(tabActivity));
            String tag = tabSpec.getTag();

            TabInfo info = new TabInfo(tag, clss, args);
            info.fragment = tabActivity.getFragmentManager().findFragmentByTag(tag);

            // Check to see if we already have a fragment for this tab, probably
            // from a previously saved state.  If so, deactivate it, because our
            // initial state is that a tab isn't shown.
            if (info.fragment != null && !info.fragment.isDetached()) {
                FragmentTransaction ft = tabActivity.getFragmentManager().beginTransaction();
                ft.detach(info.fragment);
                ft.commit();
            }

            tabs.put(tag, info);
            tabHost.addTab(tabSpec);
        }

        @Override
        public void onTabChanged(String tabId) {
            TabInfo newTab = tabs.get(tabId);
            if (lastTab != newTab) {
                FragmentTransaction ft = tabActivity.getFragmentManager().beginTransaction();
                if (lastTab != null && lastTab.fragment != null) {
                        ft.detach(lastTab.fragment);
                }
                if (newTab != null) {
                    if (newTab.fragment == null) {
                        newTab.fragment = Fragment.instantiate(tabActivity, newTab.clss.getName(), newTab.args);
                        ft.add(containerId, newTab.fragment, newTab.tag);
                    } else {
                        ft.attach(newTab.fragment);
                    }
                }

                lastTab = newTab;
                ft.commit();
                tabActivity.getFragmentManager().executePendingTransactions();
            }
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
}

