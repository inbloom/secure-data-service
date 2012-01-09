#!/bin/bash

MONGO_HOME=/usr/bin
DB_HOST=localhost
#testdal1.slidev.org
DIR="$( cd "$( dirname "$0" )" && pwd )"

echo $DIR

$MONGO_HOME/mongoimport --drop -d sli -c student -h $DB_HOST --file "$DIR/../student_fixture.json"
$MONGO_HOME/mongoimport --drop -d sli -c school -h $DB_HOST --file "$DIR/../school_fixture.json"
$MONGO_HOME/mongoimport --drop -d sli -c studentschoolassociation -h $DB_HOST --file "$DIR/../student_enrollments_fixture.json"
$MONGO_HOME/mongoimport --drop -d sli -c teacher -h $DB_HOST --file "$DIR/../teacher_fixture.json"
$MONGO_HOME/mongoimport --drop -d sli -c section -h $DB_HOST --file "$DIR/../section_fixture.json"
