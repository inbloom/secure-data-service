require_relative '../../utils/sli_utils.rb'

Then /^I see an app named "(.*?)"$/ do |arg1|
  assert(@res.code == 200, "Response code not expected: expected 200 but received "+@res.code.to_s)
  result = JSON.parse(@res.body)
  assert(result!= nil, "Result of JSON parsing is nil")
  found = false
  result.each do |app|
    if app["name"] == arg1
      found = true
    end
  end
  assert(found == true, "App named #{arg1} not found")
end

When /^I change my Linda Kim's yearsOfPriorTeachingExperience to "(.*?)"$/ do |arg1|
  @format = "application/json"
  restHttpGet("/v1/teachers")
  assert(@res.code == 200, "Response code not expected: expected 200 but received "+@res.code.to_s)
  result = JSON.parse(@res.body)
  assert(result!= nil, "Result of JSON parsing is nil")
  jsonObj = nil
  if result.class == Array
    jsonObj = result[0]
  else
    jsonObj = result
  end
  id = jsonObj["id"]
  jsonObj["yearsOfPriorProfessionalExperience"] = arg1.to_i
  restHttpPut("/v1/teachers/#{id}", jsonObj.to_json)
  assert(@res.code == 204, "Response code not expected: expected 204 but received "+@res.code.to_s)
end

Then /^My Linda Kim has "(.*?)" yearsOfPriorTeachingExperience$/ do |arg1|
  @format = "application/json"
  restHttpGet("/v1/teachers")
  assert(@res.code == 200, "Response code not expected: expected 200 but received "+@res.code.to_s)
  result = JSON.parse(@res.body)
  assert(result!= nil, "Result of JSON parsing is nil")
  if result.class == Array
    assert(arg1.to_i == result[0]["yearsOfPriorProfessionalExperience"] , "yearsOfPriorProfessionalExperience was #{result[0]["yearsOfPriorProfessionalExperience"].to_s} but expected #{arg1}")
  else
    assert(arg1.to_i == result["yearsOfPriorProfessionalExperience"] , "yearsOfPriorProfessionalExperience was #{result["yearsOfPriorProfessionalExperience"].to_s} but expected #{arg1}")
  end
end
