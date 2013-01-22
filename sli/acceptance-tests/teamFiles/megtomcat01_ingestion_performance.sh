#!/bin/sh
set -e
sh checkoutAndBuild.sh $1
cd /opt/megatron/sli/sli/acceptance-tests/teamFiles/
sh ingestDataset.sh 1
sh ingestDataset.sh 2
sh resetEnvironment.sh
sh ingestDataset.sh 1
sh ingestDataset.sh 2

