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
#require_relative '../../entities/common.rb'
require_relative '../../utils/api_utils.rb'

###############################################################################
# TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM
###############################################################################

Transform /^<(.+)>$/ do |template|
  id = template
  id = "74cf790e-84c4-4322-84b8-fca7206f1085_id" if template == "MARVIN MILLER STUDENT ID"
  id = "0c2756fd-6a30-4010-af79-488d6ef2735a_id" if template == "ROBIN MCWAIN STUDENT ID"
  id = "ef28485d-ce82-4f13-b22e-7c9e29f8f69f_id193b3d2e-bef0-467a-8bed-166f66f0517a_id" if template == "STUDENT SECTION ASSOCIATION ID 1"
  id = "baffb6f7-6d30-4341-b29e-0e1cd73ea2bf_id11b75359-811d-4234-8ef2-ce14dd472a7c_id" if template == "STUDENT SECTION ASSOCIATION ID 2"
  id = "1d345e41-f1c7-41b2-9cc4-9898c82faeda_id08d9c069-63bc-4299-9eeb-2c7d2dfc6a22_id" if template == "STUDENT SECTION ASSOCIATION ID 3"
  id = "11111111-1111-1111-1111-111111111111" if template == "INVALID ID"
  id
end

Transform /^\[(.+)\]$/ do |template|
  val = template.split(',')
  val
end

###############################################################################
# WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN
###############################################################################

When /^I change the field "([^\"]*)" to "([^\"]*)"$/ do |field, value|
  @patch_body = Hash.new if !defined?(@patch_body)
  @patch_body["#{field}"] = value
end
