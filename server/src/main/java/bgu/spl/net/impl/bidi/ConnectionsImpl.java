package bgu.spl.net.impl.bidi;

import bgu.spl.net.srv.ConnectionHandler;

import java.util.HashMap;
import java.util.Map;

public class ConnectionsImpl<T> implements Connections<T> {

    //========================================================Fields===========================================================//

    private HashMap<Integer, ConnectionHandler<T>> connections;

    //========================================================Constructor===========================================================//

    public ConnectionsImpl(HashMap<Integer, ConnectionHandler<T>>  connections){
        this.connections=connections;
    }


    //========================================================Methods===========================================================//

    @Override
    public boolean send(int connectionId, T msg){
        boolean sent=false;
        if(connections.containsKey(connectionId)){
            connections.get(connectionId).send(msg);
            sent=true;
        }
        return sent;
    }

    @Override
    public void broadcast(T msg) {
        for(ConnectionHandler<T> connects: connections.values()){
            connects.send(msg);
        }
    }

    @Override
    public void disconnect(int connectionId) {
        connections.remove(connectionId);
    }


    // adds users to connections
    public void add(int connectionID, ConnectionHandler<T> handler){
        connections.put(connectionID, handler);
    }
    //
}

