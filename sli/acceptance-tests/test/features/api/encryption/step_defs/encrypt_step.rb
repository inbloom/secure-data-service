require 'json'
require 'mongo'
require 'rest-client'
require_relative '../../../utils/sli_utils.rb'

############################################################
# ENVIRONMENT CONFIGURATION
############################################################
API_DB = PropLoader.getProps['DB_HOST']
API_DB_NAME = PropLoader.getProps['api_database_name']

############################################################
# TRANSFORM
############################################################
Transform /^<(.+)>$/ do |template|
  id = template
  id = @newId if template == "'newly created student' ID"
  id = "d431ba09-c8ac-4139-beac-be28220633e6" if template == "'Krypton Middle School' ID"
  id
end

# transform /path/<Place Holder Id>
Transform /^(\/[\w-]+\/)(<.+>)$/ do |uri, template|
  uri + Transform(template)
end

# transform /path/<Place Holder Id>/targets
Transform /^(\/[\w-]+\/)(<.+>)\/targets$/ do |uri, template|
  Transform(uri + template) + "/targets"
end

############################################################
# GIVEN
############################################################
Given /^the "([^\"]*)" collection is empty$/ do |collection|
  conn = Mongo::Connection.new(API_DB)
  db = conn[API_DB_NAME]
  col = db.collection(collection)
  col.remove({})
end

Given /^no record exists in "([^\"]*)" with a "([^\"]*)" of "([^\"]*)"$/ do |collection, field, value|
  conn = Mongo::Connection.new(API_DB)
  db = conn[API_DB_NAME]
  col = db.collection(collection)
  resp = col.remove({field => value});
  col.find({field => value}).count().should == 0
end

Given /^parameter "([^\"]*)" is "([^\"]*)"$/ do |param, value|
  step %Q{parameter "#{param}" "=" "#{value}"}
end

Given /^parameter "([^\"]*)" is not "([^\"]*)"$/ do |param, value|
  step %Q{parameter "#{param}" "!=" "#{value}"}
end

