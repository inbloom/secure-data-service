Then /^the table includes header "([^"]*)"$/ do |arg1|
  desiredContentArray = arg1.split(";")
  #let's check that we'll get the right view within the timeout
  assert(desiredContentArray.length > 0)
  @explicitWait.until{@driver.find_element(:class,"ui-jqgrid-hbox")}
  assert(tableHeaderContains(arg1))
end
