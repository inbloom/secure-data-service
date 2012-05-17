require 'json'
require 'mongo'
require 'rest-client'
require 'rexml/document'
include REXML
require_relative '../../../../utils/sli_utils.rb'
require_relative '../../../utils/api_utils.rb'

Transform /^<([^"]*)>$/ do |human_readable_id|
  id = "assessments"                                if human_readable_id == "ASSESSMENT URI"
  id = "teachers"                                   if human_readable_id == "TEACHER URI"
  id = "students"                                   if human_readable_id == "STUDENT URI"
  id = "sections"                                   if human_readable_id == "SECTION URI"
  id = "sectionAssessmentAssociations"              if human_readable_id == "SECTION ASSESSMENT ASSOC URI"
  id = "studentSectionAssociations"                 if human_readable_id == "STUDENT SECTION ASSOC URI"
  id = "studentAssessments"			                if human_readable_id == "STUDENT ASSESSMENT ASSOC URI"
  id = "teacherSectionAssociations"                 if human_readable_id == "TEACHER SECTION ASSOC URI"
  
  #sections
  id = "cb7a932f-2d44-800c-d574-cdb25a29fc76"       if human_readable_id == "'Important Section' ID"
  id = "58c9ef19-c172-4798-8e6e-c73e68ffb5a3"       if human_readable_id == "'Algebra II' ID"
  
  #students
  id = "2899a720-4196-6112-9874-edde0e2541db"       if human_readable_id == "'John Doe' ID"
  id = "9e6d1d73-a488-4311-877a-718b897a17c5"       if human_readable_id == "'Sean Deer' ID"
  id = "54c6548e-1196-86ca-ad5c-b8d72496bf78"       if human_readable_id == "'Suzy Queue' ID"
  id = "a63ee073-cd6c-9a12-a124-fa6a1b4dfc7c"       if human_readable_id == "'Mary Line' ID"
  id = "51dbb0cd-4f25-2d58-b587-5fac7605e4b3"       if human_readable_id == "'Dong Steve' ID"
  id = "4efb3a11-bc49-f388-0000-0000c93556fb"       if human_readable_id == "'Jane Doe' ID"
    
  #teachers
  id = "a936f73f-7745-b450-922f-87ad78fd6bd1"       if human_readable_id == "'Ms. Jones' ID"
  id = "e24b24aa-2556-994b-d1ed-6e6f71d1be97"       if human_readable_id == "'Ms. Smith' ID"
  
  #assessments
  id = "dd916592-7dfe-4e27-a8ac-bec5f4b757b7"       if human_readable_id == "'Grade 2 MOY READ2' ID"
  id = "dd9165f2-65fe-4e27-a8ac-bec5f4b757f6"       if human_readable_id == "'Grade 2 BOY READ2' ID"
  id = "1e0ddefb-875a-ef7e-b8c3-33bb5676115a"       if human_readable_id == "'Most Recent Student Assessment Association' ID"
  
  #teacher section associations
  id = "12f25c0f-75d7-4e45-8f36-af1bcc342871"       if human_readable_id == "'Teacher Ms. Jones and Section Algebra II' ID"
  
  id
end

Given /^I am a valid SEA\/LEA end user "([^"]*)" with password "([^"]*)"$/ do |user, pass|
  @user = user
  @passwd = pass
end

Given /^I have a Role attribute returned from the "([^"]*)"$/ do |arg1|
# No code needed, this is done during the IDP configuration
end

Given /^the role attribute equals "([^"]*)"$/ do |arg1|
# No code needed, this is done during the IDP configuration
end

Given /^I am authenticated on "([^"]*)"$/ do |arg1|
  idpRealmLogin(@user, @passwd, arg1)
  assert(@sessionId != nil, "Session returned was nil")
end

When /^I navigate to URI "([^"]*)" with filter sorting and pagination$/ do |href|
  @filterSortPaginationHref=href
end

When /^filter by "([^"]*)" = "([^"]*)"$/ do |key, value|
  if @filterSortPaginationHref.include? "?"
    @filterSortPaginationHref = @filterSortPaginationHref+"&"+URI.escape(key)+"="+URI.escape(value)
  else
    @filterSortPaginationHref = @filterSortPaginationHref+"?"+URI.escape(key)+"="+URI.escape(value)
  end
end

When /^I submit the sorting and pagination request$/ do
  step "I navigate to GET \"#{@filterSortPaginationHref}\""
  assert(@result != nil, "Response contains no data")
end

Then /^I should have a list of (\d+) "([^"]*)" entities$/ do |size, entityType|
  assert(@result != nil, "Response contains no data")
  if @result.is_a?(Hash)
    assert(@result["entityType"] == entityType)
  else
    assert(@result.is_a?(Array), "Response contains #{@result.class}, expected Array")

    @ids = Array.new
    @result.each do |entity|
      assert(entity["entityType"] == entityType)
      @ids.push(entity["id"])
    end
  end
end

Then /^I should have an entity with ID "([^"]*)"$/ do |entityId|
  found = false
  @ids.each do |id|
    if entityId == id
      found = true
    end
  end
  
  assert(found, "Entity id #{entityId} was not found")
end

Then /^the field "([^"]*)" should be "([^"]*)"$/ do |field, value|
  assert(@result != nil, "Response contains no data")
  val = @result.clone
  field.split(".").each do |part|
    is_num?(part) ? val = val[part.to_i] : val = val[part]
  end
   
  assert(val == value, "the #{field} is #{val}, not expected #{value}")
end

Then /^there are "([\d]*)" "([^"]*)"$/ do |count, collection|
  assert(@result[collection].length == convert(count), "Expected #{count} #{collection}, received #{@result[collection].length}")
  @col = @result[collection]
end

Then /^for the level at position "([\d]*)"$/ do |offset|
  @offset = convert(offset)
end

Then /^the key "([^"]*)" has value "([^"]*)"$/ do |key,value|
  val = @col[@offset].clone
  key.split(".").each do |part|
    is_num?(part) ? val = val[part.to_i] : val = val[part]
  end
  if is_num?(value)
    assert(val == value.to_i, "Expected value: #{value}, but received #{val}")
  else
    assert(val == value, "Expected value: #{value}, but received #{val}")
  end
end

def is_num?(str)
  Integer(str)
rescue ArgumentError
  false
else
  true
end

