package bgu.spl.net.impl.bidi.Messages;

import bgu.spl.net.impl.bidi.BGSDataBase;
import bgu.spl.net.impl.bidi.BGSuser;

public class LogoutMessage implements Message{


    //========================================================Fields===========================================================//

    private int opCode;
    public BGSDataBase dataBase;

    //========================================================Constructor===========================================================//

    public LogoutMessage(int opCode){
        this.opCode=opCode;
    }

    //========================================================Methods===========================================================//

    public int getOpCode(){ return opCode;}


    @Override
    public int execute(int connectionID, BGSDataBase dataBase) {
        this.dataBase=dataBase;
        synchronized (dataBase.loginUsers) {
            //checks if the user is not logged in
            BGSuser user = dataBase.loginUsers.get(connectionID);
            if (user != null) {
                synchronized (dataBase.loginUsers) {
                    dataBase.loginUsers.remove(connectionID, user);
                }
                return 1;
            } else {
                return 0;
            }
        }
    }


}
