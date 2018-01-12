package bgu.spl181.net.impl;


import bgu.spl181.net.api.bidi.BidiMessagingProtocol;
import bgu.spl181.net.api.bidi.Connections;
import bgu.spl181.net.impl.BBtpc.SharedProtocolData;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserServiceTextBaseProtocol implements BidiMessagingProtocol<String> {

    protected boolean done;
    protected int connectionId;
    protected ConnectionsImpl connections;
    protected static final ReentrantReadWriteLock moviesLock = new ReentrantReadWriteLock(true);
    protected static final ReentrantReadWriteLock usersLock = new ReentrantReadWriteLock(true);
    protected ArrayList<String> msg;
    protected SharedProtocolData sharedProtocolData;
    protected String response;
    protected boolean isLoggedIn = false;

    public UserServiceTextBaseProtocol(SharedProtocolData sharedProtocolData) {
        this.sharedProtocolData = sharedProtocolData;

    }

    @Override
    public void start(int connectionId, Connections connections) {
        done = false;
        this.connectionId = connectionId;
        this.connections = (ConnectionsImpl) connections;
        response = "";
    }

    //if msg is empty at the end of this function that means that the action is 'request'
    @Override
    public void process(String message) {
        this.msg = parseMessage(message);
        switch ((!msg.isEmpty() ? msg.remove(0) : "")) {
            //return registerProcess ?
            case "REGISTER": {
                response = registerProcess();
                break;
            }
            case "LOGIN": {
                response = loginProcess();
                break;
            }
            case "SIGNOUT": {
                response = signoutProcess();
                break;
            }
            case "REQUEST": {
                response = "CONTINUE";
                break;
            }
        }
        if (!response.equals("CONTINUE") && !response.equals("SIGNOUT"))
            connections.send(connectionId, response);
    }

    protected ArrayList<String> parseMessage(String message) {
        ArrayList<String> command = new ArrayList<>();
        String regex = "\"([^\"]*)\"|(\\S+)";
        Matcher matcher = Pattern.compile(regex).matcher(message);
        while (matcher.find()) {
            command.add(matcher.group());
        }
        return command;
    }

    private String registerProcess() {
        if (msg.size() < 3)
            return "ERROR registration failed";
        String userName = this.msg.remove(0);
        String password = this.msg.remove(0);
        if (isLoggedIn)
            return "ERROR registration failed";
        //if the user name already exists in the system- returns error
        usersLock.writeLock().lock();
        ArrayList<User> users = sharedProtocolData.getUsers();
        AtomicBoolean isRegistered = new AtomicBoolean();
        users.forEach((user -> isRegistered.compareAndSet(false, user.getName().equals(userName))));
        try {
            if (isRegistered.get())
                return "ERROR registration failed";
        } finally {
            usersLock.writeLock().unlock();
        }
        User reg = new User(userName, "normal", password, new String());
        users.add(reg);
        sharedProtocolData.updateUsers(users);
        if (this.msg.get(0) == null)
            return "ACK registration succeeded";
        else {
            this.msg.add(0, userName);
            this.msg.add(0, "REGISTER");
        }
        return "CONTINUE";
    }

    private String loginProcess() {
        String userName = this.msg.remove(0);
        String password = this.msg.remove(0);
        usersLock.readLock().lock();
        ArrayList<User> users = sharedProtocolData.getUsers();
        AtomicBoolean isValid = new AtomicBoolean();
        users.forEach((user -> isValid.compareAndSet(false,
                user.getName().equals(userName) &&
                        user.getPassword().equals(password))));
        try {
            if (!isValid.get() || !sharedProtocolData.login(userName, connectionId)) {
                return "ERROR login failed";
            }
            isLoggedIn = true;
            return "ACK login succeeded";
        } finally {
            usersLock.readLock().unlock();
        }
    }

    private String signoutProcess() {
        if (sharedProtocolData.logout(connectionId)) {
            isLoggedIn = false;
            done = false;
            connections.send(connectionId, "ACK signout succeeded");
            connections.disconnect(connectionId);
            return "SIGNOUT";
        } else return "ERROR signout failed";

    }


    @Override
    public boolean shouldTerminate() {
        return done;
    }
}
