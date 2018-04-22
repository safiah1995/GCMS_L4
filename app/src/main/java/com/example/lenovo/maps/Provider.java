package com.example.lenovo.maps;

import android.location.Location;

public class Provider extends Person {
    Location Plocation;
    String startWork;
    String endWork;
    double avrage;


    public Provider(String email, String name, String password, String phone) {
        super(email, name, password, phone);
    }

    public Provider(String email, String name, String password, String phone, Location plocation, String startWork, String endWork, double avrage) {
        super(email, name, password, phone);
        Plocation = plocation;
        this.startWork = startWork;
        this.endWork = endWork;
        this.avrage = avrage;
    }

    public Location getPlocation() {
        return Plocation;
    }

    public void setPlocation(Location plocation) {
        Plocation = plocation;
    }

    public String getStartWork() {
        return startWork;
    }

    public void setStartWork(String startWork) {
        this.startWork = startWork;
    }

    public String getEndWork() {
        return endWork;
    }

    public void setEndWork(String endWork) {
        this.endWork = endWork;
    }

    public double getAvrage() {
        return avrage;
    }

    public void setAvrage(double avrage) {
        this.avrage = avrage;
    }

}
