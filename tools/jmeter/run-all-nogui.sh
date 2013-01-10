#!/bin/sh
# Run all the tests in this directory once locally without starting the jmeter gui.
for f in *.jmx; do
  jmeter -n -q local.properties -t $f
done
