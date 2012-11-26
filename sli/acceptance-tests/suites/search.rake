desc "Run Search Tests"
task :searchTests => [:importUnifiedData] do
  runTests("test/features/search/search_extractor.feature")
  runTests("test/features/search/search_indexer.feature")
end

desc "Run Sarje-Search Listener Tests"
task :sarjeSearchTests=> [:importUnifiedData] do
  runTests("test/features/search/search_sarje_listener.feature")
end

desc "Implement Context Security for Staff & Teachers"
task :searchSecurityTests do
  runTests("test/features/search/search_context_security.feature")
end

desc "Run Bulk Extract" 
task :runSearchBulkExtract do
  runTests("test/features/search/search_bulk_extract.feature")
end
