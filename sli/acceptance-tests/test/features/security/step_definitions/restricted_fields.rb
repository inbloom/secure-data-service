Then /^I should not see the restricted field "([^\"]*)"$/ do |field|
  assert(@result[field] == nil, "The restricted field #{field} is visible")
end