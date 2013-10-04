#!/usr/bin/env ruby

# Fix issues w/ 85 script migration

# Run this script with no arguments to see usage
#
# You can give one or more tenant IDs whose databases will be migrated, or "--all"
# for all tenants.  Also you can optionally give the MongoDB host/port.

require 'mongo'

############################################################
# Migration Script
############################################################

@conn = nil

BODY_FIELD = "body"
EDORG_COLLECTION = "educationOrganization"
PARENT_REF_FIELD = "parentEducationAgencyReference"
LEA_REF_FIELD = "localEducationAgencyReference"

# Fix up edOrg collection:
#      Flatted incorrectly nested array in PARENT_REF_FIELD
#      Put LEA_REF_FIELD value into PARENT_REF_FIELD array if not already there
#      Remove LEA_REF_FIELD value if it is there
#
def updateEdOrgs(dbName)
  @db = @conn[dbName]
  @edorg_collection = @db[EDORG_COLLECTION]
  nupdated = 0
  @edorg_collection.find.each do |row|
    did_update = 0
    body = row[BODY_FIELD]

    if body[PARENT_REF_FIELD].nil?
      newParent = []
    else
      newParent = body[PARENT_REF_FIELD].flatten(1)
    end

    # Add LEA ref to parents if not there
    if body.has_key?(LEA_REF_FIELD)
      leaRef = body[LEA_REF_FIELD]
      if !leaRef.nil?
        if !newParent.include?(leaRef)
          puts "Moving LEA ref '" + leaRef + "' to parent(s) for edorg '" + row["_id"] + "'"
          newParent.push(leaRef)
        else
          puts "Removing old " + LEA_REF_FIELD + " from edorg '" + row["_id"] + "'"
        end
      end
    end

    # If parent ref was completely absent and have no LEA to add, leave it be
    next if body[PARENT_REF_FIELD].nil? and newParent.empty?
    
    if body[PARENT_REF_FIELD] != newParent
      puts "Edorg '" + row["_id"] + "': replacing '" + body[PARENT_REF_FIELD].to_s() + "' with '" + newParent.to_s() + "'"
      @edorg_collection.update({"_id" => row["_id"]},{"$set" => {BODY_FIELD + "." + PARENT_REF_FIELD => newParent}})
      nupdated += 1
    end

    # Remove the lea ref field
    if body.has_key?(LEA_REF_FIELD)
      @edorg_collection.update({"_id" => row["_id"]},{"$unset" => {BODY_FIELD + "." + LEA_REF_FIELD => 1}})
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

  # Migrate tenant(s)
  for tenant in tenants
    dbname = tenant2db[tenant]
    puts "Migrating data for tenant: " + tenant + ", database " + dbname
    updateEdOrgs(dbname)
  end

  puts "    " + "All done."
end

# Run it
main(ARGV)
