############################################################
# API Selenium tests start
# NOTE: Do not add these tests to the production run yet!
#       While they can be run locally without issues,
#       Firefox cannot start on Jenkins, and will cause
#       all these tests to fail
############################################################
desc "Run API Selenium Tests"
task :apiSeleniumTests => [:adminToolsTests, :databrowserTests] do
  displayFailureReport()
  if $SUCCESS
    puts "Completed All Tests"
  else
    raise "Tests have failed"
  end
end

desc "Run Admin-Tools Selenium Tests"
task :adminToolsTests => [:realmInit,:accountApprovalInterfaceTests, :accountApprovalTests] do
  Rake::Task["importSandboxData"].execute
  runTests("test/features/admintools")
  Rake::Task["importSandboxData"].execute
  displayFailureReport()
  if $SUCCESS
    puts "Completed All Tests"
  else
    raise "Tests have failed"
  end
end

task :adminRightsTests do
  runTests("test/features/admintools/sli_admin_authorization.feature")
end

desc "Run Dataprowler Selenium Tests"
task :databrowserTests => [:realmInit] do
  Rake::Task["importSandboxData"].execute
  runTests("test/features/databrowser/databrowser_simple_detail_view.feature")
  runTests("test/features/databrowser/critical_auth_bugfix.feature")
  runTests("test/features/databrowser/databrowser_selective_deny.feature")
  runFixtureAndTests("test/features/databrowser/databrowser_denial.feature", "application", "application_denial_fixture.json")
  displayFailureReport()
  if $SUCCESS
    puts "Completed All Tests"
  else
    raise "Tests have failed"
  end
end

desc "Run IDP Authentication Selenium Tests"
task :idpAuthTests => [:realmInit] do
  runTests("test/features/databrowser/idp_authentication.feature")
  displayFailureReport()
  if $SUCCESS
    puts "Completed All Tests"
  else
    raise "Tests have failed"
  end
end

desc "Run Sandbox Simple IDP Authentication Selenium Tests"
task :simpleIDPAuthTests => [:realmInit, :importSandboxData] do
  runTests("test/features/simple-idp")
  displayFailureReport()
  if $SUCCESS
    puts "Completed All Tests"
  else
    raise "Tests have failed"
  end
end

############################################################
# API Selenium tests end
############################################################
