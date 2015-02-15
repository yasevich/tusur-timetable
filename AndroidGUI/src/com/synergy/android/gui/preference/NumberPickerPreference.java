/**
 * Copyrights (C) 2012 Vyacheslav Yasevich.
 * All rights reserved.
 */

package com.synergy.android.gui.preference;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.synergy.android.gui.R;

/**
 * Implements <code>NumberPicker</code> preference.
 * <p>
 * This dialog preference uses <code>EditText</code> on API levels prior
 * <code>Honeycomb</code>. On other API levels it uses <code>NumberPicker</code>
 * with minimum and maximum values set.
 * 
 * @author Vyacheslav Yasevich
 */
@SuppressLint("NewApi")
public class NumberPickerPreference extends DialogPreference {
    private static final int DEFAULT_VALUE = 1;
    private static final int NUMBER_PICKER_MIN_WIDTH = 100;
    
    private NumberPicker mNumberPicker;
    private EditText mEditText;
    private int mMinValue;
    private int mMaxValue;
    private int mCurrentValue;
    private String mUnits;
    private String[] mDisplayedValues;
    
    /**
     * Creates {@link NumberPickerPreference} object.
     * 
     * @param context the context
     */
    public NumberPickerPreference(Context context) {
        super(context, null);
    }
    
    /**
     * Creates {@link NumberPickerPreference} object.
     * 
     * @param context the context
     * @param attrs   the attributes
     */
    public NumberPickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.layout.preference_numberpicker);
        TypedArray styledAttributes = context.obtainStyledAttributes(attrs,
                R.styleable.NumberPickerPreference, 0, 0);
        init(styledAttributes);
        styledAttributes.recycle();
    }
    
    /**
     * Creates {@link NumberPickerPreference} object.
     * 
     * @param context  the context
     * @param attrs    the attributes
     * @param defStyle the default style
     */
    public NumberPickerPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setDialogLayoutResource(R.layout.preference_numberpicker);
        TypedArray styledAttributes = context.obtainStyledAttributes(attrs,
                R.styleable.NumberPickerPreference, defStyle, 0);
        init(styledAttributes);
        styledAttributes.recycle();
    }
    
    @Override
    protected void onBindDialogView (View view) {
        super.onBindDialogView(view);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            setEditTextValue(mCurrentValue);
        } else {
            mNumberPicker.setValue(mCurrentValue);
        }
    }
    
    @Override
    protected View onCreateDialogView() {
        View view = super.onCreateDialogView();
        
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            mEditText = (EditText) view.findViewById(R.id.editText);
            if (mDisplayedValues != null) {
                mEditText.setFocusable(false);
            }
            
            ImageButton button = (ImageButton) view.findViewById(R.id.buttonUp);
            button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCurrentValue < mMaxValue) {
                        mCurrentValue++;
                    } else {
                        mCurrentValue = mMinValue;
                    }
                    setEditTextValue(mCurrentValue);
                }
            });
            
            button = (ImageButton) view.findViewById(R.id.buttonDown);
            button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCurrentValue > mMinValue) {
                        mCurrentValue--;
                    } else {
                        mCurrentValue = mMaxValue;
                    }
                    setEditTextValue(mCurrentValue);
                }
            });
        } else {
            mNumberPicker = (NumberPicker) view.findViewById(R.id.numberPicker);
            mNumberPicker.setMinValue(mMinValue);
            mNumberPicker.setMaxValue(mMaxValue);
            mNumberPicker.setDisplayedValues(mDisplayedValues);
            
            // makes inner views non editable by users 
            if (mDisplayedValues != null) {
                int childCount = mNumberPicker.getChildCount();
                for (int i = 0; i < childCount; ++i) {
                    View childView = mNumberPicker.getChildAt(i);
                    if (childView instanceof EditText) {
                        childView.setFocusable(false);
                        childView.setMinimumWidth(NUMBER_PICKER_MIN_WIDTH);
                        break;
                    }
                }
            }
            
            if (mUnits != null) {
                TextView unitsTextView = (TextView) view.findViewById(R.id.units);
                unitsTextView.setVisibility(View.VISIBLE);
                unitsTextView.setText(mUnits);
            }
        }
        
        return view;
    }
    
    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                if (mDisplayedValues == null) {
                    try {
                        mCurrentValue = Integer.parseInt(mEditText.getText().toString());
                    } catch (NumberFormatException exception) {
                        // do nothing
                    }
                }
            } else {
                mCurrentValue = mNumberPicker.getValue();
            }
            if (callChangeListener(mCurrentValue)) {
                persistInt(mCurrentValue);
            }
        } else {
            mCurrentValue = getPersistedInt(DEFAULT_VALUE);
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
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            if (mEditText != null) {
                setEditTextValue(savedState.mCurrentValue);
            }
        } else {
            if (mNumberPicker != null) {
                mNumberPicker.setValue(savedState.mCurrentValue);
                mNumberPicker.setMinValue(mMinValue);
                mNumberPicker.setMaxValue(mMaxValue);
            }
        }
    }
    
    @Override
    protected Parcelable onSaveInstanceState() {
        final SavedState savedState = new SavedState(super.onSaveInstanceState());
        if (mNumberPicker == null && mEditText == null) {
            savedState.mCurrentValue = mCurrentValue;
            savedState.mMinValue = mMinValue;
            savedState.mMaxValue = mMaxValue;
        } else {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                if (mDisplayedValues == null) {
                    try {
                        savedState.mCurrentValue = Integer.parseInt(mEditText.getText().toString());
                    } catch (NumberFormatException exception) {
                        savedState.mCurrentValue = mCurrentValue;
                    }
                } else {
                    savedState.mCurrentValue = mCurrentValue;
                }
            } else {
                savedState.mCurrentValue = mNumberPicker.getValue();
                savedState.mMinValue = mNumberPicker.getMinValue();
                savedState.mMaxValue = mNumberPicker.getMaxValue();
            }
        }
        return savedState;
    }
    
    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, DEFAULT_VALUE);
    }
    
    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if (restorePersistedValue) {
            mCurrentValue = getPersistedInt(DEFAULT_VALUE);
        } else {
            mCurrentValue = (Integer) defaultValue;
        }
    }
    
    private void init(TypedArray attrs) {
        mMinValue = attrs.getInt(R.styleable.NumberPickerPreference_minValue, 0);
        mMaxValue = attrs.getInt(R.styleable.NumberPickerPreference_maxValue, Integer.MAX_VALUE);
        mUnits = attrs.getString(R.styleable.NumberPickerPreference_units);
        CharSequence[] displayedValues = attrs.getTextArray(
                R.styleable.NumberPickerPreference_displayedValues);
        if (displayedValues != null) {
            mDisplayedValues = new String[displayedValues.length];
            for (int i = 0; i < mDisplayedValues.length; ++i) {
                mDisplayedValues[i] = displayedValues[i].toString();
            }
        }
    }
    
    private void setEditTextValue(int value) {
        if (mDisplayedValues == null) {
            mEditText.setText(Integer.toString(value));
        } else {
            mEditText.setText(mDisplayedValues[value - 1]);
        }
    }
    
    public static class SavedState extends BaseSavedState {
        private int mMinValue;
        private int mMaxValue;
        private int mCurrentValue;
        
        public SavedState(Parcel source) {
            super(source);
            int[] val = new int[3];
            source.readIntArray(val);
            mMinValue = val[0];
            mMaxValue = val[1];
            mCurrentValue = val[2];
        }
        
        public SavedState(Parcelable superState) {
            super(superState);
        }
        
        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            int[] val = new int[] { mMinValue, mMaxValue, mCurrentValue };
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
