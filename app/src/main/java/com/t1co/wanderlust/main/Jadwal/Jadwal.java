package com.t1co.wanderlust.main.Jadwal;

public class Jadwal {
    private int id;
    private String busName;
    private String busType;
    private double price;
    private String departureTime;
    private String arrivalTime;
    private int seatsAvailable;
    private String fromLocation;
    private String toLocation;
    private String travelDate;

    public Jadwal(int id, String busName, String busType, double price,
                  String departureTime, String arrivalTime, int seatsAvailable,
                  String fromLocation, String toLocation, String travelDate) {
        this.id = id;
        this.busName = busName;
        this.busType = busType;
        this.price = price;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.seatsAvailable = seatsAvailable;
        this.fromLocation = fromLocation;
        this.toLocation = toLocation;
        this.travelDate = travelDate;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getBusName() {
        return busName;
    }

    public String getBusType() {
        return busType;
    }

    public double getPrice() {
        return price;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public int getSeatsAvailable() {
        return seatsAvailable;
    }

    public String getFromLocation() {
        return fromLocation;
    }

    public String getToLocation() {
        return toLocation;
    }

    public String getTravelDate() {
        return travelDate;
    }
}