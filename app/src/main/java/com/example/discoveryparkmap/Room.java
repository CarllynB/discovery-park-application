package com.example.discoveryparkmap;

public class Room {
    private int id;
    private String roomNumber;
    private int floor;
    private String wing;
    private double x; // x-coordinate on the map
    private double y; // y-coordinate on the map
    private boolean isAccessible;
    private boolean isBookmarked;

    public Room() {
    }

    public Room(String roomNumber, int floor, String wing, double x, double y, boolean isAccessible) {
        this.roomNumber = roomNumber;
        this.floor = floor;
        this.wing = wing;
        this.x = x;
        this.y = y;
        this.isAccessible = isAccessible;
        this.isBookmarked = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public String getWing() {
        return wing;
    }

    public void setWing(String wing) {
        this.wing = wing;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public boolean isAccessible() {
        return isAccessible;
    }

    public void setAccessible(boolean accessible) {
        isAccessible = accessible;
    }

    public boolean isBookmarked() {
        return isBookmarked;
    }

    public void setBookmarked(boolean bookmarked) {
        isBookmarked = bookmarked;
    }

    @Override
    public String toString() {
        return roomNumber + " (Floor " + floor + ", " + wing + " Wing)";
    }
}