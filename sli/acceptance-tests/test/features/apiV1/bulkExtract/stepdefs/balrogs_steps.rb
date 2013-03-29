=begin

Copyright 2012 Shared Learning Collaborative, LLC

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

Given /^I am a valid 'service' user with an authorized long\-lived token "(.*?)"$/ do |token|
  @sessionId=token
end

When /^I make bulk extract API call$/ do
  restHttpGet("/bulk/extract")
end

Then /^I get expected tar downloaded$/ do  
  
  EXPECTED_BYTE_COUNT = 266240
  EXPECTED_CONTENT_TYPE = 'application/x-tar'
  
  assert(@res.headers[:content_type]==EXPECTED_CONTENT_TYPE, "Content Type must be #{EXPECTED_CONTENT_TYPE} was #{@res.headers[:content_type]}")
  assert(@res.headers[:content_length].to_i==EXPECTED_BYTE_COUNT, "File Size is wrong! Actual: #{@res.headers[:content_length]} Expected: #{EXPECTED_BYTE_COUNT}" )
end


#=============================================================

Given /^in my list of rights I have BULK_EXTRACT$/ do
  #  Explanatory step
end
