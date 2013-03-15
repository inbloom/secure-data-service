############################################################
# Environment Variables Set Up For RC Tests
############################################################

RUN_ON_RC = ENV['RUN_ON_RC'] ? true : false
RC_SERVER = ENV['RC_SERVER'] ? ENV['RC_SERVER'] : ""

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

require 'mongo'
require_relative '../test/features/utils/rakefile_common.rb'

desc "Run Ingestion RC Test"
task :rcIngestionTests do
    runTests("test/features/cross_app_tests/rc_integration_ingestion.feature")
end

desc "Run Provision LZ Test"
task :rcProvisioningTests do
  runTests("test/features/cross_app_tests/rc_integration_provision_lz.feature")
end

desc "Run App Approval RC Test"
task :rcAppApprovalTests do
  if RUN_ON_RC
    runTests("test/features/cross_app_tests/rc_pike_integration_app_approval.feature")
  else
    runTests("test/features/cross_app_tests/rc_integration_app_approval.feature")
  end
end

desc "Run Dashboard RC Test"
task :rcDashboardTests do
  runTests("test/features/cross_app_tests/rc_integration_dashboard.feature")
end

desc "Run DataBrowser RC Test"
task :rcDataBrowserTests do
  if RUN_ON_RC
    runTests("test/features/cross_app_tests/rc_pike_integration_databrowser.feature")
  else
    runTests("test/features/cross_app_tests/rc_integration_databrowser.feature")
  end
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
  if RUN_ON_RC
    runTests("test/features/cross_app_tests/rc_pike_integration_account_request.feature")
  else
    runTests("test/features/cross_app_tests/rc_integration_account_request.feature")
  end
end

desc "Run RC Cleanup"
task :rcCleanUpTests do
  if RUN_ON_RC
    runTests("test/features/cross_app_tests/rc_pike_integration_cleanup.feature")
  else
    runTests("test/features/cross_app_tests/rc_integration_cleanup.feature")
  end
end

desc "Run RC Tenant Cleanup"
task :rcTenantCleanUp do
  runTests("test/features/cross_app_tests/rc_integration_delete_tenant.feature")
end

desc "Run RC Tenant Purge Test"
task :rcTenantPurgeTests do
  runTests("test/features/cross_app_tests/rc_integration_purge.feature")
end

desc "Run RC Sandbox Account Request Test"
task :rcSandboxAccountRequestTests do
  runTests("test/features/cross_app_tests/rc_sandbox_account_request.feature")
end

desc "Run RC Sandbox Provision Test"
task :rcSandboxProvisionTests do
  runTests("test/features/cross_app_tests/rc_sandbox_provision_lz.feature")
end

desc "Run RC Sandbox Dashboard Test"
task :rcSandboxDashboardTests do
  runTests("test/features/cross_app_tests/rc_sandbox_dashboard.feature")
end

desc "Run RC Sandbox Databrowser Test"
task :rcSandboxAppApprovalTests do
  runTests("test/features/cross_app_tests/rc_sandbox_app_approval.feature")
end

desc "Run RC Sandbox DAMT Test"
task :rcSandboxDamtTests do
  runTests("test/features/cross_app_tests/rc_sandbox_damt.feature")
end

desc "Run RC Sandbox Databrowser Test"
task :rcSandboxDatabrowserTests do
  runTests("test/features/cross_app_tests/rc_sandbox_databrowser.feature")
end

desc "Run RC Sandbox Tenant Purge Test"
task :rcSandboxPurgeTests do
  runTests("test/features/cross_app_tests/rc_sandbox_purge.feature")
end

desc "Run RC Sandbox Cleanup"
task :rcSandboxCleanUpTests do
  runTests("test/features/cross_app_tests/rc_sandbox_cleanup.feature")
end

desc "Run RC Tenant Cleanup"
task :rcSandboxTenantCleanUp do
  runTests("test/features/cross_app_tests/rc_sandbox_delete_tenant.feature")
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
      if RUN_ON_RC
        cleanUpMiniSandboxLdapUser(email)
        puts "Successfully Deleted #{email} from mini Sandbox LDAP"
      end
    rescue Exception => e  
      puts e.message
      puts e.backtrace.inspect
      puts "Error:  Deleting #{email} from LDAP failed"
    end
  end
end

desc "Delete Dev1 and Dev2 from LDAP"
task :rcDeleteSandboxLDAPUsers do
  emailsToDelete = [(PropLoader.getProps['developer_sb_email_imap_registration_user_email']),
                    (PropLoader.getProps['developer2_sb_email_imap_registration_user_email'])]
  emailsToDelete.each do |email|
    begin
      cleanUpLdapUser(email)
      puts "Successfully Deleted #{email} from LDAP"
    rescue Exception => e  
      puts e.message
      puts e.backtrace.inspect
      puts "Error:  Deleting #{email} from LDAP failed"
    end
  end
end

desc "Run RC E2E Tests in Production mode"
task :rcTests do
  @tags = ["~@wip", "@rc", "~@sandbox"]
  Rake::Task["rcDeleteLDAPUsers"].execute
  Rake::Task["rcTenantCleanUp"].execute # if tenant_exists
  randomizeRcProdTenant() unless RUN_ON_RC
  Rake::Task["rcSamtTests"].execute
  Rake::Task["rcProvisioningTests"].execute
  Rake::Task["rcIngestionTests"].execute
  Rake::Task["rcLeaSamtTests"].execute
  Rake::Task["rcAccountRequestTests"].execute
  Rake::Task["runSearchBulkExtract"].execute unless RUN_ON_RC
  Rake::Task["rcAppApprovalTests"].execute
  Rake::Task["rcDashboardTests"].execute
  Rake::Task["rcDataBrowserTests"].execute
  Rake::Task["rcCleanUpTests"].execute
  Rake::Task["rcTenantPurgeTests"].execute

  displayFailureReport()
  if $SUCCESS
    puts "Completed All Tests"
  else
    raise "Tests have failed"
  end
end

desc "Run RC E2E Tests in Sandbox mode"
task :rcSandboxTests do
  @tags = ["~@wip", "@rc", "@sandbox"]
  begin
    Rake::Task["rcSandboxTenantCleanUp"].execute # if tenant_exists(PropLoader.getProps['sandbox_tenant'])
    randomizeRcSandboxTenant() unless RUN_ON_RC
    Rake::Task["rcSandboxAccountRequestTests"].execute
    Rake::Task["rcSandboxProvisionTests"].execute
    Rake::Task["runSearchBulkExtract"].execute unless RUN_ON_RC
    Rake::Task["rcSandboxAppApprovalTests"].execute
    Rake::Task["rcSandboxDamtTests"].execute
    Rake::Task["rcSandboxDashboardTests"].execute
    Rake::Task["rcSandboxDatabrowserTests"].execute
    Rake::Task["rcSandboxPurgeTests"].execute
    Rake::Task["rcSandboxCleanUpTests"].execute
  rescue
  ensure
    Rake::Task["rcDeleteSandboxLDAPUsers"].execute
  end
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

############################################################
# Def
############################################################

private

def tenant_exists(tenant_name = PropLoader.getProps['tenant'])
  host = PropLoader.getProps['ingestion_db']
  port = PropLoader.getProps['ingestion_db_port']
  conn = Mongo::Connection.new(host, port)
  sli_db = conn.db(PropLoader.getProps['sli_database_name'])
  (sli_db['tenant'].find("body.tenantId" => tenant_name).count == 0) ? false : true
end
