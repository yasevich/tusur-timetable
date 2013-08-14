package com.synergy.android.timetable.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.synergy.android.timetable.R;
import com.synergy.android.timetable.TimetableApplication;
import com.synergy.android.timetable.adapters.LessonAdapter;
import com.synergy.android.timetable.domains.Day;
import com.synergy.android.timetable.events.DataIsBeingLoaded;
import com.synergy.android.timetable.events.DataIsLoaded;
import com.synergy.android.timetable.events.Event;
import com.synergy.android.timetable.events.LessonStateChanged;
import com.synergy.android.timetable.events.Observer;

public class DayListFragment extends ListFragment {
    private int weekIndex;
    private int dayIndex;
    private Day day;
    
    private View listView;
    private View progressView;
    private int animTime;
    
    private TimetableApplication app;
    private Observer observer;
    
    private LessonAdapter adapter;
    
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
        listView = view.findViewById(R.id.fragmentDayListLinearLayout);
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
        observer = new DataChangedObserver();
        app.getEventBus().subscribe(observer);
        
        animTime = getResources().getInteger(android.R.integer.config_mediumAnimTime);
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
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(KEY_WEEK_INDEX, weekIndex);
        outState.putInt(KEY_DAY_INDEX, dayIndex);
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        app.getEventBus().unsubscribe(observer);
    }
    
    private void populateData() {
        adapter = new LessonAdapter(getActivity(), day, app.getBeginTimes(), app.getEndTimes());
        setListAdapter(adapter);
        showContentOrLoadingIndicator(true);
    }
    
    @SuppressLint("NewApi")
    private void showContentOrLoadingIndicator(boolean contentLoaded) {
        final View showView = contentLoaded ? listView : progressView;
        final View hideView = contentLoaded ? progressView : listView;
            
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            showView.setAlpha(0f);
            showView.setVisibility(View.VISIBLE);
            
            showView.animate()
                    .alpha(1f)
                    .setDuration(animTime)
                    .setListener(null);
            
            hideView.animate()
                    .alpha(0f)
                    .setDuration(animTime)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            hideView.setVisibility(View.GONE);
                        }
                    });
        } else {
            showView.setVisibility(View.VISIBLE);
            hideView.setVisibility(View.GONE);
        }
    }
    
    private class DataChangedObserver extends Observer {
        public DataChangedObserver() {
            subscribe(new LessonStateChanged());
            subscribe(new DataIsBeingLoaded());
            subscribe(new DataIsLoaded());
        }
        
        @Override
        public void handleEvent(Event event) {
            if (event instanceof LessonStateChanged) {
                LessonStateChanged e = (LessonStateChanged) event;
                int week = e.getPrimaryKey().getWeek();
                int day = e.getPrimaryKey().getDay();
                if (week == weekIndex && day == dayIndex) {
                    adapter.switchPosition(e.getPrimaryKey().getLesson());
                }
            } else if (event instanceof DataIsBeingLoaded) {
                showContentOrLoadingIndicator(false);
            } else if (event instanceof DataIsLoaded && app.getWeeks() != null) {
                day = app.getWeek(weekIndex).days[dayIndex];
                populateData();
            }
        }
    }
}
