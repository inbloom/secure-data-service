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

###############################################################################
# WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN
###############################################################################

When /^the developer selects to preload "(.*?)"$/ do |sample_data_set|
  if sample_data_set.downcase.include? "small"
    sample_data_set="small"
  else
    sample_data_set="medium"
  end
  @explicitWait.until{@driver.find_element(:id,"ed_org_from_sample").click}
  select = Selenium::WebDriver::Support::Select.new(@explicitWait.until{@driver.find_element(:id,"sample_data_select")})
  select.select_by(:value, sample_data_set)
  @explicitWait.until{@driver.find_element(:id,"provisionButton").click}
end
