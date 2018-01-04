package main.java.bgu.spl181.net.impl.BBtpc;

import main.java.bgu.spl181.net.impl.ConnectionsTPC;
import main.java.bgu.spl181.net.impl.EncoderDecoder;
import main.java.bgu.spl181.net.impl.MovieRentalProtocol;
import main.java.bgu.spl181.net.impl.UserServiceTextBaseProtocol;
import main.java.bgu.spl181.net.srv.Server;
import main.java.bgu.spl181.net.srv.TpcServer;

import java.io.IOException;

public class TPCMain {
    public static void main(String[] args) {
        ConnectionsTPC connectionsTPC=new ConnectionsTPC();
        SharedProtocolData sharedProtocolData =new SharedProtocolData(connectionsTPC);
        try ( Server server = new TpcServer(7777, ()->new MovieRentalProtocol(sharedProtocolData), EncoderDecoder::new,connectionsTPC)) {
            server.serve();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

