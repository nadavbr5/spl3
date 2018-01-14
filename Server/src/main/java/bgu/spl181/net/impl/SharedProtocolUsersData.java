package bgu.spl181.net.impl;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
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


public class SharedProtocolUsersData<T> {
    protected final String usersPath = "Database/Users.json";
    protected ConcurrentHashMap<Integer, String> loggedInUsers = new ConcurrentHashMap<>();
    protected ConnectionsImpl connectionsImpl;
    protected Gson gson;

    public SharedProtocolUsersData(ConnectionsImpl connectionsImpl) {
        this.connectionsImpl = connectionsImpl;
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
