package com.synergy.android.timetable.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.synergy.android.timetable.R;
import com.synergy.android.timetable.domains.Day;
import com.synergy.android.timetable.domains.Lesson;
import com.synergy.android.timetable.domains.Week;
import com.synergy.android.timetable.providers.CachedDataProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TeachersListFragment extends ListFragment {
    private Map<String, Teacher> teachers = new HashMap<String, Teacher>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_teachers, null);
    }

    @Override
    public void onResume() {
        super.onResume();
        initTeachers();
        showTeachers();
    }

    private void initTeachers() {
        CachedDataProvider provider = CachedDataProvider.getInstance(getActivity());
        Week[] weeks = provider.getWeeks();

        for (Week week : weeks) {
            for (Day day : week.days) {
                for (Lesson lesson : day.lessons) {
                    if (lesson.teacher == null) {
                        continue;
                    }

                    String[] names = lesson.teacher.split(", ");
                    for (String name : names) {
                        Teacher teacher = teachers.get(name);
                        if (teacher == null) {
                            teacher = new Teacher(name);
                            teachers.put(name, teacher);
                        }
                        teacher.add(lesson.subjectShort);
                    }
                }
            }
        }
    }

    private void showTeachers() {
        List<Teacher> list = new ArrayList<Teacher>(teachers.values());
        Collections.sort(list);
        setListAdapter(new TeachersAdapter(getActivity(), list));
    }

    private static class Teacher implements Comparable<Teacher> {
        public final Set<String> lessons =  new HashSet<String>();
        public String firstName;
        public String middleName;
        public String lastName;

        public Teacher(String name) {
            String[] names = name.split(" ");
            lastName = names[0];
            firstName = names[1];
            middleName = names[2];
        }

        public void add(String lesson) {
            lessons.add(lesson);
        }

        @Override
        public int compareTo(Teacher teacher) {
            int result = lastName.compareTo(teacher.lastName);
            if (result == 0) {
                result = firstName.compareTo(teacher.firstName);
                if (result == 0) {
                    result = lastName.compareTo(teacher.lastName);
                }
            }
            return result;
        }
    }

    private static class TeachersAdapter extends BaseAdapter {
        private final LayoutInflater inflater;
        private final List<Teacher> teachers;

        public TeachersAdapter(final Context context, final List<Teacher> teachers) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.teachers = teachers;
        }

        @Override
        public int getCount() {
            return teachers.size();
        }

        @Override
        public Object getItem(int position) {
            return teachers.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            ViewHolder viewHolder = (convertView == null ?
                    new ViewHolder(inflater.inflate(R.layout.list_item_teacher, null)) :
                    new ViewHolder(convertView)).fill(teachers.get(position));
            return viewHolder.root;
        }

        private static class ViewHolder {
            public final View root;
            public final TextView lastName;
            public final TextView fullName;
            public final TextView classes;

            public ViewHolder(final View view) {
                root = view;
                lastName = (TextView) view.findViewById(R.id.lastName);
                fullName = (TextView) view.findViewById(R.id.fullName);
                classes = (TextView) view.findViewById(R.id.classes);
            }

            public ViewHolder fill(Teacher teacher) {
                lastName.setText(teacher.lastName);
                fullName.setText(teacher.firstName + " " + teacher.middleName);

                StringBuilder stringBuilder = new StringBuilder();
                for (String lesson : teacher.lessons) {
                    stringBuilder.append(lesson).append(", ");
                }
                String lessons = stringBuilder.toString();
                classes.setText(lessons.substring(0, lessons.length() - 2));

                return this;
            }
        }
    }
}
