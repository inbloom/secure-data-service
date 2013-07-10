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


require 'rest-client'
require 'json'
require_relative '../../../utils/sli_utils.rb'
require_relative '../../entities/common.rb'
require_relative '../../../security/step_definitions/securityevent_util_steps.rb'

##############################################################################
# TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM
##############################################################################

Transform /^<(.+)>$/ do |template|
  id = template
  id = "5738d251-dd0b-4734-9ea6-417ac9320a15_id" if template == "MATT SOLLARS ID"
  id = "e04118fd-5025-4d3b-b58d-3ed0d4f270a6_id" if template == "CARMEN ORTIZ ID"
  id = "74cf790e-84c4-4322-84b8-fca7206f1085_id" if template == "MARVIN MILLER ID"
  id = "a03d1325-c70f-4132-aa1c-54ebd2692a75_id" if template == "CHEROKEE STUART ID"
  id = "11111111-1111-1111-1111-111111111111" if template == "INVALID ID"
  id = "8e3fafa4-31be-4b3d-8349-e8ef41327b65_id" if template == "INACCESSABLE ID" #Nicholas Berks
  id = @student_ids_list                      if template == "STUDENT IDs LIST"
  id
end

Transform /^(<.+>),(<.+>),(<.+>)$/ do |id1, id2, id3|
  id_list = Transform(id1) + "," + Transform(id2) + "," + Transform(id3)
  id_list
end

##############################################################################
# GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN
##############################################################################

Given /^the order of students I want is ([^\"]*)$/ do |student_ids|
  @student_ids_list = Transform(student_ids)
end

##############################################################################
# THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN
##############################################################################

Then /^the response at position (\d)+ should include the information (.+)$/ do |position, string|
  assert(@result[convert(position)].to_s.include?(string), "Can't find the specified string \"#{string}\" at position #{position}")
end

Then /^I should see a total of (\d+) entities$/ do |arg1|
  if @result.class == {}.class
    size = 0
  else
    size = @result.size
  end
  assert(size == convert(arg1), "Expected to see #{arg1} entities, actual number #{@result.size}")
end
