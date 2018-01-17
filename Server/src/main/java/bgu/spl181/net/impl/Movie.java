package bgu.spl181.net.impl;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class Movie {
    private static int id=0;
    private String name;
    private int price;
    private ArrayList<String> bannedCountries;
    private int availableAmount;
    private int totalAmount;

    //TODO:: check if the name already exists in the system and if the price or the amount are <=0
    public Movie(String name, int price, ArrayList<String> bannedCountries, int totalAmount) {
        id++;
        this.name = name;
        this.price = price;
        this.bannedCountries = bannedCountries;
        this.availableAmount = totalAmount;
        this.totalAmount = totalAmount;
    }

    public void setPrice(int newPrice) {
        this.price = newPrice;
    }

    public int getAvailableAmount(){ return this.availableAmount; }

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
        AtomicReference<String> res = new AtomicReference<>("");
        bannedCountries.forEach(country -> {
                res.set(res.get() + " " + "\""+country+"\"");
        });
        return "\""+name+"\" " + availableAmount + " " + totalAmount + res.get();

    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }
}
