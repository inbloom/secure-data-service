#!/bin/bash

############### Load Utils #########################
PRG="$0"
ROOT=`dirname "$PRG"`
source "$ROOT/utils.sh"

############### Static Data ########################

declare -A deployHash

deployHash=( [api]="$WORKSPACE/sli/api/target/api.war" 
             [dashboard]="$WORKSPACE/sli/dashbaord/target/dashboard.war" 
             [simple-idp]="$WORKSPACE/sli/simple-idp/target/simple-idp.war"
             [sample]="$WORKSPACE/sli/SDK/sample/target/sample.war"
             [ingestion-service]="$WORKSPACE/sli/ingestion/ingestion-service/target/ingestion-service.war"
  )

############### Process Inputs #####################

WHICHTEST=$1
shift
APPSTODEPLOY=$@

############## Run Tests ###########################

source "$ROOT/apiTests.sh"


####################################################
