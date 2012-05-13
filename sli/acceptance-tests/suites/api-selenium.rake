############################################################
# API Selenium tests start
# NOTE: Do not add these tests to the production run yet!
#       While they can be run locally without issues,
#       Firefox cannot start on Jenkins, and will cause
#       all these tests to fail
############################################################
task :adminRightsTests do
  runTests("test/features/admintools/sli_admin_authorization.feature")
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

