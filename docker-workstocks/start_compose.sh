#!/bin/bash

# set echo color
CYAN='\033[0;36m'
NOCOLOR='\033[0m'

echo -e "\n${CYAN}docker-compose up -d\n${NOCOLOR}"
docker-compose up -d

sleep 10
echo -e "$\n{CYAN}Execute replica: docker exec mongo-db-container /usr/src/configs/setup-mongo.sh\n${NOCOLOR}"
docker exec mongo-db-container /usr/src/configs/setup-mongo.sh

sleep 5
echo -e "$\n{CYAN}Restarting mongo-express\n${NOCOLOR}"
docker stop mongo-express
docker start mongo-express