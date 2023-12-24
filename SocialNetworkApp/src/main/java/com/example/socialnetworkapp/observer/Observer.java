package com.example.socialnetworkapp.observer;


import com.example.socialnetworkapp.event.Event;

import java.sql.SQLException;

public interface Observer<E extends Event> {
    void update(E e) throws SQLException;
}