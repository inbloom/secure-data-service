desc "Run Search Tests"
task :searchTests => [:importUnifiedData] do
  runTests("test/features/search/search_extractor.feature")
  runTests("test/features/search/search_indexer.feature")
end

desc "Run Sarje-Search Listener Tests"
task :sarjeSearchTests=> [:importUnifiedData] do
  runTests("test/features/search/search_sarje_listener.feature")
end
