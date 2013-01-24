#!/bin/sh

DIR=`pwd`
mkdir -p everyLog
cd everyLog; rm *.log
LOGDIR=$DIR/everyLog

if [ -z "$MAVEN_OPTS" ]; then
  export MAVEN_OPTS=-Xmx4096m
fi

echo PWD: $DIR
echo Starting API
cd $DIR/api; mvn jetty:run  > $LOGDIR/apiConsole.log 2>&1 &

echo Starting Ingestion
cd $DIR/ingestion/ingestion-service; mvn jetty:run  > $LOGDIR/ingestionConsole.log 2>&1 &

echo Starting Dashboard
cd $DIR/dashboard; mvn jetty:run  > $LOGDIR/dashboardConsole.log 2>&1 &

echo Starting Data Prowler
cd $DIR/databrowser; bundle exec rails server  > $LOGDIR/prowlerConsole.log 2>&1 &

echo Starting Admin Tools
cd $DIR/admin-tools/admin-rails; bundle exec rails server  > $LOGDIR/adminConsole.log 2>&1 &

echo Starting Simple IDP
cd $DIR/simple-idp; mvn jetty:run   > $LOGDIR/simpleIdpConsole.log 2>&1 &

echo Starting Sample App
#cd $DIR/SDK/sample; mvn jetty:run &> $LOGDIR/sampleAppConsole.log &

echo Starting MockZIS
cd $DIR/sif/mock-zis; mvn jetty:run > $LOGDIR/mockZisConsole.log 2>&1 &

echo Starting Sif Agent
cd $DIR/sif/sif-agent; mvn tomcat:run > $LOGDIR/sifAgentConsole.log 2>&1 &

