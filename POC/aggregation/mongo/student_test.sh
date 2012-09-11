#!/bin/bash 

TEST_DATA_DIR=/home/stephan/dev/sli/sli/acceptance-tests/test/data/aggregationData/DataSet1/json/

python load_dir.py sli $TEST_DATA_DIR
python assessment_calculated_highest_student.py $1 student
