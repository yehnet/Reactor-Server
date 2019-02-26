package bgu.spl.net.impl.bidi.Messages;

import bgu.spl.net.impl.bidi.BGSDataBase;
import bgu.spl.net.impl.bidi.BGSuser;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Vector;

public class LoginMessage implements Message{



    //========================================================Fields===========================================================//

    private int opCode;
    private String userName;
    private String password;
    private String allMessage;


    //========================================================Constructor===========================================================//


    public LoginMessage(Vector<Byte> bytes,int opCode){
        byte[] tmp=new byte[bytes.size()];
        for(int i=0; i<tmp.length; i++){
            tmp[i]=bytes.get(i);
        }
        this.opCode= opCode;
        String result = new String(tmp , 0 , tmp.length , StandardCharsets.UTF_8);
        result=result.substring(2);
        this.allMessage=result;
        this.userName=getUserNameFromString(result);
        this.password=getPasswordFromString(result);
    }


    //========================================================Methods===========================================================//

    //get the user name
    private String getUserNameFromString(String result){
        String ansUserName="";
        boolean finished= false;
        for(int i=0 ; i<result.length() & !finished; i++){
            if(result.charAt(i) != '\0'){
                ansUserName += result.charAt(i);
            }else{
                finished=true;
            }
        }
        return ansUserName;
    }

    //get the user password
    private String getPasswordFromString(String result){
        String ansPassword="";
        int counter=0;
        for(int i=0 ; i<result.length(); i++){
            if(result.charAt(i) == '\0'){
                counter++;
            }
            if(result.charAt(i) != '\0' & counter==1){
                ansPassword=ansPassword+result.charAt(i);
            }
        }
        return ansPassword;
    }

    public int getOpCode(){ return opCode;}



    @Override
    public int execute(int connectionID, BGSDataBase dataBase) {
            BGSuser user = new BGSuser(connectionID, userName, password);
            boolean sameDetails = false;

            //checks if the user is logged in already by name
            for(Map.Entry<Integer , BGSuser> tmp : dataBase.loginUsers.entrySet()){
                if(tmp.getValue().getName().equals(userName))
                    return 0;
            }

            //checks that we don't log in 2 users from the same client
            if (dataBase.loginUsers.containsKey(connectionID)) {
                return 0;
            } else {
                for (BGSuser bgSuser : dataBase.users) {
                    if (bgSuser.getName().equals(user.getName()) && bgSuser.getPassword().equals(user.getPassword())) {
                        sameDetails = true;
                        bgSuser.setConnectionID(user.getConnectionID());
                        user = bgSuser;
                    }
                }
            }

            //if he is not logged in we add him to data base
            if (sameDetails) {
                synchronized (dataBase.loginUsers) {
                    dataBase.loginUsers.put(connectionID, user);
                }
                synchronized (dataBase.postMessagesPerUser) {
                    dataBase.postMessagesPerUser.put(user, new Vector<>());
                }
                synchronized (dataBase.allMessagesPerUser) {
                    dataBase.allMessagesPerUser.put(user, new Vector<>());
                }
                return 1;
            } else {
                return 0;
            }
    }

}
