package bgu.spl181.net.impl.BBtpc;

import bgu.spl181.net.api.bidi.BidiMessagingProtocol;
import bgu.spl181.net.impl.EncoderDecoder;
import bgu.spl181.net.impl.UserServiceTextBaseProtocol;
import bgu.spl181.net.srv.Server;
import bgu.spl181.net.srv.TpcServer;

import java.io.IOException;
import java.util.function.Supplier;

public class TPCMain {
    public static void main(String[] args) {
        try ( Server server = new TpcServer(7777, UserServiceTextBaseProtocol::new, EncoderDecoder::new)) {
            server.serve();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

