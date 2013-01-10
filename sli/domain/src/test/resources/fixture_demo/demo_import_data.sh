#!/bin/bash

#MONGO_HOME=/Users/jshort/mongo/bin
DB_HOST=devdal1.slidev.org
#devdal1.slidev.org
#DIR="$( cd "$( dirname "$0" )" && pwd )"

DB_NAME=devapp1
#devApps

echo $DIR

mongoimport --drop -d $DB_NAME -c aggregationdefinition -h $DB_HOST --file "aggregationdefinition.json"
mongoimport --drop -d $DB_NAME -c application -h $DB_HOST --file "application.json"
mongoimport --drop -d $DB_NAME -c assessment -h $DB_HOST --file "assessment.json"
mongoimport --drop -d $DB_NAME -c attendance -h $DB_HOST --file "attendance.json"
mongoimport --drop -d $DB_NAME -c attendanceEvent -h $DB_HOST --file "attendanceEvent.json"
mongoimport --drop -d $DB_NAME -c course -h $DB_HOST --file "course.json"
mongoimport --drop -d $DB_NAME -c educationOrganization -h $DB_HOST --file "education_organization.json"
mongoimport --drop -d $DB_NAME -c educationOrganizationschoolassociation -h $DB_HOST --file "educationOrganizationschoolassociation.json"
mongoimport --drop -d $DB_NAME -c oauth_access_token -h $DB_HOST --file "oauth_access_token.json"
mongoimport --drop -d $DB_NAME -c oauth_refresh_token -h $DB_HOST --file "oauth_refresh_token.json"
mongoimport --drop -d $DB_NAME -c oauthAuthorizationCode -h $DB_HOST --file "oauthAuthorizationCode.json"
mongoimport --drop -d $DB_NAME -c realm -h $DB_HOST --file "realm.json"
mongoimport --drop -d $DB_NAME -c roles -h $DB_HOST --file "roles.json"
mongoimport --drop -d $DB_NAME -c school -h $DB_HOST --file "school.json"
mongoimport --drop -d $DB_NAME -c section -h $DB_HOST --file "section.json"
mongoimport --drop -d $DB_NAME -c session -h $DB_HOST --file "session.json"
mongoimport --drop -d $DB_NAME -c staff -h $DB_HOST --file "staff.json"
mongoimport --drop -d $DB_NAME -c staffEducationOrganizationAssociation -h $DB_HOST --file "staffEducationOrganizationAssociation.json"
mongoimport --drop -d $DB_NAME -c student -h $DB_HOST --file "student.json"
mongoimport --drop -d $DB_NAME -c studentAssessment -h $DB_HOST --file "studentAssessment.json"
mongoimport --drop -d $DB_NAME -c studentSchoolAssociation -h $DB_HOST --file "studentSchoolAssociation.json"
mongoimport --drop -d $DB_NAME -c studentSectionAssociation -h $DB_HOST --file "studentSectionAssociation.json"
mongoimport --drop -d $DB_NAME -c teacher -h $DB_HOST --file "teacher.json"
mongoimport --drop -d $DB_NAME -c teacherSchoolAssociation -h $DB_HOST --file "teacherSchoolAssociation.json"
mongoimport --drop -d $DB_NAME -c teacherSectionAssociation -h $DB_HOST --file "teacherSectionAssociation.json"
