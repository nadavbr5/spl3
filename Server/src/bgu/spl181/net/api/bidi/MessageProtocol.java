package Server.src.bgu.spl181.net.api.bidi;

public class MessageProtocol implements BidiMessagingProtocol<String>  {

    private boolean done;
    private int connectionId;
    private Connections<String> connections;


    @Override

    public void start(int connectionId, Connections connections) {
        done= false;
        this.connectionId=connectionId;
        this.connections=connections;
    }

    @Override
    public String process(String message) {

        throw new UnsupportedOperationException("not implemented");
    }

    public String registerProcess(String message){
        throw new UnsupportedOperationException("not implemented");
    }

    public String loginProcess(String message){
        throw new UnsupportedOperationException("not implemented");
    }

    public String logoutProcess(String message){
        throw new UnsupportedOperationException("not implemented");
    }

    public String balanceInfoProcess(String message){
        throw new UnsupportedOperationException("not implemented");
    }

    public String addProcess(String message){
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


    @Override
    public boolean shouldTerminate() {
        return done;
    }
}
