require 'rest-client'
require 'uri'
require 'json'
require 'builder'
require 'rexml/document'

include REXML
require_relative '../../../../utils/sli_utils.rb'

Transform /^<([^>]*)>$/ do |step_arg|
  id = "17a8658c-6fcb-4ece-99d1-b2dea1afd987" if step_arg == "'ImportantSection' ID"
  id = "2899a720-4186-4598-9874-edde0e2541db" if step_arg == "'John Doe' ID"
  id = "9e6d1d73-a408-41a1-877a-718b897a17c5" if step_arg == "'Sean Deer' ID"
  id = "54c6546e-7998-4c6b-ad5c-b8d72496bf78" if step_arg == "'Suzy Queue' ID"
  id = "a63ee073-cd6c-4aa4-a124-fa6a1b4dfc7c" if step_arg == "'Mary Line' ID"
  id = "51db306f-4fa5-405b-b587-5fac7605e4b3" if step_arg == "'Dong Steve' ID"
  id
end

Transform /^\/student-section-associations\/<([^>]*)>$/ do |step_arg|
  s = "/student-section-associations/"
  id = s+"17a8658c-6fcb-4ece-99d1-b2dea1afd987" if step_arg == "'ImportantSection' ID"
  id
end

Transform /^\/student-assessment-associations\/<([^>]*)>$/ do |step_arg|
  s = "/student-assessment-associations/"
  id = s+"54c6546e-7998-4c6b-ad5c-b8d72496bf78" if step_arg == "'Suzy Queue' ID"
  id = s+"7b2e6133-4224-4890-ac02-73962eb09645" if step_arg == "'ISAT MATH' ID"
  id
end

Transform /^\/teacher-section-associations\/<([^>]*)>$/ do |step_arg|
  s = "/teacher-section-associations/"
  id = s+"a936f73f-7751-412d-922f-87ad78fd6bd1" if step_arg == "'Ms. Jones' ID"
  id
end

Transform /^\/teachers\/<([^>]*)>$/ do |step_arg|
  s = "/teachers/"
  id = s+"a936f73f-7751-412d-922f-87ad78fd6bd1" if step_arg == "'Ms. Jones' ID"
  id
end

Transform /^\/students\/<([^>]*)>$/ do |step_arg|
  s = "/students/"
  id = s+"54c6546e-7998-4c6b-ad5c-b8d72496bf78" if step_arg == "'Suzy Queue' ID"
  id
end

Given /^I am logged in using "([^"]*)" "([^"]*)"$/ do |arg1, arg2|
  @user = arg1
  @passwd = arg2
end

Given /^I have access to all students  assessments and sections$/ do
  idpLogin(@user,@passwd)
  assert(@sessionId != nil, "Session returned was nil")
end

Given /^format "([^"]*)"$/ do |fmt|
  @format = fmt
#puts @format
end


When /^I navigate to GET (\/teachers\/<[^>]*>|\/students\/<[^>]*>)$/ do |uri|
  restHttpGet(uri)
  assert(@res != nil, "Response from rest-client GET is nil")
end

When /^I navigate to "([^"]*)" with URI  (\/teacher\-section\-associations\/<[^>]*>)\/targets$/ do |rel,href|
  uri = href+"/targets"
  restHttpGet(uri)
  assert(@res != nil, "Response from rest-client GET is nil")
end

When /^I navigate to "([^"]*)" with URI  (\/teacher\-section\-associations\/<[^>]*>)\/targets and filter by sectionName is "([^"]*)" and classPeriod is "([^"]*)"$/ do |rel, href, sectionName,classPeriod|
  queryParams = "sectionName="+sectionName+"&classPeriod="+classPeriod
  uri = href+"/targets?"+URI.escape(queryParams,Regexp.new("[^#{URI::PATTERN::UNRESERVED}]"))
  restHttpGet(uri)
  assert(@res != nil, "Response from rest-client GET is nil")
end

When /^I navigate to "([^"]*)" with URI (\/student\-section\-associations\/<[^>]*>)\/targets$/ do |rel,href|
  uri = href+"/targets"
  restHttpGet(uri)
  assert(@res != nil, "Response from rest-client GET is nil")
