package com.example.discoveryparkmap;

public class Landmark {
    private String name;
    private int floor;
    private Point location;

    public Landmark(String name, int floor, double x, double y) {
        this.name = name;
        this.floor = floor;
        this.location = new Point(x, y);
    }

    public String getName() {
        return name;
    }

    public int getFloor() {
        return floor;
    }

    public Point getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return name + " (Floor " + floor + ")";
    }
}
