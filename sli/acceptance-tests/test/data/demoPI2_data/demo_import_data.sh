#!/bin/bash

MONGO_HOME=/usr/bin
DB_HOST=devdal1.slidev.org
#devdal1.slidev.org
DIR="$( cd "$( dirname "$0" )" && pwd )"

DB_NAME=sli
#devApps

echo $DIR

$MONGO_HOME/mongoimport --drop -d $DB_HOST -c teacher -h $DB_HOST --file "$DIR/teacher_data.json"
$MONGO_HOME/mongoimport --drop -d $DB_HOST -c section -h $DB_HOST --file "$DIR/section_data.json"
$MONGO_HOME/mongoimport --drop -d $DB_HOST -c teacherschoolassociation -h $DB_HOST --file "$DIR/teacherSchoolAssociation_data.json"
$MONGO_HOME/mongoimport --drop -d $DB_HOST -c teachersectionassociation -h $DB_HOST --file "$DIR/teacherSectionAssociation_data.json"
$MONGO_HOME/mongoimport --drop -d $DB_HOST -c studentsectionassociation -h $DB_HOST --file "$DIR/studentSectionAssociation_data.json"

