require 'rest-client'
require 'json'
require 'builder'
require 'rexml/document'
include REXML
require_relative '../../../../utils/sli_utils.rb'

Transform /^([^"]*)<([^"]*)>$/ do |arg1, arg2|
  id = arg1+"714c1304-8a04-4e23-b043-4ad80eb60992"   if arg2 == "'Alfonso' ID"
  id = arg1+"e1af7127-743a-4437-ab15-5b0dacd1bde0"   if arg2 == "'Priscilla' ID"
  id = arg1+"61f13b73-92fa-4a86-aaab-84999c511148"   if arg2 == "'Alden' ID"
  id = arg1+"11111111-1111-1111-1111-111111111111"   if arg2 == "'Invalid' ID"
  id = arg1+"289c933b-ca69-448c-9afd-2c5879b7d221"   if arg2 == "'Donna' ID"
  id = arg1+"c7146300-5bb9-4cc6-8b95-9e401ce34a03"   if arg2 == "'Rachel' ID"
  id = arg1+"11111111-1111-1111-1111-111111111111"   if arg2 == "'WrongURI' ID"
  id = arg1+@newStudentID                            if arg2 == "'newly created student' ID"
  id = arg1                                          if arg2 == "'NoGUID' ID"
  id
end

Transform /^([^"]*)<([^"]*)>\/targets$/ do |arg1, arg2|
  id = arg1+"714c1304-8a04-4e23-b043-4ad80eb60992/targets" if arg2 == "'Alfonso' ID"
  id
end

Given /^I am logged in using "([^"]*)" "([^"]*)"$/ do |arg1, arg2|
  @user = arg1
  @passwd = arg2
end

Given /^I have access to all students$/ do
  idpLogin(@user, @passwd)
  assert(@sessionId != nil, "Session returned was nil")
end


Given /^format "([^"]*)"$/ do |arg1|
  ["application/json", "application/xml", "text/plain"].should include(arg1)
  @format = arg1
end

Given /^the "birthDate" is "([^"]*)"$/ do |arg1|
  #d = Date.strptime(arg1, '%m/%d/%Y')
  #@bdate = d.to_time.to_i*1000
  @bdate = arg1
end

When /^I set the "([^"]*)" to "([^"]*)"$/ do |arg1, arg2|
  step "the \"birthDate\" is \""+ arg2 + "\""
end

Given /^the "sex" is "([^"]*)"$/ do |arg1|
  ["Male","Female"].should include(arg1)
  @sex = arg1
end

Given /^the "name" is "([^"]*)" "([^"]*)" "([^"]*)"$/ do |arg1, arg2, arg3|
  @fname = arg1
  @fname.should_not == nil
  @mname = arg2
  @lname = arg3
  @lname.should_not == nil
end

Given /^the "studentUniqueStateId" is "([^"]*)"$/ do |arg1|
  @studentSchoolId = Integer(arg1)
  @studentSchoolId.should_not == nil
end

Then /^I should receive a return code of (\d+)$/ do |arg1|
  assert(@res.code == Integer(arg1), "Return code was not expected: "+@res.code.to_s+" but expected "+ arg1)
end

Then /^I should receive a ID for the newly created student$/ do
  headers = @res.raw_headers
  assert(headers != nil, "Headers are nil")
  assert(headers['location'] != nil, "There is no location link from the previous request")
  s = headers['location'][0]
  @newStudentID = s[s.rindex('/')+1..-1]
  assert(@newStudentID != nil, "Student ID is nil")
end

Then /^the "([^"]*)" should be "([^"]*)"$/ do |arg1, arg2|
  if(arg1 == 'birthDate')
    assert(@data['birthData'][arg1] == arg2, "Expected data incorrect: Expected #{arg2} but got #{@data[arg1]}")
  else
    assert(@data[arg1].to_s == arg2, "Expected data incorrect: Expected #{arg2} but got #{@data[arg1]}")
  end
  
end

Then /^the "([^"]*)" should be "([^"]*)" "([^"]*)" "([^"]*)"$/ do |arg1, arg2, arg3, arg4|
  assert(@data[arg1]['firstName'] == arg2, "Expected data incorrect")
  assert(@data[arg1]['lastSurname'] == arg4, "Expected data incorrect")
end

Then /^I should receive a link named "([^"]*)" with URI "([^"]*<[^"]*>|[^"]*<[^"]*>\/targets)"$/ do |rel, href|
  @data = JSON.parse(@res.body)
  assert(@data != nil, "Response contains no data")
  assert(@data.is_a?(Hash), "Response contains #{@data.class}, expected Hash")
  assert(@data.has_key?("links"), "Response contains no links")
  found = false
  @data["links"].each do |link|
    if link["rel"] == rel && link["href"] =~ /#{Regexp.escape(href)}$/
      found = true
    end
  end
  assert(found, "Link not found rel=#{rel}, href ends with=#{href}")
