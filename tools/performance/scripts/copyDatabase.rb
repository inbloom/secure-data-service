require File.expand_path("../databaseUtils.rb", __FILE__)

if (ARGV.length < 3)
  raise "Missing host and database information"
end

host = ARGV[0]
sourceDb = ARGV[1]
targetDb = ARGV[2]

copyDatabase(host, sourceDb, targetDb)

