//
// Created by yehnet@wincs.cs.bgu.ac.il on 1/2/19.
//

#include "../include/client.h"



//=============================================Constructor=========================================================================//

    Client::Client(std::string host, short port):connectionHandler(host,port),stop(false), opCode(0) ,counter(0), messageOpcode(0), loggedin(false){}

//=============================================Destructor=========================================================================//

Client::~Client() {}
    //read input from the keyboard


//=============================================Fields=========================================================================//

//the thread that reads from the keyboard and sent it to the socket
void Client::runWrite() {
        std::string tmp="";
        while (!stop) {
            const short bufsize = 1024;
            char buf[bufsize];
            std::cin.getline(buf, bufsize);
            std::string line(buf);
            tmp = line;
            line = encode(line);
            if (!connectionHandler.sendBytes(line.c_str(), line.size())) {
                std::cout << "Disconnected. Exiting...\n" << std::endl;
                break;
            }
            if(tmp == "LOGOUT" && loggedin == true){
                break;
            }
        }
    }

// listen to messages that comes from the server
    void Client::runRead() {

        while (!stop){

            char buf[1024];
            std::string answer;
            // Get back an answer
            if (!connectionHandler.getBytes(buf , 2)) {
                std::cout << "Disconnected. Exiting...\n" << std::endl;
                break;
            }
            answer = decodeNextByte(buf);
            if(answer.size()>0) {
                std::cout << answer << std::endl;
		if(answer == "ACK 2"){
                    loggedin = true;
                }
                if (answer == "ACK 3") {
                    std::cout << "Exiting...\n" << std::endl;
                    stop = true;
                    break;
                }
                answer="";
            }
        }
    }


    ConnectionHandler& Client::getConnectionHandler() {
        return connectionHandler;
    }

    bool Client::getStop() {
        return stop;
    }


//decodes the message that received
std::string Client::decodeNextByte(char *nextByte) {
    opCode = bytesToShort(nextByte);
    switch (opCode){
         case 9: {
             char type[1];
             connectionHandler.getBytes(type, 1);
             std::string ans = "NOTIFICATION ";
             std::string list;
             if(type[0] == 1) {
                 list += "Public ";
             }
             else if(type[0] == 0) {
                 list += "PM ";
             }
             for(int i=0; i< 2; i++){
                 std::string tmp;
                 connectionHandler.getLine(tmp);
                 tmp = tmp.substr(0 , tmp.length()-1);
                 list.append(tmp + " ");
             }
             ans += list;
             initfields();
             return ans;
         }


        case 10: {
            char messageOp[2];
            connectionHandler.getBytes(messageOp , 2);
            messageOpcode = bytesToShort(messageOp);
            switch (messageOpcode){
                case 1: case 2: case 3: case 5: case 6:{
                std::string ans = "ACK " + std::to_string(messageOpcode);
                initfields();
                return ans;
                }
                case 4:{
                    char numOfUsers[2];
                    connectionHandler.getBytes(numOfUsers ,2);
                    short numofusers = bytesToShort(numOfUsers);
                    std::string ans = "ACK " + std::to_string(messageOpcode)+ " " + std::to_string(numofusers) + " ";
                    std::string listusers;
                    for(int i=0; i<numofusers; i++){
                        std::string user;
                        connectionHandler.getLine(user);
                        user = user.substr(0,user.size() -1);
                        listusers.append(user + " ");
                    }
                    ans += listusers;
                    initfields();
                    return ans;
                }
                case 7:{
                    char numOfUsers[2];
                    connectionHandler.getBytes(numOfUsers ,2);
                    short numofusers = bytesToShort(numOfUsers);
                    std::string ans = "ACK " + std::to_string(messageOpcode)+ " " + std::to_string(numofusers) + " ";
                    std::string listusers;
                    for(int i=0; i<numofusers; i++){
                        std::string user;
                        connectionHandler.getLine(user);
                        user = user.substr(0 , user.length()-1);
                        listusers.append(user + " ");
                    }
                    ans += listusers;
                    initfields();
                    return ans;
                }
                case 8:{
                    char numOfPosts[2];
                    connectionHandler.getBytes(numOfPosts ,2);
                    short numofpost = bytesToShort(numOfPosts);
                    char numFollower[2];
                    connectionHandler.getBytes(numFollower ,2);
                    short numoffollows = bytesToShort(numFollower);
                    char numOfFollowing[2];
                    connectionHandler.getBytes(numOfFollowing ,2);
                    short numoffollowing = bytesToShort(numOfFollowing);
                    std::string ans = "ACK " + std::to_string(messageOpcode)+ " " + std::to_string(numofpost)+" " + std::to_string(numoffollows)+" " + std::to_string(numoffollowing);
                    initfields();
                    return ans;
                }
            }

        }


        case 11: {
            char messageOp[2];
            connectionHandler.getBytes(messageOp, 2);
            messageOpcode = bytesToShort(messageOp);
            std::string ans = "ERROR " + std::to_string(messageOpcode);
            initfields();
            return ans;
        }
    }
    return "";
}



