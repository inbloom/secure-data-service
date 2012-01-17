
Given /^I am logged in using "([^\"]*)" "([^\"]*)"$/ do |user, pass|
  @user = user
  @passwd = pass
end

Given /^I have access to all [^"]*$/ do
  idpLogin(@user, @passwd)
  assert(@sessionId != nil, "Session returned was nil")
end

Then /^I should receive a return code of (\d+)$/ do |arg1|
  assert(@res.code == Integer(arg1), "Return code was not expected: "+@res.code.to_s+" but expected "+ arg1)
end
