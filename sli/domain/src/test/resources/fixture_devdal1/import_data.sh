#!/bin/bash

MONGO_HOME=C:/Users/dliu/mongodb-win32-x86_64-2.0.1/bin
DB_HOST=dliu.slidev.org
#devdal1.slidev.org
#DIR="$( cd "$( dirname "$0" )" && pwd )"

DB_NAME=devapp1
#devApps

#echo $DIR

$MONGO_HOME/mongoimport --drop -d $DB_NAME -c realm -h $DB_HOST --file "realm.json"
$MONGO_HOME/mongoimport --drop -d $DB_NAME -c staff -h $DB_HOST --file "staff.json"
$MONGO_HOME/mongoimport --drop -d $DB_NAME -c teacher -h $DB_HOST --file "teacher.json"
$MONGO_HOME/mongoimport --drop -d $DB_NAME -c assessment -h $DB_HOST --file "assessment.json"
$MONGO_HOME/mongoimport --drop -d $DB_NAME -c course -h $DB_HOST --file "course.json"
$MONGO_HOME/mongoimport --drop -d $DB_NAME -c session -h $DB_HOST --file "session.json"
$MONGO_HOME/mongoimport --drop -d $DB_NAME -c attendance -h $DB_HOST --file "attendance.json"
$MONGO_HOME/mongoimport --drop -d $DB_NAME -c student -h $DB_HOST --file "student.json"
$MONGO_HOME/mongoimport --drop -d $DB_NAME -c school -h $DB_HOST --file "school.json"
$MONGO_HOME/mongoimport --drop -d $DB_NAME -c section -h $DB_HOST --file "section.json"
$MONGO_HOME/mongoimport --drop -d $DB_NAME -c teacherSchoolAssociation -h $DB_HOST --file "teacherSchoolAssociation.json"
$MONGO_HOME/mongoimport --drop -d $DB_NAME -c teacherSectionAssociation -h $DB_HOST --file "teacherSectionAssociation.json"
$MONGO_HOME/mongoimport --drop -d $DB_NAME -c studentSectionAssociation -h $DB_HOST --file "studentSectionAssociation.json"
$MONGO_HOME/mongoimport --drop -d $DB_NAME -c studentAssessment -h $DB_HOST --file "studentAssessment.json"
$MONGO_HOME/mongoimport --drop -d $DB_NAME -c studentSchoolAssociation -h $DB_HOST --file "studentSchoolAssociation.json"
