package main.java.bgu.spl181.net.impl.BBreactor;

import main.java.bgu.spl181.net.impl.BBtpc.SharedProtocolData;
import main.java.bgu.spl181.net.impl.ConnectionsImpl;
import main.java.bgu.spl181.net.impl.EncoderDecoder;
import main.java.bgu.spl181.net.impl.MovieRentalProtocol;
import main.java.bgu.spl181.net.srv.Reactor;
import main.java.bgu.spl181.net.srv.Server;
import main.java.bgu.spl181.net.srv.TpcServer;

import java.io.IOException;

public class BBReactor {
    public static void main(String[] args) {
        ConnectionsImpl connectionsImpl =new ConnectionsImpl();
        SharedProtocolData sharedProtocolData =new SharedProtocolData(connectionsImpl);
        try ( Server server = Server.reactor(8,7777, ()->new MovieRentalProtocol(sharedProtocolData), EncoderDecoder::new, connectionsImpl)) {
            server.serve();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
