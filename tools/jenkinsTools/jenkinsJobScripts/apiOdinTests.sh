#!/bin/bash

startSearchIndexer

processApps $APPSTODEPLOY

# Generate and Ingest Odin data
bundle exec rake FORCE_COLOR=true api_server_url=https://$NODE_NAME.slidev.org apiSuperAssessmentTests TOGGLE_TABLESCANS=true

EXITCODE=$?

mongo --eval "db.adminCommand( { setParameter: 1, notablescan: false } )"

exit $EXITCODE





