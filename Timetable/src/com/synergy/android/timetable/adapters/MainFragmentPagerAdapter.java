package com.synergy.android.timetable.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;

import com.synergy.android.timetable.TimetableApplication;
import com.synergy.android.timetable.fragments.DayListFragment;

public class MainFragmentPagerAdapter extends FragmentPagerAdapter {
    private int weekIndex;
    private String[] days;
    
    public MainFragmentPagerAdapter(Fragment fragment, int weekIndex) {
        super(fragment.getChildFragmentManager());
        TimetableApplication app = (TimetableApplication) fragment.getActivity().getApplication();
        this.weekIndex = weekIndex;
        this.days = app.getWeekDays();
    }

    @Override
    public Fragment getItem(int position) {
        return DayListFragment.createInstance(weekIndex, position);
    }

    @Override
    public int getCount() {
        return days.length;
    }
    
    @Override
    public CharSequence getPageTitle(int position) {
        return days[position];
    }
}