end

When /^I navigate to "([^"]*)" with URI (\/student\-assessment\-associations\/<[^>]*>)\/targets and filter by assessmentTitle is "([^"]*)"$/ do |rel, href,assessmentTitle|
  queryParams = "assessmentTitle="+assessmentTitle
  uri = href+"/targets?"+URI.escape(queryParams,Regexp.new("[^#{URI::PATTERN::UNRESERVED}]"))
  restHttpGet(uri)
  assert(@res != nil, "Response from rest-client GET is nil")
  puts @res
end

When /^I navigate to  "([^"]*)" with URI (\/student\-assessment\-associations\/<[^>]*>)$/ do |rel,href|
  @uriWithQuery = href+"?"
end

When /^filter by administrationDate is between "([^"]*)" and "([^"]*)"$/ do |date1,date2|
  queryParams = "administrationDate>"+date1+"&administrationDate<"+date2
  @uriWithQuery = @uriWithQuery + URI.escape(queryParams)
end

When /^filter by studentId is (<[^>]*>)$/ do |studentId|
  queryParams = "&studentId="+studentId
  @uriWithQuery = @uriWithQuery + URI.escape(queryParams)
  restHttpGet(@uriWithQuery)
  assert(@res != nil, "Response from rest-client GET is nil")
  if @format == "application/json" or @format == "application/vnd.slc+json"
    dataH=JSON.parse(@res.body)
    uri = "/student-assessment-associations/"+dataH[0]["id"]
    restHttpGet(uri)
  elsif @format == "application/xml"
    assert(false, "application/xml is not supported")
  else
    assert(false, "Unsupported MIME type")
  end
end

Then /^I should see a link named "([^"]*)" with URI (\/teacher\-section\-associations\/<[^>]*>|\/student\-assessment\-associations\/<[^>]*>)$/ do |rel,href|
  if @format == "application/json" or @format == "application/vnd.slc+json"
    dataH=JSON.parse(@res.body)
    found = false
    dataH["links"].each do|link|
      if link["rel"]==rel and link["href"].include? href
      found =true
      end
    end
      assert(found, "didnt receive link named #{rel} with URI #{href}")
  elsif @format == "application/xml"
    assert(false, "application/xml is not supported")
  else
    assert(false, "Unsupported MIME type")
  end
end

Then /^I should see a link named "([^"]*)" with URI (\/teacher\-section\-associations\/<[^>]*>|\/student\-assessment\-associations\/<[^>]*>)\/targets$/ do |rel,href|
  if @format == "application/json" or @format == "application/vnd.slc+json"
    dataH=JSON.parse(@res.body)
    found = false
    dataH["links"].each do|link|
      if link["rel"]==rel and link["href"].include? href+"/targets"
      found =true
      end
    end
      assert(found, "didnt receive link named #{rel} with URI #{href}")
  elsif @format == "application/xml"
    assert(false, "application/xml is not supported")
  else
    assert(false, "Unsupported MIME type")
  end
end

Then /^I should see a link named "([^"]*)" with URI (\/teachers\/<[^>]*>)$/ do |rel,href|
  if @format == "application/json" or @format == "application/vnd.slc+json"
    dataH=JSON.parse(@res.body)
    found = false
    dataH["links"].each do|link|
      if link["rel"]==rel and link["href"].include? href
      found =true
      end
    end
      assert(found, "didnt receive link named #{rel} with URI #{href}")
  elsif @format == "application/xml"
    assert(false, "application/xml is not supported")
  else
    assert(false, "Unsupported MIME type")
  end
end

Then /^I should receive a collection of (\d+) section links that resolve to$/ do |arg1|
  if @format == "application/json" or @format == "application/vnd.slc+json"
    dataH=JSON.parse(@res.body)
    counter=0
    @ids = Array.new
    dataH.each do|link|
      if link["link"]["rel"]=="self" and link["link"]["href"].include? "/sections/"
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

Then /^I should receive a collection of (\d+) student links that resolve to$/ do |arg1|
  if @format == "application/json" or @format == "application/vnd.slc+json"
    dataH=JSON.parse(@res.body)
    counter=0
    @ids = Array.new
    dataH.each do|link|
      if link["link"]["rel"]=="self" and link["link"]["href"].include? "/students/"
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