Given /^parameter "([^"]*)" less than "([^"]*)"$/ do |param, value|
  step %Q{parameter "#{param}" "<" "#{value}"}
end

Given /^parameter "([^"]*)" greater than "([^"]*)"$/ do |param, value|
  step %Q{parameter "#{param}" ">" "#{value}"}
end

Given /^parameter "([^"]*)" greater than or equal to "([^"]*)"$/ do |param, value|
  step %Q{parameter "#{param}" ">=" "#{value}"}
end

Given /^parameter "([^"]*)" less than or equal to "([^"]*)"$/ do |param, value|
  step %Q{parameter "#{param}" "<=" "#{value}"}
end

Given /^parameter "([^"]*)" matches via regex "([^"]*)"$/ do |param, value|
  step %Q{parameter "#{param}" "=~" "#{value}"}
end

Given /^parameter "([^\"]*)" "([^\"]*)" "([^\"]*)"$/ do |param, op, value|
  if !defined? @queryParams
    @queryParams = []
  end
  @queryParams.delete_if do |entry|
    entry.start_with? param
  end
  @queryParams << URI.escape("#{param}#{op}#{value}")
end

Given /^student Rhonda Delagio exists$/ do
  steps %Q{
    Given no record exists in "student" with a "body.studentUniqueStateId" of "530425896"
    Given a fully populated student record
    When I navigate to POST "/students/"
    Then I should receive a return code of 201
    Then I should receive an ID for the newly created student
  }
end

Given /^Ronda Delagio is associated with "([^\"]*)"\.$/ do |school_id|
  @rhonda = @newId
  record = %Q{
    {
      "studentId" : "#{@rhonda}",
      "schoolId" : "#{school_id}",
      "entryGradeLevel" : "First grade"
    }
  }
  @data = JSON.parse(record)
  steps %Q{
    When I navigate to POST "/student-school-associations/"
    Then I should receive a return code of 201
    Then I should receive an ID for the newly created association
  }
  @rhondaAssoc = @newId
end

Given /^a fully populated student record$/ do
  record = <<-EOF
  {
  		"loginId" : "rsd",
  		"sex" : "Female",
  		"studentCharacteristics" : [
  			{
  				"beginDate" : "2000-10-01",
  				"characteristic" : "Parent in Military"
  			}
  		],
  		"disabilities" : [
  			{
  				"disability" : "Other Health Impairment"
  			}
  		],
  		"hispanicLatinoEthnicity" : false,
  		"economicDisadvantaged" : false,
  		"cohortYears" : [
  			{
  				"schoolYear" : "2010-2011",
  				"cohortYearType" : "First grade"
  			}
  		],
  		"section504Disabilities" : [
  			"Medical Condition"
  		],
  		"oldEthnicity" : "Black, Not Of Hispanic Origin",
  		"race" : [
  			"Black - African American"
  		],
  		"programParticipations" : [
  			{
  				"program" : "Section 504 Placement"
  			}
  		],
  		"languages" : [
  			"English"
  		],
  		"studentUniqueStateId" : "530425896",
  		"name" : {
  			"middleName" : "Shannon",
  			"generationCodeSuffix" : "Jr",
  			"lastSurname" : "Delgado",
  			"firstName" : "Rhonda"
  		},
  		"birthData" : {
  			"birthDate" : "2006-07-02"
  		},
  		"otherName" : [
  			{
  				"middleName" : "Wren",
  				"lastSurname" : "Einstein",
  				"firstName" : "Julie",
  				"otherNameType" : "Nickname"
  			}
  		],
  		"studentIndicators" : [
  			{
  				"indicator" : "At risk",
  				"indicatorName" : "At risk"
  			}
  		],
  		"homeLanguages" : [
  			"English"
  		],
  		"learningStyles" : {
  			"visualLearning" : 33,
  			"tactileLearning" : 33,
  			"auditoryLearning" : 33
  		},
  		"limitedEnglishProficiency" : "NotLimited",
  		"studentIdentificationCode" : [ ],
  		"address" : [
  			{
  				"postalCode" : "27701",
  				"streetNumberName" : "1234 Shaggy",
  				"stateAbbreviation" : "NC",
  				"city" : "Durham"
  			}
  		],
  		"electronicMail" : [
  			{
  				"emailAddress" : "rsd@summer.nc.edu"
  			}
  		],
  		"schoolFoodServicesEligibility" : "Reduced price",
  		"displacementStatus" : "Slightly to the right",
  		"telephone" : [
  			{
  				"telephoneNumber" : "919-555-8765"
  			}
  		]
  	}
    EOF
    
    @data = JSON.parse(record)
end

############################################################
# WHEN
############################################################
When /^I navigate to POST "([^\"]*)"$/ do |arg1|
  data = prepareData(@format, @data)
  restHttpPost(arg1, data)
  assert(@res != nil, "Response from rest-client POST is nil")
end

############################################################
# THEN
############################################################
Then /^the "([^\"]*)" should be "([^\"]*)"$/ do |arg1, arg2|
  if(arg1 == 'birthDate')
    assert(@result['birthData'][arg1] == arg2, "Expected data incorrect: Expected #{arg2} but got #{@result[arg1]}")
  else
    assert(@result[arg1].to_s == arg2, "Expected data incorrect: Expected #{arg2} but got #{@result[arg1]}")
  end
  
end

Then /^the "([^\"]*)" should be "([^\"]*)" "([^\"]*)" "([^\"]*)"$/ do |arg1, arg2, arg3, arg4|
  assert(@result[arg1]['firstName'] == arg2, "Expected data incorrect")
  assert(@result[arg1]['middleName'] == arg3, "Expected data incorrect")
  assert(@result[arg1]['lastSurname'] == arg4, "Expected data incorrect")
end

Then /^I find a mongo record in "([^\"]*)" with "([^\"]*)" equal to "([^\"]*)"$/ do |collection, searchTerm, value|
  conn = Mongo::Connection.new(API_DB)
  db = conn[API_DB_NAME]
  col = db.collection(collection)
  
  @record = col.find_one({searchTerm => value})
  @record.should_not == nil
  conn.close
end

Then /^the field "([^\"]*)" has value "([^\"]*)"$/ do |field, value|
  object = @record
  field.split('.').each do |f|
    if /(.+)\[(\d+)\]/.match f
      f = $1
      i = $2.to_i
      object[f].should be_a Array
      object[f][i].should_not == nil
      object = object[f][i]
    else
      object[f].should_not == nil
      object = object[f]
    end
  end
  object.should == value
end

Then /^the field "([^\"]*)" with value "([^\"]*)" is encrypted$/ do |field, value| 
  object = @record
  field.split('.').each do |f|
    if /(.+)\[(\d+)\]/.match f
      f = $1
      i = $2.to_i
      object[f].should be_a Array
      object[f][i].should_not == nil
      object = object[f][i]
    else
      object[f].should_not == nil
      object = object[f]
    end
  end
  object.should_not == value
end

Then /^all students should have "([^\"]*)" equal to "([^\"]*)"$/ do |field, value|
  @result.should be_a Array
  @result.each do |entity|
    object = entity
    field.split(".").each do |f|
      object[f].should_not == nil
      object = object[f]
    end
    object.should == value
  end
end

Then /^no student should have "([^"]*)" equal to "([^"]*)"$/ do |field, value|
  @result.should be_a Array
  @result.each do |entity|
    object = entity
    field.split(".").each do |f|
      object[f].should_not == nil
      object = object[f]
    end
    object.should_not == value
  end
end