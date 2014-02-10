desc "Run Odin Data Set Generator Tests"
task :odinGenerateTests do
  runTests("test/features/odin/generate_odin_data_set.feature")
end

desc "Run Odin Data Set Ingestion Tests"
task :ingestOdinDataSet do
  runTests("test/features/ingestion/features/ingestion_OdinDataSet.feature")
end
