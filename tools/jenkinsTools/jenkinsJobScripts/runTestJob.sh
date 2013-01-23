#!/bin/bash

############### Load Utils #########################
PRG="$0"
ROOT=`dirname "$PRG"`
source "$ROOT/utils.sh"

############### Static Data ########################

declare -A deployHash

deployHash=( [api]="$WORKSPACE/sli/api/target/api.war" 
             [dashboard]="$WORKSPACE/sli/dashboard/target/dashboard.war" 
             [simple-idp]="$WORKSPACE/sli/simple-idp/target/simple-idp.war"
             [sample]="$WORKSPACE/sli/SDK/sample/target/sample.war"
             [ingestion-service]="$WORKSPACE/sli/ingestion/ingestion-service/target/ingestion-service.war"
             [mock-zis]="$WORKSPACE/sli/sif/mock-zis/target/mock-zis.war"
             [sif-agent]="$WORKSPACE/sli/sif/sif-agent/target/sif-agent.war"
  )

############### Process Inputs #####################

WHICHTEST=$1
GITCOMMIT=$2
shift
shift
APPSTODEPLOY=$@

############## Run Tests ###########################

source "$ROOT/${WHICHTEST}Tests.sh"


####################################################
