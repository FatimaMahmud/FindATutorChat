package com.exception.findatutor.Activities;


public class TutorCards {

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
    private int image;

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

    public int getImageResourceId() {
        return image;
    }

    public void setTutorName(String name) {
        this.name = name;
    }

    public void setEdu(String edu) {
        this.edu = edu;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public void setNotifications(String notification) {
        this.notification = notification;
    }

    public void setCourses(String courses) {
        this.courses = courses;
    }

    public void setExp(String exp) {
        this.exp = exp;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void setImageResourceId(int image) {
        this.image = image;
    }
}
