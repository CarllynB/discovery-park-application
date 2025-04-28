package com.example.discoveryparkmap;

public class ClassInfo {
    private int id;
    private String classCode;
    private String className;
    private Room room;
    private String days; // e.g., "MWF"
    private String startTime;
    private String endTime;

    private boolean hasRecitation;
    private String recitationRoom;
    private String recitationStartTime;
    private String recitationEndTime;
    private String notes;

    public ClassInfo() {
    }

    public ClassInfo(String classCode, String className, Room room, String days, String startTime, String endTime,
                     boolean hasRecitation, String recitationRoom, String recitationStartTime, String recitationEndTime, String notes) {
        this.classCode = classCode;
        this.className = className;
        this.room = room;
        this.days = days;
        this.startTime = startTime;
        this.endTime = endTime;
        this.hasRecitation = hasRecitation;
        this.recitationRoom = recitationRoom;
        this.recitationStartTime = recitationStartTime;
        this.recitationEndTime = recitationEndTime;
        this.notes = notes;
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

    public boolean hasRecitation() {
        return hasRecitation;
    }
    public void setHasRecitation(boolean hasRecitation) {
        this.hasRecitation = hasRecitation;
    }

    public String getRecitationRoom() {
        return recitationRoom;
    }
    public void setRecitationRoom(String recitationRoom) {
        this.recitationRoom = recitationRoom;
    }

    public String getRecitationStartTime() {
        return recitationStartTime;
    }
    public void setRecitationStartTime(String recitationStartTime) {
        this.recitationStartTime = recitationStartTime;
    }

    public String getRecitationEndTime() {
        return recitationEndTime;
    }
    public void setRecitationEndTime(String recitationEndTime) {
        this.recitationEndTime = recitationEndTime;
    }

    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
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
