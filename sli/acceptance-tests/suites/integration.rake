############################################################
# Environment Variables Set Up For RC Tests
############################################################

RUN_ON_RC = ENV['RUN_ON_RC'] ? true : false
STAMPER_WAIT = ENV['STAMPER_WAIT'] ? ENV['STAMPER_WAIT'].to_i : 120  # default is 2 minutes
EDORG_LOG = ENV['EDORG_LOG'] ? ENV['EDORG_LOG'] : "/var/log/edorg.log"
TEACHER_LOG = ENV['TEACHER_LOG'] ? ENV['TEACHER_LOG'] : "/var/log/teacher.log"

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

desc "Check the stamper status / run the stamper on RC/CI"
task :rcCheckStampers do
  if RUN_ON_RC
    check_stamper_log("Finished stamping tenant \'RCTestTenant\'.")
  else
    addSecurityData()
  end
end

desc "Run RC Tests"
task :rcTests do
  OTHER_TAGS = OTHER_TAGS+" --tags @rc"
  Rake::Task["rcSamtTests"].execute
  Rake::Task["rcProvisioningTests"].execute
  Rake::Task["rcIngestionTests"].execute
  Rake::Task["rcLeaSamtTests"].execute
  Rake::Task["rcAccountRequestTests"].execute
  Rake::Task["rcAppApprovalTests"].execute
  Rake::Task["rcCheckStampers"].execute
  Rake::Task["rcDashboardTests"].execute
  Rake::Task["rcCleanUpTests"].execute
  Rake::Task["rcDeleteLDAPUsers"].execute

  displayFailureReport()
  if $SUCCESS
    puts "Completed All Tests"
  else
    raise "Tests have failed"
  end
end

############################################################
# Check Stamper Logs
############################################################

private

def check_stamper_log(line = nil)
  edorg_stamper_log = EDORG_LOG
  teacher_stamper_log = TEACHER_LOG
  edorg_stamper_finished = false
  teacher_stamper_finished = false
  seconds_to_wait = STAMPER_WAIT
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
