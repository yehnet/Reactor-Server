package bgu.spl.net.impl.BGSServer;
import bgu.spl.net.impl.bidi.BGSDataBase;
import bgu.spl.net.impl.bidi.BidiMessageProtocolImpl;
import bgu.spl.net.impl.bidi.MessageEncoderDecoderImpl;
import bgu.spl.net.srv.Server;

public class TPCMain {

    public static void main(String[] args) {

        BGSDataBase dataBase = new BGSDataBase();

        Server.threadPerClient(7777, ()-> new BidiMessageProtocolImpl(dataBase), MessageEncoderDecoderImpl::new).serve();
    }
}
