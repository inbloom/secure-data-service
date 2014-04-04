desc "Run Admin Tool Smoke Tests"
task :adminWebTests => [:realmInit] do
  Rake::Task["importSandboxData"].execute

  allLeaAllowApp("Mobile App")
  authorize_ed_org("Mobile App")
  #runTests("test/features/admintools/security_events.feature")
  runTests("test/features/admintools/authorize_applications_with_federated_users.feature")
  runTests("test/features/admintools/accountRequest.feature")
  runTests("test/features/admintools/Admin_Edit_Rules.feature")
  runTests("test/features/admintools/admin_smoke.feature")
  runTests("test/features/admintools/Admin_Welcome_Email.feature")
  runTests("test/features/admintools/app_authorization.feature")
  runTests("test/features/admintools/app_registration.feature")
  runTests("test/features/admintools/bulkExtract.feature")
  runTests("test/features/admintools/custom_role_mapping.feature")
  runTests("test/features/admintools/delegate_privileges.feature")
  runTests("test/features/admintools/realm_management.feature")
  runTests("test/features/admintools/enable_and_authorize_apps_directly_by_edorg.feature")
  runTests("test/features/admintools/enable_and_authorize_bulk_extract_apps_directly_by_edorg.feature")
  runTests("test/features/admintools/encrypted_session_cookie.feature")
  runTests("test/features/admintools/ProvisioningApplication_Interface.feature")
  runTests("test/features/admintools/reset_change_password.feature")
  runTests("test/features/admintools/SAMT_Account_Management_Interface.feature")
  runTests("test/features/admintools/realm_authentication.feature")
end

desc "Run Admin Tool Smoke Tests"
task :adminSmokeTests do 
  @tags = ["~@wip", "@smoke", "~@sandbox"]
  Rake::Task["realmInit"].execute
  Rake::Task["importSandboxData"].execute
  runTests("test/features/admintools/admin_smoke.feature")
end
