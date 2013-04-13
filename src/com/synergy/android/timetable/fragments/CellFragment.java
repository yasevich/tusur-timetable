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
    private int lessonIndex;
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
        String tag = getTag();
        if (tag.contains("h")) {
            lessonIndex = Integer.parseInt(tag.substring(2)) - 1;
        } else {
            primaryKey = Lesson.PrimaryKey.getPrimaryKey(tag);
        }
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
        result.line1 = (TextView) result.findViewById(R.id.fragmentCellLine1TextView);
        result.line2 = (TextView) result.findViewById(R.id.fragmentCellLine2TextView);
        result.defaultTextColor = result.line1.getCurrentTextColor();
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
        if (primaryKey == null) {
            viewHolder.line1.setText(app.getBeginTimes()[lessonIndex]);
            viewHolder.line2.setText(app.getEndTimes()[lessonIndex]);
        } else {
            Lesson l = app.getWeek(primaryKey.getWeek()).days[primaryKey.getDay()]
                    .lessons[primaryKey.getLesson()];
            if (l.subject != null) {
                viewHolder.line1.setText(l.subjectShort);
                viewHolder.line2.setText(l.classroomShort);
                
                if (!l.enabled) {
                    switchTextColors(viewHolder, viewHolder.root.getContext().getResources()
                            .getColor(R.color.data_empty));
                }
                
                int shape = TimetableApplication.getLessonTypeIndex(l.kind);
                if (shape != -1) {
                    shape = TimetableApplication.LESSON_SHAPES[shape];
                    viewHolder.root.setBackgroundResource(shape);
                }
            } else {
                viewHolder.root.setBackgroundResource(R.drawable.cell_borders);
            }
        }
    }
    
    private static void switchTextColors(ViewHolder viewHolder, int color) {
        if (color == -1) {
            color = viewHolder.defaultTextColor;
        }
        viewHolder.line1.setTextColor(color);
        viewHolder.line2.setTextColor(color);
    }
    
    private static class ViewHolder {
        private View root;
        private TextView line1;
        private TextView line2;
        private int defaultTextColor;
        
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
