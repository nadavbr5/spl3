package bgu.spl181.net.srv;

public class User {

    private String name;
    private String type;
    private String password;
    private String [] movies;
    private int balance;

    //TODO:: check that the inputs are valid
    public User(String name, String type, String password, String[] movies, int balance) {
        this.name = name;
        this.type = type;
        this.password = password;
        this.movies = movies;
        this.balance = balance;
    }




}
