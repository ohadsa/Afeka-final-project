package com.example.final_project_afeka.services.objects;

public class Loc {

    private double lat = 0.0;
    private double lon = 0.0;
    private double speed = 0.0;

    public Loc() {}

    public Loc(double lat, double lon, double speed) {
        this.lat = lat;
        this.lon = lon;
        this.speed = speed;
    }

    public Loc(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public Loc setLat(double lat) {
        this.lat = lat;
        return this;
    }

    public double getLon() {
        return lon;
    }

    public Loc setLon(double lon) {
        this.lon = lon;
        return this;
    }

    public double getSpeed() {
        return speed;
    }

    public Loc setSpeed(double speed) {
        this.speed = speed;
        return this;
    }

    @Override
    public String toString() {
        return "(" + lat +
                "," + lon +
                ",s=" + speed +
                ')';
    }
}
