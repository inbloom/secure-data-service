require 'rest-client'
require 'json'
require 'builder'
require 'rexml/document'
include REXML

#transform to section or student uri
Transform /^(Section|Student) "([^"]*)"$/ do |type, id|
  if type == "Student"
    uri = "4efb3a11-bc49-f388-0000-0000c93556fb" if id == "Jane Doe"
    uri = "4efb3a5e-bc49-f388-0000-0000c93556fc" if id == "Albert Wright"
    uri = "4efb3a7f-bc49-f388-0000-0000c93556fd" if id == "Kevin Smith"
  elsif type == "Section"
    uri = "4efb4262-bc49-f388-0000-0000c9355700" if id == "Biology II - C"
    uri = "4efb4292-bc49-f388-0000-0000c9355701" if id == "Foreign Language - A"
    uri = "4efb4243-bc49-f388-0000-0000c93556ff" if id == "Physics I - B"
    uri = "4efb4238-bc49-f388-0000-0000c93556fe" if id == "Chemistry I - A"
  end
  uri
end

#transform to student section association by student AND section
Transform /^Student Section Association for Student "([^"]*)" and Section "([^"]*)"$/ do |student, section|
  uri = "4efb4b14-bc49-f388-0000-0000c9355702" if student == "Jane Doe" and section == "Foreign Language - A"
  uri = "4efb4b2f-bc49-f388-0000-0000c9355703" if student == "Jane Doe" and section == "Physics I - B"
  uri = "4efb4b38-bc49-f388-0000-0000c9355704" if student == "Jane Doe" and section == "Chemistry I - A"
  uri = "4efb5272-bc49-f388-0000-0000c9355705" if student == "Albert Wright" and section == "Chemistry I - A"
  uri = "4efb4b14-bc49-f388-0000-0000c9355702" if student == "Kevin Smith" and section == "Chemistry I - A"
  uri = "4efb7614-8488-7b01-0000-000059f9ba55" if student == "Albert Wright" and section == "Foreign Language - A"
  uri = @post_uri                              if student == "Jane Doe" and section == "Biology II - C"
  uri
end


#transform to student section associations by student OR section
Transform /^Student Section Associations for the (Student|Section) "([^"]*)"$/ do |type, id|
  Transform type + ' "' + id + '"'
end

#Givens

Given /^"([^"]*)" is ((Student|Section) "[^"]*")$/ do |key, value, student_or_section|
  step '"' + key + '" is "' + value + '"'
end

#Whens

When /^I navigate to (GET|DELETE|PUT) (Student Section Association for Student "[^"]*" and Section "[^"]*")$/ do |method, uri|
  step 'I navigate to ' + method +  ' "/student-section-associations/' + uri + '"'
end

When /^I navigate to GET (Student Section Associations for the (Student|Section) "[^"]*")$/ do |uri, student_or_section|
  step 'I navigate to GET "/student-section-associations/' + uri + '"'
end

When /^I navigate to (GET|DELETE) \/student\-section\-associations\/(<[^>]*>)$/ do |method, uri|
  step 'I navigate to ' + method + ' "/student-section-associations/' + uri + '"'
end

#Thens
Then /^I should receive a link named "([^"]*)" with URI for ((Student|Section) "[^"]*")$/ do |link_name, uri, type|
  step 'I should receive a link named "' + link_name + '" with URI for "' + uri + '"'
end

Then /^("[^"]*") should equal ((Section|Student) "[^"]*")$/ do |key, value, student_or_section|
  step key + ' should equal "' + value + '"'
end