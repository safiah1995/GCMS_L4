package com.example.lenovo.maps;

import com.google.android.gms.maps.model.LatLng;


public class ProviderLocation implements Comparable<ProviderLocation> {

    String email;
    String phone;
    String name;
    LatLng latlng;
    String startWorking;
    String endWorking;
    double distance;


    public ProviderLocation(String email, String phone, String name, LatLng latlng, String startWorking, String endWorking, double distance) {
        this.email = email;
        this.phone = phone;
        this.name = name;
        this.latlng = latlng;
        this.startWorking = startWorking;
        this.endWorking = endWorking;
        this.distance = distance;
    }

    public int compareTo(ProviderLocation compareLocation) {

        double c = ((ProviderLocation) compareLocation).getDistance();
        Double compare = new Double(c);
        int com = compare.intValue();

        double d = this.distance;
        Double destance = new Double(d);
        int des = destance.intValue();

        //ascending order  صغير لكبير
        return des - com;

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LatLng getLatlng() {
        return latlng;
    }

    public void setLatlng(LatLng latlng) {
        this.latlng = latlng;
    }

    public String getStartWorking() {
        return startWorking;
    }

    public void setStartWorking(String startWorking) {
        this.startWorking = startWorking;
    }

    public String getEndWorking() {
        return endWorking;
    }

    public void setEndWorking(String endWorking) {
        this.endWorking = endWorking;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}


