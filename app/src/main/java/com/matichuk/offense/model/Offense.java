package com.matichuk.offense.model;

public class Offense {
    private String userPhone;
    private String carNumber;
    private String offenseType;
    private String image;
    private String key;

    public Offense() {
    }

    public Offense(String userPhone, String carNumber, String offenseType, String image) {
        this.userPhone = userPhone;
        this.carNumber = carNumber;
        this.offenseType = offenseType;
        this.image = image;
    }

    public Offense(String userPhone, String carNumber, String offenseType, String image, String key) {
        this.userPhone = userPhone;
        this.carNumber = carNumber;
        this.offenseType = offenseType;
        this.image = image;
        this.key = key;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getCarNumber() {
        return carNumber;
    }

    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }

    public String getOffenseType() {
        return offenseType;
    }

    public void setOffenseType(String offenseType) {
        this.offenseType = offenseType;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
