require 'rubygems'
require 'mongo'
require 'fileutils'

if __FILE__ == $0
  if ARGV.length == 0
    tenantToClear = "RCTestTenant"
  elsif ARGV.length == 1
    tenantToClear = ARGV[0]
  elsif ARGV.length > 1
    puts "Usage: prompt>ruby #{$0} tenantName"
    puts "Example: ruby #{$0} Midgar"
    exit(1)
  end
end

conn = Mongo::Connection.new('localhost')
db = conn.db('sli')

########corner cases 

#realm collection
realmColl = db.collection("realm")
realmColl.remove("body.tenantId" => tenantToClear)
puts "Num records in realm collection where tenantId = #{tenantToClear} is #{realmColl.find("body.tenantId" => tenantToClear).count}"

#tenant collection
tenantColl = db.collection("tenant")
tenantColl.remove("body.tenantId" => tenantToClear)
puts "Num records in tenant collection where tenantId = #{tenantToClear} is #{realmColl.find("body.tenantId" => tenantToClear).count}"

#application collection

#clean apps created by test

db.collection("application").remove("body.name" => "NotTheAppYoureLookingFor")
db.collection("application").remove("body.name" => "Schlemiel")

#must clean out edorg guids from the body.authorized_ed_orgs array whose tenantid is the one we are clearing
edorgsInTenant = db.collection("educationOrganization").find("metaData.tenantId" => tenantToClear)
edorgGuids = []
edorgsInTenant.each do |row|
  edorgGuids.push row["_id"]
end

db.collection("application").find.each do |app|
  authedEdorgs = app["body"]["authorized_ed_orgs"]
  if authedEdorgs == nil or authedEdorgs == []
    next
  end
  
  edorgGuids.each do |edorgid|
    if authedEdorgs.include? edorgid 
      authedEdorgs.delete edorgid
    end
  end
  #save authed edorgs back to application
  db.collection("application").update({"_id" => app['_id']}, {"$set" => {"body.authorized_ed_orgs" => authedEdorgs}})
end


#application authorization collection
appAuthColl = db.collection("applicationAuthorization")
appAuthColl.remove("metaData.tenantId" => tenantToClear)


collectionsToClearNormally = ["student",
                              "studentSchoolAssociation",
                              "course",
                              "educationOrganization",
                              "section",
                              "studentSectionAssociation",
                              "staff",
                              "staffEducationOrganizationAssociation",
                              "teacherSchoolAssociation",
                              "teacherSectionAssociation",
                              "session",
                              "assessment",
                              "studentAssessmentAssociation",
                              "gradebookEntry",
                              "studentTranscriptAssociation",
                              "studentGradebookEntry",
                              "parent",
                              "studentParentAssociation",
                              "attendance",
                              "program",
                              "studentAcademicRecord",
                              "grade",
                              "studentCompetency",
                              "reportCard",
                              "cohort",
                              "courseOffering",
                              "learningObjective",
                              "learningStandard",
                              "gradingPeriod",
                              "staffCohortAssociation",
                              "staffProgramAssociation",
                              "studentCohortAssociation",
                              "studentProgramAssociation",
                              "disciplineAction",
                              "disciplineIncident",
                              "studentDisciplineIncidentAssociation",
                              "custom_entities"]
                              
                              

collectionsToClearNormally.each do |coll|
  currentColl = db.collection(coll)
  currentColl.remove("metaData.tenantId" => tenantToClear)
  puts "Num records in #{coll} collection where tenantId = #{tenantToClear} is #{currentColl.find("metaData.tenantId" => tenantToClear).count}"
end




