
#include <iostream>
#include "../include/connectionHandler.h"
#include "../include/client.h"
#include <thread>

    int main (int argc, const char* argv[]) {

    if (argc < 3) {
        std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
        return -1;
    }

    std::string host = argv[1];
    short port = atoi(argv[2]);

    Client client(host,port);



    if (!client.getConnectionHandler().connect()) {
        std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
        return 1;
    }

    std::thread first(&Client::runRead, &client);
    std::thread second(&Client::runWrite , &client);

    first.join();
    second.join();

    if(client.getStop()){
        client.getConnectionHandler().close();
    }
    return 0;

}

