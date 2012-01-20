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
$MONGO_HOME/mongoimport --drop -d sli -c section -h $DB_HOST --file "$DIR/../assessment_fixture.json"
$MONGO_HOME/mongoimport --drop -d sli -c educationOrganization -h $DB_HOST --file "$DIR/../educationOrganization_fixture.json"
$MONGO_HOME/mongoimport --drop -d sli -c staff -h $DB_HOST --file "$DIR/../staff_fixture.json"
$MONGO_HOME/mongoimport --drop -d sli -c staffEducationOrganizationAssociation -h $DB_HOST --file "$DIR/../staffEducationOrganizationAssociation_fixture.json"
$MONGO_HOME/mongoimport --drop -d sli -c educationOrganizationassociation -h $DB_HOST --file "$DIR/../educationOrganizationAssociation_fixture.json"
$MONGO_HOME/mongoimport --drop -d sli -c educationOrganizationschoolassociation -h $DB_HOST --file "$DIR/../school_educationOrganization_fixure.json"
