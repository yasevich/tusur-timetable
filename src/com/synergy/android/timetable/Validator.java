package com.synergy.android.timetable;

import android.app.Activity;
import android.view.ViewGroup;

public class Validator {
    public static final void validate(Activity activity) {
        ViewGroup viewGroup = (ViewGroup) activity.findViewById(R.id.rootViewGroup);
        if (activity instanceof MainActivity && viewGroup.getChildCount() != 2 ||
                activity instanceof GroupActivity && viewGroup.getChildCount() != 6 ||
                activity instanceof SettingsActivity && viewGroup.getChildCount() != 2) {
            activity.finish();
        }
    }
}
