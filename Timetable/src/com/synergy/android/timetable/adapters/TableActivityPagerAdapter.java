package com.synergy.android.timetable.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.synergy.android.timetable.TimetableApplication;
import com.synergy.android.timetable.fragments.TableFragment;

public class TableActivityPagerAdapter extends FragmentPagerAdapter {
    private String[] weeks;
    
    public TableActivityPagerAdapter(FragmentManager fm, String[] weeks) {
        super(fm);
        this.weeks = weeks; 
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return TableFragment.createInstance(TimetableApplication.WEEK_ODD);
        } else {
            return TableFragment.createInstance(TimetableApplication.WEEK_EVEN);
        }
    }

    @Override
    public int getCount() {
        return weeks.length;
    }
    
    @Override
    public CharSequence getPageTitle(int position) {
        return weeks[position];
    }
}
