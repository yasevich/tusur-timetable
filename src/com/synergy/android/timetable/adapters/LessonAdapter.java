package com.synergy.android.timetable.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.synergy.android.timetable.ApplicationSettings;
import com.synergy.android.timetable.R;
import com.synergy.android.timetable.ScheduleBroadcastReceiver;
import com.synergy.android.timetable.TimetableApplication;
import com.synergy.android.timetable.domains.Day;
import com.synergy.android.timetable.domains.Lesson;
import com.synergy.android.timetable.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class LessonAdapter extends BaseAdapter {
    private Context context;
    private Lesson[] lessons;
    private String[] begins;
    private String[] ends;
    
    public LessonAdapter(Context context, Day day, String[] begins, String[] ends) {
        this.context = context;
        this.lessons = day.isEmpty() ? null : day.lessons;
        this.begins = begins;
        this.ends = ends;
        prepareArrays();
    }
    
    @Override
    public int getCount() {
        if (lessons == null) return 0;
        return lessons.length;
    }

    @Override
    public Object getItem(int position) {
        return lessons[position];
    }

    @Override
    public long getItemId(int position) {
        return lessons[position].getPrimaryKey().hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        ViewBuilder builder = new ViewBuilder(inflater.inflate(R.layout.list_item_lesson, parent,
                false));
        return builder.build(lessons[position], begins[position], ends[position]);
    }
    
    private void prepareArrays() {
        ApplicationSettings settings = ApplicationSettings.getInstance(context);
        if (!settings.showEmptyLessons() && lessons != null) {
            List<Lesson> lessonsList = new ArrayList<Lesson>();
            List<String> beginsList = new ArrayList<String>();
            List<String> endsList = new ArrayList<String>();
            for (int i = 0; i < lessons.length; ++i) {
                if (lessons[i].subject != null) {
                    lessonsList.add(lessons[i]);
                    beginsList.add(begins[i]);
                    endsList.add(ends[i]);
                }
            }
            lessons = lessonsList.toArray(new Lesson[lessonsList.size()]);
            begins = beginsList.toArray(new String[lessonsList.size()]);
            ends = endsList.toArray(new String[lessonsList.size()]);
        }
    }
    
    private static class ViewBuilder {
        private View view;
        private ViewHolder viewHolder = new ViewHolder();
        
        public ViewBuilder(View view) {
            this.view = view;
            initViews();
        }
        
        public View build(Lesson lesson, String beginTime, String endTime) {
            viewHolder.beginTime.setText(beginTime);
            viewHolder.endTime.setText(endTime);
            
            if (lesson.subject == null) {
                view.setOnClickListener(null);
                viewHolder.exists.setVisibility(View.GONE);
                viewHolder.empty.setVisibility(View.VISIBLE);
            } else {
                view.setOnClickListener(new ItemOnClickListener(lesson, viewHolder));
                
                int color = TimetableApplication.getBgColor(lesson.kind);
                if (color != -1) {
                    view.setBackgroundResource(color);
                }
                
                viewHolder.subject.setText(lesson.subject);
                viewHolder.kind.setText(lesson.kind);
                if (!lesson.enabled) {
                    color = view.getContext().getResources().getColor(R.color.data_empty);
                    switchTextColors(viewHolder, color);
                }
                
                if (StringUtils.isNullOrEmpty(lesson.classroom)) {
                    viewHolder.classroom.setVisibility(View.GONE);
                } else {
                    viewHolder.classroom.setText(lesson.classroom);
                }
                
                if (StringUtils.isNullOrEmpty(lesson.teacher)) {
                    viewHolder.teacher.setVisibility(View.GONE);
                } else {
                    viewHolder.teacher.setText(lesson.teacher);
                }
                
                if (StringUtils.isNullOrEmpty(lesson.note)) {
                    viewHolder.note.setVisibility(View.GONE);
                } else {
                    viewHolder.note.setText(lesson.note);
                }
            }
            
            return view;
        }
        
        private void initViews() {
            viewHolder.exists = (LinearLayout) view.findViewById(
                    R.id.listItemLessonExistsLinearLayout);
            viewHolder.beginTime = (TextView) view.findViewById(
                    R.id.listItemLessonBeginTimeTextView);
            viewHolder.endTime = (TextView) view.findViewById(
                    R.id.listItemLessonEndTimeTextView);
            viewHolder.subject = (TextView) view.findViewById(
                    R.id.listItemLessonSubjectTextView);
            viewHolder.kind = (TextView) view.findViewById(
                    R.id.listItemLessonKindTextView);
            viewHolder.classroom = (TextView) view.findViewById(
                    R.id.listItemLessonClassroomTextView);
            viewHolder.teacher = (TextView) view.findViewById(
                    R.id.listItemLessonTeacherTextView);
            viewHolder.note = (TextView) view.findViewById(
                    R.id.listItemLessonNoteTextView);
            viewHolder.empty = (TextView) view.findViewById(
                    R.id.listItemLessonEmptyTextView);
            viewHolder.defaultTextColor = viewHolder.subject.getCurrentTextColor();
        }
    }
    
    private static class ViewHolder {
        private LinearLayout exists;
        private TextView beginTime;
        private TextView endTime;
        private TextView subject;
        private TextView kind;
        private TextView classroom;
        private TextView teacher;
        private TextView note;
        private TextView empty;
        private int defaultTextColor;
    }
    
    private static void switchTextColors(ViewHolder viewHolder, int color) {
        if (color == -1) {
            color = viewHolder.defaultTextColor;
        }
        viewHolder.subject.setTextColor(color);
        viewHolder.kind.setTextColor(color);
        viewHolder.classroom.setTextColor(color);
        viewHolder.teacher.setTextColor(color);
        viewHolder.note.setTextColor(color);
    }
    
    private static class ItemOnClickListener implements OnClickListener {
        private Lesson lesson;
        private ViewHolder viewHolder;
        
        public ItemOnClickListener(Lesson lesson, ViewHolder viewHolder) {
            this.lesson = lesson;
            this.viewHolder = viewHolder;
        }
        
        @Override
        public void onClick(View v) {
            if (!lesson.enabled) {
                switchTextColors(viewHolder, -1);
            } else {
                int color = v.getContext().getResources().getColor(R.color.data_empty);
                switchTextColors(viewHolder, color);
            }
            
            final TimetableApplication app = TimetableApplication.getInstance();
            app.getBackgroundExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    lesson.enabled = !lesson.enabled;
                    app.updateLesson(lesson);
                    ScheduleBroadcastReceiver.scheduleAlarmNotificationService(app);
                }
            });
        }
    }
}
