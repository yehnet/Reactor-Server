package bgu.spl.net.impl.bidi.Messages;

import bgu.spl.net.impl.bidi.BGSDataBase;
import bgu.spl.net.impl.bidi.BGSuser;

import java.nio.charset.StandardCharsets;
import java.util.Vector;

public class StatMessage implements Message {

    //========================================================Fields===========================================================//

    private int opCode;
    private String useName;
    public BGSDataBase dataBase;
    private BGSuser user;

    //========================================================Constructor===========================================================//

    public StatMessage(Vector<Byte> bytes, int opCode){
        byte[] tmp=new byte[bytes.size()];
        for(int i=0; i<tmp.length; i++){
            tmp[i]=bytes.get(i);
        }
        this.opCode= opCode;
        String result = new String(tmp , 0 , tmp.length , StandardCharsets.UTF_8);
        result=result.substring(2);
        this.useName=result;
    }

    //========================================================Methods===========================================================//

    public String getUseName(){ return useName;}

    public int getOpCode(){ return opCode;}

    @Override
    public int execute(int connectionID, BGSDataBase dataBase) {
        this.dataBase = dataBase;

            //gets the user name for the list of registered users
            boolean found = false;
            for (BGSuser g : dataBase.users)
                if (g.getName().equals(useName)) {
                    found = true;
                    user =  g;
                }

            if (!found) {
                return 0;
            }

            //checks if the user logged in
            BGSuser user = dataBase.loginUsers.get(connectionID);
            if (user == null) {
                return 0;
            }
            return 1;
    }

    public BGSuser getUser() {
        return user;
    }
}
