############################################################
# Cross App Tests start
############################################################
desc "Run cross application testing"
task :crossAppTests => [:appInit] do
  runTests("test/features/cross_app_tests")
end

desc "Run RC Tests"
task :rcTests do
  runTests("test/features/cross_app_tests/rc_integration_app_approval.feature")
  runTests("test/features/dashboard/dash/dashboard_rc_integration.feature")
end

desc "Run RC SAMT Tests"
task :rcSamtTests do
  runTests("test/features/cross_app_tests/rc_integration_samt.feature")
end
############################################################
# Cross App Tests end
############################################################
