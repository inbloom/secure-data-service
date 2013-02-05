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


require_relative '../../../utils/sli_utils.rb'
require "selenium-webdriver"

Then /^the scale score for assessment "([^"]*)" for student "([^"]*)" is "([^"]*)"$/ do |arg1, arg2, arg3|
  studentCell = getStudentCell(arg2)
  label = getAttribute(studentCell,"assessments."+arg1+".Scale score")
  assert(label == arg3, "Score : " + label + ", expected " + arg3)
  
end
