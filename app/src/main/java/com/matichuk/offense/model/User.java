package com.matichuk.offense.model;

public class User {
    private String name;
    private String email;
    private String password;
    private String visibleEmail;
    private String visiblePhone;
    private String image;

    public User() {
    }

    public User(String name, String email, String password, String visibleEmail, String visiblePhone, String image) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.visibleEmail = visibleEmail;
        this.visiblePhone = visiblePhone;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getVisibleEmail() {
        return visibleEmail;
    }

    public void setVisibleEmail(String visibleEmail) {
        this.visibleEmail = visibleEmail;
    }

    public String getVisiblePhone() {
        return visiblePhone;
    }

    public void setVisiblePhone(String visiblePhone) {
        this.visiblePhone = visiblePhone;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
