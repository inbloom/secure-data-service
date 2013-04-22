############################################################
# Environment Variables Set Up For Bulk Extract Tests
############################################################

CLEAN_EXTRACT_LOC = true
TRIGGER_NEW_EXTRACT = true

############################################################
# Bulk Extract Scheduler
############################################################
desc "Test the Bulk Extract Scheduler"
task :bulkExtractSchedulerTest do
  runTests("test/features/bulk_extract/features/bulk_extract_scheduler.feature")
end


############################################################
# Bulk Extract
############################################################
desc "Trigger ingestion and extract of the ingestion"
task :bulkExtractCleanup do
  runTests("test/features/bulk_extract/features/bulk_extract_cleanup.feature")
end

desc "Trigger ingestion and extract of the ingestion"
task :bulkExtractSetup do
  Rake::Task["bulkExtractCleanup"].execute
  Rake::Task["ingestionSmokeTests"].execute 
  @tags = ["~@wip", "~@sandbox"]

end

desc "Trigger ingestion and extract of the ingestion"
task :bulkExtractSmokeTests do
  Rake::Task["bulkExtractCleanup"].execute if CLEAN_EXTRACT_LOC      
  Rake::Task["bulkExtractTriggerTest"].execute
  Rake::Task["bulkExtractStudentTest"].execute  
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

desc "Trigger ingestion and extract of the ingestion"
task :bulkExtractSimpleEntitiesTest do
  Rake::Task["bulkExtractTriggerTest"].execute if TRIGGER_NEW_EXTRACT
  runTests("test/features/bulk_extract/features/bulk_extract_simple_entities.feature")
  Rake::Task["bulkExtractCleanup"].execute if CLEAN_EXTRACT_LOC
end

desc "Trigger ingestion and extract of the ingestion"
task :bulkExtractEdorgStaffTest do
  Rake::Task["bulkExtractTriggerTest"].execute if TRIGGER_NEW_EXTRACT
  runTests("test/features/bulk_extract/features/bulk_extract_edorg_staff.feature")
  Rake::Task["bulkExtractCleanup"].execute if CLEAN_EXTRACT_LOC
end

desc "Trigger ingestion and extract of the ingestion"
task :bulkExtractSuperdocTest do
  Rake::Task["bulkExtractTriggerTest"].execute if TRIGGER_NEW_EXTRACT
  runTests("test/features/bulk_extract/features/bulk_extract_superdoc.feature")
  Rake::Task["bulkExtractCleanup"].execute if CLEAN_EXTRACT_LOC
end

desc "Trigger ingestion and extract of the ingestion"
task :bulkExtractIntegrationTest do
  #Rake::Task["bulkExtractSetup"].execute if TRIGGER_NEW_EXTRACT
  runTests("test/features/bulk_extract/features/bulk_extract_integration.feature")
  Rake::Task["bulkExtractCleanup"].execute if CLEAN_EXTRACT_LOC
end

desc "Deltas and Deletes"
task :bulkExtractDeltasTest do
  runTests("test/features/bulk_extract/features/bulk_extract_deltas_api.feature")
  runTests("test/features/bulk_extract/features/delta_recording.feature")
  Rake::Task["bulkExtractCleanup"].execute if CLEAN_EXTRACT_LOC
end

desc "Negative and Edge Cases"
task :bulkExtractNegativeTests do
  runTests("test/features/bulk_extract/features/bulk_extract_neg_and_edge.feature")
  Rake::Task["bulkExtractCleanup"].execute if CLEAN_EXTRACT_LOC
end


desc "Run RC E2E Tests in Production mode"
task :bulkExtractTests => [:realmInit] do
  CLEAN_EXTRACT_LOC = false
  TRIGGER_NEW_EXTRACT = false

  Rake::Task["bulkExtractSetup"].execute
  Rake::Task["addBootstrapAppAuths"].execute
  allLeaAllowApp("SDK Sample")  
  authorizeEdorg("SDK Sample")
  Rake::Task["bulkExtractTriggerTest"].execute
  Rake::Task["bulkExtractSimpleEntitiesTest"].execute
  Rake::Task["bulkExtractSuperdocTest"].execute  
  Rake::Task["bulkExtractEdorgStaffTest"].execute
  Rake::Task["bulkExtractIntegrationTest"].execute
  Rake::Task["bulkExtractDeltasTest"].execute
  Rake::Task["bulkExtractSchedulerTest"].execute
  Rake::Task["bulkExtractNegativeTests"].execute
  Rake::Task["bulkExtractCleanup"].execute 
  displayFailureReport()
  if $SUCCESS
    puts "Completed All Tests"
  else
    raise "Tests have failed"
  end
end
