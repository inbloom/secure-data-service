require 'rest-client'

require_relative '../../../../utils/sli_utils.rb'

Transform /^<.+>$/ do |arg1|
  id = "cb7a932f-2d44-800c-d574-cdb25a29fc76" if arg1 == "<'ImportantSection' ID>"
  id = "dd916592-7dfe-4e27-a8ac-bec5f4b757b7" if arg1 == "<'Grade 2 MOY DIBELS' ID>"
  id = "2899a720-4196-6112-9874-edde0e2541db" if arg1 == "<'John Doe' ID>"
  id = "9e6d1d73-a488-4311-877a-718b897a17c5" if arg1 == "<'Sean Deer' ID>"
  id = "54c6548e-1196-86ca-ad5c-b8d72496bf78" if arg1 == "<'Suzy Queue' ID>"
  id = "a63ee073-cd6c-9a12-a124-fa6a1b4dfc7c" if arg1 == "<'Mary Line' ID>"
  id = "51dbb0cd-4f25-2d58-b587-5fac7605e4b3" if arg1 == "<'Dong Steve' ID>"
  id
end

Transform /(\/[^"].*\/)<(.+)>\/targets$/ do |arg1, arg2|
  id = arg1+"a936f73f-7745-b450-922f-87ad78fd6bd1/targets" if arg2 == "'Ms. Jones' ID"
  id = arg1+"cb7a932f-2d44-800c-d574-cdb25a29fc76/targets" if arg2 =="'ImportantSection' ID"
  id = arg1+"e24b24aa-2556-994b-d1ed-6e6f71d1be97/targets" if arg2 == "'Ms. Smith' ID"
  id = arg1+"58c9ef19-c172-4798-8e6e-c73e68ffb5a3/targets" if arg2 == "'Algebra II' ID"
  id = arg1+"12f25c0f-75d7-4e45-8f36-af1bcc342871/targets" if arg2 == "'Teacher Ms. Jones and Section Algebra II' ID"
  id = arg1+"4efb3a11-bc49-f388-0000-0000c93556fb/targets" if arg2 == "'Jane Doe' ID"
  id = arg1+"dd9165f2-65fe-4e27-a8ac-bec5f4b757f6/targets" if arg2 == "'Grade 2 BOY DIBELS' ID"
  id
end

Transform /(\/[^"].*\/)<(.+)>$/ do |arg1, arg2|
  id = arg1+"a936f73f-7745-b450-922f-87ad78fd6bd1" if arg2 == "'Ms. Jones' ID"
  id = arg1+"dd916592-7dfe-4e27-a8ac-bec5f4b757b7" if arg2 == "'Grade 2 MOY DIBELS' ID"
  id = arg1+"e24b24aa-2556-994b-d1ed-6e6f71d1be97" if arg2 == "'Ms. Smith' ID"
  id = arg1+"58c9ef19-c172-4798-8e6e-c73e68ffb5a3" if arg2 == "'Algebra II' ID"
  id = arg1+"12f25c0f-75d7-4e45-8f36-af1bcc342871" if arg2 == "'Teacher Ms. Jones and Section Algebra II' ID"
  id = arg1+"4efb3a11-bc49-f388-0000-0000c93556fb" if arg2 == "'Jane Doe' ID"
  id = arg1+"dd9165f2-65fe-4e27-a8ac-bec5f4b757f6" if arg2 == "'Grade 2 BOY DIBELS' ID"
  id = arg1+"eb3b8c35-f582-df23-e406-6947249a19f2" if arg2 == "'Apple Alternative Elementary School' ID"
  id
end

#Given /^I am a valid SEA\/LEA end user <username> with password <password>$/ do
#    @user = "aggregator"
#    @passwd = "aggregator1234"
#end

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

When /^I navigate to "([^"]*)" with URI "([^"]*)"$/ do |rel,href|
  restHttpGet(href)
  assert(@res != nil, "Response from rest-client GET is nil")
end

Then /^I should receive a collection of (\d+) ([\w-]+) links$/ do |arg1, arg2|
  @collectionType = "/"+arg2+"s/"
  if @format == "application/json" or @format == "application/vnd.slc+json"
    dataH=JSON.parse(@res.body)
    counter=0
    @ids = Array.new
    if arg2 == "student"
      @studentIds = Array.new
    end
    dataH.each do|link|
      if link["link"]["rel"]=="self" and link["link"]["href"].include? @collectionType
        counter=counter+1
        @ids.push(link["id"])
        if arg2 == "student"
          @studentIds.push(link["id"])
        end
      end
    end
    assert(counter==Integer(arg1), "Expected response of size #{arg1}, received #{counter}")
  elsif @format == "application/xml"
    assert(false, "application/xml is not supported")
  else
    assert(false, "Unsupported MIME type")
  end
end

Then /^I should find section with uniqueSectionCode is "([^"]*)"$/ do |uniqueSectionCode|
  found =false
  @ids.each do |id|
    uri = "/sections/"+id
    restHttpGet(uri)
    assert(@res != nil, "Response from rest-client GET is nil")
    if @format == "application/json" or @format == "application/vnd.slc+json"
      dataH=JSON.parse(@res.body)
      if dataH["uniqueSectionCode"]==uniqueSectionCode
      found =true
      end
    elsif @format == "application/xml"
      assert(false, "application/xml is not supported")
    else
      assert(false, "Unsupported MIME type")
    end
  end
  assert(found, "didnt find section with uniqueSectionCode #{uniqueSectionCode}")
end

Then /^I should find section with uniqueSectionCode is "([^"]*)"  with (<.+>)$/ do |uniqueSectionCode,id|
  found =false
  @ids.each do |id|
    uri = "/sections/"+id
    restHttpGet(uri)
    assert(@res != nil, "Response from rest-client GET is nil")
    if @format == "application/json" or @format == "application/vnd.slc+json"
      dataH=JSON.parse(@res.body)
      if dataH["uniqueSectionCode"]==uniqueSectionCode and dataH["id"]==id
      found =true
      end
    elsif @format == "application/xml"
      assert(false, "application/xml is not supported")
    else
      assert(false, "Unsupported MIME type")
    end
  end
  assert(found, "didnt find section with uniqueSectionCode #{uniqueSectionCode} with id #{id}")
