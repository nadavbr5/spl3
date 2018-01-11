package bgu.spl181.net.impl.BBreactor;

import bgu.spl181.net.impl.BBtpc.SharedProtocolData;
import bgu.spl181.net.impl.ConnectionsImpl;
import bgu.spl181.net.impl.EncoderDecoder;
import bgu.spl181.net.impl.MovieRentalProtocol;
import bgu.spl181.net.srv.Server;

import java.io.IOException;

public class ReactorMain {
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
