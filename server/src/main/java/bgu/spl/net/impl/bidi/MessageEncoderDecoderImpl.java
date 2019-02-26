package bgu.spl.net.impl.bidi;


import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.impl.bidi.Messages.*;
import java.util.Vector;

public class MessageEncoderDecoderImpl implements MessageEncoderDecoder<Message> {

    //========================================================Fields===========================================================//

    private int counter=0;
    private Vector<Byte> bytes=new Vector<>();
    private byte[] opCode=new byte[2];
    private short opcodeNumDec=0;
    private int numOfZerosInFollowMessage=0;
    private int numOfFollowers=0;
    private String userName;


    //========================================================Methods===========================================================//

    @Override
    public Message decodeNextByte(byte nextByte) {

        //gets the opcode of the message that was sent
        if(bytes.size() != 2 & opcodeNumDec == 0) {
            if (opCode[0] == 0) {
                opCode[1] = nextByte;
            } else {
                opCode[0] = nextByte;
            }
            bytes.add(nextByte);
            if (bytes.size() == 2 && opcodeNumDec == 0) {
                opcodeNumDec = bytesToShort(opCode);
                opCode[0] = '0';
                opCode[1] = '0';
                bytes.remove(1);
            }
        }

        //gets the bytes for the specific message
            switch (opcodeNumDec) {
                case 1:
                    if (nextByte == '\0' & counter != 2) {
                        counter++;
                    }
                    if (nextByte == '\0' & counter == 2) {
                        bytes.add(nextByte);
                        RegisterMessage registerMessage = new RegisterMessage(bytes, opcodeNumDec);
                        userName=registerMessage.getUserName();
                        counter = 0;
                        opcodeNumDec = 0;
                        bytes.removeAllElements();
                        return  registerMessage;
                    } else {
                        bytes.add(nextByte);
                    }
                    break;

                case 2:
                    if (nextByte == '\0' & counter != 2) {
                        counter++;
                    }
                    if (nextByte == '\0' & counter == 2) {
                        bytes.add(nextByte);
                        LoginMessage loginMessage = new LoginMessage(bytes, opcodeNumDec);
                        counter = 0;
                        opcodeNumDec = 0;
                        bytes.removeAllElements();
                        return loginMessage;
                    } else {
                        bytes.add(nextByte);
                    }
                    break;

                case 3:
                    LogoutMessage logoutMessage = new LogoutMessage(opcodeNumDec);
                    counter = 0;
                    opcodeNumDec = 0;
                    bytes.removeAllElements();
                    return logoutMessage;

                case 4:
                    if(nextByte == '\0' && bytes.size()>= 5 && numOfZerosInFollowMessage == 1){
                        bytes.add(nextByte);
                        FollowMessage followMessage = new FollowMessage(bytes, opcodeNumDec, numOfFollowers);
                        opcodeNumDec = 0;
                        counter = 0;
                        numOfFollowers=0;
                        numOfZerosInFollowMessage=0;
                        bytes.removeAllElements();
                        return followMessage;
                    }else{
                        bytes.add(nextByte);
                        if(bytes.size() == 5) {
                            byte[] tmp = new byte[2];
                            tmp[0] = bytes.get(3);
                            tmp[1] = bytes.get(4);
                            numOfFollowers = bytesToShort(tmp);
                            numOfZerosInFollowMessage = numOfFollowers;
                        }
                        if (nextByte == '\0') {
                            numOfZerosInFollowMessage = numOfZerosInFollowMessage - 1;
                        }
                    }
                    break;

                case 5:
                    if (nextByte == '\0') {
                        PostMessage postMessage = new PostMessage(bytes, opcodeNumDec);
                        opcodeNumDec = 0;
                        counter = 0;
                        bytes.removeAllElements();
                        return postMessage;
                    } else {
                        bytes.add(nextByte);
                    }
                    break;

                case 6:
                    if (nextByte == '\0' & counter != 2) {
                        bytes.add(nextByte);
                        counter++;
                    }
                    if (nextByte == '\0' & counter == 2) {
                        bytes.add(nextByte);
                        PMMessage pmMessage = new PMMessage(bytes, opcodeNumDec);
                        counter = 0;
                        opcodeNumDec = 0;
                        bytes.removeAllElements();
                        return pmMessage;
                    } else {
                        bytes.add(nextByte);
                    }
                    break;

                case 7:
                    UserListMessage userListMessage = new UserListMessage(opcodeNumDec);
                    counter = 0;
                    opcodeNumDec = 0;
                    bytes.removeAllElements();
                    return userListMessage;

                case 8:
                    if (nextByte == '\0') {
                        StatMessage statMessage = new StatMessage(bytes, opcodeNumDec);
                        opcodeNumDec = 0;
                        counter = 0;
                        bytes.removeAllElements();
                        return statMessage;
                    } else {
                        bytes.add(nextByte);
                    }
                    break;
            }
        return null;
    }


    //encodes the message
    @Override
    public byte[] encode(Message message) {
        if (message == null) {
            return null;
        } else if (message instanceof AckMessage) {
            return ((AckMessage) message).messageEncode();
        } else if (message instanceof ErrorMessage) {
            return ((ErrorMessage) message).messageEncode();
        } else if (message instanceof NotificationMessage) {
            return ((NotificationMessage) message).messageEncode();
        }
        return null;
    }


    // change bytes to short
    public short bytesToShort(byte[] byteArr)
    {
        short result = (short)((byteArr[0] & 0xff) << 8);
        result += (short)(byteArr[1] & 0xff);
        return result;
    }

}
