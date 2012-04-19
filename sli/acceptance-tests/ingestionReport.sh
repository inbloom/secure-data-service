#!/bin/bash


testRunId=`date +%d%m%Y-%H%M%S`
testResultDir="ATResult$testRunId"

mkdir $testResultDir || echo "Cannot create TestResultDir. Already Exists. $!"
echo "Test Results Will Be Saved In [[./$testResultDir]]"

report="$testResultDir/AllTestsReport.txt"
echo $testRunId >> $report
landingZone='../ingestion/target/lz/inbound'
rakeCommand="bundle exec rake ingestion_landing_zone=$landingZone"

for testItem in ingestionAPAssessmentTests ingestionAcceptanceSdsTest ingestionAssessmentTests ingestionAttendanceTests ingestionBatchJobTest ingestionDemoDataTest ingestionEncryptionTests ingestionGradebookEntryTests ingestionIDReferenceResolutionTest ingestionNegativeTests ingestionOfflineSimpleTest ingestionOfflineToolTests ingestionPerformanceTest ingestionProgramTest ingestionSessionTest ingestionSmooksVerificationTests ingestionStaffProgramAssociationTest ingestionStudentParentsTests ingestionStudentProgramAssociationTest ingestionStudentTranscriptAssociationTests ingestionXsdValidationTest ingestionidNamespaceTest
do
	echo "    >Running $testItem"
	echo "    >Running $testItem" >> $report
	command="$rakeCommand $testItem"
	resultFile="$testResultDir/$testItem.txt"
	startTime=`date +%s`
	`$rakeCommand $testItem 2&>$resultFile`
	endTime=`date +%s`
	testTime=$(($endTime-$startTime));
	testTime=$(($testTime));
	echo "        >Finished $testItem Took $testTime seconds."
	echo "        >Finished $testItem Took $testTime seconds." >> $report
	failCount=`grep 'Failing S' $resultFile|wc -l|tr -d ' '`
	if [[ $failCount -gt 0 ]] 
	then
	    lineNo=`grep -n 'Failing S' $resultFile |sed 's/^\([0-9]*\).*/\1/'`
	    lines=`wc -l $resultFile|sed -n 's/^[^0-9]*\([0-9]*\) .*/\1/p'`
	    toPrint=$(($lines-$lineNo))
	    toPrint=$(($toPrint+1))
	    tail -$toPrint $resultFile |sed 's/^/            /'
	    tail -$toPrint $resultFile |sed 's/^/            /' >> $report

            echo "    >****         "
            echo "    >Error Report $report"
            echo "    >****         "
	    exit 0
	fi
done
