package com.synergy.android.timetable.events;

import com.synergy.android.timetable.domains.Week;

public class DataIsLoaded extends Event {
    private Week[] weeks;
    
    public DataIsLoaded() {
    }
    
    public DataIsLoaded(Week[] weeks) {
        this.weeks = weeks;
    }
    
    public Week[] getWeeks() {
        return weeks;
    }
}
