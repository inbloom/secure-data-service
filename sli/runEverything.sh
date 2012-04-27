DIR=`pwd`
mkdir -p everyLog
cd everyLog; rm *.log
LOGDIR=$DIR/everyLog

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
cd $DIR/admin-tools; bundle exec rails server  > $LOGDIR/adminConsole.log 2>&1 &

echo Starting Simple IDP
cd $DIR/simple-idp; mvn jetty:run   > $LOGDIR/simpleIdpConsole.log 2>&1 &
