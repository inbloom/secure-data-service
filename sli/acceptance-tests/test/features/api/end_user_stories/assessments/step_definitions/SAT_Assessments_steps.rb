require 'rest-client'

require_relative '../../../../utils/sli_utils.rb'

When /^I navigate to GET "([^"]*)" and filter by studentId is "([^"]*)"$/ do |href, id|
  restHttpGet(href+"?studentId="+URI.escape(id))
  assert(@res != nil, "Response from rest-client GET is nil")
end

When /^I navigate to URI \/student\-assessment\-associations\/"([^"]*)"$/ do |arg1|
  restHttpGet("/student-assessment-associations/"+@saaId)
  assert(@res != nil, "Response from rest-client GET is nil")
end

Then /^I get (\d+) student\-assessment\-association$/ do |arg1|
  #only one student assessment association can be return from this uri
end

Then /^I should receive a "([^"]*)" with ID "([^"]*)"$/ do |rel, id|
   if @format == "application/json" or @format == "application/vnd.slc+json"
    dataH=JSON.parse(@res.body)
    entityType = dataH[0]["entityType"]
    assert(entityType==rel,"didnt receive #{rel} in response")
    @saaId=dataH[0]["id"]
  elsif @format == "application/xml"
    assert(false, "application/xml is not supported")
  else
    assert(false, "Unsupported MIME type")
  end
end

Then /^the assessment has an array of (\d+) objectiveAssessments$/ do |num|
  if @format == "application/json" or @format == "application/vnd.slc+json"
    dataH=JSON.parse(@res.body)
    counter = dataH["objectiveAssessment"].length
    assert(counter==Integer(num),"the objectiveAssessments are #{counter} not expected #{num}")
  elsif @format == "application/xml"
    assert(false, "application/xml is not supported")
  else
    assert(false, "Unsupported MIME type")
  end
end

Then /^the "([^"]*)" has the (\d+) entries$/ do |key, num|
  if @format == "application/json" or @format == "application/vnd.slc+json"
    dataH=JSON.parse(@res.body)
    counter = dataH[key].length
    assert(counter==Integer(num),"the #{key} has #{counter} entries not expected #{num}")
  elsif @format == "application/xml"
    assert(false, "application/xml is not supported")
  else
    assert(false, "Unsupported MIME type")
  end
end

Then /^the first one is "([^"]*)" = "([^"]*)"$/ do |key, value|
  if @format == "application/json" or @format == "application/vnd.slc+json"
    dataH=JSON.parse(@res.body)
    keys=key.split('.')
    if keys.length == 2
      fieldValue = dataH[keys[0]][0][keys[1]]
    elsif keys.length == 3
      if keys[1] == "scoreResults" and (value == "Scale score" or value == "680")
      fieldValue = dataH[keys[0]][0][keys[1]][0][keys[2]]
      elsif keys[1] == "scoreResults" and (value == "Percentile score" or value == "80")
        fieldValue =dataH[keys[0]][0][keys[1]][1][keys[2]]
      else
        fieldValue = dataH[keys[0]][0][keys[1]][keys[2]]
      end
    end
    assert(fieldValue==value,"#{key} is #{fieldValue} not expected #{value}")
  elsif @format == "application/xml"
    assert(false, "application/xml is not supported")
  else
    assert(false, "Unsupported MIME type")
  end
end

Then /^the first one is "([^"]*)" = (\d+)$/ do |key, value|
  if @format == "application/json" or @format == "application/vnd.slc+json"
    dataH=JSON.parse(@res.body)
    keys=key.split('.')
    if keys.length == 2
      fieldValue = dataH[keys[0]][0][keys[1]]
    elsif keys.length == 3
      fieldValue = dataH[keys[0]][0][keys[1]][keys[2]]
    end
    assert(fieldValue==Integer(value),"#{key} is #{fieldValue} not expected #{value}")
  elsif @format == "application/xml"
    assert(false, "application/xml is not supported")
  else
    assert(false, "Unsupported MIME type")
  end
