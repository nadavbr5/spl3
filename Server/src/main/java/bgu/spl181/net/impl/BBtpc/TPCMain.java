package main.java.bgu.spl181.net.impl.BBtpc;

import main.java.bgu.spl181.net.impl.EncoderDecoder;
import main.java.bgu.spl181.net.impl.MovieRentalProtocol;
import main.java.bgu.spl181.net.srv.Server;
import main.java.bgu.spl181.net.srv.TpcServer;

import java.io.IOException;

public class TPCMain {
    public static void main(String[] args) {
        main.java.bgu.spl181.net.impl.ConnectionsImpl connectionsImpl =new main.java.bgu.spl181.net.impl.ConnectionsImpl();
        SharedProtocolData sharedProtocolData =new SharedProtocolData(connectionsImpl);
        try ( Server server = new TpcServer(7777, ()->new MovieRentalProtocol(sharedProtocolData), EncoderDecoder::new, connectionsImpl)) {
            server.serve();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

