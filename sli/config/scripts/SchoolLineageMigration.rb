require 'mongo'

############################################################
# Lineage Migration Script
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
        schools.each do |school|
            edOrgs = Hash.new
            edOrgs = school["edOrgs"]
            school.delete("edOrgs")
        end
        @student_collection.update({"_id" => row["_id"]},{"$set" => {"schools" => schools}})
   end
   @student_collection.find.each do |row|
        puts row
   end
end

def updateSchoolLineage(school, dbName)
   @db = @conn[dbName]
   @entity_collection = @db["educationOrganization"]

   parents = Array.new
   parentId = school["_id"]

   while  parentId
      parents.push(parentId)
      @entity = @entity_collection.find({"_id" => parentId})
      @entity.find.each {|row| parentId = row["body"]["parentEducationAgencyReference"] }
   end
   puts "parents = "
   puts parents

   @entity_collection.update({"_id" => school["_id"]}, {"$set" => {"metaData.edOrgs" => parents}})
end

############################################################
# Function: calculate and update Lineage for all schools
# Input: dbName
# Output: N/A
############################################################
def updateAllSchoolLineage(dbName)
   @db = @conn[dbName]
   @entity_collection = @db["educationOrganization"]
   @school_collection = @entity_collection.find({"type" => "school"})
   @school_collection.find.each { |row| updateSchoolLineage(row, dbName)}
end

def updateAllDB
    puts "update All the DBs"

    @conn.database_names.each do |name|
        if name == "admin" || name == "config" || name == "ingestion_batch_job" || name == "sli"
            puts "<!--- ignore " + name + "---!>"
        else
            puts updateDB(name)
        end
    end
end

def updateDB(dbName)
    if dbName == "admin" || dbName == "config" || dbName == "ingestion_batch_job" || dbName == "sli"
       puts "<!--- ignore " + dbName + "---!>"
    else
       puts "Update " + dbName
       puts updateAllSchoolLineage(dbName)
       puts updateStudentEdOrgs(dbName)
    end
end

def dbCheck(dbName)
    names =  @conn.database_names
    puts names.length
    puts names.include? dbName
    return names.include? dbName
end

dbName = ARGV[0]
puts dbName
if dbName == "All"||dbName == "all"||dbName == nil
    puts updateAllDB
else
    if dbCheck(dbName) == true
      puts updateDB(dbName)
    else
      puts "<!--- db Name does not exist, please check ---!>"
    end
end