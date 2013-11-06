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

desc "Run Admin Bulk Extract Tests"
task :adminBulkExtractTests => [:realmInit] do
  Rake::Task["importSandboxData"].execute
  runTests("test/features/admintools/bulkExtract.feature")
end

desc "Run Admin Tool Smoke Tests"
task :adminWebTests => [:realmInit] do
  Rake::Task["importSandboxData"].execute
  runTests("test/features/admintools/accountRequest.feature")
  runTests("test/features/admintools/Admin_Edit_Rules.feature")
  runTests("test/features/admintools/admin_smoke.feature")
  runTests("test/features/admintools/Admin_Welcome_Email.feature")
  runTests("test/features/admintools/admintool_declarative.feature")
  runTests("test/features/admintools/app_authorization.feature")
  runTests("test/features/admintools/app_registration.feature")
  runTests("test/features/admintools/bulkExtract.feature")
  runTests("test/features/admintools/custom_role_mapping.feature")
  runTests("test/features/admintools/delegate_privileges.feature")
  runTests("test/features/admintools/developer_enable.feature")
  runTests("test/features/admintools/edit_realms.feature")
  runTests("test/features/admintools/enable_and_authorize_apps_directly_by_edorg.feature")
  runTests("test/features/admintools/enable_and_authorize_bulk_extract_apps_directly_by_edorg.feature")
  runTests("test/features/admintools/encrypted_session_cookie.feature")
  runTests("test/features/admintools/ProvisioningApplication_Interface.feature")
  runTests("test/features/admintools/reset_change_password.feature")
  runTests("test/features/admintools/SAMT_Account_Management_Interface.feature")
  runTests("test/features/admintools/sli_admin_authorization.feature")
end

desc "Run Admin Auth Tests"
task :adminAppAuthTests => [:realmInit] do
  Rake::Task["importSandboxData"].execute
  runTests("test/features/admintools/enable_and_authorize_apps_directly_by_edorg.feature")
  runTests("test/features/admintools/enable_and_authorize_bulk_extract_apps_directly_by_edorg.feature")
end

desc "Run Admin Tool Smoke Tests"
task :adminRealmTests => [:realmInit] do
  Rake::Task["importSandboxData"].execute
  runTests("test/features/admintools/edit_realms.feature")
end

desc "Run Admin Delegation Tests"
task :adminDelegationTests => [:realmInit] do
  Rake::Task["importSandboxData"].execute
  runTests("test/features/admintools/delegate_privileges.feature")
end

desc "Run Admin Develper App Enable Tests"
task :adminDevAppEnableTests => [:realmInit] do
  Rake::Task["importSandboxData"].execute
  runTests("test/features/admintools/developer_enable.feature")
end

desc "Run Reset and Change Password Selenium Tests"
task :resetAndChangePasswordTests => [:realmInit] do
  runTests("test/features/admintools/reset_change_password.feature")
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

desc "Run Application Authorization Tests"
task :appAuthorizationTests => [:realmInit] do
  Rake::Task["importSandboxData"].execute
  runTests("test/features/admintools/app_authorization.feature")
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
  runTests("test/features/databrowser/student_authentication.feature")
end

desc "Run Admin Tool Smoke Tests"
task :adminSmokeTests do 
  @tags = ["~@wip", "@smoke", "~@sandbox"]
  Rake::Task["realmInit"].execute
  Rake::Task["importSandboxData"].execute
  runTests("test/features/admintools/admin_smoke.feature")
end

desc "Run multi realms with same idp test"
task :adminMultiRealmSameIdpTests do
  Rake::Task["realmInit"].execute
  Rake::Task["importSandboxData"].execute
  allLeaAllowApp("Mobile App")
  authorizeEdorg("Mobile App")
  runTests("test/features/admintools/multi_realms_with_same_idp.feature")
end

desc "Run application authorization with federated users tests"
task :appAuthWithFederatedUsersTests do
  Rake::Task["realmInit"].execute
  Rake::Task["importSandboxData"].execute
  allLeaAllowApp("Mobile App")
  authorizeEdorg("Mobile App")
  runTests("test/features/admintools/authrorize_applications_with_federated_users.feature")
end

############################################################
# API Selenium tests end
############################################################

