if [ -z $1 ]; then
  mongo_port=27017
  else
    mongo_port=$1
fi
    
M=~/mongo/$mongo_port
cat $M/pids | xargs kill
sleep 1
