#!/bin/bash

NODES="nxmongo10 nxmongo11 nxmongo12 nxmongo13 nxmongo14 nxmongo15 nxmongo16 nxmongo17 nxmongo18 nxmongo19 nxmongo20 nxmongo21"
for i in $NODES; do 
    mongodump --host $i.slidev.org --db sli --collection system.profile -o $i-dump

done

zip mongodumps.zip *-dump
