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
  runTests("test/features/bulk_extract/features/bulk_extract_ingestion.feature")
  puts "DEBUG: running realmInit"
  Rake::Task["realmInit"].invoke
  
  # Don't do this, you just nuked all the bootstrapped apps
  #setFixture("application", "application_fixture.json", "test/data", false)
  puts "DEBUG: running allLeaAllowApp SDK Sample"
  allLeaAllowApp("SDK Sample")  
  puts "DEBUG: running authorizeEdorg SDK Sample"
  authorizeEdorg("SDK Sample")
  puts "DEBUG: running allLeaAllowApp Paved ZOO"
  allLeaAllowApp("Paved Z00")
  puts "DEBUG: running authorizeEdorg Paved ZOO"
  authorizeEdorg("Paved Z00")
  puts "DEBUG: running bulkExtractTriggerTest"
  Rake::Task["bulkExtractTriggerTest"].execute
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

desc "API Bulk Extract Tests"
task :bulkExtractApiTests do
  runTests("test/features/bulk_extract/features/bulk_extract_headers.feature")
  runTests("test/features/bulk_extract/features/bulk_extract_partial_gets.feature")
  runTests("test/features/bulk_extract/features/bulk_extract_versions.feature")
  runTests("test/features/bulk_extract/features/bulk_extract_lea_list.feature")
  Rake::Task["bulkExtractCleanup"].execute if CLEAN_EXTRACT_LOC
end

desc "Run RC E2E Tests in Production mode"
task :bulkExtractTests => [:bulkExtractSetup] do
  Rake::Task["bulkExtractSimpleEntitiesTest"].invoke
  Rake::Task["bulkExtractSuperdocTest"].invoke
  Rake::Task["bulkExtractEdorgStaffTest"].invoke
  Rake::Task["bulkExtractIntegrationTest"].invoke
  Rake::Task["bulkExtractApiTests"].invoke
  Rake::Task["bulkExtractDeltasTest"].invoke
  Rake::Task["bulkExtractSchedulerTest"].invoke
  Rake::Task["bulkExtractNegativeTests"].invoke
  Rake::Task["bulkExtractTlsTests"].invoke
  Rake::Task["bulkExtractSEAPublicTest"].invoke
  Rake::Task["bulkExtractCleanup"].invoke
  displayFailureReport()
  if $SUCCESS
    puts "Completed All Tests"
  else
    raise "Tests have failed"
  end
end
