package com.synergy.android.timetable.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.synergy.android.timetable.R;
import com.synergy.android.timetable.TimetableApplication;
import com.synergy.android.timetable.adapters.LessonAdapter;
import com.synergy.android.timetable.domains.Day;

public class DayListFragment extends ListFragment {
    private int weekIndex;
    private int dayIndex;
    private Day day;
    
    private View progressView;
    private LessonAdapter adapter;
    
    private TimetableApplication app;
    private BroadcastReceiver receiver;
    
    public static DayListFragment createInstance(int weekIndex, int dayIndex) {
        DayListFragment fragment = new DayListFragment();
        fragment.weekIndex = weekIndex;
        fragment.dayIndex = dayIndex;
        return fragment;
    }
    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        app = (TimetableApplication) activity.getApplication();
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_day, null);
        progressView = view.findViewById(R.id.fragmentDayProgressRelativeLayout);
        if (day != null) {
            progressView.setVisibility(View.GONE);
        }
        return view;
    }
    
    private static final String KEY_DAY_INDEX = "dayIndex";
    private static final String KEY_WEEK_INDEX = "weekIndex";
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
        if (savedInstanceState != null) {
            weekIndex = savedInstanceState.getInt(KEY_WEEK_INDEX);
            dayIndex = savedInstanceState.getInt(KEY_DAY_INDEX);
        }
        
        if (app.getWeeks() != null) {
            day = app.getWeek(weekIndex).days[dayIndex];
            populateData();
        }
    }
    
    @Override
    public void onResume() {
        super.onResume();
        if (receiver == null) {
            receiver = new DataLoadedBroadcastReceiver();
            app.registerReceiver(receiver);
        }
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(KEY_WEEK_INDEX, weekIndex);
        outState.putInt(KEY_DAY_INDEX, dayIndex);
        
        if (receiver != null) {
            app.unregisterReceiver(receiver);
            receiver = null;
        }
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        adapter.unsubcribeAll();
    }
    
    private void populateData() {
        if (adapter != null) {
            adapter.unsubcribeAll();
        }
        adapter = new LessonAdapter(getActivity(), day, app.getBeginTimes(), app.getEndTimes());
        setListAdapter(adapter);
        progressView.setVisibility(View.GONE);
    }
    
    private class DataLoadedBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(TimetableApplication.ACTION_DATA_LOADING)) {
                progressView.setVisibility(View.VISIBLE);
            } else if (action.equals(TimetableApplication.ACTION_DATA_LOADED) &&
                    app.getWeeks() != null) {
                day = app.getWeek(weekIndex).days[dayIndex];
                populateData();
            }
        }
    }
}
