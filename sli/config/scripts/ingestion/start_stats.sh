#!/bin/bash
#
# This script stats various statistics gathering things
#
#set -x

mongostat 1 > /tmp/mongostat &
iostat -c -t 1 > /tmp/iostat_cpu &
iostat -d -k -t -p xvdr 1 > /tmp/iostat_io &
vmstat -t 1 > /tmp/vmstat &
