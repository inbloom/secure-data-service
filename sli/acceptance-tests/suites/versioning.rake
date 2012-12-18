############################################################
# Versioning tests start
############################################################

desc "Run Schema Versioning Tests"
task :versioningTests => [:realmInit] do
  	Rake::Task["importSandboxData"].execute
  	runTests("test/features/versioning/versioning.feature")
end