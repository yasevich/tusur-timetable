package com.synergy.android.timetable.events;

public abstract class Event {
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
    
    @Override
    public boolean equals(Object o) {
        return getClass().equals(o.getClass());
    }
}
