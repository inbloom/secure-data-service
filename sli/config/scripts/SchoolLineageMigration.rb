#!/usr/bin/env ruby

# Move Schools.edOrgs fields from Student Collection to EducationOrganization Collection

# Usage: SchoolLineageMigration.rb [<database>]. 

# With no database, lists all the databases.
# With a database argument, shows collections in that database

require 'mongo'

############################################################
# Migration Script
############################################################

@conn = nil
host = 'localhost'
port = '27017'

SCHOOL_FIELD = "schools"
EDORGS_FIELD = "edOrgs"
METADATA_FIELD = "metaData"
PARENT_FIELD = "parentEducationAgencyReference"


def updateStudentEdOrgs(dbName)
   @db = @conn[dbName]
   @student_collection = @db["student"]
   @student_collection.find.each do |row|
        schools = Array.new
        schools = row[SCHOOL_FIELD]
        if(schools)
          schools.each do |school|
              edOrgs = Hash.new
              edOrgs = school[EDORGS_FIELD]
              school.delete(EDORGS_FIELD)
          end
          @student_collection.update({"_id" => row["_id"]},{"$set" => {SCHOOL_FIELD => schools}})
        end
   end
end

def updateSchoolLineage(school, dbName)
   @db = @conn[dbName]
   @entity_collection = @db["educationOrganization"]

   parents = Array.new
   parentId = school["_id"]

   while  parentId
      @entity = @entity_collection.find_one({"_id" => parentId})
      if @entity != nil
        parents.push(parentId)
        parentId = @entity["body"][PARENT_FIELD]
      else
        parentId = nil
      end
   end

   @entity_collection.update({"_id" => school["_id"]}, {"$set" => {"metaData.edOrgs" => parents}})
end

# Calculate and update Lineage for all schools
def updateAllSchoolLineage(dbName)
   @db = @conn[dbName]
   @entity_collection = @db["educationOrganization"]
   @school_collection = @entity_collection.find({"type" => "school"})

   @school_collection.find.each do |row|
       puts updateSchoolLineage(row, dbName)
   end
end

# Update Student and EducationOrganization Collection
def updateDB(dbName)
  if dbCheck(dbName) == true
    updateAllSchoolLineage(dbName)
    updateStudentEdOrgs(dbName)
  else
    puts "    " + " db [" + dbName + "]  does not exist, please check"
    exit
  end
end

# Check if database exist
def dbCheck(dbName)
    names =  @conn.database_names
    return names.include? dbName
end

# Example: this_script.rb --all host:myhost.com port:12345
#          this_script.rb host:myhost.com port:12345 db1 db2 db3
#
def parseArgs(argv)

  result = { "all" => false, "mongo_host" => "localhost", "mongo_port" => 27017, "dbNames" => [] }

  for arg in argv
    if arg == "--all"
      result["all"] = true
    elsif arg.include?(":")
      host_port = arg.split(':')
      result["mongo_host"] = host_port[0]
      result["mongo_port"] = host_port[1]
    else
      result["dbNames"] << arg
    end
  end
  return result
end

# Main driver
def main(argv)
  params = parseArgs(argv)

	dbNames = params["dbNames"]
  @conn = Mongo::Connection.new(params["mongo_host"], params["mongo_port"])

	if ( 0==dbNames.length and not params["all"] ) or ( dbNames.length >= 1 and params["all"] )
    puts "--------------------------------------------------------------------------------------------"
    puts "| Databases on server '" + params["mongo_host"] + "':"
    tenants = []
    @conn['sli']['tenant'].find({}).each do |tenant|
      tenants << tenant
    end
    for tenant in tenants
      puts "|    " + tenant['body']['tenantId'] + " " + tenant['body']['dbName']
    end
    puts "--------------------------------------------------------------------------------------------"
    puts "| To migrate on student and educationOrganization collection, give argument(s) as follows:\n"
    puts "|     --all                                     Migrate against all tenant dbs\n"
    puts "|     [<dbname1>,<dbname2>...]                  Migrate only database in the list\n"
    puts "|    myhost:myport                              hostname and port defaults to localhost:27017"
    puts "--------------------------------------------------------------------------------------------"
    return
  end


  if params["all"]
    @conn['sli']['tenant'].find({}).each do |tenant|
      tenants << tenant
    end
      for tenant in tenants
        updateDB(tenant['body']['dbName'])
      end
      puts "    " + "All done."
      return
  end

  for dbName in dbNames
    updateDB(dbName)
  end
  puts "    " + "All done."
end

# Run it
main(ARGV)