require_relative '../../utils/sli_utils.rb'

Given /^my role is Educator$/ do
  @format = "application/json"
  restHttpGet("/system/session/check", @format)
  check = JSON.parse(@res.body)
  assert("Should be an Educator", check["sliRoles"].include?("Educator"))
end

When /^I make an API call to access teachers$/ do
  restHttpGet("/v1/teachers", @format)
  @staff = JSON.parse(@res.body)
end

When /^I make an API call to access staff$/ do
  restHttpGet("/v1/staff", @format)
  @staff = JSON.parse(@res.body)
end

Then /^I get a response$/ do
  assert("Should have a valid list of entities.", !@staff.nil?)
end

Then /^the response does not includes the protected fields$/ do
  @staff.each do |staff|
    assert("Shouldn't see fields like BirthDay", !staff.has_key?("birthDate"))
  end
end

Given /^my role is Leader$/ do
  @format = "application/json"
  restHttpGet("/system/session/check", @format)
  check = JSON.parse(@res.body)
  assert("Should be an Educator", check["sliRoles"].include?("Leader"))
end

Then /^the response includes the protected fields$/ do
  @staff.each do |staff|
    assert("Shouldn't see fields like BirthDay", staff.has_key?("birthDate"))
  end
end

Then /^I should see my restricted information$/ do
  assert("Should see their own birthday", @staff.has_key?("birthDate"))
end

When /^I make an API call to access myself$/ do
  restHttpGet("/v1/teachers/67ed9078-431a-465e-adf7-c720d08ef512", @format)
  @staff = JSON.parse(@res.body)
end
