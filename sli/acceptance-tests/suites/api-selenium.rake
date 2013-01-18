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

desc "Run Admin Tool Smoke Tests"
task :adminWebTests => [:realmInit] do
  Rake::Task["importSandboxData"].execute
  runTests("test/features/admintools")
end

desc "Run Admin Tool Smoke Tests"
task :adminRealmTests => [:realmInit] do
  Rake::Task["importSandboxData"].execute
  runTests("test/features/admintools/edit_realms.feature")
end

desc "Run custom Roles Tests"
task :customRolesTests => [:realmInit] do
  Rake::Task["importSandboxData"].execute
  runTests("test/features/admintools/custom_role_mapping.feature")
end

desc "Run Application Registration Tests"
task :appRegistrationTests => [:realmInit] do
  Rake::Task["importSandboxData"].execute
  runTests("test/features/admintools/app_registration.feature")
end

desc "Run Admin Edit Rules Tests"
task :adminEditRules => [:realmInit] do
  Rake::Task["importSandboxData"].execute
  runTests("test/features/admintools/Admin_Edit_Rules.feature")
end

desc "Run Admin Encrypted Session Cookie Tests"
task :adminEncryptedSessionCookie=> [:realmInit] do
  Rake::Task["importSandboxData"].execute
  runTests("test/features/admintools/encrypted_session_cookie.feature")
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

desc "Run Dataprowler Smoke Tests"
task :databrowserSmokeTests do 
  @tags = ["~@wip", "@smoke", "~@sandbox"]
  runTests("test/features/databrowser/databrowser_simple_detail_view.feature")
end

desc "Run Admin Tool Smoke Tests"
task :adminSmokeTests do 
  @tags = ["~@wip", "@smoke", "~@sandbox"]
  Rake::Task["realmInit"].execute
  Rake::Task["importSandboxData"].execute
  runTests("test/features/admintools/admin_smoke.feature")
end

############################################################
# API Selenium tests end
############################################################

