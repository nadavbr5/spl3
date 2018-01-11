#!/bin/sh
cd ~/IdeaProjects/spl3/Client
make clean
make
expect ../client1.exp
exec bash