#!/usr/bin/env ruby

# Move Schools.edOrgs fields from Student Collection to EducationOrganization Collection

# Usage: SchoolLineageMigration.rb [<database>]. 

# With no database, lists all the databases.
# With a database argument, shows collections in that database

require 'mongo'

############################################################
# Migration Script
############################################################

HOST = "localhost"
INGESTION_DB_PORT = "27017"

SCHOOL_FIELD = "schools"
EDORGS_FIELD = "edOrgs"
METADATA_FIELD = "metaData"
PARENT_FIELD = "parentEducationAgencyReference"

def updateStudentEdOrgs(dbName, conn)
   @db = conn[dbName]
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

def updateSchoolLineage(school, dbName, conn)
   @db = conn[dbName]
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
def updateAllSchoolLineage(dbName, conn)
   @db = conn[dbName]
   @entity_collection = @db["educationOrganization"]
   @school_collection = @entity_collection.find({"type" => "school"})

   @school_collection.find.each do |row|
       puts "---------------------------------------"
       puts dbName
       puts row 
       puts "---------------------------------------"       
       puts updateSchoolLineage(row, dbName, conn)     
   end
end

# Update Student and EducationOrganization Collection
def updateDB(dbName, conn)
  if dbCheck(dbName, conn) == true
    updateAllSchoolLineage(dbName, conn)
    updateStudentEdOrgs(dbName, conn)
  else
    puts "    " + " db [" + dbName + "]  does not exist, please check"
    exit
  end
end

# Check if database exist
def dbCheck(dbName, conn)
    names =  conn.database_names
    return names.include? dbName
end

# Main driver
def main(argv)
    @conn = Mongo::Connection.new(HOST, INGESTION_DB_PORT)
	dbNames = argv
	if 0==dbNames.length
    puts "--------------------------------------------------------------------------------------------"
    puts "| Databases on server '" + HOST + "':"
    @conn.database_names.each do |name|
      puts "|    " + name
    end
      puts "--------------------------------------------------------------------------------------------"
      puts "| To migrate on student and educationOrganization collection, give argument(s) as follows:\n"
      puts "|     all                                     Migrate all databases\n"
      puts "|     [<dbname1>,<dbname2>...]                Migrate only database in the list\n"
      puts "--------------------------------------------------------------------------------------------"
      return
    end

  if 1==dbNames.length
    if dbNames[0].casecmp("all") == 0
      @conn.database_names.each do |name|
        updateDB(name, @conn)
      end
      puts "    " + "All done."
      return
    end
  end

	for dbName in dbNames
    updateDB(dbName, @conn)
  end
  puts "    " + "All done."
end

# Run it
main(ARGV)