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

desc "Run Provision LZ Test"
task :rcProvisioningTests do
  runTests("test/features/cross_app_tests/rc_integration_provision_lz.feature")
end

desc "Run App Approval RC Test"
task :rcAppApprovalTests do
  runTests("test/features/cross_app_tests/rc_integration_app_approval.feature")
end

desc "Run Dashboard RC Test"
task :rcDashboardTests do
  runTests("test/features/dashboard/dash/dashboard_rc_integration.feature")
end

desc "Run RC SAMT Tests"
task :rcSamtTests do
  runTests("test/features/cross_app_tests/rc_integration_samt.feature")
end

desc "Run RC Account Registration Tests"
task :rcAccountRequestTests do
  runTests("test/features/cross_app_tests/rc_integration_account_request.feature")
end

desc "Run RC Cleanup"
task :rcCleanUpTests do
  runTests("test/features/cross_app_tests/rc_integration_cleanup.feature")
end

desc "Run RC Tests"
task :rcTests do
  OTHER_TAGS = OTHER_TAGS+" --tags @rc"
  Rake::Task["rcSamtTests"].execute
  Rake::Task["rcProvisioningTests"].execute
  Rake::Task["rcAccountRequestTests"].execute
  Rake::Task["rcIngestionTests"].execute
  Rake::Task["rcAppApprovalTests"].execute
  Rake::Task["rcDashboardTests"].execute

  displayFailureReport()
  if $SUCCESS
    puts "Completed All Tests"
  else
    raise "Tests have failed"
  end
end
############################################################
# Cross App Tests end
############################################################
