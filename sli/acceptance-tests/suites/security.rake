############################################################
# Security tests start
############################################################
desc "Run Security Tests"
task :securityTests => [:realmInit] do
  Rake::Task["importSandboxData"].execute
  runTests("test/features/security")
end
############################################################
# Security tests end
############################################################


############################################################
# Onboarding tests start
############################################################
desc "Run Onboarding Tests"
task :onboardingTests => [:realmInit] do
  Rake::Task["importSandboxData"].execute
  runTests("test/features/onboarding")
end

desc "Run Ondboarding / Provisioning acceptance tests"
task :onboardingTests => [:provisioningTests] do
    displayFailureReport()
  if $SUCCESS
    puts "Completed All Tests"
  else
    raise "Tests have failed"
  end
end

desc "Run Sandbox Developer Provisioning acceptance tests"
task :provisioningTests => [:importSandboxData, :realmInit] do
    runTests("test/features/sandbox/Provision/provisioning.feature")
end
############################################################
# Onboarding tests end
############################################################

