#!/bin/bash

#Scripts runs the indexing script located at SLI_HOME/opstools/migration/index_migration/indexTenantDb.rb 
#for each tenant defined in the tenant collection

#Usage: sh ReindexTenantDatabases.sh
#       sh ReindexTenantDatabases.sh <MONGO_HOST> <PATH_TO_indexTenantDb.rb>

#Default: <MONGO_HOST>=localhost <PATH_TO_indexTenantDb.rb>=indexTenantDb.rb 

if [ -z $1 ]; then
  mongo_host=localhost
else
  mongo_host=$1
fi
if [ -z $2 ]; then
  index_script=indexTenantDb.rb
else
  index_script=$2
fi

bundle install
 echo "[MONGO] reindexing databases:"
 mongo_output=`mongo --quiet sli --eval 'db["tenant"].find().forEach(function(coll) { print(coll.body.dbName)})    '`
   for dbName in $mongo_output
   do 
       #add dbName field to tenant collection
       echo $dbName
       bundle exec ruby $index_script $mongo_host $dbName

  done