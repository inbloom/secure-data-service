#!/bin/bash
#
# This script parses an ingestion log
#
#set -x

if [ $# -ne 2 ];
then
  echo "Usage: parse_ingestion_log FILE_NAME NUMBER_OF_SERVERS"
  exit 1
fi

FILE=$1
NUMBER_OF_SERVERS=$2

###################

function SecondsFromLine {
  if [ -z "$1" ] ; then
    echo 0
  else
    DATE=`echo $1 | sed -e 's/^\([^\.]*\)\..*$/\1/'`
    if [ -f /mach_kernel ] ; then
      ABS=`date -j -f "%d %b %Y %H:%M:%S" "$DATE" +%s`
    else
      ABS=`date -d "$DATE" +%s`
    fi
    let "REL = $ABS - $INGEST_START"
    echo $REL
  fi
}

# SecondsFromGrep searchString num [tenantNum]
function SecondsFromGrep {
  if [ -n "$3" ] ; then
    LINE=`grep ${TENANTS[$3]} $FILE | grep -m $2 "$1" | tail -n 1`
  else
    LINE=`grep -m $2 "$1" $FILE | tail -n 1`
  fi
  echo `SecondsFromLine "$LINE"`
}

# SecondsFromGrep searchString [tenantNum]
function SecondsFromLastGrep {
  if [ -n "$2" ] ; then
    LINE=`grep ${TENANTS[$2]} $FILE | grep "$1" | tail -n 1`
  else
    LINE=`grep "$1" $FILE | tail -n 1`
  fi
  echo `SecondsFromLine "$LINE"`
}

###################

INTERCHANGES="InterchangeAssessmentMetadata.xml InterchangeEducationOrganization.xml InterchangeEducationOrgCalendar.xml InterchangeMasterSchedule.xml InterchangeStaffAssociation.xml InterchangeStudentCohort.xml InterchangeStudentEnrollment.xml InterchangeStudentParent.xml InterchangeStudentAssessment.xml"

ENTITIES="learningStandard assessmentFamily assessmentPeriodDescriptor assessment assessmentItem courseOffering course localEducationAgency school stateEducationAgency studentAssessment teacher studentSchoolAssociation cohort studentCohortAssociation staffCohortAssociation session gradingPeriod student calendarDate program section teacherSchoolAssociation studentAssessmentItem studentSectionAssociation teacherSectionAssociation staff staffEducationOrganizationAssociation staffProgramAssociation parent graduationPlan studentParentAssociation"

TENANTS[1]="Hyrule"
TENANTS[2]="Midgar"
TENANTS[3]="Tenant_1"
TENANTS[4]="Tenant_2"
TENANTS[5]="Tenant_3"
TENANTS[6]="Tenant_4"
TENANTS[7]="Tenant_5"

#
# Number of ingestions
#
NUM_INGESTIONS=`grep "Landing Zone message detected" $FILE | wc -l`
INGEST_START=0
INGEST_0_START=`SecondsFromGrep "Landing Zone message detected" 1`
if [ $NUM_INGESTIONS -gt 1 ] ; then
  echo "Parsing $FILE, $NUM_INGESTIONS ingestions, $NUMBER_OF_SERVERS servers"
fi

for (( INGESTION_NUM=1; $INGESTION_NUM<=$NUM_INGESTIONS; INGESTION_NUM++ )) ; do
  rm -f out.tmp

  #
  # Start/finish
  #
  # We get one of these lines per server
  let "INGESTION_NUM_X = $INGESTION_NUM * $NUMBER_OF_SERVERS"
  INGEST_START=0
  INGEST_START=`SecondsFromGrep "Landing Zone message detected" $INGESTION_NUM` 
  INGEST_TOTAL=`SecondsFromGrep "Clearing cache at job completion" $INGESTION_NUM_X` 
  if [ $INGEST_TOTAL -eq 0 ] ; then
    echo "ERROR: $INGEST_TOTAL is 0, ingestion probably did not fully complete"
    exit
  fi
  let "MINUTES = $INGEST_TOTAL / 60"
  let "REM_SECONDS = $INGEST_TOTAL - $MINUTES * 60"
  let "HOURS = $MINUTES / 60"
  let "REM_MINUTES = $MINUTES - $HOURS * 60"
  let "START_DIFF = $INGEST_START - $INGEST_0_START"
  let "START_DIFF_MINUTES = $START_DIFF / 60"
  if [ $NUM_INGESTIONS -gt 1 ] ; then
    echo
    echo "==============================================================================================="
    echo "Ingestion #$INGESTION_NUM: Started at $START_DIFF (${START_DIFF_MINUTES}m), $INGEST_TOTAL seconds (${HOURS}h, ${REM_MINUTES}m, ${REM_SECONDS}s)"
  else
    echo "Parsing $FILE, $NUMBER_OF_SERVERS servers: $INGEST_TOTAL seconds (${HOURS}h, ${REM_MINUTES}m, ${REM_SECONDS}s)"
  fi

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
    START=`SecondsFromGrep "Persisted [0-9]* records of type $ENTITY *$" 1 $INGESTION_NUM`
    if [ $START -gt 0 ] ; then
      FINISH=`SecondsFromLastGrep "Persisted [0-9]* records of type $ENTITY *$" $INGESTION_NUM`
      let "ELAPSED = $FINISH - $START"
      let "STAGING_MIN_START = $START < $STAGING_MIN_START ? $START : $STAGING_MIN_START"
      let "STAGING_MAX_FINISH = $FINISH > $STAGING_MAX_FINISH ? $FINISH : $STAGING_MAX_FINISH"
      echo "0" > sed.tmp
      grep ${TENANTS[$INGESTION_NUM]} $FILE | grep "Persisted [0-9]* records of type $ENTITY *$" | sed -e "s/^.*Persisted \([0-9]*\) records of type $ENTITY *$/\1 +/" >> sed.tmp
      echo "p" >> sed.tmp
      NUM=`dc < sed.tmp`
      let "STAGING_TOTAL = $STAGING_TOTAL + $NUM"
      rm sed.tmp
      printf "%-40s %8d %8d %8d %10d\n" $ENTITY $START $FINISH $ELAPSED $NUM >> parse.tmp
    fi
  done
  sort -k 2 -n parse.tmp >> out.tmp
  rm parse.tmp

  let "STAGING_ELAPSED = $STAGING_MAX_FINISH - $STAGING_MIN_START"
  printf "%-40s %8s %8s %8s %10d\n" "TOTAL" $STAGING_MIN_START $STAGING_MAX_FINISH $STAGING_ELAPSED $STAGING_TOTAL >> out.tmp

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
    START=`SecondsFromGrep "Bulk insert of [0-9]* queued records into collection: $ENTITY$" 1 $INGESTION_NUM`
    if [ $START -gt 0 ] ; then
      FINISH=`SecondsFromLastGrep "Bulk insert of [0-9]* queued records into collection: $ENTITY$" $INGESTION_NUM`
      let "ELAPSED = $FINISH - $START"
      let "PERSIST_MIN_START = $START < $PERSIST_MIN_START ? $START : $PERSIST_MIN_START"
      let "PERSIST_MAX_FINISH = $FINISH > $PERSIST_MAX_FINISH ? $FINISH : $PERSIST_MAX_FINISH"
      echo "0" > sed.tmp
      grep ${TENANTS[$INGESTION_NUM]} $FILE | grep "Bulk insert of [0-9]* queued records into collection: $ENTITY$" | sed -e "s/^.*Bulk insert of \([0-9]*\) queued records into collection: $ENTITY$/\1 +/" >> sed.tmp
      echo "p" >> sed.tmp
      NUM=`dc < sed.tmp`
      let "PERSIST_TOTAL = $PERSIST_TOTAL + $NUM"
      rm sed.tmp
      printf "%-40s %8d %8d %8d %10d\n" $ENTITY $START $FINISH $ELAPSED $NUM >> parse.tmp
    fi
  done
  sort -k 2 -n parse.tmp >> out.tmp
  rm parse.tmp

  let "PERSIST_ELAPSED = $PERSIST_MAX_FINISH - $PERSIST_MIN_START"
  printf "%-40s %8d %8d %8d %10d\n" "TOTAL" $PERSIST_MIN_START $PERSIST_MAX_FINISH $PERSIST_ELAPSED $PERSIST_TOTAL >> out.tmp

  #
  # Stages
  #
  let "OTHER_ELAPSED = $INGEST_TOTAL - $STAGING_ELAPSED - $PERSIST_ELAPSED"

  #
  # Summary
  #
  let "STAGING_ELAPSED_MIN = ($STAGING_ELAPSED + 30) / 60"
  let "PERSIST_ELAPSED_MIN = ($PERSIST_ELAPSED + 30) / 60"
  let "OTHER_ELAPSED_MIN = ($OTHER_ELAPSED + 30) / 60"

  let "STAGING_ELAPSED_PERCENT = (($STAGING_ELAPSED * 100) + ($INGEST_TOTAL/2)) / $INGEST_TOTAL"
  let "PERSIST_ELAPSED_PERCENT = (($PERSIST_ELAPSED * 100) + ($INGEST_TOTAL/2)) / $INGEST_TOTAL"
  let "OTHER_ELAPSED_PERCENT = (($OTHER_ELAPSED * 100) + ($INGEST_TOTAL/2)) / $INGEST_TOTAL"

  echo
  printf "%-15s %8s %8s %8s %15s %10s\n" "Stage" "Start" "Finish" "Elapsed" "Elapsed (min)" "Percent"
  printf "%-15s %8s %8s %8s %15s %10s\n" "-----" "-----" "------" "-------" "-------------" "-------"
  printf "%-15s %8d %8d %8d %15d %9d%%\n" "Staging" $STAGING_MIN_START $STAGING_MAX_FINISH $STAGING_ELAPSED $STAGING_ELAPSED_MIN $STAGING_ELAPSED_PERCENT
  printf "%-15s %8d %8d %8d %15d %9d%%\n" "Persistence" $PERSIST_MIN_START $PERSIST_MAX_FINISH $PERSIST_ELAPSED $PERSIST_ELAPSED_MIN $PERSIST_ELAPSED_PERCENT
  printf "%-15s %8s %8s %8d %15d %9d%%\n" "(other)" " " " " $OTHER_ELAPSED $OTHER_ELAPSED_MIN $OTHER_ELAPSED_PERCENT

  cat out.tmp
  rm out.tmp

done