end

When /^I navigate to "([^"]*)" with URI "([^"]*)" with filter sorting and pagination$/ do |rel, href|
  @filterSortPaginationHref=href
end

When /^filter by  "([^"]*)" = "([^"]*)"$/ do |key, value|
  if @filterSortPaginationHref.include? "?"
    @filterSortPaginationHref = @filterSortPaginationHref+"&"+URI.escape(key)+"="+URI.escape(value)
  else
    @filterSortPaginationHref = @filterSortPaginationHref+"?"+URI.escape(key)+"="+URI.escape(value)
  end
end

When /^"([^"]*)" = "([^"]*)"$/ do |key, value|
  if @filterSortPaginationHref.include? "?"
    @filterSortPaginationHref = @filterSortPaginationHref+"&"+URI.escape(key)+"="+URI.escape(value)
  else
    @filterSortPaginationHref = @filterSortPaginationHref+"?"+URI.escape(key)+"="+URI.escape(value)
  end
  if key == "max-results"
    restHttpGet(@filterSortPaginationHref)
    assert(@res != nil, "Response from rest-client GET is nil")
  end
end

When /^I navigate to "([^"]*)" with URI "([^"]*)" and  filter by "([^"]*)" is "([^"]*)"$/ do |rel, href, filterKey, filterValue|
  href="/student-assessment-associations/"+@ids[0]+"/targets"
  href=href+"?"+URI.escape(filterKey)+"="+URI.escape(filterValue)
  restHttpGet(href)
  assert(@res != nil, "Response from rest-client GET is nil")
end


Then /^I should receive a collection of (\d+) ([\w-]+) link$/ do |arg1, arg2|
  @collectionType = "/"+arg2+"s/"
  if @format == "application/json" or @format == "application/vnd.slc+json"
    dataH=JSON.parse(@res.body)
    counter=0
    @ids = Array.new
    dataH.each do|link|
      if link["link"]["rel"]=="self" and link["link"]["href"].include? @collectionType
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

Then /^I should find Assessment with "([^"]*)"$/ do |assessmentId|
  found =false
  @ids.each do |id|
    if id==assessmentId
    found = true
    end
  end
  assert(found, "didnt find assessment with ID #{assessmentId}")
end

Then /^I should receive (\d+) assessment$/ do |arg1|
# only one assessment will be return from this uri
end

Then /^the "([^"]*)" is "([^"]*)"$/ do |key, value|
  if @format == "application/json" or @format == "application/vnd.slc+json"
    dataH=JSON.parse(@res.body)
    if key== "assessmentPeriodDescriptor.beginDate"
      beginDate = dataH["assessmentPeriodDescriptor"]["beginDate"]
    assert(beginDate==value, "received #{key} is #{beginDate} not expected #{value}")
    elsif  key== "assessmentPeriodDescriptor.endDate" 
      endDate = dataH["assessmentPeriodDescriptor"]["endDate"]
    assert(endDate==value, "received #{key} is #{endDate} not expected #{value}")
    else
    assert(dataH[key]==value, "received #{key} is #{dataH[key]} not expected #{value}")
    end
  elsif @format == "application/xml"
    assert(false, "application/xml is not supported")
  else
    assert(false, "Unsupported MIME type")
  end
end

Then /^the "([^"]*)" has the (\d+) levels$/ do |key, value|
  if @format == "application/json" or @format == "application/vnd.slc+json"
    dataH=JSON.parse(@res.body)
    assert(dataH[key].length==Integer(value), "received #{key} has #{dataH[key].length} levels, not expected #{value}")
  elsif @format == "application/xml"
    assert(false, "application/xml is not supported")
  else
    assert(false, "Unsupported MIME type")
  end
