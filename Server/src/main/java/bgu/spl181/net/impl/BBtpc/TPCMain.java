package bgu.spl181.net.impl.BBtpc;

import bgu.spl181.net.impl.ConnectionsImpl;
import bgu.spl181.net.impl.EncoderDecoder;
import bgu.spl181.net.impl.MovieRentalProtocol;
import bgu.spl181.net.srv.Server;
import bgu.spl181.net.srv.TpcServer;

import java.io.IOException;

public class TPCMain {
    public static void main(String[] args) {
        ConnectionsImpl connectionsImpl =new ConnectionsImpl();
        SharedProtocolData sharedProtocolData =new SharedProtocolData(connectionsImpl);
        try ( Server server = new TpcServer(7777, ()->new MovieRentalProtocol(sharedProtocolData), EncoderDecoder::new, connectionsImpl)) {
            server.serve();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

