require_relative '../../../ingestion/features/step_definitions/ingestion_steps.rb'
require_relative '../../../ingestion/features/step_definitions/clean_database.rb'

TRIGGER_SCRIPT = File.expand_path(PropLoader.getProps['bulk_extract_script'])
OUTPUT_DIRECTORY = PropLoader.getProps['bulk_extract_output_directory']
DATABASE_NAME = PropLoader.getProps['sli_database_name']
DATABASE_HOST = PropLoader.getProps['bulk_extract_db']
DATABASE_PORT = PropLoader.getProps['bulk_extract_port']

COLLECTIONS = [
	    "assessment",
        "attendance",
        "cohort",
        "course",
        "courseOffering",
        "courseTranscript",
        "disciplineIncident",
        "disciplineAction",
        "educationOrganization",
        "grade",
        "gradebookEntry",
        "gradingPeriod",
        "learningObjective",
        "learningStandard",
        "objectiveAssessment",
        "parent",
        "program",
        "reportCard",
        "school",
        "section",
        "session",
        "staff",
        "staffCohortAssociation",
        "staffEducationOrganizationAssociation",
        "staffProgramAssociation",
        "student",
        "studentAcademicRecord",
        "studentAssessment",
        "studentCohortAssociation",
        "studentCompetency",
        "studentCompetencyObjective",
        "studentDisciplineIncidentAssociation",
        "studentObjectiveAssessment",
        "studentProgramAssociation",
        "studentGradebookEntry",
        "studentSchoolAssociation",
        "studentSectionAssociation",
        "studentParentAssociation",
        "teacher",
        "teacherSchoolAssociation",
        "teacherSectionAssociation"]

require 'zip/zip'

############################################################
# Given
############################################################
Given /^I trigger a bulk extract$/ do

puts "Running: sh #{TRIGGER_SCRIPT}"
puts runShellCommand("sh #{TRIGGER_SCRIPT}")

end

############################################################
# When
############################################################

When /^I retrieve the path to the extract file for the tenant "(.*?)"$/ do |tenant|
  @conn = Mongo::Connection.new(DATABASE_HOST, DATABASE_PORT)
  @sliDb = @conn.db(DATABASE_NAME)
  @coll = @sliDb.collection("bulkExtractFiles")

  match =  @coll.find_one("body.tenantId" => tenant)

  assert(match !=nil, "Database was not updated with bulk extract file location")

  @filePath = match['body']['path']

end

When /^I verify that an extract zip file was created for the tenant "(.*?)"$/ do |tenant|

	puts "Extract FilePath: #{@filePath}"

	assert(File.exists?(@filePath), "Extract file was not created or Output Directory was not found")
end

When /^there is a metadata file in the extract$/ do
    extractFile = Zip::ZipFile.open(@filePath, Zip::ZipFile::CREATE)
    metadataFile = extractFile.find_entry("metadata.txt")
	 assert(metadataFile!=nil, "Cannot find metadata file in extract")
end

When /^the extract contains a file for each collection$/ do
    extractFile = Zip::ZipFile.open(@filePath, Zip::ZipFile::CREATE)

	COLLECTIONS.each do |collection|
	 collFile = extractFile.find_entry(collection + ".json")
	 assert(collFile!=nil, "Cannot find #{collection}.json file in extracts")
	end

	assert(extractFile.size==(COLLECTIONS.length+1), "Expected " + (COLLECTIONS.length+1).to_s + " extract files, Actual:" + extractFile.size.to_s)
end








