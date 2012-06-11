require 'rubygems'
require 'mongo'
require 'fileutils'
require 'socket'
require 'net/sftp'

if ARGV.count<3
  puts "\e[31m ./profile-tools.rb host port exectuion-mode \e[0m"
  puts "\e[31mNeed to specify execution mode: [setup|export|parse]\e[0m"
  exit
end

profileCollSize = 1024000000
connection = Mongo::Connection.new(ARGV[0], ARGV[1])
db = connection.db("sli")

actionMode=ARGV[2]

if actionMode.to_s == "setup"
  puts "Refreshing system.profile collection"
  
  db.profiling_level = :off
  profileColl = db.drop_collection('system.profile')
  
  puts "system.profile collection size = " + profileCollSize.to_s
  coll = db.create_collection("system.profile",:capped => true, :size=>profileCollSize, :max=>profileCollSize)
  
  puts ""
  
  puts "Setting db profiling level to ALL (2)"
  db.profiling_level = :all
  puts "Current profiling level = " + db.profiling_level.to_s
  
elsif actionMode.to_s == "export"
  puts "Exporting system.profile collection"
  `#{"mongodump --db sli --collection system.profile"}`
  `#{"bsondump dump/sli/system.profile.bson > dump/sli/system.profile.json"}`
  
elsif actionMode.to_s == "parse"
  puts "Parsing and reviewing system profile"
  `#{"python parse-profile-dump.py dump/sli/system.profile.json ../indexes/sli_indexes.js"}`
  
else
  puts "Invalid execution mode is specified.  Supported execution modes are: [setup|export|parse]"
end