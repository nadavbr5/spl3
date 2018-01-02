package main.java.bgu.spl181.net.impl;

import main.java.bgu.spl181.net.api.bidi.Connections;

public class MovieRentalProtocol extends UserServiceTextBaseProtocol {

    @Override
    public void start(int connectionId, Connections connections) {
        super.start(connectionId, connections);
    }

    @Override
    public String process(String message) {
        String ans= super.process(message);
        if(ans == "REQUEST"){
            switch ((!msg.isEmpty()? msg.get(0):"" )){
                case "balance":{
                    switch ((!msg.isEmpty()? msg.remove(0):"" )){
                        case "info":{
                            msg.remove(0);
                            return balanceInfoProcess();
                        }
                        case "add":{
                            msg.remove(0);
                            return balanceAddProcess();
                        }
                    }
                }
                case "info": {
                    return infoProcess();
                }
                case  "rent" :{
                    return reMovieProcess();
                }
                case "return":{
                    return signoutProcess();
                }
                case "addmovie" :{
                    return addMovieProcess();
                }
                case "removie":{
                    return reMovieProcess();
                }
                case "changeprice":{
                    return changePriceProcess();
                }
            }
        }
        return ans;
    }

    public String balanceInfoProcess(){
//        double balance = connections.
        return "";
    }

    public String balanceAddProcess(){
        throw new UnsupportedOperationException("not implemented");
    }

    public String infoProcess(){
        throw new UnsupportedOperationException("not implemented");
    }

    public String returnProcess(){
        throw new UnsupportedOperationException("not implemented");
    }

    public String addMovieProcess(){
        throw new UnsupportedOperationException("not implemented");
    }

    public String reMovieProcess(){
        throw new UnsupportedOperationException("not implemented");
    }

    public String changePriceProcess(){
        throw new UnsupportedOperationException("not implemented");
    }
}
