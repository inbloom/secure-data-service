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
  puts "DEBUG: Running bulk extract cleanup"
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

  # TODO: why do we need this
  #@tags = ["~@wip", "~@sandbox"]
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
task :bulkExtractStudentTest => [:bulkExtractSetup] do
  runTests("test/features/bulk_extract/features/bulk_extract_student.feature")
end

desc "Trigger ingestion and extract of the ingestion"
task :bulkExtractSimpleEntitiesTest => [:bulkExtractSetup] do
  runTests("test/features/bulk_extract/features/bulk_extract_simple_entities.feature")
end

desc "Trigger ingestion and extract of the ingestion"
task :bulkExtractEdorgStaffTest => [:bulkExtractSetup] do
  runTests("test/features/bulk_extract/features/bulk_extract_edorg_staff.feature")
end

desc "Trigger ingestion and extract of the ingestion"
task :bulkExtractSuperdocTest => [:bulkExtractSetup] do
  runTests("test/features/bulk_extract/features/bulk_extract_superdoc.feature")
end

desc "Trigger ingestion and extract of the ingestion"
task :bulkExtractIntegrationTest => [:bulkExtractSetup] do
  runTests("test/features/bulk_extract/features/bulk_extract_integration.feature")
end

desc "Deltas and Deletes"
task :bulkExtractDeltasTest => [:bulkExtractSetup] do
  runTests("test/features/bulk_extract/features/bulk_extract_deltas_api.feature")
  runTests("test/features/bulk_extract/features/delta_recording.feature")
end

desc "Extract SEA only public data"
task :bulkExtractSEAPublicTest => [:bulkExtractSetup] do
  runTests("test/features/bulk_extract/features/bulk_extract_sea_public.feature")
end

desc "Negative and Edge Cases"
task :bulkExtractNegativeTests => [:bulkExtractSetup] do
  runTests("test/features/bulk_extract/features/bulk_extract_neg_and_edge.feature")
end

desc "Client Cert Auth Bulk Extract Tests"
task :bulkExtractTlsTests => [:bulkExtractSetup] do
  runTests("test/features/bulk_extract/features/bulk_extract_tls.feature")
end

desc "API Bulk Extract Tests"
task :bulkExtractApiTests => [:bulkExtractSetup] do
  runTests("test/features/bulk_extract/features/bulk_extract_headers.feature")
  runTests("test/features/bulk_extract/features/bulk_extract_partial_gets.feature")
  runTests("test/features/bulk_extract/features/bulk_extract_versions.feature")
  runTests("test/features/bulk_extract/features/bulk_extract_lea_list.feature")
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
