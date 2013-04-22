package com.synergy.android.timetable.events;

import java.util.HashSet;
import java.util.Set;

public class EventBus {
    private Set<Subscriber> subscribers;
    
    public EventBus() {
        subscribers = new HashSet<Subscriber>();
    }
    
    public synchronized void fireEvent(Event event) {
        if (event == null) {
            throw new NullPointerException("Event is null.");
        }
        
        for (Subscriber s : subscribers) {
            if (s != event.sentBy() && s.isSubscribed(event)) {
                s.handleEvent(event);
            }
        }
    }
    
    public synchronized void subscribe(Subscriber subscriber) {
        subscribers.add(subscriber);
    }
    
    public synchronized void unsubscribe(Subscriber subscriber) {
        subscribers.remove(subscriber);
    }
}
