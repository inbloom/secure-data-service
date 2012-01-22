#!/bin/bash

MONGO_HOME=/Users/jshort/mongo/bin
DB_HOST=devdal1.slidev.org
#devdal1.slidev.org
DIR="$( cd "$( dirname "$0" )" && pwd )"

DB_NAME=devApps
#devApps

echo $DIR

$MONGO_HOME/mongoimport --drop -d $DB_NAME -c teacher -h $DB_HOST --file "$DIR/teacher_data.json"
$MONGO_HOME/mongoimport --drop -d $DB_NAME -c section -h $DB_HOST --file "$DIR/section_data.json"
$MONGO_HOME/mongoimport --drop -d $DB_NAME -c teacherSchoolAssociation -h $DB_HOST --file "$DIR/teacherSchoolAssociation_data.json"
$MONGO_HOME/mongoimport --drop -d $DB_NAME -c teacherSectionAssociation -h $DB_HOST --file "$DIR/teacherSectionAssociation_data.json"
$MONGO_HOME/mongoimport --drop -d $DB_NAME -c studentSectionAssociation -h $DB_HOST --file "$DIR/studentSectionAssociation_data.json"

