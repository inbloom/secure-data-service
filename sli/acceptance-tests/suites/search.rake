desc "Run Search Tests"
task :searchTests do
  ["Midgar","Hyrule"].each do |tenant|
    ["student", "learningObjective", "learningStandard", "competencyLevelDescriptor", "studentCompetencyObjective", "assessment"].each do |collection|
      removeCollectionsCleanly(collection, tenant)
    end
  end
  Rake::Task["importUnifiedData"].execute
  runTests("test/features/search/search_extractor.feature")
  runTests("test/features/search/search_indexer.feature")
end

desc "Implement Context Security for Staff & Teachers"
task :searchApiTests => [:runSearchBulkExtract] do
  runTests("test/features/search/search_api.feature")
end

desc "Run Bulk Extract" 
task :runSearchBulkExtract do
  runTests("test/features/search/search_bulk_extract.feature")
end
