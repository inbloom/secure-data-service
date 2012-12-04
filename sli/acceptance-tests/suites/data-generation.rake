############################################################
# Data Generation tests start
############################################################
desc "Run Odin Data Generator Tests"
task :odinGenerateTests do
  runTests("test/features/odin/generate_10_students.feature")
end

desc "Run Odin Data Set Ingestion Tests"
task :ingestOdinDataSet do
  runTests("test/features/ingestion/features/ingestion_OdinDataSet.feature")
end

############################################################
# Data Generation tests end
############################################################

