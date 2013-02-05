#!/bin/bash
#
# Copyright 2012-2013 inBloom, Inc. and its affiliates.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

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

