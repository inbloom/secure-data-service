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


require_relative '../../admintools/step_definitions/app_authorization_steps.rb'
require_relative '../../admintools/step_definitions/app_registration_steps.rb'
require_relative '../../admintools/step_definitions/developer_enable_steps.rb'

Then /^I am denied access to the sample app home page$/ do
  assert(@driver.find_elements(:xpath, "//td[text()='Mark Anthony']").size == 0, webdriverDebugMessage(@driver,"User couldn't access sample page"))
end

