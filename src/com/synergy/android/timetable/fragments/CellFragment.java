package com.synergy.android.timetable.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.synergy.android.timetable.R;
import com.synergy.android.timetable.TimeStruct;
import com.synergy.android.timetable.TimetableApplication;
import com.synergy.android.timetable.domains.Kind;
import com.synergy.android.timetable.domains.Lesson;
import com.synergy.android.timetable.events.Event;
import com.synergy.android.timetable.events.LessonStateChanged;
import com.synergy.android.timetable.events.Observer;
import com.synergy.android.timetable.listeners.LessonOnClickListener;
import com.synergy.android.timetable.listeners.SwitchableView;
import com.synergy.android.timetable.utils.Common;

public class CellFragment extends Fragment {
    private ViewHolder viewHolder;
    
    private Lesson.PrimaryKey primaryKey;
    private int lessonIndex;

    private TimetableApplication app;
    private BroadcastReceiver receiver;
    private Observer observer;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.cell, null);
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
        TimetableApplication.getInstance().getEventBus().unsubscribe(observer);
    }
    
    private void populateData() {
        if (primaryKey == null) {
            viewHolder.line1.setText(app.getBeginTimes()[lessonIndex]);
            viewHolder.line2.setText(app.getEndTimes()[lessonIndex]);
            if (viewHolder.isTablet()) {
                viewHolder.line1.setTypeface(null, Typeface.BOLD);
                viewHolder.line2.setTypeface(null, Typeface.BOLD);
                viewHolder.line3.setVisibility(View.GONE);
                viewHolder.line4.setVisibility(View.GONE);
            }
        } else {
            Lesson l = app.getWeek(primaryKey.getWeek()).days[primaryKey.getDay()]
                    .lessons[primaryKey.getLesson()];
            if (l.subject != null) {
                viewHolder.root.setOnClickListener(new LessonOnClickListener(l));
                observer = new LessonChangedObserver(l, viewHolder);
                TimetableApplication.getInstance().getEventBus().subscribe(observer);
                
                if (viewHolder.isTablet()) {
                    viewHolder.line1.setText(l.subjectShort);
                    viewHolder.line2.setText(l.kindShort);
                    viewHolder.line3.setText(l.classroomShort);
                    viewHolder.line4.setText(l.teacherShort);
                } else {
                    viewHolder.line1.setText(l.subjectShort);
                    viewHolder.line2.setText(l.classroomShort);
                }
                
                if (!l.enabled) {
                    viewHolder.switchTextColor(app.getDataEmptyColor());
                }
                
                if (l.kind != Kind.UNKNOWN) {
                    viewHolder.root.setBackgroundResource(
                            TimetableApplication.LESSON_SHAPES[l.kind.ordinal()]);
                }
            } else {
                TimeStruct time = app.getTimestamp();
                if (time.week == primaryKey.getWeek() && time.day == primaryKey.getDay()) {
                    viewHolder.root.setBackgroundResource(R.drawable.cell_type_currentday);
                } else {
                    viewHolder.root.setBackgroundResource(R.drawable.cell_borders);
                }
            }
        }
    } 
    
    private class DataLoadedBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TimetableApplication.ACTION_DATA_LOADED.equals(action)) {
                populateData();
                app.unregisterReceiver(receiver);
                receiver = null;
            }
        }
    }
    
    private class LessonChangedObserver extends Observer {
        private Lesson lesson;
        private SwitchableView view;
        
        public LessonChangedObserver(Lesson lesson, SwitchableView view) {
            this.lesson = lesson;
            this.view = view;
            subscribe(new LessonStateChanged());
        }
        
        @Override
        public void handleEvent(Event event) {
            if (event instanceof LessonStateChanged) {
                LessonStateChanged e = (LessonStateChanged) event;
                if (lesson.getPrimaryKey().hashCode() == e.getPrimaryKey().hashCode()) {
                    Common.switchView(view, e.isEnabled());
                }
            }
        }
    }
    
    private static class ViewHolder implements SwitchableView {
        private View root;
        private TextView line1;
        private TextView line2;
        private TextView line3;
        private TextView line4;
        private int defaultTextColor;
        
        public ViewHolder(View root) {
            this.root = root;
            line1 = (TextView) root.findViewById(R.id.fragmentCellLine1TextView);
            line2 = (TextView) root.findViewById(R.id.fragmentCellLine2TextView);
            line3 = (TextView) root.findViewById(R.id.fragmentCellLine3TextView);
            line4 = (TextView) root.findViewById(R.id.fragmentCellLine4TextView);
            defaultTextColor = line1.getCurrentTextColor();
        }

        @Override
        public void switchTextColor(int color) {
            if (color == DEFAULT_COLOR) {
                color = defaultTextColor;
            }
            line1.setTextColor(color);
            line2.setTextColor(color);
            if (isTablet()) {
                line3.setTextColor(color);
                line4.setTextColor(color);
            }
        }
        
        public boolean isTablet() {
            return line3 != null && line4 != null;
        }
    }
}
