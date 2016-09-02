package com.exception.findatutor.Activities;

public class TutorInfoFull {
    private String name;
    private String edu;
    private String exp;
    private String courses;
    private String fee;
    private String from;
    private String to;
    private String lat;
    private String lng;
    private String notification;


    public TutorInfoFull(String name, String edu, String exp, String courses, String fee, String from, String to, String lat, String lng, String notification) {
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.to = to;
        this.from = from;
        this.fee = fee;
        this.edu = edu;
        this.exp = exp;
        this.courses = courses;
        this.notification = notification;
    }

    public String getTutorName() {
        return name;
    }

    public String getEdu() {
        return edu;
    }

    public String getLat() {
        return lat;
    }

    public String getLng() {
        return lng;
    }

    public String getNotifications() {
        return notification;
    }

    public String getCourses() {
        return courses;
    }

    public String getExp() {
        return exp;
    }


    public String getFee() {
        return fee;
    }
    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

}

