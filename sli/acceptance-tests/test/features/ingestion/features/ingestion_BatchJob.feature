
Feature: Batch Job Status Test

Background: I have a landing zone route configured
Given I am using local data store
    And I am using preconfigured Ingestion Landing Zone

Scenario: Post a zip file containing all configured interchanges as a payload of the ingestion job: Clean Database
Given I post "DemoData.zip" file as the payload of the ingestion job
	 And the following collections are empty in datastore:
	  | collectionName              |
 	  | batchJob                    |
 	  | batchJobError			    |


############################################################
#This is for mock data, in future this could be removed.
############################################################
Given I am loading json file "ingestion_batchJob_fixture.json" to mongodb
 Then I should see following map of entry counts in the corresponding collections:
    | collectionName              | count |
    | batchJob		   	   		  |  1    |



When zip file is scp to ingestion landing zone
  And "3" seconds have elapsed, the batchJob table is populated, the job is running with error
 # Then I should see following map of entry counts in the corresponding collections:
 #   | collectionName              | count |
 #   | batchJob		   	   		   |  1    |
 #   | batchJobError		       |  1    |
And I check to find if record is in collection:
    | collectionName       | expectedRecordCount | searchParameter   		 | searchValue                  |searchType           |
    | batchJob 			   | 1                   | batchJobId        	     | job-1238123-125912-3123      |string               |
	| batchJob  		   | 1                   | status	         		 | running  			        |string               |
	| batchJob  		   | 1                   | sourceId                  | nyc1_secure_landingzone      |string               |
	| batchJob  		   | 1                   | totalFiles                | 2     						|integer              |
	| batchJob  	       | 1                   | stages.stageName          | transformation               |string               |
	| batchJob  	       | 1                   | stages.startTimestamp     | 2012-03-12 14:21:10          |string               |
	| batchJob  	       | 1                   | stages.stopTimeStamp      | 2012-03-12 14:32:42          |string               |
	#file 1 info
	| batchJob  		   | 1                   | fileEntries.0.fileName    | students.xml                 |string               |
	| batchJob  		   | 1                   | fileEntries.recordCount   | 100                          |integer			  |
	| batchJob  	       | 1                   | fileEntries.0.errorCount  | 2                            |integer              |
	#file 2 info
	| batchJob  		   | 1                   | fileEntries.1.fileName    | schools.xml                  |string               |
	| batchJob  		   | 1                   | fileEntries.recordCount   | 100                          |integer			  |
	| batchJob  	       | 1                   | fileEntries.1.errorCount  | 2                            |integer              |
	| batchJob  	       | 1                   | stages.stageName          | transformation               |string               |
	#batch job error info
#	| batchJobError  	   | 1                   | errorId              | error-123-412-123-12341-1    |string               |
#	| batchJobError  	   | 1                   | batchJobId           | job-1238123-125912-3123      |string               |
#	| batchJobError  	   | 1                   | stageName            | transformation               |string               |
#	| batchJobError  	   | 1                   | fileId               | ed-fi-124-123-1455123-51     |string               |
#	| batchJobError  	   | 1                   | sourceIp             | 192.168.23.999               |string               |
#	| batchJobError  	   | 1                   | hostname             | deving1.slidev.org           |string               |
#	| batchJobError  	   | 1                   | recordIdentifier     | 234                          |string               |
#	| batchJobError  	   | 1                   | timestamp            | 2012-03-12 14:21:04          |string               |
#	| batchJobError  	   | 1                   | severity             | fatal                        |string               |
#	| batchJobError  	   | 1                   | errorType            | file level validation        |string               |
#	| batchJobError  	   | 1                   | errorDetail          | missing required field 'StudentUniqueStateId'   |string         |


#Scenario: Post a zip file containing all configured interchanges as a payload of the ingestion job: Clean Database
#Given I post "DemoData.zip" file as the payload of the ingestion job
 # And the following collections are empty in datastore:
 #   | collectionName              |
 #   | batchJob                    |
 #   | batchJobError			   |
#When zip file is scp to ingestion landing zone
#  And "10" seconds have elapsed, the batchJob table is populated, the job is running successfully
#  Then I should see following map of entry counts in the corresponding collections:
#     | collectionName              | count |
#     | batchJob		   	   		|  1    |
#	  | batchJobError		        |  0    |

#   | collectionName       | expectedRecordCount | searchParameter   		 | searchValue                  |searchType           |
#   | batchJob 			   | 1                   | batchJobId        	     | job-1238123-125912-3123      |string               |
#	| batchJob  		   | 1                   | status	         		 | completed  			        |string               |
#	| batchJob  		   | 1                   | sourceId                  | nyc1_secure_landingzone      |string               |
#	| batchJob  		   | 1                   | totalFiles                | 2     						|integer              |
#	| batchJob  	       | 1                   | stages.stageName          | transformation               |string               |
#	| batchJob  	       | 1                   | stages.startTimestamp     | 2012-03-12 14:21:10          |string               |
#	| batchJob  	       | 1                   | stages.stopTimeStamp      | 2012-03-12 14:32:42          |string               |
#	#file 1 info
#	| batchJob  		   | 1                   | fileEntries.0.fileName    | students.xml                 |string               |
#	| batchJob  		   | 1                   | fileEntries.recordCount   | 102                          |integer			  |
#	| batchJob  	       | 1                   | fileEntries.0.errorCount  | 0                            |integer              |
#	#file 2 info
#	| batchJob  		   | 1                   | fileEntries.1.fileName      | schools.xml                |string               |
#	| batchJob  		   | 1                   | fileEntries.recordCount   | 102                          |integer			  |
#	| batchJob  	       | 1                   | fileEntries.1.errorCount  | 0                            |integer              |
#	| batchJob  	       | 1                   | stages.stageName          | transformation               |string               |





















