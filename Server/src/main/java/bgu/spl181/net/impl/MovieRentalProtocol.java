package bgu.spl181.net.impl;


import bgu.spl181.net.api.bidi.Connections;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class MovieRentalProtocol extends UserServiceTextBaseProtocol {
    private String broadcastMessage;

    public MovieRentalProtocol(SharedProtocolMovieUsersData sharedProtocolData) {
        super(sharedProtocolData);
        broadcastMessage = "";
    }

    //TODO: check if the name saved with "" - if not we need to change the stirngs

    @Override
    public void start(int connectionId, Connections connections) {
        super.start(connectionId, connections);
    }

    @Override
    public void process(String message) {
        super.process(message);
        if (response.equals("CONTINUE")) {
            switch ((!msg.isEmpty() ? msg.remove(0) : "")) {
                case "balance": {
                    switch ((!msg.isEmpty() ? msg.remove(0) : "")) {
                        case "info": {
                            response = isLoggedIn ? balanceInfoProcess() : "ERROR request balance failed";
                            break;
                        }
                        case "add": {
                            response = isLoggedIn ? balanceAddProcess() : "ERROR request balance failed";
                            break;
                        }
                    }
                    break;
                }
                case "info": {
                    response = isLoggedIn ? infoProcess() : "ERROR request info failed";
                    break;
                }
                case "rent": {
                    response = isLoggedIn ? rentProcess() : "ERROR request rent failed";
                    break;
                }
                case "return": {
                    response = isLoggedIn ? returnProcess() : "ERROR request return failed";
                    break;
                }
                case "addmovie": {
                    response = isLoggedIn ? addMovieProcess() : "ERROR request addmovie failed";
                    break;
                }
                case "remmovie": {
                    response = isLoggedIn ? remMovieProcess() : "ERROR request remmovie failed";
                    break;
                }
                case "changeprice": {
                    response = isLoggedIn ? changePriceProcess() : "ERROR request changeprice failed";
                    break;
                }
                case "REGISTER": {
                    response = addCountryProcess();
                    break;
                }
            }
            connections.send(connectionId, response);
            if(!broadcastMessage.equals(""))
                sharedProtocolData.broadcastLoggedIn(new String(broadcastMessage));
            broadcastMessage = "";

        }

    }

    private String addCountryProcess() {
        //TODO: change this string 'country= ... ' to the name of the country
        String userName = this.msg.remove(0);
        String datablock = this.msg.remove(0);
        String country = datablock.substring(9, datablock.length() - 1);
        usersLock.writeLock().lock();
        ArrayList<User> users = sharedProtocolData.getUsers();
        User user = getUser(userName, users);
        user.setCountry(country);
        users.add(user);
        sharedProtocolData.updateUsers(users);
        usersLock.writeLock().unlock();
        return "ACK registration succeeded";
    }

    private String balanceInfoProcess() {
        usersLock.readLock().lock();
        ArrayList<User> users = sharedProtocolData.getUsers();
        String userName = sharedProtocolData.getNameByConnectionId(connectionId);
        String[] ans = {""};
        users.forEach(user -> {
            if (user.getName().equals(userName)) {
                ans[0] = "ACK balance " + Integer.toString(user.getBalance());
            }
        });
        usersLock.readLock().unlock();
        return ans[0];
    }

    private String balanceAddProcess() {
        //assuming that amount is greater than 0
        int amount = Integer.parseInt(msg.remove(0));
        String[] userDetails = new String[2];
        userDetails[0] = sharedProtocolData.getNameByConnectionId(connectionId);
        usersLock.writeLock().lock();
        ArrayList<User> users = sharedProtocolData.getUsers();
        users.forEach(user -> {
            if (user.getName().equals(userDetails[0])) {
                user.increaseBalance(amount);
                userDetails[1] = Integer.toString(user.getBalance());
            }
        });
        sharedProtocolData.updateUsers(users);
        usersLock.writeLock().unlock();
        return "ACK balance " + userDetails[1] + " added " + amount;
    }

    private String infoProcess() {
        String movieName = (!msg.isEmpty() ? msg.remove(0) : "");
        AtomicReference<String> res = new AtomicReference<>("ACK info");
        //case 1: we need to send the info of all the movies
        moviesLock.readLock().lock();
        ArrayList<Movie> movies = sharedProtocolData.getMovies();
        if (movieName.equals("")) {
            movies.forEach((movie) -> {
                res.set(res.get() + " \"" + movie.getName() + "\"");
            });
        } else {
            movies.forEach(movie -> {
                if (("\"" + movie.getName() + "\"").equals(movieName))
                    res.set(res.get() + " " + movie.toString());
            });
        }
        moviesLock.readLock().unlock();
        if (res.get().equals("ACK info"))
            return "ERROR request info failed";
        return res.get();
    }

    private String rentProcess() {
        String movieName = msg.remove(0);
        String userName = sharedProtocolData.getNameByConnectionId(connectionId);
        usersLock.writeLock().lock();
        moviesLock.writeLock().lock();
        ArrayList<User> users = sharedProtocolData.getUsers();
        ArrayList<Movie> movies = sharedProtocolData.getMovies();
        User user = getUser(userName, users);
        Movie movie = getMovie(movieName, movies);
        return rent(movieName, users, movies, user, movie);
    }

    private String returnProcess() {
        //fail- the user not renting the movie or the movie doesn't exist
        String movieName = msg.remove(0);
        String userName = sharedProtocolData.getNameByConnectionId(connectionId);
        usersLock.writeLock().lock();
        moviesLock.writeLock().lock();
        ArrayList<User> users = sharedProtocolData.getUsers();
        ArrayList<Movie> movies = sharedProtocolData.getMovies();
        User user = getUser(userName, users);
        Movie movie = getMovie(movieName, movies);
        return returnMovie(movieName, users, movies, user, movie);

    }

    private String addMovieProcess() {
        msg.replaceAll(s -> {
            if (s.startsWith("\""))
                return s.substring(1, s.length() - 1);
            return s;
        });
        String movieName = msg.remove(0);
        String userName = sharedProtocolData.getNameByConnectionId(connectionId);
        int amount = new Integer(msg.remove(0));
        int price = new Integer(msg.remove(0));
        ArrayList<String> bannedCountries = new ArrayList<>();
        msg.forEach(m -> {
            if (m != null)
                bannedCountries.add(m);
        });

        //fail- the price or the amount isn't valid
        if (price <= 0 | amount <= 0)
            return "ERROR request addmovie failed";

        //fail- the user's type is 'normal'
        User user = isAdmin(userName);
        if (user == null) {
            return "ERROR request addmovie failed";
        }
        moviesLock.writeLock().lock();
        ArrayList<Movie> movies = sharedProtocolData.getMovies();
        Movie isExist = getMovie("\"" + movieName + "\"", movies);

        //fail- movieName exits in the system
        if (isExist != null) {
            moviesLock.writeLock().unlock();
            return "ERROR request addmovie failed";
        }

        Movie addMovie = new Movie(movieName, price, bannedCountries, amount);
        movies.add(addMovie);
        sharedProtocolData.updateMovies(movies);
        broadcastMessage = "BROADCAST movie " + "\"" + movieName + "\" " + amount + " " + price;
        moviesLock.writeLock().unlock();

        return "ACK addmovie \"" + movieName + "\" success";
    }

    private String remMovieProcess() {
        String movieName = msg.remove(0);
        String userName = sharedProtocolData.getNameByConnectionId(connectionId);
        User user = isAdmin(userName);

        //if the user's type is 'normal'
        if (user == null)
            return "ERROR request remmovie failed";

        moviesLock.writeLock().lock();
        ArrayList<Movie> movies = sharedProtocolData.getMovies();
        Movie movie = getMovie(movieName, movies);

        //fail- movieName exits in the system
        if (movie == null) {
            moviesLock.writeLock().unlock();
            return "ERROR request remmovie failed";
        }

        //fail- the movie rented by at least one user
        if (movie.isRented()) {
            moviesLock.writeLock().unlock();
            return "ERROR request remmovie failed";
        }

        sharedProtocolData.updateMovies(movies);
        moviesLock.writeLock().unlock();
        broadcastMessage="BROADCAST movie " + movieName + " removed";
        return "ACK remmovie " + movieName + " success";
    }

    private String changePriceProcess() {
        String userName = sharedProtocolData.getNameByConnectionId(connectionId);
        String movieName = msg.remove(0);
        int price = new Integer(msg.remove(0));
        User user = isAdmin(userName);

        //if price is > 0
        if (price <= 0)
            return "ERROR request changeprice failed";

        //if the user's type is 'normal'
        if (user == null)
            return "ERROR request changeprice failed";

        moviesLock.writeLock().lock();
        ArrayList<Movie> movies = sharedProtocolData.getMovies();
        Movie movie = getMovie(movieName, movies);

        //fail- movieName exits in the system
        if (movie == null) {
            moviesLock.writeLock().unlock();
            return "ERROR request changeprice failed";
        }

        movie.setPrice(price);
        movies.add(movie);
        sharedProtocolData.updateMovies(movies);
        moviesLock.writeLock().unlock();
        broadcastMessage="BROADCAST movie " + movieName + movie.getAvailableAmount() + " " + price;
        return "ACK changeprice " + movieName + " success";

    }

    private Movie getMovie(String movieName, ArrayList<Movie> movies) {
        AtomicInteger movieIndex = new AtomicInteger(-1);
        movies.forEach(movie -> {
            if (("\"" + movie.getName() + "\"").equals(movieName)) {
                movieIndex.set(movies.indexOf(movie));
            }
        });
        return (movieIndex.get() != -1 ? movies.remove(movieIndex.get()) : null);
    }

    private User getUser(String userName, ArrayList<User> users) {
        AtomicInteger userIndex = new AtomicInteger(-1);
        users.forEach(user -> {
            if (user.getName().equals(userName)) {
                userIndex.set(users.indexOf(user));
            }
        });
        return users.remove(userIndex.get());
    }

    private String rent(String movieName, ArrayList<User> users, ArrayList<Movie> movies, User user, Movie movie) {
        try {
            //the movie isnt exists in the system or the user already rented this movie
            if (movie != null && !user.alreadyRent(movieName) && !movie.isbannedCountry(user.getCountry())) {
                //if the user doesn't live in a banned country of the movie && if the user has enough money
                if (user.reduceBalance(movie.getPrice())) {
                    //if available amount > 0
                    if (movie.rent()) {
                        //we assume that we succeed renting the movie
                        //broadcast to all Logged-in users
                        user.addMovie(movie);
                        broadcastMessage="BROADCAST movie " + movieName + " " + Integer.toString(movie.getAvailableAmount()) + " " + Integer.toString(movie.getPrice());
                        return "ACK rent " + movieName + " success";
                    } else {
                        user.increaseBalance(movie.getPrice());
                    }
                }

            }
            return "ERROR request rent failed";
        } finally {
            if (movie != null) {
                movies.add(movie);
                sharedProtocolData.updateMovies(movies);
            }
            users.add(user);
            sharedProtocolData.updateUsers(users);
            moviesLock.writeLock().unlock();
            usersLock.writeLock().unlock();
        }
    }

    private String returnMovie(String movieName, ArrayList<User> users, ArrayList<Movie> movies, User user, Movie movie) {
        try {
            //if the movie exists in the system and the user rented this movie
            if (movie != null && user.alreadyRent(movieName)) {
                user.remMovie(movie);
                movie.returnMovie();
                broadcastMessage="BROADCAST movie " + movieName + " " + Integer.toString(movie.getAvailableAmount()) + " " + Integer.toString(movie.getPrice());
                return "ACK return " + movieName + " success";
            }
        } finally {
            if (movie != null) {
                movies.add(movie);
                sharedProtocolData.updateMovies(movies);
            }
            users.add(user);
            sharedProtocolData.updateUsers(users);
            moviesLock.writeLock().unlock();
            usersLock.writeLock().unlock();
        }
        return "ERROR request return failed";
    }

    private User isAdmin(String userName) {
        usersLock.readLock().lock();
        ArrayList<User> users = sharedProtocolData.getUsers();
        User user = getUser(userName, users);

        //fail- the user's type is normal
        try {
            if (user.isAdmin()) {
                return user;
            } else return null;
        } finally {
            usersLock.readLock().unlock();
        }
    }
}
