#!/bin/bash

########################################################################
# To skip dropping 'sli' database:
#   > sh resetAllDbs_rc.sh --noSLIdrop
########################################################################

CLEAR_SLI=1

for i in $*
do
  case $i in
  --noSLIdrop)
    CLEAR_SLI=0
    ;;
  --saveDb=*)
    SAVEDB=`echo $i | sed 's/[-a-zA-Z0-9]*=//'`
    ;;
  --help|-h)
    echo "Usage:"
    echo "  > resetAllDbs_rc.sh --noSLIdrop"
    exit 0
    ;;
  *)
    echo "Invalid option(s) passed"
    exit 1
    ;;
  esac
done

dropCollectionsAndDropDB()
{
  ALL_COLLS=`mongo $1 --quiet --eval 'db.getCollectionNames()' | sed -e 's/,/ /g'`
  for COLL in $ALL_COLLS ; do
    if [ "$COLL" != "system.indexes" ] ; then
      mongo $1 --quiet --eval "db.$COLL.drop()"
    fi   
  done
  echo "Dropping database $1"
  mongo $1 --quiet --eval 'db.dropDatabase()'
}

############################################################################################
############################################################################################
############################################################################################

if [ -n "$SAVEDB"] ; then
  SAVEDB="blahblahblahb1212123123123"
fi

echo " ***** Dropping collections and then databases"
ALL_DBS=`mongo --quiet --eval 'db.getMongo().getDBNames()' | sed -e 's/,/ /g'`
for DB in $ALL_DBS ; do
  if [ "$DB" != "$SAVEDB" -a "$DB" != "test" -a "$DB" != "config" -a "$DB" != "local" -a "$DB" != "admin" ] ; then
    if [ "$DB" != "sli" ] ; then
      dropCollectionsAndDropDB $DB
    elif [ "$DB" == "sli" -a "$CLEAR_SLI" == 1 ] ; then
      dropCollectionsAndDropDB $DB
    else
      echo "Skipping database $DB"
    fi
  else
    echo "Skipping database $DB"
  fi
done




