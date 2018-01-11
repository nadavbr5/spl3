package bgu.spl181.net.impl;


import bgu.spl181.net.api.bidi.Connections;
import bgu.spl181.net.srv.bidi.ConnectionHandler;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class ConnectionsImpl<T> implements Connections<T> {
private ConcurrentHashMap<Integer,ConnectionHandler<T>> map=new ConcurrentHashMap<>();

    @Override

    public boolean send(int connectionId, T msg) {
        return map.get(connectionId).send(msg);
    }

    @Override
    public boolean broadcast(T msg) {
        AtomicBoolean sentToAll = new AtomicBoolean(true);
        map.values().forEach((connectionHandler) -> sentToAll.compareAndSet(true, connectionHandler.send(msg)));
        return sentToAll.get();
    }

    @Override
    public boolean disconnect(int connectionId) {
       return map.remove(connectionId)!=null;

    }

    //returns null if the connection doest exists in the system
    public boolean connect(int connectionId, ConnectionHandler<T> b) {
        return this.map.putIfAbsent(connectionId, b) == null;
    }

}

