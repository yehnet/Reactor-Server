package bgu.spl.net.impl.bidi;

import bgu.spl.net.impl.bidi.Messages.NotificationMessage;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class BGSuser {

    //========================================================Fields===========================================================//

    private int connectionID;
    private String name;
    private String password;
    private ConcurrentLinkedQueue<NotificationMessage> posts=new ConcurrentLinkedQueue<>(); // queue that save all the messages that received when the user is not logged in
    public List<String> followers = new LinkedList<>();


    //========================================================Constructor===========================================================//

    public BGSuser (int connectionID, String name , String password){
        this.name=name;
        this.password=password;
        this.connectionID=connectionID;
    }

    //========================================================Methods===========================================================//

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public int getConnectionID() {
        return connectionID;
    }

    public ConcurrentLinkedQueue<NotificationMessage> getPosts() {
        return posts;
    }

    public void setConnectionID(int connectionID){
        this.connectionID=connectionID;
    }
}
