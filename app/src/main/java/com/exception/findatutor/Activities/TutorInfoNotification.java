package com.exception.findatutor.Activities;

//for tutors table and accessing notification


public class TutorInfoNotification {
    private String uname;
    private String lat;
    private String lng;
    private String notification;


    public TutorInfoNotification(String uname, String lat, String lng, String notification) {
        this.uname = uname;
        this.lat = lat;
        this.lng = lng;
        this.notification = notification;
    }

    public String getName() {
        return uname;
    }

    public String getLat() {
        return lat;
    }

    public String getLng() {
        return lng;
    }

    public String getNotification() {
        return notification;
    }

}

