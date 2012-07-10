#!/bin/bash

function is_port_in_use {
  port=$1
  #echo "   " checking if port is in use: $port

  lsof -i -n -P | grep -q -F *:$port
  if [ $? -eq 0 ]; then 
    return 1
  fi
  return 0
}

function is_port_availible {
  port=$1
  printf " checking if server running on port $port";

  curl -s local.slidev.org:$port
  result=$?
  if [ result -eq 0 ]; then 
    echo found!
  else
    echo not found!
  fi
  return $result
}

function wait_until_found {
  filepath=$1
  pattern=$2
  timeout=30

  printf "  wait_until_found: pattern '$pattern' in file '$filepath' ...";
  count=0
  while [ $count -le $timeout ]
  do
    grep -q -e "$pattern" -- $filepath
    result=$?
    if [ $result -eq 0 ]; then 
      echo found!
      return 0
    fi
    printf ".";
    sleep 1s
    let count=count+1
  done

  if [ $FAILHARD ]; then
    echo not found, aborting
    exit 1
  else
    echo not found, continuing without error
  fi
}

DIR=`pwd`
echo PWD: $DIR
mkdir -p everyLog
cd everyLog; rm *.log > /dev/null 2>&1
LOGDIR=$DIR/everyLog

is_port_in_use "8080"
if (( $? )); then
  echo Not Starting API: port 8080 is already in use
else
  echo Starting API
  cd $DIR/api; mvn -o jetty:run > $LOGDIR/apiConsole.log 2>&1 &
  apiPid=$!
  echo pid $!
fi

is_port_in_use "8000"
if (( $? )); then
  echo Not Starting Ingestion: port 8888 is already in use
else
  echo Starting Ingestion
  cd $DIR/ingestion/ingestion-service; mvn -o jetty:run > $LOGDIR/ingestionConsole.log 2>&1 &
  ingestionPid=$!
  echo pid $!
fi

is_port_in_use "8888"
if (( $? )); then
  echo Not Starting Dashboard: port 8888 is already in use
else
  echo Starting Dashboard
  cd $DIR/dashboard; mvn -o jetty:run > $LOGDIR/dashboardConsole.log 2>&1 &
  dashboardPid=$!
  echo pid $!
fi

is_port_in_use "8082"
if (( $? )); then
  echo Not Starting Simple IDP: port 8082 is already in use
else
  echo Starting Simple IDP
  cd $DIR/simple-idp; mvn -o jetty:run  > $LOGDIR/simpleIdpConsole.log 2>&1 &
  simpleidpPid=$!
  echo pid $!
fi

is_port_in_use "3000"
if (( $? )); then
  echo Not Starting Data Prowler: port 3000 is already in use
else
  echo Starting Data Prowler
  cd $DIR/databrowser; bundle exec rails server > $LOGDIR/prowlerConsole.log 2>&1 &
  prowlerPid=$!
  echo pid $!
fi

is_port_in_use "3001"
if (( $? )); then
  echo Not Starting Admin Tools: port 3001 is already in use
else
  echo Starting Admin Tools
  cd $DIR/admin-tools/admin-rails; bundle exec rails server > $LOGDIR/adminConsole.log 2>&1 &
  adminToolsPid=$!
  echo pid $!
fi


jetty_pattern="Starting scanner at interval of 5 seconds"

if (( $apiPid )); then
  wait_until_found "$LOGDIR/apiConsole.log" "$jetty_pattern"
fi

if (( $ingestionPid )); then
  wait_until_found "$LOGDIR/ingestionConsole.log" "$jetty_pattern"
fi

if (( $dashboardPid )); then
  wait_until_found "$LOGDIR/dashboardConsole.log" "$jetty_pattern"
fi

if (( $simpleidpPid )); then
  wait_until_found "$LOGDIR/simpleIdpConsole.log" "$jetty_pattern"
fi

if (( $prowlerPid )); then
  wait_until_found "$LOGDIR/prowlerConsole.log" ">> Listening on"
fi

if (( $adminToolsPid )); then
  wait_until_found "$LOGDIR/adminConsole.log" "WEBrick::HTTPServer#start:"
fi

echo "Starting Smoke Tests"
cd $DIR/acceptance-tests; bundle exec rake smokeTests

