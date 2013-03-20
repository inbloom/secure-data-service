############################################################
# Environment Variables Set Up For Bulk Extract Tests
############################################################

CLEAN_EXTRACT_LOC = true
TRIGGER_NEW_EXTRACT = true


############################################################
# Bulk Extract
############################################################
desc "Trigger ingestion and extract of the ingestion"
task :bulkExtractCleanup do
  runTests("test/features/bulk_extract/features/bulk_extract_cleanup.feature")
end

desc "Trigger ingestion and extract of the ingestion"
task :bulkExtractSetup do
  Rake::Task["ingestionSmallSampleDataSet"].execute
end

desc "Trigger ingestion and extract of the ingestion"
task :bulkExtractTriggerTest do
  runTests("test/features/bulk_extract/features/bulk_extract.feature")
end

desc "Trigger ingestion and extract of the ingestion"
task :bulkExtractStudentTest do
  Rake::Task["bulkExtractTriggerTest"].execute if TRIGGER_NEW_EXTRACT
  runTests("test/features/bulk_extract/features/bulk_extract_student.feature")
  Rake::Task["bulkExtractCleanup"].execute if CLEAN_EXTRACT_LOC
end

desc "Run RC E2E Tests in Production mode"
task :bulkExtractTests do
  CLEAN_EXTRACT_LOC = false
  TRIGGER_NEW_EXTRACT = false

  Rake::Task["bulkExtractSetup"].execute
  Rake::Task["bulkExtractTriggerTest"].execute
  Rake::Task["bulkExtractStudentTest"].execute
  Rake::Task["bulkExtractCleanup"].execute 
  displayFailureReport()
  if $SUCCESS
    puts "Completed All Tests"
  else
    raise "Tests have failed"
  end
end