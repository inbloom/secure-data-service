# IMPORTANT NOTE: mongoimport must be in your PATH environment variable.

# 1. Get database name from argument list.  If none is provided,
# then the default is 'StagingDB'
import sys
import os

if (len(sys.argv) == 2):
    dbName = sys.argv[1]
elif (len(sys.argv) > 2):
	print "Error.  Only one command line argument allowed."
	exit(1);
else:
    dbName = 'StagingDB'

# ID all .csv files in the directory and put them in and array
# print dbName
os.chdir(".")
for files in os.listdir("."):
    if files.endswith(".csv"):
#		print files
# The Collection name will be the .csv filename with the .csv removed
		collectionName = os.path.splitext(files)[0]
#		print collectionName
# Call mongoimport
		sysCall = "mongoimport -d "+dbName+" -c "+collectionName+" --type csv --headerline < "+files
		print sysCall
		os.system(sysCall)