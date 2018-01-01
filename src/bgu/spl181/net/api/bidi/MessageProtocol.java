package bgu.spl181.net.api.bidi;

public class MessageProtocol implements BidiMessagingProtocol<String>  {
    @Override
    public void start(int connectionId, Connections connections) {

    }

    @Override
    public String process(String message) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public boolean shouldTerminate() {
        return false;
    }
}
