package bgu.spl181.net.srv;

import bgu.spl181.net.api.MessageEncoderDecoder;
import bgu.spl181.net.api.MessagingProtocol;
import bgu.spl181.net.api.bidi.Connections;
import bgu.spl181.net.srv.bidi.BlockingConnectionHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class TcpServer<T> implements Server<T> {

    private final int port;
    private final Supplier<MessagingProtocol<T>> protocolFactory;
    private final Supplier<MessageEncoderDecoder<T>> encdecFactory;
    private ServerSocket sock;
    private Connections<T> connections;

    public TcpServer(
            int port,
            Supplier<MessagingProtocol<T>> protocolFactory,
            Supplier<MessageEncoderDecoder<T>> encdecFactory) {

        this.port = port;
        this.protocolFactory = protocolFactory;
        this.encdecFactory = encdecFactory;
		this.sock = null;
		connections=new Connections<T>() {
		    private ConcurrentHashMap<Integer,BlockingConnectionHandler<T>> map=new ConcurrentHashMap<>();
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
        };
    }

    @Override
    public void serve() {

        try (ServerSocket serverSock = new ServerSocket(port)) {
			System.out.println("Server started");

            this.sock = serverSock; //just to be able to close

            while (!Thread.currentThread().isInterrupted()) {

                Socket clientSock = serverSock.accept();

                BlockingConnectionHandler<T> handler = new BlockingConnectionHandler<>(
                        clientSock,
                        encdecFactory.get(),
                        protocolFactory.get());

                execute(handler);
            }
        } catch (IOException ex) {
        }

        System.out.println("server closed!!!");
    }

    @Override
    public void close() throws IOException {
		if (sock != null)
			sock.close();
    }

    private void execute(BlockingConnectionHandler<T> handler) {
        new Thread(handler).start();
    }
}
