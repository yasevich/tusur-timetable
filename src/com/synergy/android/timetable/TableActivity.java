package com.synergy.android.timetable;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;

import com.synergy.android.timetable.adapters.TableActivityPagerAdapter;

public class TableActivity extends FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_week);
        
        initViews();
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            finish();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    static void startActivity(Activity from) {
        if (from == null) {
            throw new NullPointerException("The activity should not be null.");
        }
        
        Intent intent = new Intent(from, TableActivity.class);
        from.startActivity(intent);
    }
    
    @SuppressLint("NewApi")
    private void initViews() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        
        final String[] weeks = new String[] {
                getString(R.string.fragment_week_odd),
                getString(R.string.fragment_week_even)
        };
        FragmentPagerAdapter adapter = new TableActivityPagerAdapter(getSupportFragmentManager(),
                weeks);
        ViewPager pager = (ViewPager) findViewById(R.id.fragmentWeekViewPager);
        pager.setAdapter(adapter);
    }
}
