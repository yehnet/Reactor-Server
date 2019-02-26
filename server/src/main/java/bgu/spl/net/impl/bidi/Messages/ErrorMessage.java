package bgu.spl.net.impl.bidi.Messages;

import bgu.spl.net.impl.bidi.BGSDataBase;

public class ErrorMessage implements Message {

    //========================================================Fields===========================================================//

    private short messageOpcode;
    private short opCode;


    //========================================================Constructor===========================================================//

    public ErrorMessage(short messageOpcode){
        this.messageOpcode=messageOpcode;
        this.opCode=11;
    }

    //========================================================Methods===========================================================//

    public byte[] messageEncode() {
        byte[] ans= new byte[4];
        byte[] errorOpcode=shortToBytes(opCode);
        byte[] messageOpcodeBytes=shortToBytes(messageOpcode);
        ans[0]=errorOpcode[0];
        ans[1]=errorOpcode[1];
        ans[2]=messageOpcodeBytes[0];
        ans[3]=messageOpcodeBytes[1];
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
