=begin

Copyright 2012-2013 inBloom, Inc. and its affiliates.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

=end

require_relative '../../simple_idp/step_definitions/MockIDP_steps.rb'

###############################################################################
# WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN
###############################################################################

When /^I click and go back to Home$/ do
  assertWithWait("Failed to find 'Home' Link on page")  {@driver.find_element(:link_text, "data browser")}
  @driver.find_element(:link_text, "data browser").click
  assertWithWait("Failed to be directed to Databrowser's Home page")  {@driver.page_source.include?("Listing Home")}
end

Then /^I log out of Databrowser$/ do
    steps %Q{
      Then I click on log out
    }
end
###############################################################################
# THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN
###############################################################################

Then /^I am notified that "([^\"]*)"$/ do |message|
  assertText(message)
end
