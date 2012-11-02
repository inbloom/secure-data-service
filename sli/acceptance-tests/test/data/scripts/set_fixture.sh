#!/bin/bash

MONGO_HOME=/usr/bin
DB_HOST=localhost
#testdal1.slidev.org
DIR="$( cd "$( dirname "$0" )" && pwd )"

echo $DIR

$MONGO_HOME/mongoimport --drop -d 02f7abaa9764db2fa3c1ad852247cd4ff06b2c0a -c student -h $DB_HOST --file "$DIR/../Midgar_data/student_fixture.json"
$MONGO_HOME/mongoimport --drop -d 02f7abaa9764db2fa3c1ad852247cd4ff06b2c0a -c school -h $DB_HOST --file "$DIR/../Midgar_data/school_fixture.json"
$MONGO_HOME/mongoimport --drop -d 02f7abaa9764db2fa3c1ad852247cd4ff06b2c0a -c studentschoolassociation -h $DB_HOST --file "$DIR/../Midgar_data/student_enrollments_fixture.json"
$MONGO_HOME/mongoimport --drop -d 02f7abaa9764db2fa3c1ad852247cd4ff06b2c0a -c teacher -h $DB_HOST --file "$DIR/../Midgar_data/teacher_fixture.json"
$MONGO_HOME/mongoimport --drop -d 02f7abaa9764db2fa3c1ad852247cd4ff06b2c0a -c section -h $DB_HOST --file "$DIR/../Midgar_data/section_fixture.json"
$MONGO_HOME/mongoimport --drop -d 02f7abaa9764db2fa3c1ad852247cd4ff06b2c0a -c section -h $DB_HOST --file "$DIR/../Midgar_data/assessment_fixture.json"
$MONGO_HOME/mongoimport --drop -d 02f7abaa9764db2fa3c1ad852247cd4ff06b2c0a -c educationOrganization -h $DB_HOST --file "$DIR/../Midgar_data/educationOrganization_fixture.json"
$MONGO_HOME/mongoimport --drop -d 02f7abaa9764db2fa3c1ad852247cd4ff06b2c0a -c staff -h $DB_HOST --file "$DIR/../Midgar_data/staff_fixture.json"
$MONGO_HOME/mongoimport --drop -d 02f7abaa9764db2fa3c1ad852247cd4ff06b2c0a -c staffEducationOrganizationAssociation -h $DB_HOST --file "$DIR/../Midgar_data/staffEducationOrganizationAssociation_fixture.json"
$MONGO_HOME/mongoimport --drop -d 02f7abaa9764db2fa3c1ad852247cd4ff06b2c0a -c educationOrganizationassociation -h $DB_HOST --file "$DIR/../Midgar_data/educationOrganizationAssociation_fixture.json"
$MONGO_HOME/mongoimport --drop -d 02f7abaa9764db2fa3c1ad852247cd4ff06b2c0a -c educationOrganizationschoolassociation -h $DB_HOST --file "$DIR/../Midgar_data/school_educationOrganization_fixture.json"

$MONGO_HOME/mongoimport --drop -d d36f43474916ad310100c9711f21b65bd8231cc6 -c student -h $DB_HOST --file "$DIR/../Hyrule_data/student_fixture.json"
$MONGO_HOME/mongoimport --drop -d d36f43474916ad310100c9711f21b65bd8231cc6 -c school -h $DB_HOST --file "$DIR/../Hyrule_data/school_fixture.json"
$MONGO_HOME/mongoimport --drop -d d36f43474916ad310100c9711f21b65bd8231cc6 -c studentschoolassociation -h $DB_HOST --file "$DIR/../Hyrule_data/student_enrollments_fixture.json"
$MONGO_HOME/mongoimport --drop -d d36f43474916ad310100c9711f21b65bd8231cc6 -c teacher -h $DB_HOST --file "$DIR/../Hyrule_data/teacher_fixture.json"
$MONGO_HOME/mongoimport --drop -d d36f43474916ad310100c9711f21b65bd8231cc6 -c section -h $DB_HOST --file "$DIR/../Hyrule_data/section_fixture.json"
$MONGO_HOME/mongoimport --drop -d d36f43474916ad310100c9711f21b65bd8231cc6 -c section -h $DB_HOST --file "$DIR/../Hyrule_data/assessment_fixture.json"
$MONGO_HOME/mongoimport --drop -d d36f43474916ad310100c9711f21b65bd8231cc6 -c educationOrganization -h $DB_HOST --file "$DIR/../Hyrule_data/educationOrganization_fixture.json"
$MONGO_HOME/mongoimport --drop -d d36f43474916ad310100c9711f21b65bd8231cc6 -c staff -h $DB_HOST --file "$DIR/../Hyrule_data/staff_fixture.json"
$MONGO_HOME/mongoimport --drop -d d36f43474916ad310100c9711f21b65bd8231cc6 -c staffEducationOrganizationAssociation -h $DB_HOST --file "$DIR/../Hyrule_data/staffEducationOrganizationAssociation_fixture.json"
$MONGO_HOME/mongoimport --drop -d d36f43474916ad310100c9711f21b65bd8231cc6 -c educationOrganizationassociation -h $DB_HOST --file "$DIR/../Hyrule_data/educationOrganizationAssociation_fixture.json"
$MONGO_HOME/mongoimport --drop -d d36f43474916ad310100c9711f21b65bd8231cc6 -c educationOrganizationschoolassociation -h $DB_HOST --file "$DIR/../Hyrule_data/school_educationOrganization_fixture.json"
