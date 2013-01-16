#!/bin/bash

PRG="$0"
ROOT=`dirname "$PRG"`
source "$ROOT/utils.sh"

WHICHTEST=$1
echo $WHICHTEST
shift
APPSTODEPLOY=$@
echo $APPSTODEPLOY