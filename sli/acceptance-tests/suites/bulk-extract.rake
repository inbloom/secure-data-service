############################################################
# RC Tests
############################################################

desc "Trigger ingestion and extract of the ingestion"
task :bulkExtractTest do
  runTests("test/features/bulk_extract/features/bulk_extract.feature")
end

desc "Run RC E2E Tests in Production mode"
task :bulkExtractTests do
  Rake::Task["ingestionSmallSampleDataSet"].execute
  Rake::Task["bulkExtractTest"].execute
  displayFailureReport()
  if $SUCCESS
    puts "Completed All Tests"
  else
    raise "Tests have failed"
  end
end