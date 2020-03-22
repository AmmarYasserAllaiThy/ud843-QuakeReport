package com.example.android.quakereport;

public class Earthquake {

    private double mag;
    private String of;
    private String place;
    private String date;
    private String url;

    public Earthquake(double mag, String of, String place, String date, String url) {
        this.mag = mag;
        this.of = of;
        this.place = place;
        this.date = date;
        this.url = url;
    }

    public double getMag() {
        return mag;
    }

    public String getOf() {
        return of;
    }

    public String getPlace() {
        return place;
    }

    public String getDate() {
        return date;
    }

    public String getUrl() {
        return url;
    }
}