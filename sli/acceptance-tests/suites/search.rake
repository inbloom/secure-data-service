desc "Run Search Tests"
task :searchTests => [:importUnifiedData] do
  runTests("test/features/search/search_extractor.feature")
  runTests("test/features/search/search_indexer.feature")
end

