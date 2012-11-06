#!/bin/bash

mongo sli --quiet --eval 'db.adminCommand("listDatabases").databases.forEach( function (d) {
  var name = d.name;
  if (name != "local" && name != "admin" && name != "config") {
  	print("[MONGO] dropping database: " + name);
    db.getSiblingDB(d.name).dropDatabase();
  }
});'

echo "[MONGO] indexing database: sli"; mongo sli ../indexes/sli_indexes.js --quiet
echo "[MONGO] indexing database: is"; mongo is ../indexes/is_indexes.js --quiet
echo "[MONGO] indexing database: ingestion_batch_job"; mongo ingestion_batch_job ../indexes/ingestion_batch_job_indexes.js --quiet
echo "[MONGO] indexing database: midgar"; mongo 02f7abaa9764db2fa3c1ad852247cd4ff06b2c0a ../indexes/tenantDB_indexes.js --quiet
echo "[MONGO] indexing tenant-specific database"; mongo d36f43474916ad310100c9711f21b65bd8231cc6 ../indexes/tenantDB_indexes.js --quiet
echo "[MONGO] indexing database: hyrule"; mongo f25ce1b8a399bd8621a57427a20039b4b13935db ../indexes/tenantDB_indexes.js --quiet
echo "[MONGO] indexing tenant-specific database"; mongo eb962e0ee6c86d75b55e8f861737c50ca308e021 ../indexes/tenantDB_indexes.js --quiet
echo "[MONGO] indexing database: 2ddda3a58e6da7f68d764e1180f244fb59c6e30e"; mongo 2ddda3a58e6da7f68d764e1180f244fb59c6e30e ../indexes/tenantDB_indexes.js --quiet
