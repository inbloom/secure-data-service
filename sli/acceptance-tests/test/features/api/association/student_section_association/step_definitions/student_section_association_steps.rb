require 'rest-client'
require 'json'
require 'builder'
require 'rexml/document'
include REXML

#transform to section or student uri
Transform /^<'([^']*)' ID>$/ do |id|
  uri = "4efb3a11-bc49-f388-0000-0000c93556fb" if id == "Jane Doe"
  uri = "4efb3a5e-bc49-f388-0000-0000c93556fc" if id == "Albert Wright"
  uri = "4efb3a7f-bc49-f388-0000-0000c93556fd" if id == "Kevin Smith"
  uri = "4efb4262-bc49-f388-0000-0000c9355700" if id == "Biology II - C"
  uri = "4efb4292-bc49-f388-0000-0000c9355701" if id == "Foreign Language - A"
  uri = "4efb4243-bc49-f388-0000-0000c93556ff" if id == "Physics I - B"
  uri = "4efb4238-bc49-f388-0000-0000c93556fe" if id == "Chemistry I - A"
  uri = "4efb7614-8488-7b01-0000-000059f9ba55" if id == 'Section "Foreign Language - A" and Student "Albert Wright"'
  uri = @new_id if id == "newly created student-section-association" or id == "the previous association"
  uri = "11111111-1111-1111-1111-111111111111" if id == "WrongURI"
  uri = "" if id == "No GUID"
  uri
end

#transform to student section association by student AND section
Transform /^<'Student "([^"]*)" and Section "([^"]*)"' ID>$/ do |student, section|
  uri = "4efb4b14-bc49-f388-0000-0000c9355702" if student == "Jane Doe" and section == "Foreign Language - A"
  uri = "4efb4b2f-bc49-f388-0000-0000c9355703" if student == "Jane Doe" and section == "Physics I - B"
  uri = "4efb4b38-bc49-f388-0000-0000c9355704" if student == "Jane Doe" and section == "Chemistry I - A"
  uri = "4efb5272-bc49-f388-0000-0000c9355705" if student == "Albert Wright" and section == "Chemistry I - A"
  uri = "4efb4b14-bc49-f388-0000-0000c9355702" if student == "Kevin Smith" and section == "Chemistry I - A"
  uri = "4efb7614-8488-7b01-0000-000059f9ba55" if student == "Albert Wright" and section == "Foreign Language - A"
  uri
end

#Givens

Given /^(.*) is (<'[^']*' ID>)$/ do |key, value|
  if key == "Student"
    type = 'studentId'
  elsif key == "Section"
    type = 'sectionId'
  end
  
  step type + ' is "' + value + '"'
end

#Whens

When /^I navigate to (GET|DELETE|PUT) \/student\-section\-association\/(<'Student "[^"]*" and Section "[^"]*"' ID>)$/ do |method, uri|
  step 'I navigate to ' + method +  ' "/student-section-associations/' + uri + '"'
end


When /^I navigate to (GET|DELETE|PUT) \/student\-section\-associations\/(<'[^']*' ID>)$/ do |method, uri|
  step 'I navigate to ' + method +  ' "/student-section-associations/' + uri + '"'
end

#Thens
Then /^I should receive a link named "([^"]*)" with URI \/(students|sections)\/(<[^>]*>)$/ do |link_name, type, uri|
  step 'I should receive a link named "' + link_name + '" with URI for "' + type + '/' + uri + '"'
end

Then /^("[^"]*") should equal ((Section|Student) "[^"]*")$/ do |key, value, student_or_section|
  step key + ' should equal "' + value + '"'
end