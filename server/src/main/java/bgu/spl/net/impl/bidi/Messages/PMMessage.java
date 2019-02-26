package bgu.spl.net.impl.bidi.Messages;

import bgu.spl.net.impl.bidi.BGSDataBase;
import bgu.spl.net.impl.bidi.BGSuser;

import java.nio.charset.StandardCharsets;
import java.util.Vector;

public class PMMessage implements Message{

    //========================================================Fields===========================================================//

    private int opCode;
    private String userName;
    private String Content;
    private String allMessage;
    public BGSDataBase dataBase;

    //========================================================Constructor===========================================================//

    public PMMessage(Vector<Byte> bytes, int opCode){
        byte[] tmp=new byte[bytes.size()];
        for(int i=0; i<tmp.length; i++){
            tmp[i]=bytes.get(i);
        }
        this.opCode= opCode;
        String result = new String(tmp , 0 , tmp.length , StandardCharsets.UTF_8);
        result=result.substring(2);
        String[] tmpString = splitTheResult(result);
        this.userName=tmpString[0];
        this.Content=tmpString[1];
        this.allMessage=result;
    }

    //========================================================Methods===========================================================//

    //gets the user name we want tosend to and the content
    private String[] splitTheResult(String result){
        String name="";
        String content= "";
        String[] ans= new String[2];
        boolean foundName = false;
        for(int i=0 ; i< result.length(); i++){
            if(result.charAt(i) != '\0' & !foundName){
                name += result.charAt(i);
            }else{
                foundName = true;
                if(result.charAt(i) != '\0')
                    content += result.charAt(i);
            }
        }
        ans[0]=name;
        ans[1]=content;
        return ans;
    }

    public String getUserName(){ return userName;}

    public String getContent(){ return Content;}

    public String getAllMessage(){ return allMessage;}

    public int getOpCode(){ return opCode;}

    @Override
    public int execute(int connectionID, BGSDataBase dataBase) {
        this.dataBase=dataBase;
            //checks if the user is looged in
            BGSuser user = dataBase.loginUsers.get(connectionID);
            if (user == null) {
                return 0;
            }
            else {
                boolean found = false;
                for (BGSuser s : dataBase.users) {
                    if (s.getName().equals(userName) & !s.getName().equals(user.getName())) {
                        found = true;
                    }
                }
                if (!found)
                    return 0;
                return 1;
            }
    }

}
