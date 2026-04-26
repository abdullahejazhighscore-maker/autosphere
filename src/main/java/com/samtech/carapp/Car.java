package com.samtech.carapp;

public class Car {
    private final int id;
    private final String title;
    private final String make;
    private final String model;
    private final String color;
    private final String city;
    private final int year;
    private final double price;
    private final int mileage;
    private final String description;
    private final String imagePath;
    private final int sellerId;
    private final String sellerName;
    private final String status;
    private final String vehicleType;
    private final String transmission;
    private final String fuelType;
    private final String assembly;
    private final String paintCondition;
    private final String showeredParts;

    public Car(
            int id,
            String title,
            String make,
            String model,
            String color,
            String city,
            int year,
            double price,
            int mileage,
            String description,
            String imagePath,
            int sellerId,
            String sellerName,
            String status,
            String vehicleType,
            String transmission,
            String fuelType,
            String assembly,
            String paintCondition,
            String showeredParts
    ) {
        this.id = id;
        this.title = title;
        this.make = make;
        this.model = model;
        this.color = color;
        this.city = city;
        this.year = year;
        this.price = price;
        this.mileage = mileage;
        this.description = description;
        this.imagePath = imagePath;
        this.sellerId = sellerId;
        this.sellerName = sellerName;
        this.status = status;
        this.vehicleType = vehicleType;
        this.transmission = transmission;
        this.fuelType = fuelType;
        this.assembly = assembly;
        this.paintCondition = paintCondition;
        this.showeredParts = showeredParts;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getMake() {
        return make;
    }

    public String getModel() {
        return model;
    }

    public String getColor() {
        return color;
    }

    public String getCity() {
        return city;
    }

    public int getYear() {
        return year;
    }

    public double getPrice() {
        return price;
    }

    public int getMileage() {
        return mileage;
    }

    public String getDescription() {
        return description;
    }

    public String getImagePath() {
        return imagePath;
    }

    public int getSellerId() {
        return sellerId;
    }

    public String getSellerName() {
        return sellerName;
    }

    public String getStatus() {
        return status;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public String getTransmission() {
        return transmission;
    }

    public String getFuelType() {
        return fuelType;
    }

    public String getAssembly() {
        return assembly;
    }

    public String getPaintCondition() {
        return paintCondition;
    }

    public String getShoweredParts() {
        return showeredParts;
    }
}
