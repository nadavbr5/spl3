package bgu.spl181.net.api.bidi;

public interface Connections<T> {

    boolean send(int connectionId, T msg);

    boolean broadcast(T msg);

    boolean disconnect(int connectionId);
}
