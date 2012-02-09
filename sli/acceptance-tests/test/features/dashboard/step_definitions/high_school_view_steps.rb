Then /^the table includes header "([^"]*)"$/ do |arg1|
  assert(tableHeaderContains(arg1))
end
