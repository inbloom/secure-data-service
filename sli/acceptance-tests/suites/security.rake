############################################################
# Onboarding tests start
############################################################
desc "Run Onboarding Tests"
task :onboardingTests => [:realmInit] do
  Rake::Task["importSandboxData"].execute
  runTests("test/features/onboarding")
end

desc "Run Sandbox Developer Provisioning acceptance tests"
task :provisioningTests => [:importSandboxData, :realmInit] do
    runTests("test/features/sandbox/Provision/provisioning.feature")
end
############################################################
# Onboarding tests end
############################################################

