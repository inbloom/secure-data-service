#!/usr/bin/env ruby

# Add index to targetEdOrgList field (body.targetEdOrgList) in securityEvent Collection

# Run this script with no arguments to see usage:
#
#   ruby 81_securityevent_index_migration.rb
#
# You can optionally give the MongoDB host/port.

require 'mongo'

############################################################
# Migration Script
############################################################

@conn = nil

SECEVT_COLLECTION = "securityEvent"

# Apply index to calendarDate Collection(s)
def updateDB(dbName)
    puts "Creating index on " + SECEVT_COLLECTION
    @conn[dbName][SECEVT_COLLECTION].create_index({"body.targetEdOrgList" => Mongo::ASCENDING}, {:sparse => false })
end


# Example: this_script.rb --all host:myhost.com port:12345
#          this_script.rb host:myhost.com port:12345 db1 db2 db3
#
def parseArgs(argv)

  result = { "mongo_host" => "localhost", "mongo_port" => 27017 }

  for arg in argv
    if arg.include?(":")
      host_port = arg.split(':')
      result["mongo_host"] = host_port[0]
      result["mongo_port"] = host_port[1]
    end
  end
  return result
end

# Main driver
def main(argv)
  params = parseArgs(argv)

  @conn = Mongo::Connection.new(params["mongo_host"], params["mongo_port"])

  dbname = "sli"
  puts "Adding index to database " + dbname
  updateDB(dbname)

  puts "    " + "All done."
end

# Run it
main(ARGV)
