package com.synergy.android.timetable.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.synergy.android.timetable.R;
import com.synergy.android.timetable.TimetableApplication;
import com.synergy.android.timetable.domains.Lesson;

public class CellFragment extends Fragment {
    private ViewHolder viewHolder;
    
    private TimetableApplication app;
    private Lesson.PrimaryKey primaryKey;
    private BroadcastReceiver receiver;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_cell, null);
        viewHolder = initViews(root);
        return root;
    }
    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        app = (TimetableApplication) activity.getApplication();
        primaryKey = Lesson.PrimaryKey.getPrimaryKey(getTag());
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        registerDateLoadedBroadcastReceiver();
        if (app.getWeeks() != null) {
            populateData();
        }
    }
    
    private ViewHolder initViews(View root) {
        ViewHolder result = new ViewHolder(root);
        result.subjectShort = (TextView) result.findViewById(R.id.fragmentCellSubjectShortTextView);
        result.classroomShort = (TextView) result.findViewById(
                R.id.fragmentCellClassroomShortTextView);
        return result;
    }
    
    private void registerDateLoadedBroadcastReceiver() {
        app.getBackgroundExecutor().execute(new Runnable() {
            @Override
            public void run() {
                if (receiver == null) {
                    receiver = new DataLoadedBroadcastReceiver();
                    IntentFilter filter = new IntentFilter();
                    filter.addAction(TimetableApplication.ACTION_DATA_LOADED);
                    app.registerReceiver(receiver, filter);
                }
            }
        });
    }
    
    private void populateData() {
        if (primaryKey != null) {
            Lesson l = app.getWeek(primaryKey.getWeek()).days[primaryKey.getDay()]
                    .lessons[primaryKey.getLesson()];
            if (l.subject != null) {
                viewHolder.subjectShort.setText(l.subjectShort);
                viewHolder.classroomShort.setText(l.classroomShort);
                
                int color = TimetableApplication.getBgColor(l.kind);
                if (color != -1) {
                    viewHolder.root.setBackgroundResource(color);
                }
            }
        }
    }
    
    private static class ViewHolder {
        private View root;
        private TextView subjectShort;
        private TextView classroomShort;
        
        public ViewHolder(View root) {
            this.root = root;
        }
        
        public View findViewById(int id) {
            return root.findViewById(id);
        }
    }
    
    private class DataLoadedBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(TimetableApplication.ACTION_DATA_LOADED) &&
                    app.getWeeks() != null) {
                populateData();
            }
        }
    }
}
