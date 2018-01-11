#!/bin/sh
cd ~/IdeaProjects/spl3/Server
echo "enter 1 for reactor, 2 for tpc server"
select server in BBreactor.ReactorMain BBtpc.TPCMain
do
mvn clean compile
mvn exec:java -Dexec.mainClass="bgu.spl181.net.impl.$server" -Dexec.args="7777"
done
exit
