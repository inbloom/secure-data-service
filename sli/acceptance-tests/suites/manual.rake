############################################################
# Manual tests start
############################################################

desc "Run Activemq Redundancy Test"
task :ingestionActivemqRedundancyTest do
  runTests("test/features/ingestion/features/ingestion_mqRedundancy.feature")
end

desc "Run Mongo Tracking Aspect Test"
task :ingestionMongoTrackingAspect do
  runTests("test/features/ingestion/features/ingesti2on_MongoTrackingAspect.feature")
end

desc "Run Multiple Maestros Test"
task :ingestionMultipleMaestro do
  runTests("test/features/ingestion/features/ingestion_MultipleMaestro.feature")
end

############################################################
# Manual tests end
############################################################