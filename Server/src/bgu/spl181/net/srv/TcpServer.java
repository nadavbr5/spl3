package Server.src.bgu.spl181.net.srv;

import Server.src.bgu.spl181.net.api.MessageEncoderDecoder;
import Server.src.bgu.spl181.net.api.bidi.BidiMessagingProtocol;
import Server.src.bgu.spl181.net.api.bidi.Connections;
import Server.src.bgu.spl181.net.api.bidi.ConnectionsTPC;
import Server.src.bgu.spl181.net.srv.bidi.BlockingConnectionHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Supplier;

public class TcpServer<T> implements Server<T> {

    private final int port;
    private final Supplier<BidiMessagingProtocol<T>> protocolFactory;
    private final Supplier<MessageEncoderDecoder<T>> encdecFactory;
    private ServerSocket sock;
    private Connections<String> connections;

    public TcpServer(
            int port,
            Supplier<BidiMessagingProtocol<T>> protocolFactory,
            Supplier<MessageEncoderDecoder<T>> encdecFactory) {

        this.port = port;
        this.protocolFactory = protocolFactory;
        this.encdecFactory = encdecFactory;
        this.sock = null;
        connections = new ConnectionsTPC<>();
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
