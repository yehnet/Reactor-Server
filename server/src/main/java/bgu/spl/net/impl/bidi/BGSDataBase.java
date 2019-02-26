package bgu.spl.net.impl.bidi;


import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

public class BGSDataBase {

    //========================================================Fields===========================================================//

    public List<BGSuser> users; //list of users that logged in
    public ConcurrentHashMap<Integer,BGSuser> loginUsers; //map of users that registered
    public final ConcurrentHashMap<BGSuser, Vector<String>> postMessagesPerUser; //all the messages that post and pm by a user
    public final ConcurrentHashMap<BGSuser, Vector<String>> allMessagesPerUser; //all the post messages in the system per user

    //========================================================Constructor===========================================================//

    public BGSDataBase(){
        this.users=new LinkedList<>();
        this.postMessagesPerUser=new ConcurrentHashMap<>();
        this.allMessagesPerUser=new ConcurrentHashMap<>();
        this.loginUsers=new ConcurrentHashMap<>();
    }

}
