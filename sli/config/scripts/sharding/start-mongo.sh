#! /bin/bash

if [ -z $1 ]; then
  mongo_port=27017
else
  mongo_port=$1
fi

ulimit -n 10000

M=~/mongo/$mongo_port

rm -rf $M/data $M/logs $M/pids

mkdir -p $M/data/db/config
mkdir -p $M/logs

wait_for_mongo() {
    local port="$1"
    printf "  waiting for mongo process on port $port...";
    result=1
    while [ $result -ne 0 ]
    do
        printf ".";
        sleep 1s
        mongo --port $port --eval "1" > /dev/null 2>&1
        result=$?
    done
    echo "done!";
}

echo "Starting up server..."
mkdir -p $M/data/db
mongod --dbpath $M/data/db --port $mongo_port > $M/logs/mongod.log &
echo $! >> $M/pids

wait_for_mongo $mongo_port

echo "mongo is running on port $mongo_port"
