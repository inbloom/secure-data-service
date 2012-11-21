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
       mongo sli --eval "db['tenant'].find({'body.tenantId' : '${tenant}'}).forEach(function(coll) { coll.dbName='$sha1DBName';db['tenant'].save(coll)})"

       #move customRole into tenant db
       mongo sli --eval "db.customRole.find({'metaData.tenantId': '${tenant}'}).forEach(function(d) {db = db.getSisterDB('${sha1DBName}');db.customRole.insert(d);})"
       #move custom_entities into tenant db
       mongo sli --eval "db.custom_entities.find({'metaData.tenantId': '${tenant}'}).forEach(function(d) {db = db.getSisterDB('${sha1DBName}');db.custom_entities.insert(d);})"
       #move applicationAuthorization into tenant db
       mongo sli --eval "db.applicationAuthorization.find({'metaData.tenantId': '${tenant}'}).forEach(function(d) {db = db.getSisterDB('${sha1DBName}');db.applicationAuthorization.insert(d);})"
       #move adminDelegation into tenant db
       mongo sli --eval "db.adminDelegation.find({'metaData.tenantId': '${tenant}'}).forEach(function(d) {db = db.getSisterDB('${sha1DBName}');db.adminDelegation.insert(d);})"

       #rename the SLC realm
       mongo sli --eval "db.realm.update({'body.uniqueIdentifier':'Shared Learning Infrastructure'}, {$set:{'body.uniqueIdentifier':'Shared Learning Collaborative'}})"
  done

#remove all the tenant-specified collections from sli
mongo sli --eval "db.getCollectionNames().forEach(function(coll) {if (coll!='system.indexes' && coll != 'system.js' && coll != 'system.profile' && coll != 'system.users' && coll != 'system.namespaces' && coll != 'tenant' && coll != 'securityEvent' && coll != 'realm' && coll != 'application' && coll != 'roles' && coll != 'tenantJobLock' && coll != 'userSession' && coll != 'userAccount') db[coll].drop(); })"
