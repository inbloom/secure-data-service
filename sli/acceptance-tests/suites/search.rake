desc "Implement Context Security for Staff & Teachers"
task :searchApiTests => [:runSearchBulkExtract] do
  runTests("test/features/search/search_api.feature")
end

desc "Run Bulk Extract" 
task :runSearchBulkExtract do
  runTests("test/features/search/search_bulk_extract.feature")
end

desc "Run Assessment search indexer test" 
task :runAssmtSearchIndexer do
  Rake::Task["importSandboxData"].execute
  runTests("test/features/search/search_assessment_extract_update.feature")
end
