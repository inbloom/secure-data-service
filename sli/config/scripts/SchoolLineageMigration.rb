#!/usr/bin/env ruby

# Move Schools.edOrgs fields from Student Collection to EducationOrganization Collection

# Usage: SchoolLineageMigration.rb [<database>]. 

# With no database, lists all the databases.
# With a database argument, shows collections in that database

require 'mongo'

############################################################
# Migration Script
############################################################

INGESTION_DB = "localhost"
INGESTION_DB_PORT = "27017"

@conn = Mongo::Connection.new(INGESTION_DB, INGESTION_DB_PORT)

def updateStudentEdOrgs(dbName)
   @db = @conn[dbName]
   @student_collection = @db["student"]
   @student_collection.find.each do |row|
        schools = Array.new
        schools = row["schools"]
        if(schools)
          schools.each do |school|
              edOrgs = Hash.new
              edOrgs = school["edOrgs"]
              school.delete("edOrgs")
          end
          @student_collection.update({"_id" => row["_id"]},{"$set" => {"schools" => schools}})
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
        parentId = @entity["body"]["parentEducationAgencyReference"]
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
    puts updateAllSchoolLineage(dbName)
    puts updateStudentEdOrgs(dbName)
end

# Check if database exist
def dbCheck(dbName)
    names =  @conn.database_names
    return names.include? dbName
end

# Main driver
def main(argv)
	dbNames = argv
	puts dbNames
	if 0==dbNames.length
    	@conn.database_names.each do |name|
        	if name == "admin" || name == "config" || name == "ingestion_batch_job" || name == "sli"
            	puts "<--- ignore " + name + "--->"
       		else
            	puts updateDB(name)
        	end
        end
        puts "<--- done. --->"
        return
    end

	for dbName in dbNames	
    	if dbCheck(dbName) == true
      		puts updateDB(dbName)
    	else
      		puts "<--- db Name does not exist, please check --->"
   		end
   	end   	
   	puts "<--- done. --->"
end

# Run it
main(ARGV)