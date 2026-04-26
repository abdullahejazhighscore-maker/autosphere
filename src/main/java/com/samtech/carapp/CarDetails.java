package com.samtech.carapp;

import java.util.List;

public class CarDetails {
    private final Car car;
    private final User seller;
    private final List<String> imagePaths;

    public CarDetails(Car car, User seller, List<String> imagePaths) {
        this.car = car;
        this.seller = seller;
        this.imagePaths = imagePaths;
    }

    public Car getCar() {
        return car;
    }

    public User getSeller() {
        return seller;
    }

    public List<String> getImagePaths() {
        return imagePaths;
    }
}
