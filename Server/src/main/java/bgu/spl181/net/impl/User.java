package main.java.bgu.spl181.net.impl;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class User {

    private String username;
    private String type;
    private String password;
    private ArrayList<Movie> movies;
    private int balance;
    private String country;

    //TODO:: check that the inputs are valid- if the name is not already exits in the system ,type is admin/ normal, balance is a number >=0
    public User(String name, String type, String password, String country) {
        this.username = name;
        this.type = type;
        this.password = password;
        this.movies = new ArrayList<Movie>();
        this.balance = 0;
        this.country= country;
    }

    public void setCountry(String country){
        this.country=country;
    }

    public boolean isAdmin() {
        return this.type.equals("admin");
    }

    //we can assume that the number is greater than 0
    public void increaseBalance(int newBalance){
        this.balance+=newBalance;
    }

    //TODO:: check if newBalance is valid
    public boolean reduceBalance(int newBalance){
        int nBalance= this.balance-newBalance;
        if(nBalance >= 0){
            this.balance=nBalance;
            return true;
        }
        return false;
    }

    //returns true if this user already rent the input movie
    public boolean alreadyRent(String movieName){
        AtomicBoolean isRented = new AtomicBoolean(false);
        movies.forEach(movie -> {
            if (("\""+movie.getName()+"\"").equals(movieName)) {
               isRented.set(true);
            }
        });
        return isRented.get();
    }


    public String getName() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getBalance(){ return balance; }

    public String getCountry() {
        return country;
    }

    public void addMovie(Movie m){
        this.movies.add(m);
    }

    public boolean remMovie(Movie m){
        AtomicInteger movieIndex = new AtomicInteger(-1);
        movies.forEach(movie -> {
            if ((movie.getName()).equals(m.getName())) {
                movieIndex.set(movies.indexOf(movie));
            }
        });
        return (movieIndex.get() != -1 && movies.remove(movieIndex.get()) != null);
    }
}