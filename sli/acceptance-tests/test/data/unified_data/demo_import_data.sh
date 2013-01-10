#!/bin/bash

#MONGO_HOME=/Users/jshort/mongo/bin
DB_HOST=devdal1.slidev.org
#devdal1.slidev.org
#DIR="$( cd "$( dirname "$0" )" && pwd )"

DB_NAME=devapp1
#devApps

echo $DIR

mongoimport --drop -d $DB_NAME -c assessment -h $DB_HOST --file "uds_assessments.json"
mongoimport --drop -d $DB_NAME -c attendance -h $DB_HOST --file "uds_attendance.json"
mongoimport --drop -d $DB_NAME -c course -h $DB_HOST --file "uds_courses.json"
mongoimport --drop -d $DB_NAME -c educationOrganization -h $DB_HOST --file "uds_education_organizations.json"
mongoimport --drop -d $DB_NAME -c school -h $DB_HOST --file "uds_schools.json"
mongoimport --drop -d $DB_NAME -c section -h $DB_HOST --file "uds_sections.json"
mongoimport --drop -d $DB_NAME -c sessions -h $DB_HOST --file "uds_sessions.json"
mongoimport --drop -d $DB_NAME -c studentAssessment -h $DB_HOST --file "uds_student_assessment.json"
mongoimport --drop -d $DB_NAME -c studentSchoolAssociation -h $DB_HOST --file "uds_student_school_associations.json"
mongoimport --drop -d $DB_NAME -c studentSectionAssociation -h $DB_HOST --file "uds_student_section_associations.json"
mongoimport --drop -d $DB_NAME -c student -h $DB_HOST --file "uds_students.json"
mongoimport --drop -d $DB_NAME -c teacherSchoolAssociation -h $DB_HOST --file "uds_teacher_school_associations.json"
mongoimport --drop -d $DB_NAME -c teacherSectionAssociation -h $DB_HOST --file "uds_teacher_section_associations.json"
mongoimport --drop -d $DB_NAME -c teacher -h $DB_HOST --file "uds_teachers.json"
