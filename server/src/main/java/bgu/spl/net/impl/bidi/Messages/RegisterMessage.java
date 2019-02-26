package bgu.spl.net.impl.bidi.Messages;
import bgu.spl.net.impl.bidi.BGSDataBase;
import bgu.spl.net.impl.bidi.BGSuser;
import java.nio.charset.StandardCharsets;
import java.util.Vector;

public class RegisterMessage  implements Message{

    //========================================================Fields===========================================================//

    private String userName;
    private String password;
    private int opCode;
    private String allMessage;
    public BGSDataBase dataBase;

    //========================================================Constructor===========================================================//

    public RegisterMessage(Vector<Byte> bytes ,int opCode){
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

    //gets the user name
    private String getUserNameFromString(String result){
        String ansUserName="";
        boolean finished= false;
        for(int i=0 ; i<result.length() & !finished; i++){
            if(result.charAt(i) != '\0'){
                ansUserName=ansUserName+result.charAt(i);
            }else{
                finished=true;
            }
        }
        return ansUserName;
    }

    //gets the user password
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

    public String getUserName(){ return userName;}

    public int getOpCode(){ return opCode;}



    @Override
    public int execute(int connectionID, BGSDataBase dataBase) {
            this.dataBase=dataBase;
            BGSuser user = new BGSuser(connectionID, userName, password);

            //checks if the user already registered
            for(BGSuser g : dataBase.users){
                if(g.getName().equals(user.getName())) {
                    return 0;
                }
            }
            //add to the list of users that registered
            synchronized (dataBase.users) {
                dataBase.users.add(user);
            }
            return 1;
    }
}
