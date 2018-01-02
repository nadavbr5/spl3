package bgu.spl181.net.impl;

import java.util.ArrayList;

public class User {

    private String name;
    private String type;
    private String password;
    private ArrayList<String> movies;
    private int balance;

    //TODO:: check that the inputs are valid- if the name is not already exits in the system ,type is admin/ normal, balance is a number >=0
    public User(String name, String type, String password) {
        this.name = name;
        this.type = type;
        this.password = password;
        this.movies = new ArrayList<>();
        this.balance = 0;
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
        return this.movies.contains(movieName);
    }


    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }
}