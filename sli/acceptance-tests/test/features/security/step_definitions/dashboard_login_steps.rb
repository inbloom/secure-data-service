Given /^I have an open web browser$/ do
  @driver = Selenium::WebDriver.for :firefox
end

Given /^I am not authenticated to SLI$/ do
  pending # express the regexp above with the code you wish you had
end

Given /^I am authenticated to SLI$/ do
  pending # express the regexp above with the code you wish you had
end

When /^I type the Dashboard home page$/ do
  @url = "https://"+PropLoader.getProps['api_server_url']+"/dashboard/login" 
end

When /^I click on the Enter button$/ do
  @driver.get @url if @url
end

Then /^I should be redirected to the Realm page$/ do
  assert(@driver.current_url != @url, "Failed to get redirected from "+@url)
end

Then /^I should be redirected to the Dashboard home page$/ do
  assert(@driver.current_url == @url, "Failed to navigate to "+@url)
end

Given /^I have tried to access the Dashboard home page$/ do
  pending # express the regexp above with the code you wish you had
end

Given /^was redirected to the Realm page$/ do
  pending # express the regexp above with the code you wish you had
end

Given /^I chose "([^"]*)"$/ do |arg1|
  pending # express the regexp above with the code you wish you had
end

Given /^I clicked the button Go$/ do
  pending # express the regexp above with the code you wish you had
end

Given /^I was redirected to the SLI IDP Login page$/ do
  pending # express the regexp above with the code you wish you had
end

Then /^I am redirected to the Dashboard home page$/ do
  pending # express the regexp above with the code you wish you had
end

Then /^I am informed that "([^"]*)" does not exists$/ do |arg1|
  pending # express the regexp above with the code you wish you had
end

Then /^I am redirected to the SLI\-IDP Login Page$/ do
  pending # express the regexp above with the code you wish you had
end