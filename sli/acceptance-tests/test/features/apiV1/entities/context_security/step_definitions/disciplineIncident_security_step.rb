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

Transform /the disciplineIncident "([^"]*)"/ do |arg1|
  id = "0e26de79-222a-5e67-9201-5113ad50a03b" if arg1 == "DISC-INC-2"
  id = "0e26de79-22ea-5d67-9201-5113ad50a03b" if arg1 == "DISC-INC-1"
  id
end

When /^I make an API call to get (the disciplineIncident "[^"]*")$/ do |arg1|
  @format = "application/vnd.slc+json"
  restHttpGet("/v1/disciplineIncidents/"+arg1)
  assert(@res != nil, "Response from rest-client GET is nil")
end
