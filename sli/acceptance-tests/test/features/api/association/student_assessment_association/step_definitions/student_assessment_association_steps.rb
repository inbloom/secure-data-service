require 'rest-client'
require 'json'
require 'builder'
require 'rexml/document'

include REXML
require_relative '../../../../utils/sli_utils.rb'

Transform /^\/student-assessment-associations\/<([^>]*)>$/ do |step_arg|
  s = "/student-assessment-associations/"
  id = s+"1e0ddefb-6b61-4f7d-b8c3-33bb5676115a" if step_arg == "Student 'Jane Doe' and AssessmentTitle 'Writing Achievement Assessment Test' ID"
  id = s+"7afddec3-89ec-402c-8fe6-cced79ae3ef5" if step_arg == "'Jane Doe' ID"
  id = s+"a22532c4-6455-41da-b24d-4f93224f526d" if step_arg == "'Mathematics Achievement Assessment Test' ID"
  id = "newId" if step_arg == "Student 'Jane Doe' and AssessmentTitle 'Mathematics Achievement  Assessment Test' ID"
  id = "oldId" if step_arg =="the previous association ID"
  id = s+"11111111-1111-1111-1111-111111111111" if step_arg == "NonExistence Id"
  id = s+"68fbec8e-2041-4536-aad7-1105ab042c77" if step_arg == "AssessmentTitle 'French Advanced Placement' and Student 'Joe Brown' Id"
  id = s                                        if step_arg == "No GUID"
  id
end

Transform /^ID is <([^>]*)>$/ do |step_arg|
  id = "a22532c4-6455-41da-b24d-4f93224f526d" if step_arg == "'Mathematics Achievement  Assessment Test' ID"
  id = "7afddec3-89ec-402c-8fe6-cced79ae3ef5" if step_arg == "'Jane Doe' ID"
  id = "11111111-1111-1111-1111-111111111111" if step_arg == "Invalid ID"
  id = ""                                      if step_arg == "No GUID"
  id
end

Transform /^\/students\/<([^>]*)>$/ do |step_arg|
  s = "/students/"
  id = s+"7afddec3-89ec-402c-8fe6-cced79ae3ef5" if step_arg == "'Jane Doe' ID"
  id = s+"034e6e7f-9da2-454a-b67c-b95bd9f36433" if step_arg == "'Albert Wright' ID"
  id = s+"bda1a4df-c155-4897-85c2-953926a3ebd8" if step_arg == "'Kevin Smith' ID"
  id
end

Transform /^\/assessments\/<([^>]*)>$/ do |step_arg|
  s = "/assessments/"
  id = s+"6a53f63e-deb8-443d-8138-fc5a7368239c" if step_arg == "'Writing Achievement Assessment Test' ID"
  id = s+"a22532c4-6455-41da-b24d-4f93224f526d" if step_arg =="'Mathematics Achievement Assessment Test' ID"
  id
end

Given /^I am logged in using "([^"]*)" "([^"]*)"$/ do |arg1, arg2|
  @user = arg1
  @passwd = arg2
end

Given /^I have access to all students and assessments$/ do
  idpLogin(@user,@passwd)
  assert(@cookie != nil, "Cookie retrieved was nil")
end

Given /^format "([^"]*)"$/ do |fmt|
  @format = fmt
#puts @format
end

Given /^Assessment (ID is <[^>]*>)$/ do |arg1|
  @assessmentId = arg1
  @assessmentId.should_not == nil
#puts @assessmentId

end

Given /^Student (ID is <[^>]*>)$/ do |arg1|
  @studentId = arg1
  @studentId.should_not == nil
#puts @studentId
end

Given /^AdministrationDate is "([^"]*)"$/ do |arg1|
  @administrationDate = arg1
  @administrationDate.should_not == nil
end

Given /^ScoreResults is "([^"]*)"$/ do |arg1|
  @scoreResults = arg1
  @scoreResults.should_not == nil
