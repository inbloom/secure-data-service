#!/bin/bash
#
# This script parses an ingestion log
#
#set -x

if [ $# -ne 1 ];
then
  echo "Usage: scripts/parse_ingerstion_log FILE_NAME (run from the config/ directory)"
  exit 1
fi

FILE=$1

###################

function SecondsFromLine {
  if [ -z "$1" ] ; then
    echo 0
  else
    DATE=`echo $1 | sed -e 's/^\([^\.]*\)\..*$/\1/'`
    ABS=`date -d "$DATE" +%s`
    let "REL = $ABS - $INGEST_START"
    echo $REL
  fi
}

function SecondsFromGrep {
  LINE=`grep -m 1 "$1" $FILE`
  echo `SecondsFromLine "$LINE"`
}

function SecondsFromLastGrep {
  LINE=`grep "$1" $FILE | tail -n 1`
  echo `SecondsFromLine "$LINE"`
}

###################

INTERCHANGES="InterchangeAssessmentMetadata.xml InterchangeEducationOrganization.xml InterchangeEducationOrgCalendar.xml InterchangeMasterSchedule.xml InterchangeStaffAssociation.xml InterchangeStudentCohort.xml InterchangeStudentEnrollment.xml InterchangeStudentParent.xml InterchangeStudentAssessment.xml"

ENTITIES="learningStandard assessmentFamily assessmentPeriodDescriptor assessment assessmentItem courseOffering course localEducationAgency school stateEducationAgency studentAssessment teacher studentSchoolAssociation cohort studentCohortAssociation staffCohortAssociation session gradingPeriod student calendarDate program section teacherSchoolAssociation studentAssessmentItem studentSectionAssociation teacherSectionAssociation staff staffEducationOrganizationAssociation staffProgramAssociation parent graduationPlan studentParentAssociation"

rm -f out.tmp
#
# Start/finish
#
INGEST_START=0
INGEST_START=`SecondsFromGrep "Zip file detected"` 
INGEST_TOTAL=`SecondsFromGrep "Clearing cache at job completion"` 
if [ $INGEST_TOTAL -eq 0 ] ; then
  echo "ERROR: $INGEST_TOTAL is 0, ingestion probably did not fully complete"
  exit
fi
let "INGEST_TOTAL_MIN = INGEST_TOTAL / 60"
echo "Parsing $FILE, started at $INGEST_START, $INGEST_TOTAL seconds ($INGEST_TOTAL_MIN minutes)"

#
# IdRefResolutionCallable
#
echo >> out.tmp
printf "%-40s %8s %8s %8s\n" "IdRefResolutionCallable" "Start" "Finish" "Elapsed" >> out.tmp
printf "%-40s %8s %8s %8s\n" "-----------------------" "-----" "------" "-------" >> out.tmp

IDREF_MIN_START=999999999
IDREF_MAX_FINISH=0
rm -f parse.tmp
for INTERCHANGE in $INTERCHANGES ; do
  START=`SecondsFromGrep "Starting IdRefResolutionCallable for: $INTERCHANGE"` 
  if [ $START -gt 0 ] ; then
    FINISH=`SecondsFromGrep "Finished IdRefResolutionCallable for: $INTERCHANGE"` 
    let "ELAPSED = $FINISH - $START"
    if [ $ELAPSED -gt 0 ] ; then
      let "IDREF_MIN_START = $START < $IDREF_MIN_START ? $START : $IDREF_MIN_START"
      let "IDREF_MAX_FINISH = $FINISH > $IDREF_MAX_FINISH ? $FINISH : $IDREF_MAX_FINISH"
      printf "%-40s %8d %8d %8d\n" $INTERCHANGE $START $FINISH $ELAPSED >> parse.tmp
    fi
  fi
done
sort -k 2 -n parse.tmp >> out.tmp
rm parse.tmp

let "IDREF_ELAPSED = $IDREF_MAX_FINISH - $IDREF_MIN_START"
printf "%-40s %8d %8d %8d\n" "TOTAL" $IDREF_MIN_START $IDREF_MAX_FINISH $IDREF_ELAPSED >> out.tmp

#
# SmooksCallable
#
echo >> out.tmp
printf "%-40s %8s %8s %8s\n" "SmooksCallable" "Start" "Finish" "Elapsed" >> out.tmp
printf "%-40s %8s %8s %8s\n" "--------------" "-----" "------" "-------" >> out.tmp

SMOOKS_MIN_START=999999999
SMOOKS_MAX_FINISH=0
rm -f parse.tmp
for INTERCHANGE in $INTERCHANGES ; do
  START=`SecondsFromGrep "Starting SmooksCallable for: $INTERCHANGE"` 
  if [ $START -gt 0 ] ; then
    FINISH=`SecondsFromGrep "Finished SmooksCallable for: $INTERCHANGE"` 
    let "ELAPSED = $FINISH - $START"
    if [ $ELAPSED -gt 0 ] ; then
      let "SMOOKS_MIN_START = $START < $SMOOKS_MIN_START ? $START : $SMOOKS_MIN_START"
      let "SMOOKS_MAX_FINISH = $FINISH > $SMOOKS_MAX_FINISH ? $FINISH : $SMOOKS_MAX_FINISH"
      printf "%-40s %8d %8d %8d\n" $INTERCHANGE $START $FINISH $ELAPSED >> parse.tmp
    fi
  fi
done
sort -k 2 -n parse.tmp >> out.tmp
rm parse.tmp

let "SMOOKS_ELAPSED = $SMOOKS_MAX_FINISH - $SMOOKS_MIN_START"
printf "%-40s %8d %8d %8d\n" "TOTAL" $SMOOKS_MIN_START $SMOOKS_MAX_FINISH $SMOOKS_ELAPSED >> out.tmp

#
# Staging
#
echo >> out.tmp
printf "%-40s %8s %8s %8s %10s\n" "Staging" "Start" "Finish" "Elapsed" "Count" >> out.tmp
printf "%-40s %8s %8s %8s %10s\n" "-------" "-----" "------" "-------" "-----" >> out.tmp

STAGING_MIN_START=999999999
STAGING_MAX_FINISH=0
STAGING_TOTAL=0
rm -f parse.tmp
for ENTITY in $ENTITIES ; do
  START=`SecondsFromGrep "Persisted [0-9]* records of type $ENTITY *$"` 
  if [ $START -gt 0 ] ; then
    FINISH=`SecondsFromLastGrep "Persisted [0-9]* records of type $ENTITY *$"` 
    let "ELAPSED = $FINISH - $START"
    if [ $ELAPSED -gt 0 ] ; then
      let "STAGING_MIN_START = $START < $STAGING_MIN_START ? $START : $STAGING_MIN_START"
      let "STAGING_MAX_FINISH = $FINISH > $STAGING_MAX_FINISH ? $FINISH : $STAGING_MAX_FINISH"
      echo "0" > sed.tmp
      grep "Persisted [0-9]* records of type $ENTITY *$" $FILE | sed -e "s/^.*Persisted \([0-9]*\) records of type $ENTITY *$/\1 +/" >> sed.tmp
      echo "p" >> sed.tmp
      NUM=`dc < sed.tmp`
      let "STAGING_TOTAL = $STAGING_TOTAL + $NUM"
      rm sed.tmp
      printf "%-40s %8d %8d %8d %10d\n" $ENTITY $START $FINISH $ELAPSED $NUM >> parse.tmp
    fi
  fi
done
sort -k 2 -n parse.tmp >> out.tmp
rm parse.tmp

let "STAGING_ELAPSED = $STAGING_MAX_FINISH - $STAGING_MIN_START"
printf "%-40s %8d %8d %8d %10d\n" "TOTAL" $STAGING_MIN_START $STAGING_MAX_FINISH $STAGING_ELAPSED $STAGING_TOTAL >> out.tmp

#
# Persistence
#
echo >> out.tmp
printf "%-40s %8s %8s %8s %10s\n" "Persistence" "Start" "Finish" "Elapsed" "Count" >> out.tmp
printf "%-40s %8s %8s %8s %10s\n" "-----------" "-----" "------" "-------" "-----" >> out.tmp

PERSIST_MIN_START=999999999
PERSIST_MAX_FINISH=0
PERSIST_TOTAL=0
rm -f parse.tmp
for ENTITY in $ENTITIES ; do
  START=`SecondsFromGrep "Bulk insert of [0-9]* queued records into collection: $ENTITY$"` 
  if [ $START -gt 0 ] ; then
    FINISH=`SecondsFromLastGrep "Bulk insert of [0-9]* queued records into collection: $ENTITY$"` 
    let "ELAPSED = $FINISH - $START"
    if [ $ELAPSED -gt 0 ] ; then
      let "PERSIST_MIN_START = $START < $PERSIST_MIN_START ? $START : $PERSIST_MIN_START"
      let "PERSIST_MAX_FINISH = $FINISH > $PERSIST_MAX_FINISH ? $FINISH : $PERSIST_MAX_FINISH"
      echo "0" > sed.tmp
      grep "Bulk insert of [0-9]* queued records into collection: $ENTITY$" $FILE | sed -e "s/^.*Bulk insert of \([0-9]*\) queued records into collection: $ENTITY$/\1 +/" >> sed.tmp
      echo "p" >> sed.tmp
      NUM=`dc < sed.tmp`
      let "PERSIST_TOTAL = $PERSIST_TOTAL + $NUM"
      rm sed.tmp
      printf "%-40s %8d %8d %8d %10d\n" $ENTITY $START $FINISH $ELAPSED $NUM >> parse.tmp
    fi
  fi
done
sort -k 2 -n parse.tmp >> out.tmp
rm parse.tmp

let "PERSIST_ELAPSED = $PERSIST_MAX_FINISH - $PERSIST_MIN_START"
printf "%-40s %8d %8d %8d %10d\n" "TOTAL" $PERSIST_MIN_START $PERSIST_MAX_FINISH $PERSIST_ELAPSED $PERSIST_TOTAL >> out.tmp

#
# Summary
#
EXTRACT_ZIP_ELAPSED=`SecondsFromGrep "Extracted zip file to"` 

DBCREATE_START=`SecondsFromGrep "Running spin up scripts now"` 
if [ $DBCREATE_START -gt 0 ] ; then
  DBCREATE_FINISH=`SecondsFromLastGrep "Tenant ready flag for [^ ]* now marked: true"` 
  let "DBCREATE_ELAPSED = $DBCREATE_FINISH - $DBCREATE_START"
  let "DBCREATE_ELAPSED_MIN = $DBCREATE_ELAPSED / 60"
  let "DBCREATE_ELAPSED_PERCENT = (($DBCREATE_ELAPSED * 100) + ($INGEST_TOTAL/2)) / $INGEST_TOTAL"
fi

REMOVE_START=`SecondsFromGrep "Removing staged entities in collection"` 
REMOVE_FINISH=`SecondsFromLastGrep "Removing staged entities in collection"` 
let "REMOVE_ELAPSED = $REMOVE_FINISH - $REMOVE_START"

let "EXTRACT_ZIP_ELAPSED_MIN = $EXTRACT_ZIP_ELAPSED / 60"
let "IDREF_ELAPSED_MIN = $IDREF_ELAPSED / 60"
let "STAGING_ELAPSED_MIN = $STAGING_ELAPSED / 60"
let "SMOOKS_ELAPSED_MIN = $SMOOKS_ELAPSED / 60"
let "PERSIST_ELAPSED_MIN = $PERSIST_ELAPSED / 60"
let "REMOVE_ELAPSED_MIN = $REMOVE_ELAPSED / 60"

let "EXTRACT_ZIP_ELAPSED_PERCENT = (($EXTRACT_ZIP_ELAPSED * 100) + ($INGEST_TOTAL/2)) / $INGEST_TOTAL"
let "IDREF_ELAPSED_PERCENT = (($IDREF_ELAPSED * 100) + ($INGEST_TOTAL/2)) / $INGEST_TOTAL"
let "STAGING_ELAPSED_PERCENT = (($STAGING_ELAPSED * 100) + ($INGEST_TOTAL/2)) / $INGEST_TOTAL"
let "SMOOKS_ELAPSED_PERCENT = (($SMOOKS_ELAPSED * 100) + ($INGEST_TOTAL/2)) / $INGEST_TOTAL"
let "PERSIST_ELAPSED_PERCENT = (($PERSIST_ELAPSED * 100) + ($INGEST_TOTAL/2)) / $INGEST_TOTAL"
let "REMOVE_ELAPSED_PERCENT = (($REMOVE_ELAPSED * 100) + ($INGEST_TOTAL/2)) / $INGEST_TOTAL"

echo
printf "%-12s %8s %8s %8s %15s %10s\n" "Stage" "Start" "Finish" "Elapsed" "Elapsed (min)" "Percent"
printf "%-12s %8s %8s %8s %15s %10s\n" "-----" "-----" "------" "-------" "-------------" "-------"
printf "%-12s %8d %8d %8d %15d %9d%%\n" "Extract" 0 $EXTRACT_ZIP_ELAPSED $EXTRACT_ZIP_ELAPSED $EXTRACT_ZIP_ELAPSED_MIN $EXTRACT_ZIP_ELAPSED_PERCENT
if [ $DBCREATE_START -gt 0 ] ; then
printf "%-12s %8d %8d %8d %15d %9d%%\n" "DB Create" $DBCREATE_START $DBCREATE_FINISH $DBCREATE_ELAPSED_MIN $DBCREATE_ELAPSED_MIN $DBCREATE_ELAPSED_PERCENT
fi
printf "%-12s %8d %8d %8d %15d %9d%%\n" "IdRef" $IDREF_MIN_START $IDREF_MAX_FINISH $IDREF_ELAPSED $IDREF_ELAPSED_MIN $IDREF_ELAPSED_PERCENT
printf "%-12s %8d %8d %8d %15d %9d%%\n" "SmooksCall" $SMOOKS_MIN_START $SMOOKS_MAX_FINISH $SMOOKS_ELAPSED $SMOOKS_ELAPSED_MIN $SMOOKS_ELAPSED_PERCENT
printf "  %-10s %8d %8d %8d %15d %9d%%\n" "Staging" $STAGING_MIN_START $STAGING_MAX_FINISH $STAGING_ELAPSED $STAGING_ELAPSED_MIN $STAGING_ELAPSED_PERCENT
printf "%-12s %8d %8d %8d %15d %9d%%\n" "Persistence" $PERSIST_MIN_START $PERSIST_MAX_FINISH $PERSIST_ELAPSED $PERSIST_ELAPSED_MIN $PERSIST_ELAPSED_PERCENT
printf "%-12s %8d %8d %8d %15d %9d%%\n" "Remove" $REMOVE_START $REMOVE_FINISH $REMOVE_ELAPSED $REMOVE_ELAPSED_MIN $REMOVE_ELAPSED_PERCENT

cat out.tmp
rm out.tmp
