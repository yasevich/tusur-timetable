package com.synergy.android.timetable.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.synergy.android.timetable.ApplicationSettings;
import com.synergy.android.timetable.R;
import com.synergy.android.timetable.TimetableApplication;
import com.synergy.android.timetable.domains.Day;
import com.synergy.android.timetable.domains.Lesson;
import com.synergy.android.timetable.listeners.LessonOnClickListener;
import com.synergy.android.timetable.listeners.SwitchableView;
import com.synergy.android.timetable.utils.Common;
import com.synergy.android.timetable.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class LessonAdapter extends BaseAdapter {
    private Context context;
    private Lesson[] lessons;
    private String[] begins;
    private String[] ends;
    private ViewHolder[] views;
    
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
        ViewBuilder builder = new ViewBuilder(inflater.inflate(R.layout.list_item_lesson,
                parent, false));
        views[position] = builder.build(lessons[position], begins[position], ends[position]);
        return views[position].root;
    }
    
    public void switchPosition(int position) {
        int index = 0;
        for (int i = 0; i < lessons.length; ++i) {
            if (lessons[i].getPrimaryKey().getLesson() == position) {
                index = i;
            }
        }
        if (views[index] != null) {
            Common.switchView(views[index], lessons[index].enabled);
        }
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
        if (lessons != null) {
            views = new ViewHolder[lessons.length];
        }
    }
    
    private static class ViewBuilder {
        private ViewHolder viewHolder;
        
        public ViewBuilder(View view) {
            viewHolder = new ViewHolder(view);
        }
        
        public ViewHolder build(Lesson lesson, String beginTime, String endTime) {
            viewHolder.beginTime.setText(beginTime);
            viewHolder.endTime.setText(endTime);
            
            if (lesson.subject == null) {
                viewHolder.root.setOnClickListener(null);
                viewHolder.exists.setVisibility(View.GONE);
                viewHolder.empty.setVisibility(View.VISIBLE);
            } else {
                viewHolder.root.setOnClickListener(new LessonOnClickListener(lesson));
                
                int color = TimetableApplication.getLessonTypeIndex(lesson.kindTitle);
                if (color != TimetableApplication.UNKNOWN_LESSON_TYPE) {
                    color = TimetableApplication.LESSON_COLORS[color];
                    viewHolder.root.setBackgroundResource(color);
                }
                
                viewHolder.subject.setText(lesson.subject);
                viewHolder.kind.setText(lesson.kindTitle);
                if (!lesson.enabled) {
                    viewHolder.switchTextColor(TimetableApplication.getInstance()
                            .getDataEmptyColor());
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
            
            return viewHolder;
        }
    }
    
    private static class ViewHolder implements SwitchableView {
        private View root;
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
        
        public ViewHolder(View view) {
            this.root = view;
            exists = (LinearLayout) view.findViewById(R.id.listItemLessonExistsLinearLayout);
            beginTime = (TextView) view.findViewById(R.id.listItemLessonBeginTimeTextView);
            endTime = (TextView) view.findViewById(R.id.listItemLessonEndTimeTextView);
            subject = (TextView) view.findViewById(R.id.listItemLessonSubjectTextView);
            kind = (TextView) view.findViewById(R.id.listItemLessonKindTextView);
            classroom = (TextView) view.findViewById(R.id.listItemLessonClassroomTextView);
            teacher = (TextView) view.findViewById(R.id.listItemLessonTeacherTextView);
            note = (TextView) view.findViewById(R.id.listItemLessonNoteTextView);
            empty = (TextView) view.findViewById(R.id.listItemLessonEmptyTextView);
            defaultTextColor = subject.getCurrentTextColor();
        }
        
        @Override
        public void switchTextColor(int color) {
            if (color == DEFAULT_COLOR) {
                color = defaultTextColor;
            }
            subject.setTextColor(color);
            kind.setTextColor(color);
            classroom.setTextColor(color);
            teacher.setTextColor(color);
            note.setTextColor(color);
        }
    }
}
