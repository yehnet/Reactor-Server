package bgu.spl.net.impl.bidi.Messages;

import bgu.spl.net.impl.bidi.BGSDataBase;
import bgu.spl.net.impl.bidi.BGSuser;

import java.nio.charset.StandardCharsets;
import java.util.Vector;



public class FollowMessage implements Message {


    //========================================================Fields===========================================================//

    private int opCode;
    private int FollowUnFollow;
    private int numOfUsers;
    private String[] users;
    private String allUsersThatSuccedFollowAndunFollow="";
    private String allMessage;
    public BGSDataBase dataBase;
    private int numOfSuccessFollowUnFollow;


    //========================================================Constructor===========================================================//

    public FollowMessage(Vector<Byte> bytes, int opCode, int numOfUsers){
        byte[] tmp=new byte[bytes.size()];
        for(int i=0; i<tmp.length; i++){
            tmp[i]=bytes.get(i);
        }
        this.opCode= opCode;
        this.numOfSuccessFollowUnFollow=0;
        this.numOfUsers=numOfUsers;
        this.FollowUnFollow = tmp[2];
        String result = new String(tmp , 0 , tmp.length , StandardCharsets.UTF_8);
        result=result.substring(5);
        users=getUsersFromString(result);
        this.allMessage=result;

    }

    //========================================================Methods===========================================================//


    @Override
    public int execute(int connectionID, BGSDataBase dataBase) {
        this.dataBase=dataBase;
            BGSuser user = dataBase.loginUsers.get(connectionID);
            if (user == null) {
                // the user is not connected
                return 0;
            }

            //Follow the users on the list we gave
            if(FollowUnFollow == 0){
                for(String s : users){
                    for(BGSuser g : dataBase.users){
                        if(g.getName().equals(s) &  !g.followers.contains(user.getName()) & !s.equals(user.getName())  ){
                            g.followers.add(user.getName());
                            numOfSuccessFollowUnFollow++;
                            allUsersThatSuccedFollowAndunFollow +=  g.getName() + " ";
                        }
                    }
                }
                if(numOfSuccessFollowUnFollow == 0){
                    return 0;
                }else{
                    return 1;
                }
            }

            //UnFollow the users on the list we gave
            else if(FollowUnFollow == 1 ){
                for(String s : users) {
                    for (BGSuser g : dataBase.users) {
                        if(g.getName().equals(s) & g.followers.contains(user.getName())){
                            g.followers.remove(user.getName());
                            numOfSuccessFollowUnFollow++;
                            allUsersThatSuccedFollowAndunFollow += g.getName()+ " " ;
                        }
                    }
                }
                if(numOfSuccessFollowUnFollow == 0){
                    return 0;
                }else{
                    return 1;
                }
            }
        return 0;
    }



    //split the message and get the users we want to follow or unFollow
    private String[] getUsersFromString(String result){
        String[] tmp = new String[numOfUsers];
        String name = "";
        boolean found = false;
        int lastIndex=0;
        for(int i=0 ; i<tmp.length; i++){
            for(int j=lastIndex;j<result.length() & !found; j++){
                if(result.charAt(j) == '\0'){
                    lastIndex = j+1;
                    found = true;
                }else{
                    name += result.charAt(j);
                }
            }
            tmp[i] = name;
            name="";
            found = false;
        }
        return tmp;
    }

    public int getOpCode(){ return opCode;}

    public String getAllMessage(){ return allMessage;}

    public int getNumOfSuccessFollowUnFollow(){return numOfSuccessFollowUnFollow;}

    public String getAllUsersThatSuccedFollowAndunFollow(){ return allUsersThatSuccedFollowAndunFollow;}


}
