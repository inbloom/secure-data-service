=begin

Copyright 2012-2013 inBloom, Inc. and its affiliates.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

=end


require 'rest-client'
require 'json'
require 'builder'
require 'rexml/document'
require 'uri'
include REXML
require_relative '../../../../utils/sli_utils.rb'
require_relative '../../../utils/api_utils.rb'
require_relative '../../../../apiV1/integration/step_definitions/app_oauth.rb'

def findStaffId(staffLogin)
  @conn = Mongo::Connection.new(PropLoader.getProps['ingestion_db'], PropLoader.getProps['ingestion_db_port']) if !defined? @conn
  @db = @conn.db(convertTenantIdToDbName('Midgar'))
  @coll = @db['staff']
  staffId = @coll.find_one('body.staffUniqueStateId'=>staffLogin)['_id']
end

def findEdOrgId(stateOrganizationId)
  @conn = Mongo::Connection.new(PropLoader.getProps['ingestion_db'], PropLoader.getProps['ingestion_db_port']) if !defined? @conn
  @db = @conn.db(convertTenantIdToDbName('Midgar'))
  @coll = @db['educationOrganization']
  edOrgIdId = @coll.find_one('body.stateOrganizationId'=>stateOrganizationId)['_id']
end

When(/^I create a LEA named "([^"]*)" with parent IL$/) do |lea|
  edOrg = <<-jsonDelimiter
    {
        "address":[
           {
              "addressType":"Physical",
              "city":"Gotham City",
              "nameOfCounty":"Wake",
              "postalCode":"27500",
              "stateAbbreviation":"NC",
              "streetNumberName":"118 Main St."
           }
        ],
        "nameOfInstitution":"Test School",
        "organizationCategories":[
           "School"
        ],
        "stateOrganizationId":"-----PLACEHOLDER-------"
    }
  jsonDelimiter
  edOrgBody = JSON.parse(edOrg)
  edOrgBody['stateOrganizationId']            = lea
  edOrgBody['organizationCategories']         = ['Local Education Agency']
  edOrgBody['parentEducationAgencyReference'] = [findEdOrgId('IL')]
  restHttpPost('/v1/educationOrganizations', edOrgBody.to_json, 'application/vnd.slc+json')
  location = @res.raw_headers['location'][0]
  restHttpGetAbs(location, 'application/vnd.slc+json')
  $createdEntities[lea] = JSON.parse @res
end

When(/^I create a School named "([^"]*)" with parents "([^"]*)"$/) do |school, parentLEAs|
  edOrg = <<-jsonDelimiter
    {
        "address":[
           {
              "addressType":"Physical",
              "city":"Gotham City",
              "nameOfCounty":"Wake",
              "postalCode":"27500",
              "stateAbbreviation":"NC",
              "streetNumberName":"118 Main St."
           }
        ],
        "nameOfInstitution":"Test School",
        "organizationCategories":[
           "School"
        ],
        "stateOrganizationId":"-----PLACEHOLDER-------"
    }
  jsonDelimiter
  edOrgBody = JSON.parse(edOrg)
  parentLeaIds = parentLEAs.split(/,/).map {|x| x.strip!; $createdEntities[x]['id']}
  edOrgBody['stateOrganizationId']            = school
  edOrgBody['organizationCategories']         = ['School']
  edOrgBody['parentEducationAgencyReference'] = parentLeaIds
  restHttpPost('/v1/educationOrganizations', edOrgBody.to_json, 'application/vnd.slc+json')
  location = @res.raw_headers['location'][0]
  restHttpGetAbs(location, 'application/vnd.slc+json')
  $createdEntities[school] = JSON.parse @res
end

When(/^I create a Student named "([^"]*)"$/) do |studentId|
  student = <<-jsonDelimiter
  {
		"hispanicLatinoEthnicity" : false,
		"studentUniqueStateId" : "800000015",
		"name" : {
			"generationCodeSuffix" : "Jr",
			"lastSurname" : "Iskra",
			"firstName" : "Damon"
		},
		"birthData" : {
			"birthDate" : "1996-01-17"
		}
	}
  jsonDelimiter
  student = JSON.parse(student)
  student['studentUniqueStateId'] = studentId
  restHttpPost('/v1/students', student.to_json, 'application/vnd.slc+json')
  location = @res.raw_headers['location'][0]
  restHttpGetAbs(location, 'application/vnd.slc+json')
  $createdEntities[studentId] = JSON.parse @res
end

