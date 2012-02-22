#!/bin/bash

MONGO_HOME=/Users/jshort/mongo/bin
DB_HOST=devdal1.slidev.org
#devdal1.slidev.org
DIR="$( cd "$( dirname "$0" )" && pwd )"

DB_NAME=devapp1
#devApps

echo $DIR

$MONGO_HOME/mongoimport --drop -d $DB_NAME -c assessment -h $DB_HOST --file "$DIR/assessment_data.json"
$MONGO_HOME/mongoimport --drop -d $DB_NAME -c studentAssessmentAssociation -h $DB_HOST --file "$DIR/studentAssessmentAssociation_data.json"
