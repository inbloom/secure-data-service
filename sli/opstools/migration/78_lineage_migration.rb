#!/usr/bin/env ruby

# Move Schools.edOrgs fields from Student Collection to EducationOrganization Collection

# Run this script with no arguments to see usage:
#
#   ruby 78_lineage_migration.rb
#
# You can give one or more tenant IDs whose databases will be migrated, or "--all"
# for all tenants.  Also you can optionally give the MongoDB host/port.

require 'mongo'

############################################################
# Migration Script
############################################################

@conn = nil

SCHOOL_FIELD = "schools"
EDORGS_FIELD = "edOrgs"
METADATA_FIELD = "metaData"
PARENT_FIELD = "parentEducationAgencyReference"

STUDENT_COLLECTION = "student"
EDORG_COLLECTION = "educationOrganization"

def updateStudentEdOrgs(dbName)
   @db = @conn[dbName]
   @student_collection = @db[STUDENT_COLLECTION]
   @student_collection.find.each do |row|
        schools = row[SCHOOL_FIELD]
        if(schools)
          schools.each do |school|
              edOrgs = school[EDORGS_FIELD]
              school.delete(EDORGS_FIELD)
          end
          @student_collection.update({"_id" => row["_id"]},{"$set" => {SCHOOL_FIELD => schools}})
        end
   end
end

def updateSchoolLineage(school, dbName)
   @db = @conn[dbName]
   @edOrg_collection = @db[EDORG_COLLECTION]

   parents = Array.new
   parentId = school["_id"]

   while  parentId
      @edOrg = @edOrg_collection.find_one({"_id" => parentId})
      if @edOrg != nil
        parents.push(parentId)
        parentId = @edOrg["body"][PARENT_FIELD]
      else
        parentId = nil
      end
   end

   @edOrg_collection.update({"_id" => school["_id"]}, {"$set" => {"metaData.edOrgs" => parents}})
end

# Calculate and update Lineage for all schools
def updateAllSchoolLineage(dbName)
   @db = @conn[dbName]
   @edOrg_collection = @db[EDORG_COLLECTION]
   @school_collection = @edOrg_collection.find({"type" => "school"})

   @school_collection.find.each do |row|
       puts updateSchoolLineage(row, dbName)
   end
end

# Update Student and EducationOrganization Collection
def updateDB(dbName)
    updateAllSchoolLineage(dbName)
    updateStudentEdOrgs(dbName)
    puts "Creating index on " + EDORG_COLLECTION
    @conn[dbName][EDORG_COLLECTION].create_index({"metaData.edOrgs" => Mongo::ASCENDING}, {:sparse => true })
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
    puts "| To migrate on student and educationOrganization collection, give argument(s) as follows:\n"
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
    puts "Migrating data for tenant: " + tenant + ", database " + dbname
    updateDB(dbname)
  end

  puts "    " + "All done."
end

# Run it
main(ARGV)
