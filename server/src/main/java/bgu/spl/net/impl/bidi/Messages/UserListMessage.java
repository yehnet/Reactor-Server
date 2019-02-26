package bgu.spl.net.impl.bidi.Messages;

import bgu.spl.net.impl.bidi.BGSDataBase;
import bgu.spl.net.impl.bidi.BGSuser;

public class UserListMessage implements Message {

    //========================================================Fields===========================================================//

    private int opCode;
    public BGSDataBase dataBase;

    //========================================================Constructor===========================================================//

    public UserListMessage(int opCode){
        this.opCode=opCode;
    }


    //========================================================Methods===========================================================//

    public int getOpCode(){ return opCode;}

    @Override
    public int execute(int connectionID, BGSDataBase dataBase) {
        this.dataBase = dataBase;
        //checks if the user logged in
            BGSuser user = dataBase.loginUsers.get(connectionID);
            if (user == null)
                return 0;

            return 1;
        }
}
