############################################################
# Environment Variables Set Up For RC Tests
############################################################

RUN_ON_RC = ENV['RUN_ON_RC'] ? true : false

############################################################
# Cross App Tests
############################################################

desc "Run cross application testing"
task :crossAppTests => [:appInit] do
  runTests("test/features/cross_app_tests")
end

############################################################
# RC Tests
############################################################

require_relative '../test/features/utils/rakefile_common.rb'

desc "Run Ingestion RC Test"
task :rcIngestionTests do
  if RUN_ON_RC
    runTests("test/features/ingestion/rc_test/rc_integration_ingestion.feature")
  else
    runTests("test/features/ingestion/rc_test/rc_integration_ingestion_ci.feature")
  end
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
  runTests("test/features/cross_app_tests/rc_integration_dashboard.feature")
end

desc "Run RC SAMT Tests"
task :rcSamtTests do
  runTests("test/features/cross_app_tests/rc_integration_samt.feature")
end

desc "Run RC SAMT Tests"
task :rcLeaSamtTests do
  runTests("test/features/cross_app_tests/rc_integration_lea_samt.feature")
end

desc "Run RC Account Registration Tests"
task :rcAccountRequestTests do
  runTests("test/features/cross_app_tests/rc_integration_account_request.feature")
end

desc "Run RC Cleanup"
task :rcCleanUpTests do
  runTests("test/features/cross_app_tests/rc_integration_cleanup.feature")
end

desc "Run RC Tenant Cleanup"
task :rcTenantCleanUp do
  runTests("test/features/cross_app_tests/rc_integration_delete_tenant.feature")
end

desc "Delete SEA, LEA and dev from LDAP"
task :rcDeleteLDAPUsers do
  #emailsToDelete = ["testuser0.wgen@gmail.com", "testuser1.wgen@gmail.com", "testdev.wgen@gmail.com"]
  emailsToDelete = [(PropLoader.getProps['primary_email_imap_registration_user_email']), 
                    (PropLoader.getProps['secondary_email_imap_registration_user_email']), 
                    (PropLoader.getProps['developer_email_imap_registration_user_email'])]
  emailsToDelete.each do |email|
    begin
      cleanUpLdapUser(email)
      puts "Successfully Deleted #{email} from LDAP"
    rescue
      puts "Error:  Deleting #{email} from LDAP failed"
    end
  end
end

desc "Run RC Tests"
task :rcTests do
  OTHER_TAGS = OTHER_TAGS+" --tags @rc"
#  Rake::Task["rcCleanUpTests"].execute
  Rake::Task["rcSamtTests"].execute
  Rake::Task["rcProvisioningTests"].execute
  Rake::Task["rcIngestionTests"].execute
  Rake::Task["rcLeaSamtTests"].execute
  Rake::Task["rcAccountRequestTests"].execute
  Rake::Task["rcAppApprovalTests"].execute
  Rake::Task["rcDashboardTests"].execute
  Rake::Task["rcDeleteLDAPUsers"].execute

  displayFailureReport()
  if $SUCCESS
    puts "Completed All Tests"
  else
    raise "Tests have failed"
  end
end

desc "Run Sandbox Integration Test for Dev checklist"
task :sandboxDevChecklistTest do
  runTests("test/features/cross_app_tests/sandbox_integration_developer.feature")
end
