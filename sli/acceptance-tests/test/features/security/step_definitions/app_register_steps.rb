require_relative '../../utils/sli_utils.rb'

Transform /^([^"]*)<([^"]*)>$/ do |arg1, arg2|
  id = arg1+"fm67sH6vZZ" if arg2 == "Testing App"
  id = arg1+@newId       if arg2 == "New App ID"
  #id = step_arg if id == nil
  id
end

When /^I navigate to POST "([^"]*)"$/ do |arg1|
  @format = "application/json"
  dataObj = DataProvider.getValidAppData()
  data = prepareData("application/json", dataObj)

  restHttpPost(arg1, data)

  assert(@res != nil, "Response from POST operation was null")
end

Then /^I should receive the data for the specified application entry$/ do
  assert(@res.code == 200, "Return code was not expected: "+@res.code.to_s+" but expected 200")
  result = JSON.parse(@res.body)
  assert(result != nil, "Result of JSON parsing is nil")
end

When /^I navigate to PUT "([^"]*)"$/ do |arg1|
  @format = "application/json"
  dataObj = DataProvider.getValidAppData()
  dataObj["description"] = "New and Improved"
  data = prepareData("application/json", dataObj)

  restHttpPut(arg1, data)

  assert(@res != nil, "Response from PUT operation was null")
end

Then /^I should no longer be able to get that application's data'$/ do
  @format = "application/json"
  restHttpGet("/apps/#{@newId}")
  
  assert(@res != nil, "Response from PUT operation was null")
  assert(@res.code == 404, "Return code was not expected: "+@res.code.to_s+" but expected 404")
end

When /^I POST an application specifying an invalid field$/ do
  @format = "application/json"
  dataObj = DataProvider.getValidAppData()
  dataObj["foo"] = "A Bar App"
  data = prepareData("application/json", dataObj)

  restHttpPost("/apps/", data)

  assert(@res != nil, "Response from POST operation was null")
end

When /^I POST an application specifying the auto\-generated field "([^"]*)"$/ do |arg1|
  @format = "application/json"
  dataObj = DataProvider.getValidAppData()
  dataObj[arg1] = "fm67sH6vZZ"
  data = prepareData("application/json", dataObj)

  restHttpPost("/apps/", data)

  assert(@res != nil, "Response from POST operation was null")
end

When /^I PUT an application specifying the auto\-generated field "([^"]*)"$/ do |arg1|
  pending # express the regexp above with the code you wish you had
end