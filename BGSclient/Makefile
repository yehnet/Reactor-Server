# define some Makefile variables for the compiler and compiler flags
# to use Makefile variables later in the Makefile: $()
CC = g++
CFLAGS  = -g -Wall -Weffc++ -std=c++11
LFLAGS  = -lboost_system -pthread

# All Targets
all: BGSclient

# Tool invocations
# Executable "hello" depends on the files hello.o and run.o.
BGSclient: bin/connectionHandler.o bin/client.o bin/main.o
	@echo 'Building target: hello'
	@echo 'Invoking: C++ Linker'
	$(CC) -o bin/BGSclient bin/connectionHandler.o bin/client.o bin/main.o $(LFLAGS)
	@echo 'Finished building target: BGSclient'
	@echo ' '

bin/connectionHandler.o: src/connectionHandler.cpp
	$(CC) $(CFLAGS) -c -Iinclude -o bin/connectionHandler.o src/connectionHandler.cpp

bin/client.o: src/client.cpp
	$(CC) $(CFLAGS) -c -Iinclude -o bin/client.o src/client.cpp

bin/main.o: src/main.cpp
	$(CC) $(CFLAGS) -c -Iinclude -o bin/main.o src/main.cpp



#Clean the build directory
clean: 
	rm -f bin/*
