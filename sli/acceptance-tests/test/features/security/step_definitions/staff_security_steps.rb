require_relative '../../utils/sli_utils.rb'

Given /^my role is Educator$/ do
  @format = "application/json"
  restHttpGet("/system/session/check", @format)
  check = JSON.parse(@res.body)
  assert(check["sliRoles"].include?("Educator"), "Should be an Educator") 
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
  assert(!@staff.nil?, "Should have a valid list of entities.") 
end

Then /^the response does not includes the protected fields$/ do

  restHttpGet("/home", "application/json")
  home = JSON.parse(@res.body)
  home['links'].each do |link|
    if link['rel'] == 'self'
      $my_id = link['href'].split('/').last
    end
  end

  @staff.each do |staff|
    next if staff['id'] == $my_id
    assert(!staff.has_key?("birthDate"), "Shouldn't see fields like BirthDay")
    staff['telephone'].each do |phone|
      type = phone['telephoneNumberType']
      assert(phone == nil || type == 'Work', 'Should not see non-work telephone')
    end
    staff['electronicMail'].each do |email|
      type = email['emailAddressType']
      assert(email == nil || type == 'Work', 'Should not see non-work email')
    end
  end
end

Given /^my role is Leader$/ do
  @format = "application/json"
  restHttpGet("/system/session/check", @format)
  check = JSON.parse(@res.body)
  assert(check["sliRoles"].include?("Leader"), "Should be an Educator") 
end

Then /^the response includes the protected fields$/ do
  restHttpGet("/home", "application/json")
  home = JSON.parse(@res.body)
  home['links'].each do |link|
    if link['rel'] == 'self'
      $my_id = link['href'].split('/').last
    end
  end

  found_protected = false
  @staff.each do |staff|
    next if staff['id'] == $my_id
    found_protected = true if staff.has_key?('birthDate')
  end
  assert(found_protected, "Should have staff with protected fields (birthDate)")


  found_protected = false
  @staff.each do |staff|
    next if staff['id'] == $my_id
    staff['telephone'].each do |phone|
      type = phone['telephoneNumberType']
      found_protected = true if phone != nil && type != 'Work'
    end
  end
  assert(found_protected, 'Should have response with protected fields (telephone)')

  found_protected = false
  @staff.each do |staff|
    next if staff['id'] == $my_id
    staff['electronicMail'].each do |email|
      type = email['emailAddressType']
      found_protected = true if email != nil && type != 'Work'
    end
  end
  assert(found_protected, 'Should have response with protected fields (email)')

end

Then /^I should see my restricted information$/ do
  assert(@staff.has_key?("birthDate"), "Should see their own birthday")
end

When /^I make an API call to access myself$/ do
  restHttpGet("/v1/teachers/67ed9078-431a-465e-adf7-c720d08ef512", @format)
  @staff = JSON.parse(@res.body)
end
