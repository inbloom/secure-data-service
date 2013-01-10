POC for using the MongoDB aggregation framework.

This requires a recent version of Python to be installed. 

To get ready to run the aggregation scripts run the following commands from this directory:


./scripts/setuppy.sh             # setup a virtualenv with all dependencies 
source py-local/bin/activate     # activate the python interpreter 

After executing the two lines above you are able to run the aggregation jobs against
a mongodb with the appropriate data. 

The input to both scripts is the id of the assessment of interest. They have to be run in 
the order mentioned below because aggregating over schools requires that the highest score be
stored in mongodb. 

Commands to run the scripts use these commands: 

python assessment_calculated_highest_student.py <hostname> {<assessment id>}
python assessment_aggregated_highest_school.py  <hostname> {<assessment id>}

If the <assessment id> is ommitted the scripts will list all available assessment ids. 
<hostname> specifies the mongodb host to connect to. 




