package bgu.spl181.net.impl;

import bgu.spl181.net.impl.BBtpc.SharedProtocolData;
import bgu.spl181.net.api.bidi.BidiMessagingProtocol;
import bgu.spl181.net.api.bidi.Connections;

import java.util.ArrayList;
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
    protected ArrayList<String> msg;
    protected static SharedProtocolData sharedProtocolData= new SharedProtocolData();
    protected String response;
    @Override
    public void start(int connectionId, Connections connections) {
        done= false;
        this.connectionId=connectionId;
        this.connections= (ConnectionsTPC) connections;
        response=null;
    }

    //if msg is empty at the end of this function that means that the action is 'request'
    @Override
    public void process(String message) {
        this.msg =parseMessage(message);
        switch ((!msg.isEmpty()? msg.remove(0):"" )){
            //return registerProcess ?
            case "REGISTER":{
                response= registerProcess();
            }
            case  "LOGIN" :{
                response= loginProcess();
            }
            case "SIGNOUT":{
                response= signoutProcess();
            }
            case "REQUEST" :{
                response= requestProccess();
            }
            if (!response.equals(""))
                connections.send(connectionId,msg);
        }
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

    private String registerProcess(){
        if(msg.size()<3)
            return "ERROR registration failed";
        String userName= this.msg.remove(0);
        String password= this.msg.remove(0);
       //if the user name already exists in the system- returns error
        usersLock.writeLock().lock();
            ArrayList <User> users= sharedProtocolData.getUsers();
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
        if(this.msg.get(0) == null)
            return "ACK registration succeeded";
        else{
            this.msg.add(0,userName);
            this.msg.add(0, "REGISTER");
        }
        return "";
    }

    private String loginProcess(){
        String userName= this.msg.remove(0);
        String password= this.msg.remove(0);
        usersLock.readLock().lock();
        ArrayList <User> users= sharedProtocolData.getUsers();
        AtomicBoolean isValid=new AtomicBoolean();
        users.forEach((user ->isValid.compareAndSet(false,
                user.getName().equals(userName)&&
                        user.getPassword().equals(password)) ));
        try{
            if (!isValid.get()||!sharedProtocolData.login(userName,connectionId)) {
                return "ERROR login failed";
            }
            return "ACK login succeeded";
        }finally {
            usersLock.readLock().unlock();
        }
    }

    private String signoutProcess(){
        if (sharedProtocolData.logout(connectionId)&&connections.disconnect(connectionId)){
            return "ACK logout succeeded";
        }
        else return "ERROR logout failed";
    }

    //returns "REQUEST" if the user is login , else returns ""
    private String requestProccess(){
        String userName= sharedProtocolData.returnName(connectionId);
        if(sharedProtocolData.login(userName, connectionId))
            return "REQUEST";
        return "";
    }


        @Override
    public boolean shouldTerminate() {
        return done;
    }
}
