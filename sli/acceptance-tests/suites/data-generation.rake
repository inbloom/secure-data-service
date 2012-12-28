############################################################
# Data Generation tests start
############################################################
desc "Run Odin 10 students Data Generator Tests"
task :odinGenerateTests do
  runTests("test/features/odin/generate_10_students.feature")
end

desc "Run Odin 10k1 students Data Generator Tests"
task :odin10k1GenerateTests do
  runTests("test/features/odin/generate_10k1_students.feature")
end

desc "Run Odin 10 students Data Set Ingestion Tests"
task :ingestOdinDataSet do
  runTests("test/features/ingestion/features/ingestion_OdinDataSet.feature")
end

desc "Run Odin 10k1 students Data Set Ingestion Tests"
task :ingestOdin10k1DataSet do
  runTests("test/features/ingestion/features/ingestion_Odin10k1DataSet.feature")
end

############################################################
# Data Generation tests end
############################################################

