package com.synergy.android.timetable.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.synergy.android.timetable.R;
import com.synergy.android.timetable.TimetableApplication;

public class TableFragment extends Fragment {
    private int resource;
    
    public static TableFragment createInstance(int week) {
        TableFragment fragment = new TableFragment();
        if (week == TimetableApplication.WEEK_ODD) {
            fragment.resource = R.layout.fragment_week_odd;
        } else {
            fragment.resource = R.layout.fragment_week_even;
        }
        return fragment;
    }
    
    private static final String KEY_RESOURCE = "resource";
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            resource = savedInstanceState.getInt(KEY_RESOURCE);
        }
        return inflater.inflate(resource, null);
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(KEY_RESOURCE, resource);
    }
}
