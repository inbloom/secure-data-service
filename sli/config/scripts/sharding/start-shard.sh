#! /bin/bash

rm -rf data logs
mkdir -p data/db/a
mkdir -p data/db/b
mkdir -p data/db/config
mkdir -p logs

if [ -z $1 ]; then
    mongos_port=27017
else
    mongos_port=$1
fi

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

echo "Starting up shard servers..."
mongod --shardsvr --dbpath data/db/a --port 10001 > logs/sharda.log &
echo $! >> pids
mongod --shardsvr --dbpath data/db/b --port 10002 > logs/shardb.log &
echo $! >> pids
wait_for_mongo 10001
wait_for_mongo 10002

echo "Setting chunk size on shard servers..."
mongo config --port 10001 --eval 'db.settings.save({"_id":"chunksize", "value":1});'
mongo config --port 10002 --eval 'db.settings.save({"_id":"chunksize", "value":1});'

echo "Starting up config server..."
mongod --configsvr --dbpath data/db/config --port 20000 > logs/configdb.log &
echo $! >> pids
wait_for_mongo 20000

echo "Setting chunk size on conig server..."
mongo config --port 20000 --eval 'db.settings.save({"_id":"chunksize", "value":1});'

echo "Starting up mongos router..."
mongos --configdb localhost:20000 --port $mongos_port> logs/mongos.log &
echo $! >> pids
wait_for_mongo $mongos_port

echo "Adding shards to cluster..."
mongo admin --port $mongos_port --eval 'db.runCommand( { addshard : "localhost:10001" } )'
mongo admin --port $mongos_port --eval 'db.runCommand( { addshard : "localhost:10002" } )'

echo "mongos is running on port $mongos_port"
