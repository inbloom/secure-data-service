#!/bin/bash 

mongo sli --eval 'db.dropDatabase();'
mongo ingestion_batch_job --eval 'db.dropDatabase();'
mongo is --eval 'db.dropDatabase();'
