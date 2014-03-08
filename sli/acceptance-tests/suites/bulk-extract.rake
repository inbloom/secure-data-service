############################################################
# Environment Variables Set Up For Bulk Extract Tests
############################################################

CLEAN_EXTRACT_LOC = true
TRIGGER_NEW_EXTRACT = true

desc "Cleanup the extracts"
task :bulkExtractCleanup do
  runTests("test/features/bulk_extract/features/bulk_extract_cleanup.feature")
end

desc "Trigger bulk extract and verify students are extracted correctly"
task :bulkExtractSmokeTests do
  runTests("test/features/bulk_extract/features/bulk_extract_cleanup.feature")
  runTests("test/features/bulk_extract/features/bulk_extract.feature")
  runTests("test/features/bulk_extract/features/bulk_extract_student.feature")
  runTests("test/features/bulk_extract/features/bulk_extract_cleanup.feature")
end

desc "LEA Level Extract"
task :bulkExtractLeasTests do
  runTests("test/features/ingestion/features/ingestion_BulkExtractLeas.feature")
  allLeaAllowApp("SDK Sample")
  authorizeEdorg("SDK Sample")
  runTests("test/features/bulk_extract/features/bulk_extract_integration_lea.feature")
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
  runTests("test/features/bulk_extract/features/bulk_extract_deltas_ingestion.feature")
  runTests("test/features/bulk_extract/features/bulk_extract_deltas_api.feature")
  runTests("test/features/bulk_extract/features/delta_recording.feature")
  Rake::Task["bulkExtractCleanup"].execute if CLEAN_EXTRACT_LOC
end

desc "Extract SEA only public data"
task :bulkExtractPublicTest do
  runTests("test/features/bulk_extract/features/bulk_extract_sea_ingest.feature")
  allLeaAllowApp("SDK Sample")
  authorizeEdorg("SDK Sample")
  runTests("test/features/bulk_extract/features/bulk_extract_public.feature")
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

desc "Run the full suite of Bulk Extract Tests"
task :bulkExtractTests => [:realmInit] do
  CLEAN_EXTRACT_LOC = false
  TRIGGER_NEW_EXTRACT = false

  Rake::Task["bulkExtractCleanup"].execute
  Rake::Task["ingestionSmokeTests"].execute
  @tags = ["~@wip", "~@sandbox"]

  Rake::Task["addBootstrapAppAuths"].execute
  allLeaAllowApp("SDK Sample")
  authorizeEdorg("SDK Sample")
  allLeaAllowApp("Paved Z00")
  authorizeEdorg("Paved Z00")

  runTests("test/features/bulk_extract/features/bulk_extract.feature")
  runTests("test/features/bulk_extract/features/bulk_extract_simple_entities.feature")
  runTests("test/features/bulk_extract/features/bulk_extract_superdoc.feature")
  runTests("test/features/bulk_extract/features/bulk_extract_edorg_staff.feature")
  runTests("test/features/bulk_extract/features/bulk_extract_integration.feature")
  runTests("test/features/bulk_extract/features/bulk_extract_headers.feature")
  runTests("test/features/bulk_extract/features/bulk_extract_partial_gets.feature")
  runTests("test/features/bulk_extract/features/bulk_extract_versions.feature")
  runTests("test/features/bulk_extract/features/bulk_extract_list.feature")
  runTests("test/features/bulk_extract/features/bulk_extract_security_event.feature")
  Rake::Task["bulkExtractLeasTests"].execute
  runTests("test/features/bulk_extract/features/bulk_extract_all_edOrgs.feature")
  Rake::Task["bulkExtractDeltasTest"].execute
  runTests("test/features/bulk_extract/features/bulk_extract_scheduler.feature")
  runTests("test/features/bulk_extract/features/bulk_extract_neg_and_edge.feature")
  Rake::Task["bulkExtractTlsTests"].execute
  Rake::Task["bulkExtractPublicTest"].execute
  runTests("test/features/bulk_extract/features/bulk_extract_cleanup_script.feature")
  runTests("test/features/bulk_extract/features/bulk_extract_extractor_security_event.feature")
  runTests("test/features/bulk_extract/features/bulk_extract_delta_purge.feature")
  runTests("test/features/bulk_extract/features/bulk_extract_prior_data.feature")
  runTests("test/features/bulk_extract/features/bulk_extract_delta_prior_data.feature")
  runTests("test/features/bulk_extract/features/bulk_extract_top_level_education_organization_private_data.feature")
  Rake::Task["bulkExtractCleanup"].execute

  display_failure_report
end
