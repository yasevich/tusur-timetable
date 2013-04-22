package com.synergy.android.timetable.listeners;

import android.view.View;
import android.view.View.OnClickListener;

import com.synergy.android.timetable.R;
import com.synergy.android.timetable.TimetableApplication;
import com.synergy.android.timetable.domains.Lesson;
import com.synergy.android.timetable.events.Event;
import com.synergy.android.timetable.events.LessonStateChanged;
import com.synergy.android.timetable.events.Subscriber;

public class LessonOnClickListener extends Subscriber implements OnClickListener {
    private Lesson lesson;
    private SwitchableView view;
    
    public LessonOnClickListener(Lesson lesson, SwitchableView view) {
        this.lesson = lesson;
        this.view = view;
    }
    
    @Override
    public void onClick(View v) {
        if (lesson.enabled) {
            int color = v.getContext().getResources().getColor(R.color.data_empty);
            view.switchTextColor(color);
        } else {
            view.switchTextColor(SwitchableView.DEFAULT_COLOR);
        }
        
        final TimetableApplication app = TimetableApplication.getInstance();
        app.getBackgroundExecutor().execute(new Runnable() {
            @Override
            public void run() {
                lesson.enabled = !lesson.enabled;
                app.updateLesson(lesson);
                TimetableApplication.onDataUpdated(app);
            }
        });
        app.getEventBus().fireEvent(new LessonStateChanged(this, lesson.getPrimaryKey(),
                lesson.enabled));
    }

    @Override
    public void handleEvent(Event event) {
        if (event instanceof LessonStateChanged) {
            LessonStateChanged e = (LessonStateChanged) event;
            if (e.getPrimaryKey().hashCode() == lesson.getPrimaryKey().hashCode()) {
                TimetableApplication app = TimetableApplication.getInstance();
                int color = e.isEnabled() ? -1 : app.getDataEmptyColor();
                view.switchTextColor(color);
            }
        }
    }
}
