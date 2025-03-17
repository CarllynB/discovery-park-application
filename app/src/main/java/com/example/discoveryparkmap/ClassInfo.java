package com.example.discoveryparkmap;

public class ClassInfo {
    private int id;
    private String classCode;
    private String className;
    private Room room;
    private String days; // e.g., "MWF" for Monday, Wednesday, Friday
    private String startTime;
    private String endTime;

    public ClassInfo() {
    }

    public ClassInfo(String classCode, String className, Room room, String days, String startTime, String endTime) {
        this.classCode = classCode;
        this.className = className;
        this.room = room;
        this.days = days;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getClassCode() {
        return classCode;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClassInfo classInfo = (ClassInfo) o;
        return classCode.equals(classInfo.classCode);
    }

    @Override
    public int hashCode() {
        return classCode.hashCode();
    }

    @Override
    public String toString() {
        return classCode + " - " + className + " (" + room.getRoomNumber() + ")";
    }
}