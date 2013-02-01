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


require_relative '../../utils/sli_utils.rb'

Given /^my role is SLI Administrator$/ do
  @format = "application/json"
  restHttpGet("/system/session/check", @format)
  check = JSON.parse(@res.body)
  assert("Should be an Educator", check["sliRoles"].include?("SLI Administrator"))
end

Then /^I should not see any teacher data$/ do
  teachers = JSON.parse(@res.body)
  assert("There shouldn't be any teachers", teachers.size == 0)
end

When /^I make an API call to access applications$/ do
  restHttpGet("/v1/apps", @format)
end

Then /^I do not get a (\d+)$/ do |arg1|
  assert("Status should NOT be #{arg1}", @res.code != Integer(arg1))
end
