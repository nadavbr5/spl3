package bgu.spl181.net.impl;

import bgu.spl181.net.api.bidi.MessageEncoderDecoder;

public class EncoderDecoder implements MessageEncoderDecoder<String> {
    private byte[] msg=new byte[1<<16];
    private int i=0;
    @Override
    public String decodeNextByte(byte nextByte) {
        msg[i++]=nextByte;
        if (nextByte == '\n') {
            String str=new String(msg,0,i);
            i=0;
            return str;
        }
        return null;
    }

    @Override
    public byte[] encode(String message) {
        return message.getBytes();
    }
}
