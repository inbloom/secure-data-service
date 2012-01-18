require 'rest-client'
require 'json'
require 'builder'
require 'rexml/document'

include REXML
require_relative '../../../../utils/sli_utils.rb'

Transform /^([^"]*)<([^"]*)>$/ do |arg1, arg2|
  id = arg1+@newId                                 if arg2 == "'newly created student assessment association' ID"
  id = arg1+"1e0ddefb-6b61-4f7d-b8c3-33bb5676115a" if arg2 =="Student 'Jane Doe' and AssessmentTitle 'Writing Achievement Assessment Test' ID"
  id = arg1+"c8672d3b-0953-4ad7-a1b5-d5395bc0150a" if arg2 =="Student 'Jane Doe' and AssessmentTitle 'Mathematics Achievement  Assessment Test' ID"
  id = arg1+"68fbec8e-2041-4536-aad7-1105ab042c77" if arg2 =="AssessmentTitle 'French Advanced Placement' and Student 'Joe Brown' ID"
  id = arg1+"7afddec3-89ec-402c-8fe6-cced79ae3ef5" if arg2 =="'Jane Doe' ID"
  id = arg1+"a22532c4-6455-41da-b24d-4f93224f526d" if arg2 =="'Mathematics Achievement Assessment Test' ID"
  id = arg1+"034e6e7f-9da2-454a-b67c-b95bd9f36433" if arg2 =="'Albert Wright' ID"
  id = arg1+"bda1a4df-c155-4897-85c2-953926a3ebd8" if arg2 =="'Kevin Smith' ID"
  id = arg1+"6a53f63e-deb8-443d-8138-fc5a7368239c" if arg2 =="'Writing Achievement Assessment Test' ID"
  id = arg1+"034e6e7f-9da2-454a-b67c-b95bd9f36433" if arg2 =="'Albert Wright' ID"
  id = arg1                                        if arg2 == "'No GUID'"
  id = arg1+"11111111-1111-1111-1111-111111111111" if arg2 == "'NonExistence' ID"
  id
end

Transform /^<([^"]*)>$/ do |step_arg|
  id = "a22532c4-6455-41da-b24d-4f93224f526d" if step_arg == "'Mathematics Achievement  Assessment Test' ID"
  id = "7afddec3-89ec-402c-8fe6-cced79ae3ef5" if step_arg == "'Jane Doe' ID"
  id = "11111111-1111-1111-1111-111111111111" if step_arg == "Invalid ID"
  id = ""                                      if step_arg == "No GUID"
  id
end

Given /^"([^"]*)" is "([^"]*|<[^"]*>)"$/ do |key, value|
  if !defined? @fields
    @fields = {}
  end
  @fields[key] = value
end

When /^I navigate to POST "([^"]*)"$/ do |uri|
  if @format == "application/json" or @format == "application/vnd.slc+json"
    dataH = Hash["studentId"=> @fields["studentId"],
    "assessmentId" => @fields["assessmentId"],
    "administrationDate" => @fields["administrationDate"],
    "scoreResults"=>[Hash["assessmentReportingMethod"=>"Raw_score","result"=>@fields["scoreResults"]]],
    "performanceLevel"=> @fields["performanceLevel"]]
  data=dataH.to_json
  elsif @format == "application/xml"
    assert(false, "application/xml is not supported")
  else
    assert(false, "Unsupported MIME type")
  end
  restHttpPost(uri, data)
  assert(@res != nil, "Response from rest-client POST is nil")
end


When /^I set the "([^"]*)" to "([^"]*)"$/ do |key, value|
  if !defined? @fields
    @fields = {}
  end
  @fields[key]=value
end


When /^I navigate to PUT "([^"]*<[^"]*>)"$/ do |uri|
  restHttpGet(uri)
  assert(@res != nil, "Response from rest-client GET is nil")
  assert(@res.code == 200, "Return code was not expected: "+@res.code.to_s+" but expected 200")
   if @format == "application/json"
      modified = JSON.parse(@res.body)
      @fields.each do |key, value|
        if key == "scoreResults"
        modified[key]=[Hash["assessmentReportingMethod"=>"Raw_score","result"=>value]]
        elsif modified[key] = value
        end
      end
      data = modified.to_json
    elsif @format == "application/xml"
      assert(false, "application/xml is not supported")
    else
      assert(false, "Unsupported MIME type")
    end
    restHttpPut(uri, data)
    assert(@res != nil, "Response from rest-client PUT is nil")
end

When /^I attempt to update a non\-existing association "([^"]*<[^"]*>)"$/ do |uri|
  data = {}
  restHttpPut(uri, data.to_json)
  assert(@res != nil, "Response from rest-client PUT is nil")
end

Then /^I should receive a collection of (\d+) student\-assessment\-association links$/ do |arg1|
if @format == "application/json" or @format == "application/vnd.slc+json"
    counter=0
    @ids = Array.new
    @result.each do|link|
      if link["link"]["rel"]=="self"
        counter=counter+1
        @ids.push(link["id"])
      end
    end
    assert(counter==Integer(arg1), "Expected response of size #{arg1}, received #{counter}")
  elsif @format == "application/xml"
    assert(false, "application/xml is not supported")
  else
    assert(false, "Unsupported MIME type")
  end
end

Then /^the "([^"]*)" should be "([^"]*)"$/ do |key,value|  
  if key == "scoreResults"
    assert(@result["scoreResults"][0]["result"]==value,"Expected #{key} not found in response")
  elsif
  assert(@result[key]==value,"Expected #{key} not found in response")
  end
end

Then /^after resolving each link, I should receive a link named "([^"]*)" with URI "([^"]*)"$/ do |rel, href|
  @ids.each do |id|
    found =false
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
     assert(found, "didnt receive link named #{rel} with URI #{href}")
  end
end

Then /^after resolution, I should receive a link named "([^"]*)" with URI "([^"]*)"$/ do |rel, href|
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


