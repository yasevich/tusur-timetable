package com.synergy.android.timetable.utils;

import com.synergy.android.timetable.TimetableApplication;
import com.synergy.android.timetable.listeners.SwitchableView;

public class Common {
    public static void switchView(SwitchableView view, boolean isEnabled) {
        TimetableApplication app = TimetableApplication.getInstance();
        int color = isEnabled ? SwitchableView.DEFAULT_COLOR : app.getDataEmptyColor();
        view.switchTextColor(color);
    }
}
