/**
 * Copyrights (C) 2012 Vyacheslav Yasevich.
 * All rights reserved.
 */

package com.synergy.android.gui.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.DialogPreference;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;

/**
 * The dialog preference that flattens TimePicker widget.
 * 
 * @author Vyacheslav Yasevich
 */
public class TimePickerPreference extends DialogPreference {
    private static final String DEFAULT_VALUE = "00:00";
    
    private TimePicker mTimePicker;
    private Integer mHour = 0;
    private Integer mMinute = 0;
    
    /**
     * Creates {@link TimePickerPreference} instance.
     * 
     * @param context the context
     */
    public TimePickerPreference(Context context) {
        super(context, null);
    }
    
    /**
     * Creates {@link TimePickerPreference} instance.
     * 
     * @param context the context
     * @param attrs   the attributes
     */
    public TimePickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    /**
     * Creates {@link TimePickerPreference} instance.
     * 
     * @param context  the context
     * @param attrs    the attributes
     * @param defStyle the default style
     */
    public TimePickerPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    
    @Override
    protected void onBindDialogView (View view) {
        super.onBindDialogView(view);
        mTimePicker.setCurrentHour(mHour);
        mTimePicker.setCurrentMinute(mMinute);
    }
    
    @Override
    protected View onCreateDialogView() {
        mTimePicker = new TimePicker(getContext());
        mTimePicker.setIs24HourView(DateFormat.is24HourFormat(getContext()));
        return mTimePicker;
    }
    
    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            mHour = mTimePicker.getCurrentHour();
            mMinute = mTimePicker.getCurrentMinute();
            String time = new StringBuilder().append(mHour).append(':').append(mMinute).toString();
            if (callChangeListener(time)) {
                persistString(time);
            }
        }
    }
    
    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state == null || !state.getClass().equals(SavedState.class)) {
            super.onRestoreInstanceState(state);
            return;
        }
        
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        if (mTimePicker != null) {
            mTimePicker.setCurrentHour(savedState.mHour);
            mTimePicker.setCurrentMinute(savedState.mMinute);
        }
    }
    
    @Override
    protected Parcelable onSaveInstanceState() {
        final SavedState savedState = new SavedState(super.onSaveInstanceState());
        if (mTimePicker == null) {
            savedState.mHour = mHour;
            savedState.mMinute = mMinute;
        } else {
            savedState.mHour = mTimePicker.getCurrentHour();
            savedState.mMinute = mTimePicker.getCurrentMinute();
        }
        return savedState;
    }
    
    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }
    
    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        String time = null;
        if (restorePersistedValue) {
            time = getPersistedString(DEFAULT_VALUE);
        } else {
            time = defaultValue.toString();
        }
        parseTime(time);
    }
    
    private void parseTime(String time) {
        String[] data = time.split(":");
        mHour = Integer.parseInt(data[0]);
        mMinute = Integer.parseInt(data[1]);
    }
    
    public static class SavedState extends BaseSavedState {
        private Integer mHour;
        private Integer mMinute;
        
        public SavedState(Parcel source) {
            super(source);
            int[] val = new int[2];
            source.readIntArray(val);
            mHour = val[0];
            mMinute = val[1];
        }
        
        public SavedState(Parcelable superState) {
            super(superState);
        }
        
        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            int[] val = new int[] { mHour, mMinute };
            dest.writeIntArray(val);
        }
        
        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel source) {
                return new SavedState(source);
            }
            
            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}
