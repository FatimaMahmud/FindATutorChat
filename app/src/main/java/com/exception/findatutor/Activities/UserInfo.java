package com.exception.findatutor.Activities;

/**
 * The Class Conversation is a Java Bean class that represents a single chat
 * conversation message.
 */
public class UserInfo {
    private String name;
    private String lat;
    private String lng;
    private String status;
    private String gender;
    private String age;


    public UserInfo(String name, String lat, String lng) {
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.status = status;
        this.gender = gender;
        this.age = age;
    }

    public String getName() {return name;}
    public String getStatus() {return status;}
    public String getLat() {return lat;}
    public String getLng() {return lng;}
    public String getGender() {return gender;}
    public String getAge() {return age;}

}

