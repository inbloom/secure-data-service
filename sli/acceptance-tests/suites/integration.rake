############################################################
# Cross App Tests start
############################################################
desc "Run cross application testing"
task :crossAppTests => [:appInit] do
  runTests("test/features/cross_app_tests")
end

desc "Run Ingestion RC Test"
task :rcIngestionTests do
  runTests("test/features/ingestion/rc_test/rc_integration_ingestion.feature")
end

desc "Run RC Tests"
task :rcTests do
  runTests("test/features/cross_app_tests/rc_integration_app_approval.feature")
  runTests("test/features/ingestion/rc_test/rc_ingestion_integration.feature")
  runTests("test/features/dashboard/dash/dashboard_rc_integration.feature")
end
############################################################
# Cross App Tests end
############################################################
