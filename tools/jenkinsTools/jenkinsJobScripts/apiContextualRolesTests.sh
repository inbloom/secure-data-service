#!/bin/bash

# Test multiple contextual roles capabilities.
bundle exec rake FORCE_COLOR=true app_bootstrap_server=ci api_server_url=https://$NODE_NAME.slidev.org api_server_url=https://$NODE_NAME.slidev.org api_ssl_server_url=https://$NODE_NAME.slidev.org:8443 apiContextualRolesTests TOGGLE_TABLESCANS=true

EXITCODE=$?

mongo --eval "db.adminCommand( { setParameter: 1, notablescan: false } )"

exit $EXITCODE





