package com.synergy.android.timetable.listeners;

import android.view.View;
import android.view.View.OnClickListener;

import com.synergy.android.timetable.TimetableApplication;
import com.synergy.android.timetable.domains.Lesson;
import com.synergy.android.timetable.events.LessonStateChanged;

public class LessonOnClickListener implements OnClickListener {
    private Lesson lesson;
    
    public LessonOnClickListener(Lesson lesson) {
        this.lesson = lesson;
    }
    
    @Override
    public void onClick(View v) {
        final TimetableApplication app = TimetableApplication.getInstance();
        lesson.changeState();
        app.getEventBus().fireEvent(new LessonStateChanged(lesson));
        app.getBackgroundExecutor().execute(new Runnable() {
            @Override
            public void run() {
                app.updateLesson(lesson);
                TimetableApplication.onDataUpdated(app);
            }
        });
    }
}