//encode the message from keyboard
std::string Client::encode(std::string message) {
    std::vector<char> tmp;
    char tmpbyte[2];
    std::vector<std::string> splitedMessage = split(message , ' ');

    if(splitedMessage[0] == "REGISTER"){
        shortToBytes(1 , tmpbyte);
        tmp.push_back(tmpbyte[0]);
        tmp.push_back(tmpbyte[1]);
        for (char i : splitedMessage[1]) {
            tmp.push_back(i);
        }
        tmp.push_back('\0');
        for (char i : splitedMessage[2]) {
            tmp.push_back(i);
        }
        tmp.push_back('\0');

    }else if(splitedMessage[0] == "LOGIN"){
        shortToBytes(2 , tmpbyte);
        tmp.push_back(tmpbyte[0]);
        tmp.push_back(tmpbyte[1]);
        for (char i : splitedMessage[1]) {
            tmp.push_back(i);
        }
        tmp.push_back('\0');
        for (char i : splitedMessage[2]) {
            tmp.push_back(i);
        }
        tmp.push_back('\0');

    }else if(splitedMessage[0] == "LOGOUT"){
        shortToBytes(3 , tmpbyte);
        tmp.push_back(tmpbyte[0]);
        tmp.push_back(tmpbyte[1]);

    }else if(splitedMessage[0] == "FOLLOW"){
        shortToBytes(4 , tmpbyte);
        tmp.push_back(tmpbyte[0]);
        tmp.push_back(tmpbyte[1]);


        short tmp2 = (short) std::stoi(splitedMessage[1]);
        tmp.push_back(tmp2);
        short tmp1 = (short) (std::stoi(splitedMessage[2]));
        char num[2];
        shortToBytes(tmp1 , num);
        tmp.push_back(num[0]);
        tmp.push_back(num[1]);



        for(unsigned int i=3 ; i< splitedMessage.size(); i ++){
            for(char j : splitedMessage[i]){
                tmp.push_back(j);
            }
            tmp.push_back('\0');
        }



    }else if(splitedMessage[0] == "POST"){
        shortToBytes(5 , tmpbyte);
        tmp.push_back(tmpbyte[0]);
        tmp.push_back(tmpbyte[1]);
        for(unsigned int i=1; i<splitedMessage.size(); i++) {
            for (char j : splitedMessage[i]) {
                tmp.push_back(j);
            }
            tmp.push_back(' ');
        }
        tmp.push_back('\0');

    }else if(splitedMessage[0] == "PM"){
        shortToBytes(6 , tmpbyte);
        tmp.push_back(tmpbyte[0]);
        tmp.push_back(tmpbyte[1]);

        for (char i : splitedMessage[1]) {
            tmp.push_back(i);
        }
        tmp.push_back('\0');
        for(unsigned int i=2; i<splitedMessage.size(); i++) {
            for (char j : splitedMessage[i]) {
                tmp.push_back(j);
            }
            tmp.push_back(' ');
        }
        tmp.push_back('\0');


    }else if(splitedMessage[0] == "USERLIST"){
        shortToBytes(7 , tmpbyte);
        tmp.push_back(tmpbyte[0]);
        tmp.push_back(tmpbyte[1]);

    }else if(splitedMessage[0] == "STAT"){
        shortToBytes(8 , tmpbyte);
        tmp.push_back(tmpbyte[0]);
        tmp.push_back(tmpbyte[1]);
        for (char i : splitedMessage[1]) {
            tmp.push_back(i);
        }
        tmp.push_back('\0');
    }

    std::string ans;
    for(unsigned int i=0; i<tmp.size(); i++){
        ans += tmp[i];
    }
    return ans;

}




short Client::bytesToShort(char *bytesArr){
    short result = (short)((bytesArr[0] & 0xff) << 8);
    result += (short)(bytesArr[1] & 0xff);
    return result;
}

void Client::shortToBytes(short num, char *bytesArr) {
    bytesArr[0] = static_cast<char>((num >> 8) & 0xFF);
    bytesArr[1] = static_cast<char>(num & 0xFF);
}


void Client::initfields() {
    opCode=0;
    messageOpcode=0;
}


//return a vector  of split string by specific char
std::vector<std::string> Client::split(const std::string &s, char x) {
    std::vector<std::string> splitString;
    std::string ans;
    for (char i : s) {
        if(i ==x){
            splitString.push_back(ans);
            ans="";
        }
        else{
            ans += i;

        }
    }
    splitString.push_back(ans);
    return splitString;
}
