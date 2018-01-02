package main.java.bgu.spl181.net.impl;

import java.util.ArrayList;

public class Movie {
    private int id;
    private String name;
    private double price;
    private ArrayList<String> bannedCountries;
    private int availableAmount;
    private int totalAmount;

    //TODO:: check if the name already exists in the system and if the price or the amount are <=0
    public Movie(int id, String name, double price, ArrayList<String> bannedCountries, int availableAmount, int totalAmount) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.bannedCountries = bannedCountries;
        this.availableAmount = availableAmount;
        this.totalAmount = totalAmount;
    }

    public void setPrice(double newPrice) {
        this.price = newPrice;
    }

    //returns false if the movie is not available, otherwise decrease availableAmount and returns true
    public boolean rentMovie() {
        if (availableAmount == 0) return false;
        this.availableAmount--;
        return true;
    }

    public boolean isbannedCountry(String country) {
        return this.bannedCountries.contains(country);
    }

    public boolean rent() {
        if (availableAmount == 0)
            return false;
        this.availableAmount--;
        return true;
    }

    public void returnMovie() {
        this.availableAmount++;
    }

    //return true if there is at least one user renting this movie
    public boolean isRented() {
        return availableAmount != totalAmount;
    }

    //TODO:: change the function
    public String toString() {
        return "\"name\" " + availableAmount + " " + totalAmount + " " + bannedCountries.toString();

    }
}
