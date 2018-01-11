#!/bin/sh
cd Client
make clean
make
echo "enter 1 for Admin test or 2 for normal client test"
select test in Admin1 client1
do
expect ../Tests/$test.exp
done
exec bash
