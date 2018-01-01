package bgu.spl181.net.impl;


import bgu.spl181.net.api.bidi.Connections;
import bgu.spl181.net.srv.bidi.BlockingConnectionHandler;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class ConnectionsTPC<String> implements Connections<String> {

    private ConcurrentHashMap<Integer, BlockingConnectionHandler<String>> map = new ConcurrentHashMap<>();

    @Override
    public boolean send(int connectionId, String msg) {
        return map.containsKey(connectionId) && map.get(connectionId).send(msg);
    }

    @Override
    public boolean broadcast(String msg) {
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
}

