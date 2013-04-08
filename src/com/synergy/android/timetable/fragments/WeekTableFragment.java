package com.synergy.android.timetable.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.synergy.android.timetable.R;
import com.synergy.android.timetable.TimetableApplication;

public class WeekTableFragment extends Fragment {
    private int resource;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args.getInt(TimetableApplication.EXTRA_WEEK) == TimetableApplication.WEEK_EVEN) {
            resource = R.layout.fragment_week_even;
        } else {
            resource = R.layout.fragment_week_odd;
        }
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(resource, null);
    }
}
