#!/usr/bin/env ruby

# Add index to EdOrgReference field (body.educationOrganizationId) in calendarDate Collection

# Run this script with no arguments to see usage:
#
#   ruby 79_calendardate_index_migration.rb
#
# You can give one or more tenant IDs whose databases will be migrated, or "--all"
# for all tenants.  Also you can optionally give the MongoDB host/port.

require 'mongo'

############################################################
# Migration Script
############################################################

@conn = nil

CALDATE_COLLECTION = "calendarDate"

# Apply index to calendarDate Collection(s)
def updateDB(dbName)
    puts "Creating index on " + CALDATE_COLLECTION
    @conn[dbName][CALDATE_COLLECTION].create_index({"body.educationOrganizationId" => Mongo::ASCENDING}, {:sparse => false })
end


# Example: this_script.rb --all host:myhost.com port:12345
#          this_script.rb host:myhost.com port:12345 db1 db2 db3
#
def parseArgs(argv)

  result = { "all" => false, "mongo_host" => "localhost", "mongo_port" => 27017, "tenants" => [] }

  for arg in argv
    if arg == "--all"
      result["all"] = true
    elsif arg.include?(":")
      host_port = arg.split(':')
      result["mongo_host"] = host_port[0]
      result["mongo_port"] = host_port[1]
    else
      result["tenants"] << arg
    end
  end
  return result
end

# Main driver
def main(argv)
  params = parseArgs(argv)

	tenants = params["tenants"]
  @conn = Mongo::Connection.new(params["mongo_host"], params["mongo_port"])

  # Map tenant name to database by getting records in sli.tenant collection
  tenant2db = {}
  @conn['sli']['tenant'].find({}).each do |tenant|
    name = tenant['body']['tenantId']
    db = tenant['body']['dbName']
    tenant2db[name] = db
  end

  # Make sure any tenants given explicitly actually exist
  for tname in tenants
    if not tenant2db.has_key?(tname)
      puts "No such tenant: " + tname
      return
    end
  end

  # Make sure that either a list of tenants, or "-all" was given (and not both)
  if ( 0==tenants.length and not params["all"] ) or ( tenants.length >= 1 and params["all"] )
    puts "--------------------------------------------------------------------------------------------"
    puts "| Tenant databases on server '" + params["mongo_host"] + "':"
    tenant2db.each_pair do |tname, dbname|
      puts "|    " + tname + " " + dbname
    end

    puts "--------------------------------------------------------------------------------------------"
    puts "| To apply the index to the calendarDate collection, give argument(s) as follows:\n"
    puts "|\n"
    puts "|     --all                                     Migrate against all tenant dbs\n"
    puts "|       --OR--\n"
    puts "|     <tenant1> [<tenant2> ...]                 Migrate only database(s) for the given tenant(s)\n"
    puts "|\n"
    puts "|    myhost:myport                              Optional hostname and port defaults to localhost:27017"
    puts "--------------------------------------------------------------------------------------------"
    return
  end

  # Set tenants to complete list
  if params["all"]
    tenants = tenant2db.keys
  end

  for tenant in tenants
    dbname = tenant2db[tenant]
    puts "Adding index for tenant: " + tenant + ", database " + dbname
    updateDB(dbname)
  end

  puts "    " + "All done."
end

# Run it
main(ARGV)
