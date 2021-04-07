package com.niamh.sailingbuddy.SessionCRUD.CreateSession;

public class Session {

    private long id;
    private String instructorName;
    private String date;
    private String level;
    private String noStudents;
    private String launchTime;
    private String recoveryTime;
    private String landActivity;
    private String waterActivity;
    private String sailArea;
    private String highTide;
    private String lowTide;
    private String weather;

    public Session( long id, String instructorName, String date, String level, String noStudents,
                    String launchTime, String recoveryTime, String landActivity, String waterActivity,
                    String sailArea, String highTide, String lowTide, String weather){

        this.id = id;
        this.instructorName = instructorName;
        this.date = date;
        this.level = level;
        this.noStudents = noStudents;
        this.launchTime = launchTime;
        this.recoveryTime = recoveryTime;
        this.landActivity = landActivity;
        this.waterActivity = waterActivity;
        this.sailArea = sailArea;
        this.highTide = highTide;
        this.lowTide = lowTide;
        this.weather = weather;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getInstructorName() {
        return instructorName;
    }

    public void setInstructorName(String instructorName) {
        this.instructorName = instructorName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getNoStudents() {
        return noStudents;
    }

    public void setNoStudents(String noStudents) {
        this.noStudents = noStudents;
    }

    public String getLaunchTime() {
        return launchTime;
    }

    public void setLaunchTime(String launchTime) {
        this.launchTime = launchTime;
    }

    public String getRecoveryTime() {
        return recoveryTime;
    }

    public void setRecoveryTime(String recoveryTime) {
        this.recoveryTime = recoveryTime;
    }

    public String getLandActvity() {
        return landActivity;
    }

    public void setLandActvity(String landActvity) {
        this.landActivity = landActvity;
    }

    public String getWaterActivity() {
        return waterActivity;
    }

    public void setWaterActivity(String waterActivity) {
        waterActivity = waterActivity;
    }

    public String getSailArea() {
        return sailArea;
    }

    public void setSailArea(String sailArea) {
        this.sailArea = sailArea;
    }

    public String getHighTide() {
        return highTide;
    }

    public void setHighTide(String highTide) {
        this.highTide = highTide;
    }

    public String getLowTide() {
        return lowTide;
    }

    public void setLowTide(String lowTide) {
        this.lowTide = lowTide;
    }
}
