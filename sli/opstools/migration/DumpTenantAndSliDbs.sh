#!/bin/bash

#This script is to migrate the application and system collections from a 6.1 to 6.3 state.
#All ingestable data that exists in the "sli" database will be dropped.
#It should be executed before ingestion service is on.
#
#Before execution, have mongod or mongos running.
#To execute, from the current directory, run "sh 61to63DBMigration.sh"

 mongo_output=`mongo --quiet sli --eval 'db["tenant"].find().forEach(function(coll) { print(coll.body.tenantId)})    '`
   for tenant in $mongo_output
   do 
       echo $tenant
       sha1DBName=$(printf '%s' "$tenant" | openssl sha1 | awk '{print $2}')
       
       if [[ -z "$sha1DBName" ]]; 
       then
           sha1DBName=$(printf '%s' "$tenant" | openssl sha1)
       fi
       #add dbName field to tenant collection
       mongodump --db $sha1DBName

  done

 mongodump --db sli