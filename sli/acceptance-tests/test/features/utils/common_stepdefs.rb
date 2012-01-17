
Given /^I am logged in using "([^\"]*)" "([^\"]*)"$/ do |user, pass|
  @user = user
  @passwd = pass
end

Given /^I have access to all [^"]*$/ do
  idpLogin(@user, @passwd)
  assert(@sessionId != nil, "Session returned was nil")
end

Given /^format "([^\"]*)"$/ do |fmt|
  ["application/json", "application/xml", "text/plain"].should include(fmt)
  @format = fmt
end

Then /^I should receive a return code of (\d+)$/ do |arg1|
  assert(@res.code == Integer(arg1), "Return code was not expected: "+@res.code.to_s+" but expected "+ arg1)
end

Then /^I should receive an ID for the newly created ([\w-]+)$/ do |entity|
  headers = @res.raw_headers
  assert(headers != nil, "Headers are nil")
  assert(headers['location'] != nil, "There is no location link from the previous request")
  s = headers['location'][0]
  @newId = s[s.rindex('/')+1..-1]
  assert(@newId != nil, "After POST, #{entity} ID is nil")
end