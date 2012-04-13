require 'json'
require_relative '../../utils/sli_utils.rb'
require_relative '../../utils/common_stepdefs.rb'


Transform /^the data for "([^"]*)"$/ do |path|
  id = "/schools/PUT MONGO ID HERE" if path == "/schools/Daybreak"
  id = "/schools/PUT MONGO ID HERE" if path == "/schools/Sunset"
end


When /^I try to access the data for "([^"]*)" in my "([^"]*)" from the API$/ do |dataPath|
  @res = restHttpGet(dataPath)
end

Then /^I get the data returned in json format$/ do
  assert(@res.code == 200, "Received a #{@res.code} response from the request, expected 200")
  assert(@res != nil, "Did not receive a response from the API")
  result = JSON.parse(@res.body)
  assert(result != nil)
end

When /^I try to access the data for "([^"]*)" in another "([^"]*)" from the API$/ do |dataPath, level|
  @res = restHttpGet(dataPath)
end

When /^I try to update the data for "([^"]*)" in another "([^"]*)" from the API$/ do |dataPath, level|
  @path = dataPath
  @originalObj = JSON.parse(restHttpGet(@path).body)
  assert(@originalObj != nil, "Could not get the existing JSON body")
  @updatedObj = @originalObj.clone
  @updatedObj["nameOfInstitution"] = "Updated Name"
  @updatedObj["schoolCategories"] = ["High School", "Middle School", "Elementary School"]
  @res = restHttpPost(dataPath, @updatedObj)
end


Then /^the data should be updated$/ do
  current = JSON.parse(restHttpGet(@path).body)
  assert(current != nil, "Could not get the JSON object for #{@path}")
  assert(current == @updatedObj, "Unsuccesful update to #{@path}")
end

Then /^the data should not have changed$/ do
  current = JSON.parse(restHttpGet(@path).body)
  assert(current != nil, "Could not get the JSON object for #{@path}")
  assert(current == @originalObj, "The data should not have updated")
end

Given /^my State is "([^"]*)"$/ do |arg1|
  # No code needed, this step is informational
end

When /^I try to update the data for "([^"]*)" in my "([^"]*)" from the API$/ do |dataPath, level|
  step "I try to update the data for \"#{dataPath}\" in another \"#{level}\""
end

Given /^my "([^"]*)" is "([^"]*)"$/ do |level, name|
  # No code needed, this step is informational
end
