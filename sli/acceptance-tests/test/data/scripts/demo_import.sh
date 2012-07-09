#!/bin/bash

MONGO_HOME=/Users/jshort/mongo/bin
DB_HOST=devdal1.slidev.org
#devdal1.slidev.org
DIR="$( cd "$( dirname "$0" )" && pwd )"

DB_NAME=devapp1
#devApps

echo $DIR

$MONGO_HOME/mongoimport --drop -d $DB_NAME -c realm -h $DB_HOST --file "$DIR/realm_data.json"
$MONGO_HOME/mongoimport --drop -d $DB_NAME -c staff -h $DB_HOST --file "$DIR/staff_data.json"
$MONGO_HOME/mongoimport --drop -d $DB_NAME -c teacher -h $DB_HOST --file "$DIR/teacher_data.json"
$MONGO_HOME/mongoimport --drop -d $DB_NAME -c student -h $DB_HOST --file "$DIR/student_data.json"
$MONGO_HOME/mongoimport --drop -d $DB_NAME -c school -h $DB_HOST --file "$DIR/school_data.json"
$MONGO_HOME/mongoimport --drop -d $DB_NAME -c section -h $DB_HOST --file "$DIR/section_data.json"
$MONGO_HOME/mongoimport --drop -d $DB_NAME -c teacherSchoolAssociation -h $DB_HOST --file "$DIR/teacherSchoolAssociation_data.json"
$MONGO_HOME/mongoimport --drop -d $DB_NAME -c teacherSectionAssociation -h $DB_HOST --file "$DIR/teacherSectionAssociation_data.json"
$MONGO_HOME/mongoimport --drop -d $DB_NAME -c studentSectionAssociation -h $DB_HOST --file "$DIR/studentSectionAssociation_data.json"
$MONGO_HOME/mongoimport --drop -d $DB_NAME -c studentSchoolAssociation -h $DB_HOST --file "$DIR/studentSchoolAssociation_data.json"
