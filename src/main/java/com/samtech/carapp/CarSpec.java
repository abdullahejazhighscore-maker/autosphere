package com.samtech.carapp;

public class CarSpec {
    private int id;
    private String name;
    private String engine;
    private String fuelType;
    private String maxPower;
    private String maxTorque;
    private String transmission;
    private String driveType;
    private String bodyType;
    private int seats;
    private String fuelTank;
    private String fuelEconomy;
    private String dimensions;
    private String wheelbase;
    private String groundClearance;
    private String kerbWeight;
    private String safety;
    private String priceRange;

    public CarSpec(int id, String name, String engine, String fuelType, String maxPower, String maxTorque, String transmission, String driveType, String bodyType, int seats, String fuelTank, String fuelEconomy, String dimensions, String wheelbase, String groundClearance, String kerbWeight, String safety, String priceRange) {
        this.id = id;
        this.name = name;
        this.engine = engine;
        this.fuelType = fuelType;
        this.maxPower = maxPower;
        this.maxTorque = maxTorque;
        this.transmission = transmission;
        this.driveType = driveType;
        this.bodyType = bodyType;
        this.seats = seats;
        this.fuelTank = fuelTank;
        this.fuelEconomy = fuelEconomy;
        this.dimensions = dimensions;
        this.wheelbase = wheelbase;
        this.groundClearance = groundClearance;
        this.kerbWeight = kerbWeight;
        this.safety = safety;
        this.priceRange = priceRange;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getEngine() { return engine; }
    public String getFuelType() { return fuelType; }
    public String getMaxPower() { return maxPower; }
    public String getMaxTorque() { return maxTorque; }
    public String getTransmission() { return transmission; }
    public String getDriveType() { return driveType; }
    public String getBodyType() { return bodyType; }
    public int getSeats() { return seats; }
    public String getFuelTank() { return fuelTank; }
    public String getFuelEconomy() { return fuelEconomy; }
    public String getDimensions() { return dimensions; }
    public String getWheelbase() { return wheelbase; }
    public String getGroundClearance() { return groundClearance; }
    public String getKerbWeight() { return kerbWeight; }
    public String getSafety() { return safety; }
    public String getPriceRange() { return priceRange; }
}
