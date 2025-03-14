package com.example.socialnetworkapp.domain;

import java.time.LocalDateTime;

public class Friendship extends Entity<Tuple<Long,Long>> {
    LocalDateTime friendsFrom;

    public Friendship(Long u1, Long u2) {
        super.setId(new Tuple<Long,Long>(u1,u2));
        this.friendsFrom = LocalDateTime.now();
    }

    public void setFriendsFrom(LocalDateTime friendsFrom) {
        this.friendsFrom = friendsFrom;
    }

    public LocalDateTime getFriendsFrom() {
        return friendsFrom;
    }

    @Override
    public String toString() {

        return "Friendship id= ("+ this.getId().toString()+") | Date= ("+this.getFriendsFrom()+")";
    }
}