end

When /^I attempt to update "([^"]*<[^"]*>)"$/ do |arg1|
  if @format == "application/json"
    dataH = Hash[
      "studentUniqueStateId" => "",
      "name" => Hash[
        "firstName" => "should",
        "lastSurname" => "not",
        "middleName" => "exist"],
      "sex" => "",
      "birthData" => Hash[
        "birthDate" => ""
        ]
      ]
    data = dataH.to_json
  elsif @format == "application/xml"
    #not supported
  else
    assert(false, "Unsupported MIME type")
  end
  restHttpPut(arg1, data)
  assert(@res != nil, "Response from rest-client PUT is nil")
end


When /^I navigate to POST "([^"]*)"$/ do |arg1|
  if @format == "application/json"
    dataH = Hash[
      "studentUniqueStateId" => @studentSchoolId,
      "name" => Hash[
        "firstName" => @fname,
        "lastSurname" => @lname,
        "middleName" => @mname],
      "sex" => @sex,
      "birthData" => Hash[
        "birthDate" => @bdate
        ]
      ]
    data = dataH.to_json
  elsif @format == "application/xml"
    builder = Builder::XmlMarkup.new(:indent=>2)
    data = builder.student { |b| 
      b.studentSchoolId(@studentSchoolId)
      b.firstName(@fname) 
      b.lastSurname(@lname)
      b.middleName(@mname)
      b.sex(@sex)
      b.birthDate(@bdate)}      
  else
    assert(false, "Unsupported MIME type")
  end
  restHttpPost(arg1, data)
  assert(@res != nil, "Response from rest-client POST is nil")
end

When /^I navigate to GET "([^"]*<[^"]*>)"$/ do |student_uri|
  restHttpGet(student_uri)
  assert(@res != nil, "Response from rest-client GET is nil")
  if @format == "application/json"
    begin
      @data = JSON.parse(@res.body);
    rescue
      @data = nil
    end
  end
end

When /^I navigate to PUT "([^"]*<[^"]*>)"$/ do |student_uri|
  restHttpGet(student_uri)
  assert(@res != nil, "Response from rest-client GET is nil")
  assert(@res.code == 200, "Return code was not expected: "+@res.code.to_s+" but expected 200")
  
  if @format == "application/json"
    dataH = JSON.parse(@res.body)
    dataH['birthData']['birthDate'].should_not == @bdate
    dataH['birthData']['birthDate'] = @bdate
    data = dataH.to_json
  elsif @format == "application/xml"    
    doc = Document.new(@res.body)  
    doc.root.elements["birthDate"].text.should_not == @bdate
    doc.root.elements["birthDate"].text = @bdate
    data = doc
  else
    assert(false, "Unsupported MIME type")
  end
  
  restHttpPut(student_uri, data)
  assert(@res != nil, "Response from rest-client PUT is nil")
end

When /^I navigate to DELETE "([^"]*<[^"]*>)"$/ do |student_uri|
  restHttpDelete(student_uri)
  assert(@res != nil, "Response from rest-client DELETE is nil")
end

Then /^I should receive a link where rel is "([^"]*)" and href ends with "([^"]*)"$/ do |rel, href|
  @data = JSON.parse(@res.body)
  assert(@data != nil, "Response contains no data")
  assert(@data.is_a?(Hash), "Response contains #{@data.class}, expected Hash")
  assert(@data.has_key?("links"), "Response contains no links")
  found = false
  @data["links"].each do |link|
    if link["rel"] == rel && link["href"] =~ /#{Regexp.escape(href)}$/
      found = true
    end
  end
  assert(found, "Link not found rel=#{rel}, href ends with=#{href}")
end

