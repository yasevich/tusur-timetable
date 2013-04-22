package com.synergy.android.timetable.events;

import java.util.HashSet;
import java.util.Set;

public abstract class Subscriber {
    protected Set<Event> events = new HashSet<Event>();
    
    public synchronized void subscribe(Event event) {
        events.add(event);
    }
    
    public synchronized void unsubscribe(Event event) {
        events.remove(event);
    }
    
    public synchronized boolean isSubscribed(Event event) {
        return events.contains(event);
    }
    
    public abstract void handleEvent(Event event);
}
