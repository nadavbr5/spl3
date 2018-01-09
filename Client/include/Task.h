//
// Created by nogakl on 1/4/18.
//

#ifndef CLIENT_TASK_H
#define CLIENT_TASK_H

#include <string>
#include <iostream>
#include <boost/asio.hpp>
#include <boost/thread.hpp>
#include "connectionHandler.h"

using boost::asio::ip::tcp;


class Task {
private:
    boost::mutex *mutex;
    ConnectionHandler *connectionHandler;
    std::atomic<bool> *isLoggedIn;
public:
    Task(boost::mutex *mutex, ConnectionHandler *connectionHandler, std::atomic<bool> *pAtomic);

    void run();
};


#endif //CLIENT_TASK_H
