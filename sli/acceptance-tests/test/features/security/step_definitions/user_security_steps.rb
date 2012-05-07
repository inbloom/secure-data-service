require_relative '../../utils/sli_utils.rb'

Given /^my role is SLI Administrator$/ do
  @format = "application/json"
  restHttpGet("/system/session/check", @format)
  check = JSON.parse(@res.body)
  assert("Should be an Educator", check["sliRoles"].include?("SLI Administrator"))
end

Then /^I should not see any teacher data$/ do
  teachers = JSON.parse(@res.body)
  assert("There shouldn't be any teachers", teachers.size == 0)
end

When /^I make an API call to access applications$/ do
  restHttpGet("/v1/apps", @format)
end

Then /^I do not get a (\d+)$/ do |arg1|
  assert("Status should NOT be #{arg1}", @res.code != Integer(arg1))
end