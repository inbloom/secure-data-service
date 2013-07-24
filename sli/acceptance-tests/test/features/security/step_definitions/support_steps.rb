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


require 'json'
require_relative '../../utils/sli_utils.rb'

When /^I make an API call to get the support email$/ do
  @format = "application/json"
  restHttpGet("/v1/system/support/email")
end

Then /^I receive JSON response that includes the address$/ do
  data = JSON.parse(@res.body)
  assert(data["email"] =~ /^(\w|-)+@(\w)+\.(\w)+$/, "Should have received an email address")
end
