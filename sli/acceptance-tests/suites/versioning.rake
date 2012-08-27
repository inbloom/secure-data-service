############################################################
# versioning tests start
############################################################

task :versioningBeforeStart => [:realmInit] do
  runTests("test/features/versioning/versioningBeforeStart.feature")
end

task :versioningAfterStart => [:realmInit] do
  runTests("test/features/versioning/versioningAfterStart.feature")
end

task :versioningAfterRestart => [:realmInit] do
  runTests("test/features/versioning/versioningAfterRestart.feature")
end
