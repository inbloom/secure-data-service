############################################################
# Environment Variables Set Up For Bulk Extract Tests
############################################################

CLEAN_EXTRACT_LOC = true
TRIGGER_NEW_EXTRACT = true

############################################################
# Bulk Extract
############################################################
desc "Test the Bulk Extract Scheduler"
task :bulkExtractSchedulerTest do
  runTests("test/features/bulk_extract/features/bulk_extract_scheduler.feature")
end

desc "Cleanup the extracts"
task :bulkExtractCleanup do
  runTests("test/features/bulk_extract/features/bulk_extract_cleanup.feature")
end

desc "Ingest data to setup other tests"
task :bulkExtractSetup do
  Rake::Task["bulkExtractCleanup"].execute
  Rake::Task["ingestionSmokeTests"].execute 
  @tags = ["~@wip", "~@sandbox"]

end

desc "Trigger bulk extract and verify students are extracted correctly"
task :bulkExtractSmokeTests do
  Rake::Task["bulkExtractCleanup"].execute if CLEAN_EXTRACT_LOC      
  Rake::Task["bulkExtractTriggerTest"].execute
  Rake::Task["bulkExtractStudentTest"].execute  
end

desc "Trigger bulk extract"
task :bulkExtractTriggerTest do
  runTests("test/features/bulk_extract/features/bulk_extract.feature")
end

desc "Verify students are extracted correctly (used in bulk extract smoke)"
task :bulkExtractStudentTest do
  Rake::Task["bulkExtractTriggerTest"].execute if TRIGGER_NEW_EXTRACT
  runTests("test/features/bulk_extract/features/bulk_extract_student.feature")
  Rake::Task["bulkExtractCleanup"].execute if CLEAN_EXTRACT_LOC
end

desc "Verify simple entities (those that are not sub or superdoced) are extracted correctly"
task :bulkExtractSimpleEntitiesTest do
  Rake::Task["bulkExtractTriggerTest"].execute if TRIGGER_NEW_EXTRACT
  runTests("test/features/bulk_extract/features/bulk_extract_simple_entities.feature")
  Rake::Task["bulkExtractCleanup"].execute if CLEAN_EXTRACT_LOC
end

desc "Verify Staff, teacher, edorg, and school entities are extracted correctly"
task :bulkExtractEdorgStaffTest do
  Rake::Task["bulkExtractTriggerTest"].execute if TRIGGER_NEW_EXTRACT
  runTests("test/features/bulk_extract/features/bulk_extract_edorg_staff.feature")
  Rake::Task["bulkExtractCleanup"].execute if CLEAN_EXTRACT_LOC
end

desc "Verify Super and subdoc entities are extracted correctly"
task :bulkExtractSuperdocTest do
  Rake::Task["bulkExtractTriggerTest"].execute if TRIGGER_NEW_EXTRACT
  runTests("test/features/bulk_extract/features/bulk_extract_superdoc.feature")
  Rake::Task["bulkExtractCleanup"].execute if CLEAN_EXTRACT_LOC
end

desc "Trigger bulk extract and retrieve the extract through the API"
task :bulkExtractIntegrationTest do
  #Rake::Task["bulkExtractSetup"].execute if TRIGGER_NEW_EXTRACT
  runTests("test/features/bulk_extract/features/bulk_extract_integration.feature")
  Rake::Task["bulkExtractCleanup"].execute if CLEAN_EXTRACT_LOC
end

desc "LEA Level Extract"
task :bulkExtractLeasTests do
  Rake::Task["ingestionBulkExtractLeas"].execute
  allLeaAllowApp("SDK Sample")
  authorizeEdorg("SDK Sample")
  runTests("test/features/bulk_extract/features/bulk_extract_integration_lea.feature")
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

desc "Hybird EdOrg Cases"
task :bulkExtractHybridEdOrgsTests do
  Rake::Task["bulkExtractCleanup"].execute
  runTests("test/features/bulk_extract/features/bulk_extract_sea_ingest.feature")
  Rake::Task["realmInit"].execute
  Rake::Task["appInit"].execute
  allLeaAllowApp("SDK Sample")  
  authorizeEdorg("SDK Sample")
  allLeaAllowApp("Paved Z00")
  authorizeEdorg("Paved Z00")
  runTests("test/features/bulk_extract/features/bulk_extract_hybrid_edorgs.feature")
  Rake::Task["bulkExtractCleanup"].execute if CLEAN_EXTRACT_LOC
