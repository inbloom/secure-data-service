#!/bin/bash

MONGO_HOME=/usr/bin
DB_HOST=localhost
#testdal1.slidev.org
DIR="$( cd "$( dirname "$0" )" && pwd )"

echo $DIR

$MONGO_HOME/mongoimport --drop -d sli -c teacher -h $DB_HOST --file "$DIR/teacher_data.json"
$MONGO_HOME/mongoimport --drop -d sli -c section -h $DB_HOST --file "$DIR/section_data.json"
$MONGO_HOME/mongoimport --drop -d sli -c teacherschoolassociation -h $DB_HOST --file "$DIR/teacherSchoolAssociation_data.json"
$MONGO_HOME/mongoimport --drop -d sli -c teachersectionassociation -h $DB_HOST --file "$DIR/teacherSectionAssociation_data.json"
$MONGO_HOME/mongoimport --drop -d sli -c studentsectionassociation -h $DB_HOST --file "$DIR/studentSectionAssociation_data.json"

