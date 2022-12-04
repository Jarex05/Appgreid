package com.example.gps_apgreid.adapter;

import java.io.Serializable;

public class ListItem implements Serializable {

    private String title;
    private String speed;
    private int id = 0;

    public String getTitle(){
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }
}
