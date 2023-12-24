package com.example.socialnetworkapp.event;

import com.example.socialnetworkapp.domain.Entity;

public class EventImplementation implements Event{
    private Entity newData, oldData;

    public EventImplementation(Entity oldData, Entity newData){
        this.newData = newData;
        this.oldData = oldData;
    }

    public EventImplementation(Entity newData){
        this.newData = newData;
    }

    public Entity getNewData() {
        return newData;
    }

    public void setNewData(Entity newData) {
        this.newData = newData;
    }

    public Entity getOldData() {
        return oldData;
    }

    public void setOldData(Entity oldData) {
        this.oldData = oldData;
    }
}
