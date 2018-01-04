CFLAGS:=-c -Wall -Weffc++ -g -std=c++11 -Iinclude
LDFLAGS:= -lboost_system -lboost_thread

all: Client
	g++ bin/connectionHandler.o bin/Client.o $(LDFLAGS) -o bin/Client

Client: bin/connectionHandler.o bin/Client.o

bin/connectionHandler.o: src/connectionHandler.cpp
	g++ $(CFLAGS) src/connectionHandler.cpp -o bin/connectionHandler.o

bin/client.o: src/Client.cpp
	g++ $(CFLAGS) src/Client.cpp -o bin/Client.o
	
.PHONY: clean
clean:
	rm -f bin/*
