package bgu.spl.net.impl.bidi.Messages;

import bgu.spl.net.impl.bidi.BGSDataBase;
import bgu.spl.net.impl.bidi.BGSuser;
import com.sun.javafx.collections.MappingChange;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.stream.StreamSupport;

public class PostMessage implements Message {

    //========================================================Fields===========================================================//

    private int opCode;
    private String allMessage;
    private String[] usersToNotify;
    private String useNname;

    //========================================================Constructor===========================================================//

    public PostMessage(Vector<Byte> bytes , int opCode){
        byte[] tmp=new byte[bytes.size()];
        for(int i=0; i<tmp.length; i++){
            tmp[i]=bytes.get(i);
        }
        this.useNname="";
        this.opCode= opCode;
        String result = new String(tmp , 0 , tmp.length , StandardCharsets.UTF_8);
        result=result.substring(2);
        this.usersToNotify=getSplitedMessageFromString(result);
        this.allMessage=result;
    }

    //========================================================Methods===========================================================//

    //gets the names that we tagged on the message
    private String[] getSplitedMessageFromString(String result) {
        Set<String> tmpSetOfusers= new LinkedHashSet<>();
        String tmpUserName="";
        boolean found= false;
        for(int i=0; i< result.length(); i++){
            if(result.charAt(i) == '@'){ //&&  ((i != 0 && result.charAt(i-1) == ' ') || i == 0)){
                for(int j=i;j<result.length() & !found; j++){
                    if(result.charAt(j) != ' '){
                        tmpUserName=tmpUserName+result.charAt(j);
                    }else {
                        found=true;
                        tmpSetOfusers.add(tmpUserName);
                        tmpUserName="";
                        i=j;
                    }
                }
                found=false;
            }
        }

        String[] ans = tmpSetOfusers.toArray(new String[tmpSetOfusers.size()]);
        return ans;
    }


    public String[] getTheUsers(){ return usersToNotify;}

    public String getAllMessage(){ return allMessage;}

    public int getOpCode(){ return opCode;}


    @Override
    public int execute(int connectionID, BGSDataBase dataBase) {
            //checks if the user is not logged in
            BGSuser user = dataBase.loginUsers.get(connectionID);
            if (user == null) {
                return 0;
            }
            useNname= user.getName();
            return 1;

    }


    public String getUsername() {
        return useNname;
    }
}