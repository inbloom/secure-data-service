desc "Run Search Tests"
task :searchTests do
  runTests("test/features/search/search_indexer.feature")
end