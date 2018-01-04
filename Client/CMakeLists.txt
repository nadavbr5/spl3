cmake_minimum_required(VERSION 3.8)
project(Client)

set(CMAKE_CXX_STANDARD 11)
set(SOURCE_FILES
        include/connectionHandler.h
        src/connectionHandler.cpp
        src/Client.cpp
        )

find_package(Boost)
if(Boost_FOUND)
    include_directories(${Boost_INCLUDE_DIRS})
    add_executable(Client ${SOURCE_FILES})
        endif()