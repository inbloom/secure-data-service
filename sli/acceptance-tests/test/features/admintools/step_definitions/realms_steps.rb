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

# TODO: Remove this file when I replace the remaining ugly steps

require "selenium-webdriver"

require_relative '../../utils/sli_utils.rb'
require_relative '../../utils/selenium_common.rb'

When /^I click on the Custom Roles button next to Illinois Daybreak School District 4529$/ do
  @driver.find_element(:xpath, '//*[@id="45b02cb0-1bad-4606-a936-094331bd47fe"]/td[3]/a[2]').click
end
