############################################################
# Cross App Tests start
############################################################
require_relative '../test/features/utils/rakefile_common.rb'

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
  runTests("test/features/cross_app_tests/rc_integration_dashboard.feature")
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

desc "Delete SEA, LEA and dev from LDAP"
task :rcDeleteLDAPUsers do
  emailsToDelete = ["testuser0.wgen@gmail.com", "testuser1.wgen@gmail.com", "testdev.wgen@gmail.com"]
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
  Rake::Task["rcSamtTests"].execute
  Rake::Task["rcProvisioningTests"].execute
  Rake::Task["rcAccountRequestTests"].execute
  Rake::Task["rcIngestionTests"].execute
  Rake::Task["rcAppApprovalTests"].execute
  check_stamper_log("Finished stamping tenant \'RCTestTenant\'.")
  Rake::Task["rcDashboardTests"].execute
  Rake::Task["rcCleanUpTests"].execute

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

############################################################
# STAMPER LOG MONITORING
############################################################

private

def check_stamper_log(line = nil)
  edorg_stamper_log = "/var/log/edorg.log"
  teacher_stamper_log = "/var/log/teacher.log"
  edorg_stamper_finished = false
  teacher_stamper_finished = false
  seconds_to_wait = 120
  backward = 1000

  seconds_to_wait.times do
    if edorg_stamper_finished && teacher_stamper_finished
      puts line
      return
    end
    edorg_stamper_finished  = `tail -n #{backward} #{edorg_stamper_log}`.to_s.include?(line) if !edorg_stamper_finished
    teacher_stamper_finished = `tail -n #{backward} #{teacher_stamper_log}`.to_s.include?(line) if !teacher_stamper_finished
    sleep 1
  end

  fail("Tenant data not stamped within #{seconds_to_wait} seconds.")
end
