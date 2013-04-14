package com.synergy.android.timetable;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.Menu;
import android.view.MenuItem;

import com.synergy.android.timetable.fragments.WeekFragment;
import com.synergy.android.timetable.utils.StringUtils;

public class MainActivity extends FragmentActivity {
    private ApplicationSettings settings;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initViews();
        loadData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.action_update:
            TimetableApplication app = (TimetableApplication) getApplication();
            app.loadWebData();
            return true;
        case R.id.action_table:
            TableActivity.startActivity(this);
            return true;
        case R.id.action_group:
            GroupActivity.startActivityForResult(this);
            return true;
        case R.id.action_settings:
            SettingsActivity.startActivityForResult(this);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GroupActivity.REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                TimetableApplication app = (TimetableApplication) getApplication();
                app.loadWebData();
            } else if (resultCode == RESULT_CANCELED &&
                    StringUtils.isNullOrEmpty(settings.getGroup())) {
                finish();
            }
        } else if (requestCode == SettingsActivity.REQUEST_CODE &&
                resultCode == SettingsActivity.RESULT_EMPTY_CHANGED) {
            TimetableApplication app = (TimetableApplication) getApplication();
            app.loadCache();
        }
    }
    
    public static void startActivity(Context context, int flags) {
        if (context == null) {
            throw new NullPointerException("Context shoul not be null.");
        }
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(flags);
        context.startActivity(intent);
    }
    
    private void initViews() {
        FragmentTabHost tabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        tabHost.setup(this, getSupportFragmentManager(), R.id.realTabContent);

        TimetableApplication app = (TimetableApplication) getApplication();
        TimeStruct time = new TimeStruct(app.getTimestamp());
        
        if (time.week == TimetableApplication.WEEK_ODD) {
            addTabSpec(tabHost, getString(R.string.fragment_week_odd),
                    TimetableApplication.WEEK_ODD, time.day);
            addTabSpec(tabHost, getString(R.string.fragment_week_even),
                    TimetableApplication.WEEK_EVEN, -1);
            tabHost.setCurrentTab(TimetableApplication.WEEK_ODD);
        } else {
            addTabSpec(tabHost, getString(R.string.fragment_week_odd),
                    TimetableApplication.WEEK_ODD, -1);
            addTabSpec(tabHost, getString(R.string.fragment_week_even),
                    TimetableApplication.WEEK_EVEN, time.day);
            tabHost.setCurrentTab(TimetableApplication.WEEK_EVEN);
        }
    }
    
    private void loadData() {
        settings = ApplicationSettings.getInstance(this);
        String group = settings.getGroup();
        if (StringUtils.isNullOrEmpty(group)) {
            GroupActivity.startActivityForResult(this);
        } else {
            TimetableApplication app = (TimetableApplication) getApplication();
            app.loadCache();
        }
    }
    
    private void addTabSpec(FragmentTabHost tabHost, String tag, int weekIndex, int currentDay) {
        Bundle bundle = new Bundle();
        bundle.putInt(TimetableApplication.EXTRA_WEEK, weekIndex);
        bundle.putInt(TimetableApplication.EXTRA_DAY, currentDay);
        tabHost.addTab(tabHost.newTabSpec(tag).setIndicator(tag), WeekFragment.class, bundle);
    }
}