end

Given /^PerformanceLevel is "([^"]*)"$/ do |arg1|
  @performanceLevel = arg1
  @performanceLevel.should_not == nil
end

When /^I navigate to POST "([^"]*)"$/ do |uri|
  if @format == "application/json" or @format == "application/vnd.slc+json"
    dataH = Hash["studentId"=> @studentId,
    "assessmentId" => @assessmentId,
    "administrationDate" => @administrationDate,
    "scoreResults"=>[Hash["assessmentReportingMethod"=>"Raw_score","result"=>@scoreResults]],
    "performanceLevel"=> @performanceLevel]
  data=dataH.to_json
  elsif @format == "application/xml"
    assert(false, "application/xml is not supported")
  else
    assert(false, "Unsupported MIME type")
  end
  restHttpPost(uri, data)
  assert(@res != nil, "Response from rest-client POST is nil")
  @oldId =uri
end

When /^I navigate to GET (\/student\-assessment\-associations\/<[^>]*>)$/ do |uri|
#puts uri
  if uri == "newId"
  #puts @@newId
  uri=@@newId
  elsif uri =="oldId"
  uri=@oldId
  end
  #puts uri
  restHttpGet(uri)
  assert(@res != nil, "Response from rest-client GET is nil")
  @oldId=uri
end

When /^I set the ScoreResult to "([^"]*)"$/ do |arg1|
  @scoreResults = arg1
  @scoreResults.should_not == nil
end

When /^I set the PerformanceLevel to"([^"]*)"$/ do |arg1|
  @performanceLevel = arg1
  @performanceLevel.should_not == nil
end

When /^I navigate to PUT (\/student\-assessment\-associations\/<[^>]*>)$/ do |uri|
  if uri == "oldId"
  uri = @oldId
  elsif uri == "newId"
  uri = @@newId
  end
  if @format == "application/json" or @format == "application/vnd.slc+json"
    if @res != nil
      dataH=JSON.parse(@res.body)

      dataH["scoreResults"]=[Hash["assessmentReportingMethod"=>"Raw_score","result"=>@scoreResults]]
      dataH["performanceLevel"]=@performanceLevel
    else
      dataH = Hash[]
    end
  elsif @format == "application/xml"
    assert(false, "application/xml is not supported")
  else
    assert(false, "Unsupported MIME type")
  end
  data=dataH.to_json
  restHttpPut(uri, data)
  assert(@res != nil, "Response from rest-client POST is nil")
  @oldId=uri
end

Given /^I navigate to DELETE (\/student\-assessment\-associations\/<[^>]*>)$/ do |uri|
  restHttpDelete(uri)
  assert(@res != nil, "Response from rest-client DELETE is nil")
end

Then /^I should receive a return code of (\d+)$/ do |arg1|
  assert(@res.code == Integer(arg1), "Return code was not expected: #{@res.code.to_s} but expected #{arg1}")
end

Then /^I should receive a ID for the newly created student\-assessment\-association$/ do
  headers = @res.raw_headers
  assert(headers != nil, "Result contained no headers")
  assert(headers['location'] != nil, "There is no location link from the previous request")
  s = headers['location'][0]
  @@newId = "/student-assessment-associations/"+s[s.rindex('/')+1..-1]
  assert(@@newId != nil, "Student-Assessment-Association ID is nil")
#puts @@newId
end

Then /^I should receive (\d+) student\-assessment\-assoications$/ do |arg1|
  if @format == "application/json" or @format == "application/vnd.slc+json"
    dataH=JSON.parse(@res.body)
    counter=0
    dataH["links"].each do|link|
      if link["rel"]=="self"
      counter=counter+1
      end
    end
    assert(counter==Integer(arg1), "Expected response of size #{arg1}, received #{counter}")
  elsif @format == "application/xml"
    assert(false, "application/xml is not supported")
  else
    assert(false, "Unsupported MIME type")
  end
end

