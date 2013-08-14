package com.synergy.android.timetable.events;

import java.util.HashSet;
import java.util.Set;

public class EventBus {
    private Set<Observer> observers;
    
    public EventBus() {
        observers = new HashSet<Observer>();
    }
    
    public synchronized void fireEvent(Event event) {
        if (event == null) {
            throw new NullPointerException("Event is null.");
        }
        
        for (Observer o : observers) {
            if (o.isSubscribed(event)) {
                o.handleEvent(event);
            }
        }
    }
    
    public synchronized void subscribe(Observer observer) {
        observers.add(observer);
    }
    
    public synchronized void unsubscribe(Observer observer) {
        observers.remove(observer);
    }
}
