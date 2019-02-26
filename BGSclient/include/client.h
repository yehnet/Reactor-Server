//
// Created by yehnet@wincs.cs.bgu.ac.il on 1/2/19.
//

#ifndef CLIENT_CLIENT_H
#define CLIENT_CLIENT_H


#include "connectionHandler.h"

class Client {
private:

    ConnectionHandler connectionHandler;
    bool stop;
    int opCode;
    int counter;
    int messageOpcode;
    bool loggedin;

public:

    Client(std::string host, short port);
    ConnectionHandler& getConnectionHandler();
    bool getStop();
    virtual ~Client();
    //read input from the keyboard
    void runWrite();
    // listen to messages that comes from the server
    void runRead();




    std::string decodeNextByte(char* nextByte);
    std::string encode(std::string message);
    short bytesToShort(char* bytesArr);
    void shortToBytes(short num, char* bytesArr);
    void initfields();
    std::vector<std::string> split(const std::string &s, char x);
 };



#endif //CLIENT_CLIENT_H
