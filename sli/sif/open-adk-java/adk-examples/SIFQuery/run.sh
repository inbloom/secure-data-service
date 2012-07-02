#!/bin/sh
#mvn exec:java -Dexec.args='/zone zistest /url http://localhost:7080/zistest /sourceId zistest /ver 2.4 /pull'
mvn exec:java -Dexec.args="$@"

