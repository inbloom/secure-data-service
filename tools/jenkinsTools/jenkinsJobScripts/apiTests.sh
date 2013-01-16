#!/bin/bash

PRG="$0"
ROOT=`dirname "$PRG"`
source "$ROOT/utils.sh"

mongo --eval "db.adminCommand( { setParameter: 1, notablescan: false } )"
/usr/sbin/cleanup_tomcat

resetDatabases

profileSwapAndPropGen

startSearchIndexer

cd $WORKSPACE/sli/acceptance-tests
export LANG=en_US.UTF-8
bundle install --deployment
bundle exec rake FORCE_COLOR=true api_server_url=https://$NODE_NAME.slidev.org apiAndSecurityTests TOGGLE_TABLESCANS=true

mongo --eval "db.adminCommand( { setParameter: 1, notablescan: false } )"


