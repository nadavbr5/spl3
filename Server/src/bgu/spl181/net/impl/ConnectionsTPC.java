package bgu.spl181.net.impl;


import bgu.spl181.net.api.bidi.Connections;
import bgu.spl181.net.srv.bidi.BlockingConnectionHandler;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class ConnectionsTPC<T> implements Connections<T> {

    private ConcurrentHashMap<Integer, BlockingConnectionHandler<T>> map = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String,Integer> connectionByName = new ConcurrentHashMap<>();

    @Override
    public boolean send(int connectionId, T msg) {
        String name= returnName(connectionId);
        return (connectionByName.contains(name) && map.get(connectionId).send(msg));
    }

    @Override
    public boolean broadcast(T msg) {
        AtomicBoolean sentToAll = new AtomicBoolean(true);
       connectionByName.values().forEach((connectionId) -> sentToAll.compareAndSet(true, map.get(connectionId).send(msg)));
        return sentToAll.get();
    }

    @Override
    public void disconnect(int connectionId) {
        BlockingConnectionHandler handler = map.remove(connectionId);
        if (handler != null) {
            try {
                handler.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //returns null if the connection doest exists in the system
    public boolean connect(int connectionId, BlockingConnectionHandler<T> b) {
        return this.map.putIfAbsent(connectionId, b) == null;
    }

    public boolean login(String userName, int connectionId) {
        return returnName(connectionId).equals("")&&this.connectionByName.putIfAbsent(userName, connectionId) == null;
    }

    public boolean logout(int connectionId) {
        return this.connectionByName.remove(returnName(connectionId)) != null;
    }

    public String returnName (int connectionId){
        final String[] name = {""};
       connectionByName.forEach((String userName, Integer connection) ->{
           if(connection.equals(connectionId)) {
               name[0] = userName;
               return;
           }
       });
       return name[0];
    }


}

