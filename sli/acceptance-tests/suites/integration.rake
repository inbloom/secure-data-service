############################################################
# Integration Tests start
############################################################

desc "Run Integration Acceptance Tests"
task :integrationTests => [:realmInitNoPeople] do
  OTHER_TAGS = OTHER_TAGS+" --tags @integration"
  Rake::Task["ingestionAcceptanceSdsTest"].execute
  Rake::Task["crossAppTests"].execute
  Rake::Task["dashboardSdsTests"].invoke
  displayFailureReport()
  if $SUCCESS
    puts "Completed All Tests"
  else
    raise "Tests have failed"
  end
end

############################################################
# Integration Tests end
############################################################

############################################################
# Cross App Tests start
############################################################

desc "Run cross application testing"
task :crossAppTests do
  runTests("test/features/cross_app_tests")
end

############################################################
# Cross App Tests end
############################################################