When(/^I create a StudentSchoolAssociation between "([^"]*)" and "([^"]*)"$/) do |studentId, schoolId|
  studentSchoolAssociation = <<-jsonDelimiter
  {
		"studentId" : "-----PLACEHOLDER-------",
		"schoolId" :  "-----PLACEHOLDER-------",
		"entryDate" : "2011-09-01",
		"entryGradeLevel" : "Eighth grade"
	}
  jsonDelimiter
  studentSchoolAssociation                         = JSON.parse(studentSchoolAssociation)
  studentSchoolAssociation['studentId']            = $createdEntities[studentId]['id']
  studentSchoolAssociation['schoolId' ]            = $createdEntities[schoolId]['id']
  restHttpPost('/v1/studentSchoolAssociations', studentSchoolAssociation.to_json, 'application/vnd.slc+json')
end

When(/^I try to get "([^"]*)" and get a response code "([^"]*)"$/) do |id, respCode|
  entityLink = $createdEntities[id]['links'].select{|link| link['rel'] == 'self'}[0]['href']
  restHttpGetAbs(entityLink)
  assert(@res.code.to_s == respCode, "Got [#{@res.code}] while fetching #{entityLink}. Expected [#{respCode}]")
end

When(/^I create a StaffEducationOrganizationAssociation  between "([^"]*)" and "([^"]*)"$/) do |staff, edOrgId|
  staffEducationOrganizationAssociation = <<-jsonDelimiter
  {
		"staffClassification" : "Leader",
		"educationOrganizationReference" : "-----PLACEHOLDER-------",
		"positionTitle" : "Leader",
		"staffReference" : "-----PLACEHOLDER-------",
		"endDate" : "2023-08-13",
		"beginDate" : "1967-08-13"
	}
  jsonDelimiter
  staffEducationOrganizationAssociation                                        = JSON.parse(staffEducationOrganizationAssociation)
  staffEducationOrganizationAssociation['staffReference']                      = findStaffId(staff)
  staffEducationOrganizationAssociation['educationOrganizationReference' ]     = $createdEntities[edOrgId]['id']
  restHttpPost('/v1/staffEducationOrgAssignmentAssociations', staffEducationOrganizationAssociation.to_json, 'application/vnd.slc+json')
end

When(/^I authorize all apps to access "([^"]*)"$/) do |schoolId|
  school = $createdEntities[schoolId]['id']
  puts "Authorizing all edorgs to access #{school}"
  @conn = Mongo::Connection.new(PropLoader.getProps['ingestion_db'], PropLoader.getProps['ingestion_db_port']) if !defined? @conn
  @db = @conn.db(convertTenantIdToDbName('Midgar'))
  @coll = @db['applicationAuthorization']
  updateResult = @coll.update({}, {'$push' => {'body.edorgs' => school}},:upsert => true, :safe => true, :multi=> true )
  assert(updateResult['ok'] == 1 && updateResult['err'] == nil, 'Authorizing update failed!')
end

When(/^I create a Cohort "([^"]*)" for "([^"]*)" and associate "([^"]*)" and "([^"]*)" with it$/) do |cohortName, schoolName, studentName, adminName|
    cohort = <<-jsonDelimiter
    {
		  "cohortType" : "Academic Intervention",
		  "cohortScope" : "District",
		  "educationOrgId" : "-----PLACEHOLDER-------",
		  "cohortIdentifier" : "IL-SUN-COH-PAST1"
	  }
    jsonDelimiter
    cohort = JSON.parse(cohort)
    cohort['educationOrgId']   =  $createdEntities[schoolName]['id']
    cohort['cohortIdentifier'] =  cohortName
    restHttpPost('/v1/cohorts', cohort.to_json, 'application/vnd.slc+json')
    assert(@res.code == 201, 'Could not creat cohorts!')
    location = @res.raw_headers['location'][0]
    restHttpGetAbs(location, 'application/vnd.slc+json')
    $createdEntities[cohortName] = JSON.parse @res

    sca = <<-jsonDelimiter
    {
		  "staffId" : "-----PLACEHOLDER-------",
		  "cohortId" : "Mathematics",
		  "beginDate" : "2010-09-01",
		  "endDate" : "2031-09-01",
		  "studentRecordAccess" : true
	  }
    jsonDelimiter
    sca = JSON.parse(sca)
    sca['staffId'] = findStaffId(adminName)
    sca['cohortId'] = $createdEntities[cohortName]['id']
    restHttpPost('/v1/staffCohortAssociations', sca.to_json, 'application/vnd.slc+json')
    assert(@res.code == 201, 'Could not creat staffCohortAssociations!')

    sca = <<-jsonDelimiter
    {
		    "studentId" : "Mathematics",
		    "cohortId" : "Mathematics",
		    "beginDate" : "2010-09-01",
		    "endDate" : "2031-09-01"
	  }
    jsonDelimiter
    sca = JSON.parse(sca)
    sca['studentId'] = $createdEntities[studentName]['id']
    sca['cohortId'] = $createdEntities[cohortName]['id']
    restHttpPost('/v1/studentCohortAssociations', sca.to_json, 'application/vnd.slc+json')
    assert(@res.code == 201, 'Could not creat studentCohortAssociations!')
end

When(/^I try to update "([^"]*)" name to "([^"]*)"$/) do |student, newName|
  student = $createdEntities[student]
  student['name']['firstName'] = newName
  student.delete('links')
  restHttpPut("/v1/students/#{student['id']}", student.to_json, 'application/vnd.slc+json')
  puts(@res.code,@res.body,@res.raw_headers)
  assert(body['name']['firstName'] == newName, 'PUT request response does not have modified name!')
end