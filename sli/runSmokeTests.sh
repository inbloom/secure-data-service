#!/bin/bash


function is_port_availible {
  port=$1

  printf " checking if server running on port $port";

  curl -s local.slidev.org:$port
  result=$?
  echo result equals $result
  if [ $result -eq 0 ]; then 
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
mkdir -p everyLog
cd everyLog; rm *.log
LOGDIR=$DIR/everyLog

is_port_availible "8080"
ret=$?
if [ 0 -eq $ret ]; then
  printf "no server on port 8080"
else
  printf "server is up on port 8080"
fi

echo PWD: $DIR
echo Starting API
cd $DIR/api; mvn jetty:run > $LOGDIR/apiConsole.log 2>&1 &
apiPid=$!
echo pid $!

echo Starting Ingestion
cd $DIR/ingestion/ingestion-service; mvn jetty:run > $LOGDIR/ingestionConsole.log 2>&1 &
ingestionPid=$!
echo pid $!

echo Starting Dashboard
cd $DIR/dashboard; mvn jetty:run > $LOGDIR/dashboardConsole.log 2>&1 &
dashboardPid=$!
echo pid $!

echo Starting Simple IDP
cd $DIR/simple-idp; mvn jetty:run  > $LOGDIR/simpleIdpConsole.log 2>&1 &
simpleidpPid=$!
echo pid $!

echo Starting Data Prowler
cd $DIR/databrowser; bundle exec rails server > $LOGDIR/prowlerConsole.log 2>&1 &
prowlerPid=$!
echo pid $!

echo Starting Admin Tools
cd $DIR/admin-tools/admin-rails; bundle exec rails server > $LOGDIR/adminConsole.log 2>&1 &
adminToolsPid=$!
echo pid $!


jetty_pattern="Starting scanner at interval of 5 seconds"

wait_until_found "$LOGDIR/apiConsole.log" "$jetty_pattern"
wait_until_found "$LOGDIR/ingestionConsole.log" "$jetty_pattern"
wait_until_found "$LOGDIR/dashboardConsole.log" "$jetty_pattern"
wait_until_found "$LOGDIR/simpleIdpConsole.log" "$jetty_pattern"
wait_until_found "$LOGDIR/prowlerConsole.log" ">> Listening on"
wait_until_found "$LOGDIR/adminConsole.log" "WEBrick::HTTPServer#start:"

echo "Starting Smoke Tests"
cd $DIR/acceptance-tests; bundle exec rake smokeTests

