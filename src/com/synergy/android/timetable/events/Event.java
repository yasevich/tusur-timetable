package com.synergy.android.timetable.events;

public class Event {
    private Subscriber subscriber;
    
    public Event() {
    }
    
    public Event(Subscriber subscriber) {
        this.subscriber = subscriber;
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
    
    @Override
    public boolean equals(Object o) {
        return getClass().equals(o.getClass());
    }
    
    public Subscriber sentBy() {
        return subscriber;
    }
}
