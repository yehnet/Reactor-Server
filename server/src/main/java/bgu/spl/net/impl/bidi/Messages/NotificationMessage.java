package bgu.spl.net.impl.bidi.Messages;

import bgu.spl.net.impl.bidi.BGSDataBase;

import java.util.Vector;

public class NotificationMessage implements Message {

    //========================================================Fields===========================================================//

    private Message message;
    private short opCode;

    //========================================================Constructor===========================================================//

    public NotificationMessage(Message message){
        this.opCode=9;
        this.message=message;
    }
    //========================================================Methods===========================================================//

    public byte[] messageEncode() {
        Vector<Byte> tmp=new Vector<>();
        byte[] notificationOpcode=shortToBytes(opCode);
        tmp.add(notificationOpcode[0]);
        tmp.add(notificationOpcode[1]);
        if(message instanceof PMMessage){
            tmp.add((byte) 0);
            byte[] username=((PMMessage) message).getUserName().getBytes();
            for(int i=0 ; i<username.length; i++){
                tmp.add(username[i]);
            }
            tmp.add((byte) '\0');
            byte[] content = ((PMMessage) message).getContent().getBytes();
            for(int i=0 ; i<content.length; i++){
                tmp.add(content[i]);
            }
            tmp.add((byte) '\0');

        }else if(message instanceof PostMessage){
            tmp.add((byte) 1);
            byte[] username=((PostMessage) message).getUsername().getBytes();
            for(int i=0 ; i<username.length; i++){
                tmp.add(username[i]);
            }
            tmp.add((byte) '\0');
            byte[] content = ((PostMessage) message).getAllMessage().getBytes();
            for(int i=0 ; i<content.length; i++){
                tmp.add(content[i]);
            }
            tmp.add((byte) '\0');
        }

        byte[] ans =new byte[tmp.size()];
        for(int i=0; i< ans.length;i++){
            ans[i]=tmp.get(i);
        }
        return ans;
    }


    private byte[] shortToBytes(short num)
    {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte)((num >> 8) & 0xFF);
        bytesArr[1] = (byte)(num & 0xFF);
        return bytesArr;
    }

    @Override
    public int execute(int connectionID, BGSDataBase dataBase) {
        return 0;
    }
}
