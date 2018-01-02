package bgu.spl181.net.impl;

import bgu.spl181.net.api.bidi.Connections;

public class MovieRentalProtocol extends UserServiceTextBaseProtocol {

    @Override
    public void start(int connectionId, Connections connections) {
        super.start(connectionId, connections);
    }

    @Override
    public String process(String message) {
        return super.process(message);
    }

    public String balanceInfoProcess(String message){
        throw new UnsupportedOperationException("not implemented");
    }

    public String balanceAddProcess(String message){
        throw new UnsupportedOperationException("not implemented");
    }

    public String infoProcess(String message){
        throw new UnsupportedOperationException("not implemented");
    }

    public String returnProcess(String message){
        throw new UnsupportedOperationException("not implemented");
    }

    public String addMovieProcess(String message){
        throw new UnsupportedOperationException("not implemented");
    }

    public String reMovieProcess(String message){
        throw new UnsupportedOperationException("not implemented");
    }

    public String changePriceProcess(String message){
        throw new UnsupportedOperationException("not implemented");
    }
}