end

Then /^the second one is "([^"]*)" = "([^"]*)"$/ do |key, value|
  if @format == "application/json" or @format == "application/vnd.slc+json"
    dataH=JSON.parse(@res.body)
    keys=key.split('.')
    if keys.length == 2
      fieldValue = dataH[keys[0]][1][keys[1]]
    elsif keys.length == 3
       if keys[1] == "scoreResults" and (value == "Scale score" or value == "680")
      fieldValue = dataH[keys[0]][1][keys[1]][0][keys[2]]
      elsif keys[1] == "scoreResults" and (value == "Percentile score" or value == "80")
        fieldValue = dataH[keys[0]][1][keys[1]][1][keys[2]]
      else
        fieldValue = dataH[keys[0]][1][keys[1]][keys[2]]
      end
    end
    assert(fieldValue==value,"#{key} is #{fieldValue} not expected #{value}")
  elsif @format == "application/xml"
    assert(false, "application/xml is not supported")
  else
    assert(false, "Unsupported MIME type")
  end
end


Then /^the second one is "([^"]*)" = (\d+)$/ do |key, value|
  if @format == "application/json" or @format == "application/vnd.slc+json"
    dataH=JSON.parse(@res.body)
    keys=key.split('.')
    if keys.length == 2
      fieldValue = dataH[keys[0]][1][keys[1]]
    elsif keys.length == 3
      fieldValue = dataH[keys[0]][1][keys[1]][keys[2]]
    end
    assert(fieldValue==Integer(value),"#{key} is #{fieldValue} not expected #{value}")
  elsif @format == "application/xml"
    assert(false, "application/xml is not supported")
  else
    assert(false, "Unsupported MIME type")
  end
end

Then /^the third one is "([^"]*)" = "([^"]*)"$/ do |key, value|
  if @format == "application/json" or @format == "application/vnd.slc+json"
    dataH=JSON.parse(@res.body)
    keys=key.split('.')
    if keys.length == 2
      fieldValue = dataH[keys[0]][2][keys[1]]
    elsif keys.length == 3
       if keys[1] == "scoreResults" and (value == "Scale score" or value == "680")
      fieldValue = dataH[keys[0]][2][keys[1]][0][keys[2]]
      elsif keys[1] == "scoreResults" and (value == "Percentile score" or value == "80")
        fieldValue = dataH[keys[0]][2][keys[1]][1][keys[2]]
      else
        fieldValue = dataH[keys[0]][2][keys[1]][keys[2]]
      end
    end
    assert(fieldValue==value,"#{key} is #{fieldValue} not expected #{value}")
  elsif @format == "application/xml"
    assert(false, "application/xml is not supported")
  else
    assert(false, "Unsupported MIME type")
  end
end

Then /^the third one is "([^"]*)" = (\d+)$/ do |key, value|
  if @format == "application/json" or @format == "application/vnd.slc+json"
    dataH=JSON.parse(@res.body)
    keys=key.split('.')
    if keys.length == 2
      fieldValue = dataH[keys[0]][2][keys[1]]
    elsif keys.length == 3
      fieldValue = dataH[keys[0]][2][keys[1]][keys[2]]
    end
    assert(fieldValue==Integer(value),"#{key} is #{fieldValue} not expected #{value}")
  elsif @format == "application/xml"
    assert(false, "application/xml is not supported")
  else
    assert(false, "Unsupported MIME type")
  end
end

Then /^I get a collection of (\d+) student\-assessment\-associations links$/ do |num|
   if @format == "application/json" or @format == "application/vnd.slc+json"
    dataH=JSON.parse(@res.body)
    counter = dataH.length
    assert(counter==Integer(num),"received a collection of #{counter} not expected #{num}")
  elsif @format == "application/xml"
    assert(false, "application/xml is not supported")
  else
    assert(false, "Unsupported MIME type")
  end
end