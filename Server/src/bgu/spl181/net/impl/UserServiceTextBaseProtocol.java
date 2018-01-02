package bgu.spl181.net.impl;

import bgu.spl181.net.api.bidi.BidiMessagingProtocol;
import bgu.spl181.net.api.bidi.Connections;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserServiceTextBaseProtocol implements BidiMessagingProtocol<String> {

    protected boolean done;
    protected int connectionId;
    protected Connections<String> connections;
    protected static final ReentrantReadWriteLock moviesLock=new ReentrantReadWriteLock();
    protected static final ReentrantReadWriteLock usersLock= new ReentrantReadWriteLock();
    protected Gson gson;
    protected ArrayList<String> msg;

    @Override
    public void start(int connectionId, Connections connections) {
        done= false;
        this.connectionId=connectionId;
        this.connections=connections;
        gson = new Gson();
    }

    //if msg is empty at the end of this function that means that the action is 'request'
    @Override
    public String process(String message) {
        this.msg =parseMessage(message);
        switch ((!msg.isEmpty()? msg.remove(0):"" )){
            //return registerProcess ?
            case "REGISTER":{
                return registerProcess();
            }
            case  "LOGIN" :{
                return loginProcess();
            }
            case "SIGNOUT":{
                return signoutProcess();
            }
            case "REQUEST" :{
                return "REQUEST";
            }
        }
        return "";
    }

    protected ArrayList<String> parseMessage(String message) {
        ArrayList<String> command = new ArrayList<>();
        String regex= "\"([^\"]*)\"|(\\S+)";
        Matcher matcher = Pattern.compile(regex).matcher(message);
        while (matcher.find()) {
            command.add(matcher.group());
        }
        return command;
    }

    public String registerProcess(){

    }

    public String loginProcess(){
        throw new UnsupportedOperationException("not implemented");
    }

    public String signoutProcess(){
        throw new UnsupportedOperationException("not implemented");
    }


    //after finishing with the arrayList- should call readLock().unlock
    private ArrayList<Movie> getMovies() {
        moviesLock.readLock().lock();
        File jsonFile = new File("Server/Database/Movies.json");
        try (FileReader reader=new FileReader(jsonFile)){
            Type arrayListType = new TypeToken<Collection<Movie>>() {
            }.getType();
            return gson.fromJson(reader,arrayListType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    //after finishing with the arrayList- should call readLock().unlock
    private ArrayList<Movie> getUsers() {
        usersLock.readLock().lock();
        File jsonFile = new File("Server/Database/Users.json");
        try (FileReader reader=new FileReader(jsonFile)){
            Type arrayListType = new TypeToken<Collection<User>>() {
            }.getType();
            return gson.fromJson(reader,arrayListType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void updateMovies(ArrayList<Movie> movies) {
        moviesLock.writeLock().lock();
        File jsonFile = new File("Server/Database/Movies.json");
        try (FileWriter writer = new FileWriter(jsonFile)) {
            String updatedFile = gson.toJson(movies);
            writer.write(updatedFile);
            writer.flush();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            moviesLock.writeLock().unlock();
        }
    }

    private void updateUsers(ArrayList<User> users) {
        usersLock.writeLock().lock();
        File jsonFile = new File("Server/Database/Users.json");
        try (FileWriter writer = new FileWriter(jsonFile)) {
            String updatedFile = gson.toJson(users);
            writer.write(updatedFile);
            writer.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            usersLock.writeLock().unlock();
        }

    }


        @Override
    public boolean shouldTerminate() {
        return done;
    }
}
