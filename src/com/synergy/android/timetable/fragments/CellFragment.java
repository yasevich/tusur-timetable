package com.synergy.android.timetable.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.synergy.android.timetable.R;
import com.synergy.android.timetable.TimetableApplication;
import com.synergy.android.timetable.domains.Lesson;
import com.synergy.android.timetable.events.LessonStateChanged;
import com.synergy.android.timetable.listeners.LessonOnClickListener;
import com.synergy.android.timetable.listeners.SwitchableView;

public class CellFragment extends Fragment {
    private ViewHolder viewHolder;
    
    private Lesson.PrimaryKey primaryKey;
    private int lessonIndex;

    private TimetableApplication app;
    private BroadcastReceiver receiver;
    private LessonOnClickListener listener;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_cell, null);
        viewHolder = new ViewHolder(root);
        return root;
    }
    
    @Override
    public void onResume() {
        super.onResume();
        
        String tag = getTag();
        if (tag.contains("h")) {
            lessonIndex = Integer.parseInt(tag.substring(2)) - 1;
        } else {
            primaryKey = Lesson.PrimaryKey.getPrimaryKey(tag);
        }
        
        app = TimetableApplication.getInstance();
        if (app.getWeeks() == null) {
            receiver = new DataLoadedBroadcastReceiver();
            app.registerReceiver(receiver);
            app.loadCache();
        } else {
            populateData();
        }
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (listener != null) {
            app.getEventBus().unsubscribe(listener);
            listener = null;
        }
    }
    
    private void populateData() {
        if (primaryKey == null) {
            viewHolder.line1.setText(app.getBeginTimes()[lessonIndex]);
            viewHolder.line2.setText(app.getEndTimes()[lessonIndex]);
        } else {
            Lesson l = app.getWeek(primaryKey.getWeek()).days[primaryKey.getDay()]
                    .lessons[primaryKey.getLesson()];
            if (l.subject != null) {
                if (listener == null) {
                    listener = new LessonOnClickListener(l, viewHolder);
                    listener.subscribe(new LessonStateChanged());
                    app.getEventBus().subscribe(listener);
                }
                
                viewHolder.root.setOnClickListener(listener);
                viewHolder.line1.setText(l.subjectShort);
                viewHolder.line2.setText(l.classroomShort);
                
                if (!l.enabled) {
                    viewHolder.switchTextColor(app.getDataEmptyColor());
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
    
    private class DataLoadedBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(TimetableApplication.ACTION_DATA_LOADED)) {
                populateData();
                app.unregisterReceiver(receiver);
                receiver = null;
            }
        }
    }
    
    private static class ViewHolder implements SwitchableView {
        private View root;
        private TextView line1;
        private TextView line2;
        private int defaultTextColor;
        
        public ViewHolder(View root) {
            this.root = root;
            line1 = (TextView) root.findViewById(R.id.fragmentCellLine1TextView);
            line2 = (TextView) root.findViewById(R.id.fragmentCellLine2TextView);
            defaultTextColor = line1.getCurrentTextColor();
        }

        @Override
        public void switchTextColor(int color) {
            if (color == -1) {
                color = defaultTextColor;
            }
            line1.setTextColor(color);
            line2.setTextColor(color);
        }
    }
}
