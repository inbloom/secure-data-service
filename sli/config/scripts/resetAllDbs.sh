#!/bin/bash

########################################################################
# To skip dropping databases:
#   > sh resetAllDbs.sh --nodrop
########################################################################

CLEAR_DB=1
if [ ! -z $1 ] && [ $1 = "--nodrop" ]
then
  CLEAR_DB=0
fi

if [ $CLEAR_DB = 1 ]
then
  mongo sli --quiet --eval 'db.adminCommand("listDatabases").databases.forEach( function (d) {
    var name = d.name;
    if (name != "local" && name != "admin" && name != "config") {
      print("[MONGO] dropping database: " + name);
      db.getSiblingDB(d.name).dropDatabase();
    }
  });'
fi

echo "[MONGO] indexing database: sli"; mongo sli ../indexes/sli_indexes.js --quiet 
echo "[MONGO] indexing database: ingestion_batch_job"; mongo ingestion_batch_job ../indexes/ingestion_batch_job_indexes.js --quiet
echo "[MONGO] indexing database: midgar"; ruby indexTenantDB.rb localhost 02f7abaa9764db2fa3c1ad852247cd4ff06b2c0a
echo "[MONGO] indexing tenant-specific database"; ruby indexTenantDB.rb localhost d36f43474916ad310100c9711f21b65bd8231cc6
echo "[MONGO] indexing database: hyrule"; ruby indexTenantDB.rb localhost f25ce1b8a399bd8621a57427a20039b4b13935db
echo "[MONGO] indexing tenant-specific database"; ruby indexTenantDB.rb localhost eb962e0ee6c86d75b55e8f861737c50ca308e021
echo "[MONGO] indexing database: 2ddda3a58e6da7f68d764e1180f244fb59c6e30e"; ruby indexTenantDB.rb localhost 2ddda3a58e6da7f68d764e1180f244fb59c6e30e
echo "[MONGO] indexing database: chaos_mokey_org"; ruby indexTenantDB.rb localhost 4ab064f9f39d1b1a82f42807a9491abf18592ce6
echo "[MONGO] indexing database: fakedev_zork_net"; ruby indexTenantDB.rb localhost 15723708b106ebe7f018885fe62ea14fc6cf36c3
echo "[MONGO] indexing database: developer-email@slidev.org"; ruby indexTenantDB.rb localhost e4b96dfb6c102e5cd98859ff4e92710cd6efded2
echo "[MONGO] indexing database: sandboxadministrator@slidev.org"; ruby indexTenantDB.rb localhost 40b558db2acdbbe0d12e9e4dc84380b178d16017
echo "[MONGO] indexing database: anothersandboxdeveloper@slidev.org"; ruby indexTenantDB.rb localhost ba2ef910d2258a0b23b27ed6f3d53c30b3d090a0
echo "[MONGO] indexing database: mreynolds"; ruby indexTenantDB.rb localhost dd682663e4e3da13df308b2ed46f5cc58aead4f9
echo "[MONGO] indexing database: devldapuser@slidev.org"; ruby indexTenantDB.rb localhost 225d796c7e1346e1b1e3d6287b897fa467bb1e72
