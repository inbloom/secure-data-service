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
require_relative '../../../../utils/sli_utils.rb'

Transform /the cohort "([^"]*)"/ do |arg1|
  id = "b40926af-8fd5-11e1-86ec-0021701f543f_id" if arg1 == "ACC-TEST-COH-2"
  id
end

When /^I make an API call to get (the cohort "[^"]*")$/ do |arg1|
  @format = "application/vnd.slc+json"
  restHttpGet("/v1/cohorts/"+arg1)
  assert(@res != nil, "Response from rest-client GET is nil")
end
