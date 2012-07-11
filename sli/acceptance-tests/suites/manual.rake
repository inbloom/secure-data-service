############################################################
# Manual tests start
############################################################

desc "Run Activemq Redundancy Test"
task :ingestionActivemqRedundancyTest do
  runTests("test/features/ingestion/features/ingestion_mqRedundancy.feature")
end

############################################################
# Manual tests end
############################################################