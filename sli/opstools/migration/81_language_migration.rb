#!/usr/bin/env ruby

# Updates format of language-related fields in student and studentAssessment collections
# Intended to be run against tenant data created before release 1.5.78
# If data is already in correct format, no changes will be made by the script

# Run this script with no arguments to see usage:
#
#   ruby 81_language_migration.rb
#
# You can give one or more tenant IDs whose databases will be migrated, or "--all"
# for all tenants.  Also you can optionally give the MongoDB host/port.

require 'mongo'

############################################################
# Migration Script
############################################################

@conn = nil

LANGUAGES_FIELD = "body.languages"
HOMELANGUAGES_FIELD = "body.homeLanguages"
ADMINLANGUAGE_FIELD = "body.administrationLanguage"
NEW_ADMINLANGUAGE_FIELD = "body.administrationLanguage.language"

STUDENT_COLLECTION = "student"
STUDENTASSESSMENT_COLLECTION = "studentAssessment"

# Updates languages and homeLanguages fields in student collection
def updateStudentLanguages(dbName)
   @db = @conn[dbName]
   @student_collection = @db[STUDENT_COLLECTION]
   # process languages field
   @student_collection.find({LANGUAGES_FIELD => {"$exists" => true}}).each do |row|
       languages = row["body"]["languages"]
       if(languages)
           newLanguages = []
           needsUpdate = false
           languages.each do |language|
               if language.class.name == "String"
                   newLanguages << {"language" => language}
                   needsUpdate = true
               else
                   newLanguages << language
               end
            end
            if needsUpdate
                @student_collection.update({"_id" => row["_id"]},{"$set" => {LANGUAGES_FIELD => newLanguages}})
            end
       end
   end
   # process homeLanguages field
   @student_collection.find({HOMELANGUAGES_FIELD => {"$exists" => true}}).each do |row|
       languages = row["body"]["homeLanguages"]
       if(languages)
           newLanguages = []
           needsUpdate = false
           languages.each do |language|
               if language.class.name == "String"
                   newLanguages << {"language" => language}
                   needsUpdate = true
               else
                   newLanguages << language
               end
           end
           if needsUpdate
               @student_collection.update({"_id" => row["_id"]},{"$set" => {HOMELANGUAGES_FIELD => newLanguages}})
           end
       end
   end
end

# Updates administrationLanguage field in studentAssessment collection
def updateStudentAssessmentLanguages(dbName)
   @db = @conn[dbName]
   @studentassessment_collection = @db[STUDENTASSESSMENT_COLLECTION]
   # process administrationLanguage field
   @studentassessment_collection.find({ADMINLANGUAGE_FIELD => {"$exists" => true}, NEW_ADMINLANGUAGE_FIELD => {"$exists" => false}}).each do |row|
       language = row["body"]["administrationLanguage"]
       if(language)
          if language.class.name == "String"
              @studentassessment_collection.update({"_id" => row["_id"]},{"$set" => {ADMINLANGUAGE_FIELD => {"language" => language}}})
          end
       end
   end
end

# Update Student and StudentAssessment Collections
def updateDB(dbName)
    updateStudentLanguages(dbName)
    updateStudentAssessmentLanguages(dbName)
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
    puts "| To migrate language data for student and studentAssessment collections, give argument(s) as follows:\n"
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
