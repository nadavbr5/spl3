package bgu.spl181.net.impl;


import bgu.spl181.net.api.bidi.Connections;
import bgu.spl181.net.srv.bidi.BlockingConnectionHandler;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class ConnectionsTPC<T> implements Connections<T> {

    private ConcurrentHashMap<Integer, BlockingConnectionHandler<T>> map = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Integer, String> connectionByName = new ConcurrentHashMap<>();

    @Override
    public boolean send(int connectionId, T msg) {
        return map.containsKey(connectionId) && map.get(connectionId).send(msg);
    }

    @Override
    public boolean broadcast(T msg) {
        AtomicBoolean sentToAll = new AtomicBoolean(true);
        map.values().forEach((connection) -> sentToAll.compareAndSet(true, connection.send(msg)));
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
        return this.connectionByName.putIfAbsent(connectionId, userName) == null;
    }

    public boolean logout(int connectionId) {
        return this.connectionByName.remove(connectionId) != null;
    }

    public String returnName (int connectionId){
        return this.connectionByName.getOrDefault(connectionId,"");
    }


}

