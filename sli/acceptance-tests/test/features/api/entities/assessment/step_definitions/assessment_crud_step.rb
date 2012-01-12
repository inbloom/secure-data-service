require 'rest-client'
require 'json'
require 'builder'
require 'rexml/document'
include REXML
require_relative '../../../../utils/sli_utils.rb'


Transform /^([^"]*)<([^"]*)>$/ do |arg1, arg2|
  id = arg1+"dd9165f2-65fe-4e27-a8ac-bec5f4b757f6" if arg2 == "'Mathematics Achievement Assessment Test' ID"
  id = arg1+"29f044bd-1449-4fb7-8e9a-5e2cf9ad252a" if arg2 == "'Mathematics Assessment 2' ID"
  id = arg1+"542b0b38-ea57-4d81-aa9c-b55a629a3bd6" if arg2 == "'Mathematics Assessment 3' ID"
  id = arg1+"6c572483-fe75-421c-9588-d82f1f5f3af5" if arg2 == "'Writing Advanced Placement Test' ID"
  id = arg1+"df897f7a-7ac4-42e4-bcbc-8cc6fd88b91a" if arg2 == "'Writing Assessment II' ID"
  id = arg1+"11111111-1111-1111-1111-111111111111" if arg2 == "'NonExistentAssessment' ID" or arg2 == "'WrongURI' ID"
  id = arg1+@newId                                 if arg2 == "'newly created assessment' ID"
  id = arg1                                        if arg2 == "'NoGUID' ID" 
  id
end

Transform /^([^"]*)<([^"]*)>\/targets$/ do |arg1, arg2|
  id = arg1+"6c572483-fe75-421c-9588-d82f1f5f3af5/targets" if arg2 == "'Writing Advanced Placement Test' ID"
  id
end


Given /^I am logged in using "([^"]*)" "([^"]*)"$/ do |arg1, arg2|
  @user = arg1
  @passwd = arg2
end

Given /^I have access to all assessments$/ do
  idpLogin(@user, @passwd)
  assert(@sessionId != nil, "Session returned was nil")
end

Given /^format "([^"]*)"$/ do |arg1|
  ["application/json", "application/xml", "text/plain"].should include(arg1)
  @format = arg1
end

Given /^"([^"]*)" is "([^"]*)"$/ do |key, value|
  @data = {} if !defined? @data
  if key == "assessmentIdentificationCode"
    @data[key] = [Hash["identificationSystem"=>"School","id"=>value]]
  else
    @data[key] = value
  end
end

Then /^"([^"]*)" should be "([^"]*)"$/ do |key, value|
  if key == "assessmentIdentificationCode"
    assert(@data[key][0]['id'] == value, "Expected value of #{value} but received #{@data[key][0]['id']} in the response body")
  else
    assert(@data[key] == value, "Expected value of #{value} but received #{@data[key]}")
  end
end

When /^I set the "([^"]*)" to "([^"]*)"$/ do |arg1, arg2|
  @data[arg1] = arg2
end


When /^I navigate to POST "([^"]*)"$/ do |arg1|
  if @format == "application/json"
    dataH = Hash[
      "assessmentTitle" => @assessmentTitle,
      "assessmentIdentificationCode" => [Hash["identificationSystem"=>"School","id"=>@assessmentIdentificationCode]],
      "academicSubject" => @academicSubject,
      "assessmentCategory" => @assessmentCategory,
      "gradeLevelAssessed" => @gradeLevelAssessed,
      "contentStandard" => @contentStandard,
      "version" => @version
      ]
    data = @data.to_json
      
  elsif @format == "application/xml"
    builder = Builder::XmlMarkup.new(:indent=>2)
    
      
  else
    assert(false, "Unsupported MIME type")
  end

  restHttpPost(arg1, data)    
  assert(@res != nil, "Response from rest-client POST is nil")

end

Then /^I should receive an ID for a newly created assessment$/ do
  headers = @res.raw_headers
  assert(headers != nil, "Headers are nil")
  assert(headers['location'] != nil, "There is no location link from the previous request")
  s = headers['location'][0]
  @newId = s[s.rindex('/')+1..-1]
  assert(@newId != nil, "Assessment ID is nil")
end

When /^I navigate to GET "([^"]*<[^"]*>)"$/ do |arg1|
  restHttpGet(arg1)
  assert(@res != nil, "Response from rest-client GET is nil")
  
  if @format == "application/json"
    begin
      @data = JSON.parse(@res.body);
    rescue
      @data = nil
    end
  end
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



When /^I navigate to PUT "([^"]*<[^"]*>)"$/ do |arg1|

  if @format == "application/json"   
    data = @data.to_json
  elsif @format == "application/xml"
    data = @data
  else
    assert(false, "Unsupported MIME type")
  end
  
  restHttpPut(arg1, data)
  assert(@res != nil, "Response from rest-client PUT is nil")
  
end

When /^I navigate to DELETE "([^"]*<[^"]*>)"$/ do |arg1|
  restHttpDelete(arg1)
  assert(@res != nil, "Response from rest-client DELETE is nil")
end


Then /^I should receive a return code of (\d+)$/ do |arg1|
  assert(@res.code == Integer(arg1), "Return code was not expected: "+@res.code.to_s+" but expected "+ arg1)
end



When /^I attempt to update "([^"]*<[^"]*>)"$/ do |arg1|
  if @format == "application/json"
    dataH = Hash[
      "assessmentTitle" => @assessmentTitle,
      "academicSubject" => @academicSubject,
      "assessmentCategory" => @assessmentCategory,
      "gradeLevelAssessed" => @gradeLevelAssessed,
      "contentStandard" => @contentStandard]
    
    data = dataH.to_json
  elsif @format == "application/xml"
    builder = Builder::XmlMarkup.new(:indent=>2)
    data = builder.assessment { |b|
      b.assessmentTitle(@assessmentTitle)
      b.academicSubject(@academicSubject) 
      b.assessmentCategory(@assessmentCategory)
      b.gradeLevelAssessed(@gradeLevelAssessed)
      b.contentStandard(@contentStandard)
      }
    
  else
    assert(false, "Unsupported MIME type")
  end
  restHttpPut(arg1, data)
  assert(@res != nil, "Response from rest-client PUT is nil")
end