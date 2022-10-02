#!/bin/sh
echo ***************************************************************
echo Starting the replica set
echo ***************************************************************

#sleep 15 | echo Sleeping
mongo -u blog-user -p blog-password replicaSet.js


