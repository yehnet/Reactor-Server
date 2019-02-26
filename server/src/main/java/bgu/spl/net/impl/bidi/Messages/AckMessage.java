package bgu.spl.net.impl.bidi.Messages;
import bgu.spl.net.impl.bidi.BGSDataBase;
import bgu.spl.net.impl.bidi.BGSuser;
import java.util.Vector;

public class AckMessage implements Message {

    //========================================================Fields===========================================================//

    private Message message;
    private short opCode;
    private short messageOpcode;


    //========================================================Constructor===========================================================//

    public AckMessage(Message message, short messageOpcode){
        this.messageOpcode=messageOpcode;
        this.message=message;
        this.opCode=10;
    }


    //========================================================Methods===========================================================//
    public byte[] messageEncode() {
        Vector<Byte> tmp=new Vector<>();
        byte[] ackOpcode=shortToBytes(opCode);
        byte[] messageOpcodeBytes=shortToBytes(messageOpcode);
        tmp.add(ackOpcode[0]);
        tmp.add(ackOpcode[1]);
        tmp.add(messageOpcodeBytes[0]);
        tmp.add(messageOpcodeBytes[1]);


        if(message instanceof FollowMessage) {
            byte[] numOfUsers = shortToBytes((short) ((FollowMessage) message).getNumOfSuccessFollowUnFollow());
            tmp.add(numOfUsers[0]);
            tmp.add(numOfUsers[1]);
            String[] usersnames = ((FollowMessage) message).getAllUsersThatSuccedFollowAndunFollow().split(" ");

            for (int i = 0; i < ((FollowMessage) message).getNumOfSuccessFollowUnFollow(); i++) {
                byte[] name = usersnames[i].getBytes();
                for (byte b : name) {
                    tmp.add(b);
                }
                tmp.add((byte) '\0');
            }
            }else if(message instanceof UserListMessage){
            byte[] numOfUsers=shortToBytes((short) ((UserListMessage) message).dataBase.users.size());
            tmp.add(numOfUsers[0]);
            tmp.add(numOfUsers[1]);
            for (BGSuser name : ((UserListMessage) message).dataBase.users){
                byte[] tmpUserNameBytes=name.getName().getBytes();
                for (int i=0; i< tmpUserNameBytes.length; i++){
                    tmp.add(tmpUserNameBytes[i]);
                }
                tmp.add((byte) 0);
            }

        }else if(message instanceof StatMessage){
            byte[] numPosts=shortToBytes((short) ((StatMessage) message).dataBase.postMessagesPerUser.get(((StatMessage) message).getUser()).size());
            tmp.add(numPosts[0]);
            tmp.add(numPosts[1]);
            byte[] numOfFollowers=shortToBytes((short) ((StatMessage) message).getUser().followers.size());
            tmp.add(numOfFollowers[0]);
            tmp.add(numOfFollowers[1]);
            short followCount=0;
            for(BGSuser g : ((StatMessage) message).dataBase.users){
                for(String s : g.followers){
                    if(s.equals(((StatMessage) message).getUseName()))
                        followCount++;
                }
            }
            byte[] numOfFollowing=shortToBytes(followCount);
            tmp.add(numOfFollowing[0]);
            tmp.add(numOfFollowing[1]);

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
