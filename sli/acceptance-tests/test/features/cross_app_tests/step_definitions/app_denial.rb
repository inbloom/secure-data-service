require_relative '../../admintools/step_definitions/app_authorization_steps.rb'

Then /^I am denied access to the sample app home page$/ do
  assert(@driver.find_elements(:xpath, "//td[text()='Mark Anthony']").size == 0, webdriverDebugMessage(@driver,"User couldn't access sample page"))
end

