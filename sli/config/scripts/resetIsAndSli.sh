#!/bin/bash

mongo sli --eval 'db.dropDatabase()'
mongo sli ../indexes/sli_indexes.js

mongo is --eval 'db.dropDatabase()'
mongo is ../indexes/is_indexes.js

mongo ingestion_batch_job --eval 'db.dropDatabase()'
mongo ingestion_batch_job ../indexes/ingestion_batch_job_indexes.js
