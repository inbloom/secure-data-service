#!/usr/bin/env ruby

# Update "type" field in educationOrganization documents

# Run this script with no arguments to see usage:
#
#   migrateEdOrgType.rb
#
# You can give one or more tenant IDs whose databases will be migrated, or "--all"
# for all tenants.  Also you can optionally give the MongoDB host/port.

require 'mongo'

############################################################
# Migration Script
############################################################

@conn = nil

EDORG_COLLECTION = "educationOrganization"

TYPE_FIELD = "type"
BODY_FIELD = "body"
ORGCAT_FIELD = "organizationCategories"

# Update field "type" in collection "educationOrganization" ...
# From these values ...
TYPE_VALUE_SCHOOL = "school"
TYPE_VALUE_LEA = "localEducationAgency"
TYPE_VALUE_SEA = "stateEducationAgency"
# ... to this generic value
TYPE_VALUE_EDORG = "educationOrganization"

# Check that above legacy types have a corresponding type
# indicator in the field BODY_FIELD.ORGCAT_FIELD
ORGCAT_VALUE_SCHOOL = "School"
ORGCAT_VALUE_LEA = "Local Education Agency"
ORGCAT_VALUE_SEA = "State Education Agency"


# Check data: make sure various types are known and have corresponding
# organization category set.
def checkData(dbName)
  @db = @conn[dbName]
  @edorg_collection = @db[EDORG_COLLECTION]
  @edorg_collection.find.each do |row|
    type = row[TYPE_FIELD]
    body = row[BODY_FIELD]
    orgcats = body[ORGCAT_FIELD]

    if type == TYPE_VALUE_SCHOOL and not orgcats.include?(ORGCAT_VALUE_SCHOOL)
      puts("Error: missing school org cat")
      return false
    end
    if type == TYPE_VALUE_LEA and not orgcats.include?(ORGCAT_VALUE_LEA)
      puts("Error: missing LEA org cat")
      return false
    end
    if type == TYPE_VALUE_SEA and not orgcats.include?(ORGCAT_VALUE_SEA)
      puts("Error: missing SEA org cat")
      return false
    end
  end
  return true
end

# Update entity type to the generic value
def updateEdOrgs(dbName)
  @db = @conn[dbName]
  @edorg_collection = @db[EDORG_COLLECTION]
  nupdated = 0
  @edorg_collection.find.each do |row|
    if row[TYPE_FIELD] != TYPE_VALUE_EDORG
      @edorg_collection.update({"_id" => row["_id"]},{"$set" => {TYPE_FIELD => TYPE_VALUE_EDORG}})
      nupdated += 1
    end
  end
  puts "Updated " + nupdated.to_s + " document(s)"
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
    puts "| To migrate educationOrganization collection, give argument(s) as follows:\n"
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

  # First check the data
  for tenant in tenants
    dbname = tenant2db[tenant]
    puts "Checking data for tenant: " + tenant + ", database " + dbname
    if not checkData(dbname)
      puts "Errors in data, aborting entire update"
      return false
    end
  end

  # Now migrate it
  for tenant in tenants
    dbname = tenant2db[tenant]
    puts "Migrating data for tenant: " + tenant + ", database " + dbname
    updateEdOrgs(dbname)
  end

  puts "    " + "All done."
end

# Run it
main(ARGV)
