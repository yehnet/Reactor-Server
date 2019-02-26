package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.impl.bidi.BGSDataBase;
import bgu.spl.net.impl.bidi.BidiMessageProtocolImpl;
import bgu.spl.net.impl.bidi.MessageEncoderDecoderImpl;
import bgu.spl.net.srv.Server;

public class ReactorMain {

    public static void main(String[] args) {

        BGSDataBase dataBase=new BGSDataBase();

        Server.reactor(
                Runtime.getRuntime().availableProcessors(), 7777, ()-> new BidiMessageProtocolImpl(dataBase) , MessageEncoderDecoderImpl::new).serve();
    }
}

