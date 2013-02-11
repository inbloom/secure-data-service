#!/bin/bash

cd $WORKSPACE
branchname=`git rev-parse --symbolic-full-name --abbrev-ref HEAD`
head_commit=`git rev-parse HEAD`

# Put git in a headless state
# This is useful so that the cleaned configs can 
# be commited, then the new commit can be exported, 
# and finally the orginal branch can checked out, 
# which will let the temp commit get lost and eventually
# garbage collected
git checkout $head_commit

echo "Cleaning configs"
cd $WORKSPACE/tools
ruby clean-config.rb

# create temp commit for exporting purposes
git commit -a -m "Commit for cleaned configs"

echo "Exporting source code"
cd $WORKSPACE
git archive HEAD | gzip > sliCodebase.tar.gz

# now get rid of temp commit
git checkout $branchname
