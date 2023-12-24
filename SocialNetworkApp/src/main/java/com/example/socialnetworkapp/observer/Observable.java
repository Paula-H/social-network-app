package com.example.socialnetworkapp.observer;


import com.example.socialnetworkapp.event.Event;

public interface Observable<E extends Event> {
    void addObserver(Observer<E> e);
    void removeObserver(Observer<E> e);
    void notifyObservers(E t);
}
