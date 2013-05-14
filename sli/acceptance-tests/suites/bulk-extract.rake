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
  runTests("test/features/bulk_extract/features/bulk_extract_ingestion.feature")
  Rake::Task["realmInit"].execute
  Rake::Task["appInit"].execute
  allLeaAllowApp("SDK Sample")  
  authorizeEdorg("SDK Sample")
  allLeaAllowApp("Paved Z00")
  authorizeEdorg("Paved Z00")
  runTests("test/features/bulk_extract/features/bulk_extract_deltas_api.feature")
  runTests("test/features/bulk_extract/features/delta_recording.feature")
  Rake::Task["bulkExtractCleanup"].execute if CLEAN_EXTRACT_LOC
end

desc "Extract SEA only public data"
task :bulkExtractSEAPublicTest do
  runTests("test/features/bulk_extract/features/bulk_extract_sea_ingest.feature")
  allLeaAllowApp("SDK Sample")
  authorizeEdorg("SDK Sample")
  runTests("test/features/bulk_extract/features/bulk_extract_sea_public.feature")
  Rake::Task["bulkExtractCleanup"].execute if CLEAN_EXTRACT_LOC
end

desc "Negative and Edge Cases"
task :bulkExtractNegativeTests do
  runTests("test/features/bulk_extract/features/bulk_extract_neg_and_edge.feature")
  Rake::Task["bulkExtractCleanup"].execute if CLEAN_EXTRACT_LOC
end

desc "Client Cert Auth Bulk Extract Tests"
task :bulkExtractTlsTests do
  runTests("test/features/bulk_extract/features/bulk_extract_tls.feature")
  Rake::Task["bulkExtractCleanup"].execute if CLEAN_EXTRACT_LOC
end

desc "Cleanup script Tests"
task :bulkExtractCleanupTests do
  runTests("test/features/bulk_extract/features/bulk_extract_cleanup_script.feature")
end

desc "API Bulk Extract Tests"
task :bulkExtractApiTests do
  runTests("test/features/bulk_extract/features/bulk_extract_headers.feature")
  runTests("test/features/bulk_extract/features/bulk_extract_partial_gets.feature")
  runTests("test/features/bulk_extract/features/bulk_extract_versions.feature")
  runTests("test/features/bulk_extract/features/bulk_extract_lea_list.feature")
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
  Rake::Task["bulkExtractApiTests"].execute
  Rake::Task["bulkExtractDeltasTest"].execute
  Rake::Task["bulkExtractSchedulerTest"].execute
  Rake::Task["bulkExtractNegativeTests"].execute
  Rake::Task["bulkExtractTlsTests"].execute
  Rake::Task["bulkExtractSEAPublicTest"].execute
  Rake::Task["bulkExtractCleanup"].execute
  displayFailureReport()
  if $SUCCESS
    puts "Completed All Tests"
  else
    raise "Tests have failed"
  end
end
