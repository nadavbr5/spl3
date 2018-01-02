package bgu.spl181.net.impl;

import bgu.spl181.net.api.bidi.BidiMessagingProtocol;
import bgu.spl181.net.api.bidi.Connections;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserServiceTextBaseProtocol implements BidiMessagingProtocol<String> {

    protected boolean done;
    protected int connectionId;
    protected ConnectionsTPC connections;
    protected static final ReentrantReadWriteLock moviesLock=new ReentrantReadWriteLock();
    protected static final ReentrantReadWriteLock usersLock= new ReentrantReadWriteLock();
    protected Gson gson;
    protected ArrayList<String> msg;

    @Override
    public void start(int connectionId, Connections connections) {
        done= false;
        this.connectionId=connectionId;
        this.connections= (ConnectionsTPC) connections;
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
        if(msg.size()<3)
            return "ERROR registration failed";
        String userName= this.msg.remove(0);
        String password= this.msg.remove(0);
        //TODO: change this string 'country= ... ' to the name of the country
        String country= this.msg.remove(0);
       //if the user name already exists in the system- returns error
       ArrayList <User> users= getUsers();
        AtomicBoolean isRegistered=new AtomicBoolean();
       users.forEach((user ->isRegistered.compareAndSet(false,user.getName().equals(userName)) ));
      try {
          if (isRegistered.get())
              return "ERROR registration failed";
      }finally {
          usersLock.writeLock().unlock();
      }
        User reg = new User(userName,"normal", password);
        users.add(reg);
        updateUsers(users);
        return "ACK registration succeeded";
    }

    public String loginProcess(){
        String userName= this.msg.remove(0);
        String password= this.msg.remove(0);
        ArrayList <User> users= getUsers();
        AtomicBoolean isValid=new AtomicBoolean();
        users.forEach((user ->isValid.compareAndSet(false,user.getName().equals(userName)&&user.getPassword().equals(password)) ));
        try{
            if (!isValid.get()||!connections.login(userName,connectionId)) {
                return "ERROR login failed";
            }
            return "ACK login succeeded";
        }finally {
            usersLock.writeLock().unlock();
        }
    }

    public String signoutProcess(){
        return connections.logout(connectionId) ? "ACK logout succeeded" : "ERROR logout failed";
    }

    //after finishing with the arrayList- should call readLock().unlock
    private ArrayList<Movie> getMovies() {
        moviesLock.writeLock().lock();
        File jsonFile = new File("Server/Database/Movies.json");
        try (FileReader reader=new FileReader(jsonFile)){
            Type arrayListType = new TypeToken<Collection<Movie>>() {
            }.getType();
            return gson.fromJson(reader,arrayListType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
    //after finishing with the arrayList- should call readLock().unlock
    private ArrayList<User> getUsers() {
        usersLock.writeLock().lock();
        File jsonFile = new File("Server/Database/Users.json");
        try (FileReader reader=new FileReader(jsonFile)){
            Type arrayListType = new TypeToken<Collection<User>>() {
            }.getType();
            return gson.fromJson(reader,arrayListType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    private void updateMovies(ArrayList<Movie> movies) {
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
