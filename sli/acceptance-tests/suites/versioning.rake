############################################################
# Versioning tests start
############################################################

desc "Run Schema Versioning Tests"
task :versioningTests => [:realmInit] do
  	Rake::Task["importMigrationData"].execute
  	runTests("test/features/versioning/versioning.feature")
end

desc "Run Schema Migration Tests"
task :migrationTests => [:realmInit] do
  	Rake::Task["importMigrationData"].execute
  	runTests("test/features/versioning/migration.feature")
end