#!/bin/sh
if [ -z "$1" ]; then
    echo "Branch defaulted to master"
    branch="master"
else
    branch=$1
fi
set -e
sh checkoutAndBuild.sh $branch
cd /opt/megatron/sli/sli/acceptance-tests/teamFiles/
sh ingestDataset.sh 1
sh ingestDataset.sh 2
sh resetEnvironment.sh
sh ingestDataset.sh 1
sh ingestDataset.sh 2
echo "Done!"

