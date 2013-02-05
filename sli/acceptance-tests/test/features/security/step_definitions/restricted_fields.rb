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


Then /^I should not see the restricted field "([^\"]*)"$/ do |field|
  assert(@result[field] == nil, "The restricted field #{field} is visible")
end

Then /^I create request data out of the response$/ do
  @json_req_data = @result.to_json
  ["links", "_id"].each do |k|
    @result.delete(k)
  end
end

When /^I set parameter "([^\"]*)" to "([^\"]*)"$/ do |field, value|
  @result[field] = value
end

When /^I make an API call to update the student "([^\"]*)"$/ do |arg1|
  @format = "application/vnd.slc+json"
  student_uri ="/v1/students/"+arg1
  restHttpPut(student_uri, @result.to_json, "application/json")
  assert(@res != nil, "Response from rest-client PUT is nil")
end
