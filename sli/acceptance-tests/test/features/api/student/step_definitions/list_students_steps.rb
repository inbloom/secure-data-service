Then /^I should see (\d+) students$/ do |arg1|
  result = JSON.parse(@res.body)
  assert(result.size == Integer(arg1), "Expected "+ arg1 +" students but received " + result.size.to_s)
end

Then /^I should not find "([^"]*)"$/ do |arg1|
  pending # express the regexp above with the code you wish you had
end

Then /^In the list I should see the student "([^"]*)" "([^"]*)" "([^"]*)"$/ do |arg1, arg2, arg3|
  found = false
  result = JSON.parse(@res.body)
  assert(result != nil, "Result of JSON parsing is nil")
  result.each do |student|
    url = "http://"+PropLoader.getProps['api_server_url']+"/api/rest/students/"+ student['id']
    temp = RestClient.get(url,{:accept => @format, :cookies => {:sliSessionId => @cookie}}){|response, request, result| response }
    result2 = JSON.parse(temp.body)
    assert(result2 != nil, "Result of JSON parsing is nil")
    name = result2['name']
    if(name['firstName'] == arg1 && name['middleName'] == arg2 && name['lastSurname'] == arg3)
      found = true
      break
    end
  end
  assert(found, "Student " + arg1 + " " + arg2 + " " + arg3 + " was not found")
end