Then /^I should find Student with (<[^>]*>)$/ do |studentId|
  found =false
  @ids.each do |id|
    if id==studentId
      found = true
    end
  end
  assert(found, "didnt find student with ID #{studentId}")
end

Then /^I should receive (\d+) section link that resolve to$/ do |arg1|
  if @format == "application/json" or @format == "application/vnd.slc+json"
    dataH=JSON.parse(@res.body)
    counter=0
    @ids = Array.new
    dataH.each do|link|
      if link["link"]["rel"]=="self" and link["link"]["href"].include? "/sections/"
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

Then /^I should find section with sectionName is "([^"]*)" and classPeriod is "([^"]*)"$/ do |sectionName, classPeriod|
  found =false
  @ids.each do |id|
    uri = "/sections/"+id
    restHttpGet(uri)
    assert(@res != nil, "Response from rest-client GET is nil")
    if @format == "application/json" or @format == "application/vnd.slc+json"
      dataH=JSON.parse(@res.body)
        if dataH["sectionName"]==sectionName and dataH["classPeriod"]==classPeriod
        found =true
      end
    elsif @format == "application/xml"
      assert(false, "application/xml is not supported")
    else
      assert(false, "Unsupported MIME type")
    end
  end
  assert(found, "didnt find section with sectionName #{sectionName} and classPeriod #{classPeriod}")
end

Then /^I should find section with sectionName is "([^"]*)" and classPeriod is "([^"]*)"  with (<[^>]*>)$/ do |sectionName, classPeriod,id|
  found =false
  @ids.each do |id|
    uri = "/sections/"+id
    restHttpGet(uri)
    assert(@res != nil, "Response from rest-client GET is nil")
    if @format == "application/json" or @format == "application/vnd.slc+json"
      dataH=JSON.parse(@res.body)
        if dataH["sectionName"]==sectionName and dataH["classPeriod"]==classPeriod and dataH["id"]==id
        found =true
      end
    elsif @format == "application/xml"
      assert(false, "application/xml is not supported")
    else
      assert(false, "Unsupported MIME type")
    end
  end
  assert(found, "didnt find section with sectionName #{sectionName} and classPeriod #{classPeriod} with #{id}")
end

Then /^I should see a link named "([^"]*)" with URI (\/student\-section\-associations\/<[^>]*>)\/targets$/ do |rel,href|
  found =false
  @ids.each do |id|
    uri = "/sections/"+id
    restHttpGet(uri)
    assert(@res != nil, "Response from rest-client GET is nil")
    if @format == "application/json" or @format == "application/vnd.slc+json"
      dataH=JSON.parse(@res.body)
      dataH["links"].each do |link|
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
  assert(found, "didnt find link named #{rel} with URI #{href}/targets")
  end
  




Then /^I should receive (\d+) assessment$/ do |arg1|
  if @format == "application/json" or @format == "application/vnd.slc+json"
    dataH=JSON.parse(@res.body)
    counter=0
    dataH.each do|link|
      if link["link"]["rel"]=="self" and link["link"]["href"].include? "/assessments/"
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

Then /^I should find a ScoreResult is (\d+)$/ do |scoreResult|
  if @format == "application/json" or @format == "application/vnd.slc+json"
    dataH=JSON.parse(@res.body)
    found = false
    dataH["scoreResults"].each do |result|
      if result["result"]==scoreResult
        found = true
      end
    end
    assert(found, "did not find scoreResult #{scoreResult}")
  elsif @format == "application/xml"
    assert(false, "application/xml is not supported")
  else
    assert(false, "Unsupported MIME type")
  end
end

Then /^I should find a PerformanceLevel is (\d+)$/ do |performanceLevel|
  if @format == "application/json" or @format == "application/vnd.slc+json"
    dataH=JSON.parse(@res.body)
    level = dataH["performanceLevel"]
    assert(level==performanceLevel, "Expected performanceLevel is #{performanceLevel}, received #{level}")
  elsif @format == "application/xml"
    assert(false, "application/xml is not supported")
  else
    assert(false, "Unsupported MIME type")
  end
end

