
#include "../include/connectionHandler.h"
#include "../include/Task.h"

/**
* This code assumes that the server replies the exact text the client sent it (as opposed to the practical session example)
*/
int main (int argc, char *argv[]) {
    boost::mutex mutex;
    std::atomic<bool> shouldTerminate(false);
    if (argc < 3) {
        std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
        return -1;
    }
    std::string host = argv[1];
    short port = atoi(argv[2]);

    ConnectionHandler connectionHandler(host, port);
    if (!connectionHandler.connect()) {
        std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
        return 1;
    }
    Task task(&mutex, &connectionHandler, &shouldTerminate);
    boost::thread thread2(&Task::run, &task);
    //From here we will see the rest of the ehco client implementation:
    while (!shouldTerminate.load()) {
        const short bufsize = 1024;
        char buf[bufsize];
        std::cin.getline(buf, bufsize);
        std::string line(buf);
        int len = line.length();
        if (!connectionHandler.sendLine(line)) {
//                std::cout << "Disconnected. Exiting...\n" << std::endl;
            break;
        }

        // connectionHandler.sendLine(line) appends '\n' to the message. Therefor we send len+1 bytes.
        //TODO: change
        std::cout << "Sent " << len + 1 << " bytes to server" << std::endl;
    }
        return 0;
    }

