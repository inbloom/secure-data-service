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


require "selenium-webdriver"

require_relative '../../utils/sli_utils.rb'
require_relative '../../utils/selenium_common.rb'

Then /^I see the list of \(only\) my applications$/ do
  assert(@driver.find_elements(:xpath, "//tr").count > 0, "Should be more than one application listed")
end

Then /^I can see the on\-boarded states$/ do
  ['Midgar', 'Hyrule'].each {|tenantId|
    sliDb = @conn.db(convertTenantIdToDbName(tenantId))
    coll  = sliDb.collection('educationOrganization')
    seas  = coll.find('body.organizationCategories' => 'State Education Agency')
    puts "Looking for #{seas.count} SEAs of #{tenantId} on page!"
    seas.each { |sea|
      seaName = sea['body']['nameOfInstitution']
      puts " Looking for #{seaName} on page"
      seaOnPage = @driver.find_elements(:xpath, "//span[contains(., '#{seaName}')]")
      assert(seaOnPage.count > 0, " Looking for SEA #{seaName} of #{tenantId} on page. Found #{seaOnPage.count}.")
    }
  }
end

#
# NOTE: the actions and assertion below apply to a selection mode where
# the user selects a state from the dropdown, which triggers a refresh
# of the list of the (newly selected) state's districts for checkbox
# selection.  This mode is now obsolete

Then /^I see all of the Districts$/ do
  lis = @driver.find_elements(:css, 'div#enable-menu div#lea-menu table tbody tr')
  assert(lis.count >= 1, "One district should exist")
end

Then /^I check the Districts$/ do
  @driver.find_element(:link_text, 'Enable All').click
end

Then /^I uncheck the Districts$/ do
  @driver.find_element(:link_text, 'Disable All').click
end

When /^I click on Save$/ do
  @driver.find_element(:css, 'input:enabled[type="submit"]').click
end