end

Then /^the "([^"]*)" = "([^"]*)"$/ do |key, value|
  found = false
  if key.include? "codeValue"
    if @format == "application/json" or @format == "application/vnd.slc+json"
      dataH=JSON.parse(@res.body)
      performanceLevels= Array.new
      performanceLevels = dataH["assessmentPerformanceLevel"]
      performanceLevels.each do |performanceLevel|
        if performanceLevel["performanceLevelDescriptor"]["codeValue"]==value
          found=true
          @performanceLevel = performanceLevel
        end
      end

    elsif @format == "application/xml"
      assert(false, "application/xml is not supported")
    else
      assert(false, "Unsupported MIME type")
    end
  elsif @performanceLevel["performanceLevelDescriptor"]["description"]==value
    found = true
  end
  assert(found, "received #{key} is not #{value}")
end

Then /^the "([^"]*)" = (\d+)$/ do |key, value|
  if key.include? "maximumScore"
    assert(@performanceLevel["maximumScore"]==Integer(value),"received #{key} is not #{value}")
  else
    assert(@performanceLevel["minimumScore"]==Integer(value),"received #{key} is not #{value}")
  end
end

Then /^the "([^"]*)" is (\d+)$/ do |key, value|
  if @format == "application/json" or @format == "application/vnd.slc+json"
    dataH=JSON.parse(@res.body)
    assert(dataH[key]==Integer(value), "received #{key} is #{dataH[key]} not expected #{value}")
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

Given /^I loop through the collection of student links$/ do
   # do nothing, each student will be accessed from @ids
end

When /^for each student, I navigate to GET "([^"]*)" with filter sorting and pagination$/ do |href|
  @hrefs=Array.new
  counter=0
  @studentIds.each do |id|
    @hrefs[counter]=href
    counter=counter+1
  end
end
  

When /^for each student, filter by "([^"]*)" = <'Current_student' ID>$/ do |key|
 counter = 0
  @studentIds.each do |id|
    @hrefs[counter]=@hrefs[counter]+"?studentId="+URI.escape(id)
    counter=counter+1
  end
end
   
When /^for each student, "([^"]*)" = "([^"]*)"$/ do |key, value|
  counter = 0
    @studentIds.each do |id|
    @hrefs[counter]=@hrefs[counter]+"&"+URI.escape(key)+"="+URI.escape(value)
    counter=counter+1
  end
end

Then /^for each student, I should receive a collection of (\d+) studentAssessmentAssociation link$/ do |value|
  counter = 0
  @studentIds.each do |id|
    restHttpGet(@hrefs[counter])
    assert(@res != nil, "Response from rest-client GET is nil")
    dataH=JSON.parse(@res.body)
    assert(dataH.length==Integer(value),"received a collection of #{dataH.length} studentAssessmentAssociation link, not #{value}")
    counter = counter+1
  end
end

Then /^for each student, I should receive a "([^"]*)" with ID "([^"]*)"$/ do |type, id|
  counter = 0
  found = false
  @saaIds = Array.new
  @studentIds.each do |id|
    restHttpGet(@hrefs[counter])
    assert(@res != nil, "Response from rest-client GET is nil")
    dataH=JSON.parse(@res.body)
    href = dataH[0]["link"]["href"]
    if href.include? type and dataH[0]["id"]!=nil
    found = true
    end
    assert(found, "didnt receive #{type}")
    @saaIds[counter]=dataH[0]["id"]
    counter = counter+1
  end
end

When /^for each student, I navigate to URI \/student\-assessment\-associations\/"([^"]*)"$/ do |arg1|
  @saaResults=Array.new
  counter=0
  @saaIds.each do |saaId|
  href="/student-assessment-associations/"+saaId
  restHttpGet(href)
  assert(@res!=nil,"Response from rest-client GET is nil")
  @saaResults[counter]=@res
  counter=counter+1
  end
end

Then /^for each student, I get (\d+) student\-assessment\-association$/ do |value|
  @saaResults.each do |saaResult|
  assert(saaResult!=nil,"didnt recieve student-assessment-association")
  end
end

Then /^for each student, the "([^"]*)" is "([^"]*)"$/ do |key, value|
  @saaResults.each do |saaResult|
    dataH=JSON.parse(saaResult)
    if key == "performanceLevelDescriptors.description"
      receivedValue = dataH["performanceLevelDescriptors"][0]["description"]
    elsif key == "scoreResults.assessmentReportingMethod"
      receivedValue = dataH["scoreResults"][0]["assessmentReportingMethod"]
    elsif key == "scoreResults.result"
      receivedValue = dataH["scoreResults"][0]["result"]
    else
      receivedValue = dataH[key]
    end
      assert(receivedValue==value,"the #{key} is #{receivedValue}, not expected #{value}")
  end
end