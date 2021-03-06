package bgu.spl181.net.impl.BBreactor;

import bgu.spl181.net.impl.ConnectionsImpl;
import bgu.spl181.net.impl.EncoderDecoder;
import bgu.spl181.net.impl.MovieRentalProtocol;
import bgu.spl181.net.impl.SharedProtocolMovieData;
import bgu.spl181.net.srv.Server;

import java.io.IOException;

public class ReactorMain {
    public static void main(String[] args) {
        ConnectionsImpl connectionsImpl =new ConnectionsImpl();
        SharedProtocolMovieData sharedProtocolData = new SharedProtocolMovieData(connectionsImpl);
        try (Server server = Server.reactor(8, Integer.parseInt(args[0]), () -> new MovieRentalProtocol(sharedProtocolData), EncoderDecoder::new, connectionsImpl)) {
            server.serve();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
