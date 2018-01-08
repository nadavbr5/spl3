package main.java.bgu.spl181.net.impl.BBtpc;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import main.java.bgu.spl181.net.impl.ConnectionsImpl;
import main.java.bgu.spl181.net.impl.Movie;
import main.java.bgu.spl181.net.impl.User;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class SharedProtocolData<T> {
    private ConcurrentHashMap<Integer, String> loggedInUsers = new ConcurrentHashMap<>();
    private ConnectionsImpl connectionsImpl;
    protected Gson gson;
    private final String usersPath = "Server/Database/Users.json";
    private final String moviesPath = "Server/Database/Movies.json";

    public SharedProtocolData(ConnectionsImpl connectionsImpl) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        gsonBuilder.registerTypeAdapter(Integer.class, (JsonSerializer<Integer>) (integer, type, jsonSerializationContext) -> new JsonPrimitive(integer.toString()));
        gson=gsonBuilder.create();
        this.connectionsImpl=connectionsImpl;
    }

    public boolean broadcastLoggedIn(T msg) {
        AtomicBoolean sentToAll = new AtomicBoolean(true);
        loggedInUsers.keySet().forEach((connectionId) -> sentToAll.compareAndSet(true, connectionsImpl.send(connectionId, msg)));
        return sentToAll.get();
    }

    public boolean login(String userName, int connectionId) {
        return (!loggedInUsers.containsKey(connectionId) && !loggedInUsers.contains(userName)) && this.loggedInUsers.putIfAbsent(connectionId, userName) == null;
    }

    public boolean logout(int connectionId) {
        return this.loggedInUsers.remove(connectionId) != null;

    }


    //after finishing with the arrayList- should call readLock().unlock
    public ArrayList<Movie> getMovies() {
        File jsonFile = new File(moviesPath);
        try (FileReader reader = new FileReader(jsonFile)) {
            Type arrayListType = new TypeToken<ArrayList<Movie>>() {
            }.getType();
            JsonElement jsonElement = gson.fromJson(reader, JsonElement.class);
            return gson.fromJson(jsonElement.getAsJsonObject().get("movies"), arrayListType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    //after finishing with the arrayList- should call readLock().unlock
    public ArrayList<User> getUsers() {
        File jsonFile = new File(usersPath);
        try (FileReader reader = new FileReader(jsonFile)) {
            Type arrayListType = new TypeToken<ArrayList<User>>() {
            }.getType();
            JsonElement jsonElement = gson.fromJson(reader, JsonElement.class);
            return gson.fromJson(jsonElement.getAsJsonObject().get("users"), arrayListType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public void updateMovies(ArrayList<Movie> movies) {
        File jsonFile = new File(moviesPath);
        try (FileWriter writer = new FileWriter(jsonFile)) {
            JsonArray jsonArrayMovies = gson.toJsonTree(movies).getAsJsonArray();
            JsonObject jsonMovies = new JsonObject();
            jsonMovies.add("movies", jsonArrayMovies);
            writer.write(jsonMovies.toString());
            writer.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateUsers(ArrayList<User> users) {
        File jsonFile = new File(usersPath);
        try (FileWriter writer = new FileWriter(jsonFile)) {
            JsonArray jsonArrayUsers = gson.toJsonTree(users).getAsJsonArray();
            JsonObject jsonUsers = new JsonObject();
            jsonUsers.add("users", jsonArrayUsers);
            writer.write(jsonUsers.toString());
            writer.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String getNameByConnectionId(int connectionId) {
        return loggedInUsers.get(connectionId);
    }


}
