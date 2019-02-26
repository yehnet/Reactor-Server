package bgu.spl.net.srv;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.impl.bidi.BidiMessagingProtocol;
import bgu.spl.net.impl.bidi.Connections;
import bgu.spl.net.impl.bidi.ConnectionsImpl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

public class BlockingConnectionHandler<T> implements Runnable, ConnectionHandler<T> {

    private final BidiMessagingProtocol<T> protocol;
    private final MessageEncoderDecoder<T> encdec;
    private final Socket sock;
    private BufferedInputStream in;
    private BufferedOutputStream out;
    private volatile boolean connected = true;

    //
    private Connections<T> connections;
    private int connectionID;
    //

    public BlockingConnectionHandler(Socket sock, MessageEncoderDecoder<T> reader, BidiMessagingProtocol<T> protocol , int connectionID , Connections connections) {
        this.sock = sock;
        this.encdec = reader;
        this.protocol = protocol;


        //
        this.connectionID=connectionID;
        this.connections=connections;
        //
    }

    @Override
    public void run() {
        try (Socket sock = this.sock) { //just for automatic closing
            int read;


            //
            protocol.start(connectionID, connections);
            //



            in = new BufferedInputStream(sock.getInputStream());
            out = new BufferedOutputStream(sock.getOutputStream());

            while (!protocol.shouldTerminate() && connected && (read = in.read()) >= 0) {
                T nextMessage = encdec.decodeNextByte((byte) read);
                if (nextMessage != null) {
                    protocol.process(nextMessage);
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    @Override //my add
    public void send(T msg) {
        try {
            byte[] ans = encdec.encode(msg);
            if (ans.length != 0) {
                synchronized (out) {
                    out.write(ans);
                    out.flush();
                }
            }
        }catch (IOException ex){
            ex.printStackTrace();
        }

    }

    @Override
    public void close() throws IOException {
        connected = false;
        sock.close();
    }
}