end

desc "Mutiple Parent Cases"
task :bulkExtractMultiParentsTests do
  Rake::Task["bulkExtractCleanup"].execute
  runTests("test/features/bulk_extract/features/bulk_extract_sea_ingest.feature")
  Rake::Task["realmInit"].execute
  Rake::Task["appInit"].execute
  allLeaAllowApp("SDK Sample")  
  authorizeEdorg("SDK Sample")
  allLeaAllowApp("Paved Z00")
  authorizeEdorg("Paved Z00")
  runTests("test/features/bulk_extract/features/bulk_extract_multi_parents.feature")
  Rake::Task["bulkExtractCleanup"].execute if CLEAN_EXTRACT_LOC
end

desc "Client Cert Auth Bulk Extract Tests"
task :bulkExtractTlsTests do
  runTests("test/features/bulk_extract/features/bulk_extract_sea_ingest.feature")
  Rake::Task["realmInit"].execute
  Rake::Task["appInit"].execute
  allLeaAllowApp("SDK Sample")
  authorizeEdorg("SDK Sample")
  allLeaAllowApp("Paved Z00")
  authorizeEdorg("Paved Z00")
  runTests("test/features/bulk_extract/features/bulk_extract_tls.feature")
  Rake::Task["bulkExtractCleanup"].execute if CLEAN_EXTRACT_LOC
end

desc "Security Events generated by the BE component"
task :bulkExtractSecurityEventTests do
  runTests("test/features/bulk_extract/features/bulk_extract_extractor_security_event.feature")
  Rake::Task["bulkExtractCleanup"].execute if CLEAN_EXTRACT_LOC
end

desc "Delta extract after a purge"
task :bulkExtractDeltaPurgeTests do
  runTests("test/features/bulk_extract/features/bulk_extract_delta_purge.feature")
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
  runTests("test/features/bulk_extract/features/bulk_extract_list.feature")
  runTests("test/features/bulk_extract/features/bulk_extract_security_event.feature")
  Rake::Task["bulkExtractCleanup"].execute if CLEAN_EXTRACT_LOC
end

desc "API Bulk Extract non LEA/SEA edOrgs Tests"
task :bulkExtractEdOrgsTests do
  runTests("test/features/bulk_extract/features/bulk_extract_all_edOrgs.feature")
  Rake::Task["bulkExtractCleanup"].execute if CLEAN_EXTRACT_LOC
end

desc "Run the full suite of Bulk Extract Tests"
task :bulkExtractAllEdOrgsTests => [:realmInit] do
  CLEAN_EXTRACT_LOC = false
  TRIGGER_NEW_EXTRACT = false
  Rake::Task["bulkExtractSetup"].execute
  Rake::Task["addBootstrapAppAuths"].execute
  allLeaAllowApp("SDK Sample")
  authorizeEdorg("SDK Sample")
  Rake::Task["bulkExtractTriggerTest"].execute
  Rake::Task["bulkExtractSimpleEntitiesTest"].execute
  Rake::Task["bulkExtractEdOrgsTests"].execute
  displayFailureReport()
  if $SUCCESS
    puts "Completed All Tests"
  else
    raise "Tests have failed"
  end
end

desc "Run the full suite of Bulk Extract Tests"
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
  Rake::Task["bulkExtractLeasTests"].execute
  Rake::Task["bulkExtractDeltasTest"].execute
  Rake::Task["bulkExtractSchedulerTest"].execute
  Rake::Task["bulkExtractNegativeTests"].execute
  Rake::Task["bulkExtractTlsTests"].execute
  Rake::Task["bulkExtractSEAPublicTest"].execute
  Rake::Task["bulkExtractCleanupTests"].execute
  Rake::Task["bulkExtractSecurityEventTests"].execute
  Rake::Task["bulkExtractDeltaPurgeTests"].execute
  Rake::Task["bulkExtractEdOrgsTests"].execute
  Rake::Task["bulkExtractCleanup"].execute
  displayFailureReport()
  if $SUCCESS
    puts "Completed All Tests"
  else
    raise "Tests have failed"
  end
end
