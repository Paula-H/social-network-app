package com.example.socialnetworkapp.domain;

import java.util.ArrayList;
import java.util.Objects;

public class User extends Entity<Long>{

    private String first_name;
    private String last_name;

    private String username;

    private String email;

    private String password;


    public User(String first_name, String last_name,String username, String email, String password){
        this.first_name = first_name;
        this.last_name = last_name;
        this.username = username;
        this.email = email;
        this.password = password;

    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }


    public void setPassword(String password) {
        this.password = password;
    }


    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getUsername() {
        return username;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        User that = (User) o;
        return Objects.equals(getFirst_name(), that.getFirst_name()) && Objects.equals(getLast_name(), that.getLast_name()) && Objects.equals(getUsername(), that.getUsername());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFirst_name(), getLast_name(), getUsername());
    }

    @Override
    public String toString() {
        return username;
    }

    public String forListView(){
        return "Username: "+this.username+ " | first name: "+this.first_name+" | last name: "+this.last_name;
    }
}
