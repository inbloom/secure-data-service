#!/bin/bash

# Keeps a token alive on the given server by issuing a GET on 
# /api/rest/v1/system/session/check
# 
# Usage: sessionChecker.bash token url
#
# Example usage: 
#   sessionChecker.bash 4cf7a5d4-37a1-ca19-8b13-b5f95131ac85  http://local.slidev.org:8080

if [ $# -ne 2 ] 
then
    echo Usage: sessionChecker.bash token url
    exit 1
fi

header="Authorization: bearer $1"
url="$2/api/rest/v1/system/session/check"

command="curl --header '$header' -X GET $url"

eval $command

exit 0
