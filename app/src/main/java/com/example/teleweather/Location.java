package com.example.teleweather;

public class Location {
    private final int id;
    private final String name;
    private final String region;
    private final String country;
    private final double lat;
    private final double lon;

    public Location(int id, String name, String region, String country, double lat, double lon) {
        this.id = id;
        this.name = name;
        this.region = region;
        this.country = country;
        this.lat = lat;
        this.lon = lon;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getRegion() {
        return region;
    }

    public String getCountry() {
        return country;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }
}