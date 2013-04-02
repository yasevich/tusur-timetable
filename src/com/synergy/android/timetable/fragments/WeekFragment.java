package com.synergy.android.timetable.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.synergy.android.timetable.R;
import com.synergy.android.timetable.TimetableApplication;
import com.synergy.android.timetable.adapters.MainFragmentPagerAdapter;

public class WeekFragment extends Fragment {
    private int weekIndex;
    private String[] weekDays;
    private int currentDay;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        weekIndex = args.getInt(TimetableApplication.EXTRA_WEEK);
        weekDays = args.getStringArray(TimetableApplication.EXTRA_WEEK_DAYS);
        currentDay = args.getInt(TimetableApplication.EXTRA_DAY);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_week, null);
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FragmentPagerAdapter adapter = new MainFragmentPagerAdapter(this, weekIndex, weekDays);
        ViewPager pager = (ViewPager) getView().findViewById(R.id.activityMainViewPager);
        pager.setAdapter(adapter);
        if (currentDay != -1) {
            pager.setCurrentItem(currentDay);
        }
        pager.setOffscreenPageLimit(TimetableApplication.NUMBER_OF_LESSONS);
    }
}
