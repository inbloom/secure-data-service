#!/bin/bash

DB_HOST=localhost
DB_NAME=sli

DIR="$( cd "$( dirname "$0" )" && pwd )"
echo $DIR

for f in *.json; do
    COLLECTION=${f:4}
    COLLECTION=${COLLECTION%.*n}
    mongoimport --drop -d $DB_NAME -c $COLLECTION -h $DB_HOST --file "${f}"
done
