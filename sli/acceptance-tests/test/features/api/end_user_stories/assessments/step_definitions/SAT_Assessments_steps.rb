require 'rest-client'

require_relative '../../../../utils/sli_utils.rb'

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

Then /^the first one is "([^"]*)" = "([^"]*)"$/ do |arg1, arg2|
  pending # express the regexp above with the code you wish you had
end

Then /^the first one is "([^"]*)" = (\d+)$/ do |arg1, arg2|
  pending # express the regexp above with the code you wish you had
end

Then /^the second one is"([^"]*)" = "([^"]*)"$/ do |arg1, arg2|
  pending # express the regexp above with the code you wish you had
end

Then /^the second one is  "([^"]*)" = "([^"]*)"$/ do |arg1, arg2|
  pending # express the regexp above with the code you wish you had
end

Then /^the second one is "([^"]*)" = (\d+)$/ do |arg1, arg2|
  pending # express the regexp above with the code you wish you had
end

Then /^the third one is "([^"]*)" = "([^"]*)"$/ do |arg1, arg2|
  pending # express the regexp above with the code you wish you had
end

Then /^the third one is "([^"]*)" = (\d+)$/ do |arg1, arg2|
  pending # express the regexp above with the code you wish you had
end