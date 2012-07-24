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
if [ -z $3 ]; then
  num_per_shard=1
else
  num_per_shard=$3
fi

ulimit -n 10000

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

create_replicaSet() {
    local port="$1"
    local dbpath="$2"
    local rsname="$3"

    mkdir -p $dbpath/$port/data/db
    mongod --replSet $rsname --dbpath $dbpath/$port/data/db --port $port > $dbpath/logs/rs_$port.log &
    echo $! >> $dbpath/pids
    
    sleep 1s
    
    wait_for_mongo $port
}

create_mongod() {
    local port="$1"
    local dbpath="$2"

	mkdir -p $dbpath/data/$port/db
    mongod --shardsvr --dbpath $dbpath/data/$port/db --port $port > $dbpath/logs/mongod_$port.log &
    echo $! >> $dbpath/pids
    
    sleep 1s
    
    wait_for_mongo $port
}

create_shards() {
	local i
    local port="$1"
    local hosts_per_shard="$2"
    local rs_members="["
    local shard="rs_$port/"
    
    if [ $hosts_per_shard -gt 1 ]; then
        for ((i = 0; i < $hosts_per_shard; i++))
        do
		    create_replicaSet $(($port + $i)) $M "rs_$port"
		    rs_members="$rs_members {\"_id\":$i, \"host\":\"localhost:$(($port + $i))\"}"
		    shard=$shard"localhost:$(($port + $i))"
		    if [ $i -ne $(($hosts_per_shard - 1)) ] ; then
			    rs_members="$rs_members,"
			    shard="$shard,"
		    fi
        done
    
        rs_members="$rs_members ]";
    
    	echo "Adding replica set: $rs_members"
    	
        mongo admin --port $port --eval "'db.runCommand({\"replSetInitiate\" : {\"_id\" : \"rs_$port\", \"members\" : $rs_members }}); assert.eq( null, db.getLastError() );'"
    else
    	create_mongod $port $M
		shard="localhost:$port"
    fi
    
    echo "Adding shard: $shard"
    
    sleep 8s
    
	mongo admin --port $mongos_port --eval "'db.runCommand({ addshard : \"$shard\" }); assert.eq( null, db.getLastError() );'"
}

echo "Starting up config server..."
mongod --configsvr --dbpath $M/data/db/config --port 20000 > $M/logs/configdb.log &
echo $! >> $M/pids

wait_for_mongo 20000

echo "Starting up mongos router..."
mkdir -p $M/data/db/mongos
mongos --configdb localhost:20000 --chunkSize 128 --port $mongos_port > $M/logs/mongos.log &
echo $! >> $M/pids

wait_for_mongo $mongos_port

echo "mongos is running on port $mongos_port"

echo "Starting up shard servers..."
for ((i = 1; i <= $num_shards; i++))
do
    create_shards $((10000 + 100 * $i)) $num_per_shard
    sleep 1s
done