Then /^I should receive a link named "([^"]*)" with URI (\/students\/<[^>]*>)$/ do |rel,href|
  if @format == "application/json" or @format == "application/vnd.slc+json"
    dataH=JSON.parse(@res.body)
    found = false
    dataH["links"].each do|link|
      if link["rel"]==rel and link["href"].include? href
      found =true
      end
    end
    if found == false
      assert(found, "didnt receive link named #{rel} with URI #{href}")
    end
  elsif @format == "application/xml"
    assert(false, "application/xml is not supported")
  else
    assert(false, "Unsupported MIME type")
  end
end

Then /^I should receive a link named "([^"]*)" with URI (\/assessments\/<[^>]*>)$/ do |rel,href|
  if @format == "application/json" or @format == "application/vnd.slc+json"
    dataH=JSON.parse(@res.body)
    found = false
    dataH["links"].each do|link|
      if link["rel"]==rel and link["href"].include? href
      found =true
      end
    end
    if found == false
      assert(found, "didnt receive link named #{ref} with URI #{href}")
    end
  elsif @format == "application/xml"
    assert(false, "application/xml is not supported")
  else
    assert(false, "Unsupported MIME type")
  end
end

Then /^the "([^"]*)" should be "([^"]*)"$/ do |key,value|
  if @format == "application/json" or @format == "application/vnd.slc+json"
    dataH=JSON.parse(@res.body)
    if key == "scoreResults"
      assert(dataH["scoreResults"][0]["result"]==value,"Expected #{key} not found in response")
    elsif
    assert(dataH[key]==value,"Expected #{key} not found in response")
    end
  elsif @format == "application/xml"
    assert(false, "application/xml is not supported")
  else
    assert(false, "Unsupported MIME type")
  end
end

Then /^I should receive a collection of (\d+) student\-assessment\-associations that resolve to$/ do |arg1|
  if @format == "application/json" or @format == "application/vnd.slc+json"
    dataH=JSON.parse(@res.body)
    @collectionLinks = []
    counter=0
    @ids = Array.new
    dataH.each do|link|
      if link["link"]["rel"]=="self"
        counter=counter+1
        @ids.push(link["id"])
      # puts @ids
      end
    end
    assert(counter==Integer(arg1), "Expected response of size #{arg1}, received #{counter}")
  elsif @format == "application/xml"
    assert(false, "application/xml is not supported")
  else
    assert(false, "Unsupported MIME type")
  end
end

Then /^I should get a link named "([^"]*)" with URI (\/students\/<[^>]*>)$/ do |rel,href|
  found =false
  @ids.each do |id|
    uri = "/student-assessment-associations/"+id
    restHttpGet(uri)
    assert(@res != nil, "Response from rest-client GET is nil")
    if @format == "application/json" or @format == "application/vnd.slc+json"
      dataH=JSON.parse(@res.body)
      dataH["links"].each do|link|
        if link["rel"]==rel and link["href"].include? href
        found =true
        end
      end
    elsif @format == "application/xml"
      assert(false, "application/xml is not supported")
    else
      assert(false, "Unsupported MIME type")
    end
  end
  assert(found, "didnt receive link named #{rel} with URI #{href}")
end

Then /^I should get a link named "([^"]*)" with URI (\/assessments\/<[^>]*>)$/ do |rel,href|
  found =false
  @ids.each do |id|
    uri = "/student-assessment-associations/"+id
    restHttpGet(uri)
    assert(@res != nil, "Response from rest-client GET is nil")
    if @format == "application/json" or @format == "application/vnd.slc+json"
      dataH=JSON.parse(@res.body)
      dataH["links"].each do|link|
        if link["rel"]==rel and link["href"].include? href
        found =true
        end
      end
    elsif @format == "application/xml"
      assert(false, "application/xml is not supported")
    else
      assert(false, "Unsupported MIME type")
    end
  end
  assert(found, "didnt receive link named #{rel} with URI #{href}")
end

