############################################################
# Versioning tests start
############################################################

desc "Run Schema Versioning Tests"
task :versioningTests do
  runTests("test/features/versioning/versioning.feature")
end