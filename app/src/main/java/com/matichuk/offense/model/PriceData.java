package com.matichuk.offense.model;

public class PriceData {
    private String carTitle;
    private String price;
    private String mileage;
    private String location;
    private String fuel;
    private String transmission;
    private String photo;

    public PriceData() {
    }

    public PriceData(String carTitle, String price, String mileage, String location, String fuel, String transmission, String photo) {
        this.carTitle = carTitle;
        this.price = price;
        this.mileage = mileage;
        this.location = location;
        this.fuel = fuel;
        this.transmission = transmission;
        this.photo = photo;
    }

    public String getCarTitle() {
        return carTitle;
    }

    public void setCarTitle(String carTitle) {
        this.carTitle = carTitle;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getMileage() {
        return mileage;
    }

    public void setMileage(String mileage) {
        this.mileage = mileage;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getFuel() {
        return fuel;
    }

    public void setFuel(String fuel) {
        this.fuel = fuel;
    }

    public String getTransmission() {
        return transmission;
    }

    public void setTransmission(String transmission) {
        this.transmission = transmission;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    @Override
    public String toString() {
        return "PriceData{" +
                "carTitle='" + carTitle + '\'' +
                ", price='" + price + '\'' +
                ", mileage='" + mileage + '\'' +
                ", location='" + location + '\'' +
                ", fuel='" + fuel + '\'' +
                ", transmission='" + transmission + '\'' +
                ", photo='" + photo + '\'' +
                '}';
    }
}
