package bgu.spl181.net.impl.BBtpc;

import com.google.gson.Gson;
import bgu.spl181.net.impl.ConnectionsTPC;
import bgu.spl181.net.impl.Movie;
import bgu.spl181.net.impl.User;
import bgu.spl181.net.srv.bidi.BlockingConnectionHandler;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class SharedProtocolData<T>{
    private ConcurrentHashMap<String,Integer> loggedInUsers = new ConcurrentHashMap<>();
    private ConnectionsTPC connectionsTPC;
    protected Gson gson= new Gson();

    public SharedProtocolData(ConnectionsTPC connectionsTPC) {
        this.connectionsTPC =connectionsTPC;
    }
    public boolean broadcastLoggedIn(T msg) {
        AtomicBoolean sentToAll = new AtomicBoolean(true);
        loggedInUsers.values().forEach((connectionId) -> sentToAll.compareAndSet(true, connectionsTPC.send(connectionId,msg)));
        return sentToAll.get();
    }

    public boolean login(String userName, int connectionId) {
        return returnName(connectionId).equals("")&&this.loggedInUsers.putIfAbsent(userName, connectionId) == null;
    }

    public boolean logout(int connectionId) {
        return this.loggedInUsers.remove(returnName(connectionId))!=null;

    }

    public String returnName (int connectionId){
        final String[] name = {""};
        loggedInUsers.forEach((String userName, Integer connection) ->{
            if(connection.equals(connectionId)) {
                name[0] = userName;
                return;
            }
        });
        return name[0];
    }

    //after finishing with the arrayList- should call readLock().unlock
    public ArrayList<Movie> getMovies() {
        File jsonFile = new File("Server/Database/Movies.json");
        try (FileReader reader=new FileReader(jsonFile)){
            Type arrayListType = new TypeToken<ArrayList<Movie>>() {
            }.getType();
            JsonElement jsonElement= gson.fromJson(reader,JsonElement.class);
            return  gson.fromJson(jsonElement.getAsJsonObject().get("movies"),arrayListType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
    //after finishing with the arrayList- should call readLock().unlock
    public ArrayList<User> getUsers() {
        File jsonFile = new File("Server/Database/Users.json");
        try (FileReader reader=new FileReader(jsonFile)){
            Type arrayListType = new TypeToken<ArrayList<User>>() {
            }.getType();
            JsonElement jsonElement= gson.fromJson(reader,JsonElement.class);
            return  gson.fromJson(jsonElement.getAsJsonObject().get("users"),arrayListType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public void updateMovies(ArrayList<Movie> movies) {
        File jsonFile = new File("Server/Database/Movies.json");
        try (FileWriter writer = new FileWriter(jsonFile)) {
            String updatedFile = gson.toJson(movies);
            JsonObject jsonMovies=new JsonObject();
            jsonMovies.addProperty("movies",updatedFile);
            writer.write(jsonMovies.toString());
            writer.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateUsers(ArrayList<User> users) {
        File jsonFile = new File("Server/Database/Users.json");
        try (FileWriter writer = new FileWriter(jsonFile)) {
            String updatedFile = gson.toJson(users);
            JsonObject jsonUsers=new JsonObject();
            jsonUsers.addProperty("users",updatedFile);
            writer.write(jsonUsers.toString());
            writer.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }





}
