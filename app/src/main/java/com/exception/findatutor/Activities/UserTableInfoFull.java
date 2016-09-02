package com.exception.findatutor.Activities;


public class UserTableInfoFull {

    public String Occupation;
    public String name;
    public String age;
    public String phoneno;
    public String city;
    public String registeringas;
    public String email;


    public UserTableInfoFull(String name, String email, String phoneno, String Occupation, String city, String age) {
        this.name = name;
        this.email = email;
        this.phoneno= phoneno;
        this.Occupation= Occupation;
        this.city= city;
        this.age= age;
    }

    public String getOccupation() {
        return Occupation;
    }

    public String getPhoneno() {
        return phoneno;
    }

    public String getEmail() {
        return email;
    }

    public String getCity() {
        return city;
    }

    public String getAge() {
        return age;
    }

    public void setOccupation(String Occupation) {
        this.Occupation= Occupation;
    }

    public void setPhoneno(String phoneno) {
        this.phoneno= phoneno;
    }

    public void setEmail(String email) {
        this.email= email;
    }

    public void setCity(String city) {
        this.city= city;
    }

    public void setAge(String age) {
        this.age= age;
    }

}
