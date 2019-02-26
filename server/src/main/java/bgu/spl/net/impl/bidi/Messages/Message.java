package bgu.spl.net.impl.bidi.Messages;


import bgu.spl.net.impl.bidi.BGSDataBase;

public interface Message {


    /**
     * gets the status of the message in the current time
     * @return 0 -ERROR , 1 -ACK , 2 -Notification
     */

    /**
     * execute the process need to be done for the massage
     * @return ths status of the message (ACK, ERROR , NOTIFICATION)
     */
    int execute(int connectionID , BGSDataBase dataBase);


}
