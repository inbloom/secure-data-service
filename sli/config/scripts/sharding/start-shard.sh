#! /bin/bash


if [ -z $1 ]; then
  mongos_port=27017
else
  mongos_port=$1
fi
if [ -z $2 ]; then
  num_shards=2
else
  num_shards=$2
fi


M=~/mongo/shard

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

echo "Starting up shard servers..."
for ((i=1; i<=$num_shards; i++))
do
  shard_port=$(( 10000 + $i ))
  mkdir -p $M/data/db/$i
  mongod --shardsvr --dbpath $M/data/db/$i --port $shard_port > $M/logs/shard$i.log &
  echo $! >> $M/pids

  wait_for_mongo $shard_port

  mongo config --port $shard_port --eval 'db.settings.save({"_id":"chunksize", "value":1});'
  echo "Shard $i up and running on port $shard_port"
done

echo "Starting up config server..."
mongod --configsvr --dbpath $M/data/db/config --port 20000 > $M/logs/configdb.log &
echo $! >> $M/pids

wait_for_mongo 20000

echo "Setting chunk size on config server..."
mongo config --port 20000 --eval 'db.settings.save({"_id":"chunksize", "value":1});'

echo "Starting up mongos router..."
mongos --configdb localhost:20000 --port $mongos_port> $M/logs/mongos.log &
echo $! >> $M/pids

wait_for_mongo $mongos_port

echo "Adding shards to cluster..."
for ((i=1; i<=$num_shards; i++))
do
  shard_port=$(( 10000 + $i ))
  mongo admin --port $mongos_port --eval 'db.runCommand( { addshard : "localhost:'$shard_port'" } )'
done
echo "mongos is running on port $mongos_port"
