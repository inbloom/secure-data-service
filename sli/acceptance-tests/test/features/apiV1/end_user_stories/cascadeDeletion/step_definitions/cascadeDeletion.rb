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
require 'builder'
require 'rexml/document'
include REXML
require_relative '../../../../utils/sli_utils.rb'
require_relative '../../../utils/api_utils.rb'


Transform /^<([^>]*)>$/ do |human_readable_text|
  #expose URIs
  value = "teachers"                             if human_readable_text == "TEACHER URI"
  value = "assessments"                          if human_readable_text == "ASSESSMENT URI"
  value = "schools"                              if human_readable_text == "SCHOOL URI"
  value = "sections"                             if human_readable_text == "SECTION URI"
  value = "teacherSectionAssociations"           if human_readable_text == "TEACHER SECTION ASSOCIATION URI"
  
  #IDs
  value = "bcfcc33f-f4a6-488f-baee-b92fbd062e8d"    if human_readable_text == "TEACHER ID"
  value = "e9ca4497-e1e5-4fc4-ac7b-24bad1f2998b"    if human_readable_text == "TEACHER ID 2"
  value = "a189b6f2-cc17-4d66-8b0d-0478dcf0cdfb"    if human_readable_text == "SCHOOL ID"
  value = "92d6d5a0-852c-45f4-907a-912752831772"    if human_readable_text == "SCHOOL ID 2" 
  value = "8ed12459-eae5-49bc-8b6b-6ebe1a56384f_id"    if human_readable_text == "SECTION ID"
  value = "15ab6363-5509-470c-8b59-4f289c224107_id" if human_readable_text == "SECTION ID 2"
  value = "8ed12459-eae5-49bc-8b6b-6ebe1a56384f_id"    if human_readable_text == "SECTION ID FOR ARRAY TEST"
  value = "15ab6363-5509-470c-8b59-4f289c224107_id" if human_readable_text == "SECTION ID 2 FOR ARRAY TEST"
  value = "2108c0c84ca6998eb157e1efd4d894746e1fdf8b_id"    if human_readable_text == "ASSESSMENT ID 1 FOR ARRAY TEST"
  value = "9708ea11dbca2707013ed5bfdaad77bfb79d08d5_id"    if human_readable_text == "ASSESSMENT ID 2 FOR ARRAY TEST"
  value = "8ed12459-eae5-49bc-8b6b-6ebe1a56384f_idba1908a8-e7b9-4984-93e8-4266d2d8675a_id"    if human_readable_text == "TEACHER SECTION ASSOCIATION ID"
  value = "15ab6363-5509-470c-8b59-4f289c224107_id32b86a2a-e55c-4689-aedf-4b676f3da3fc_id" if human_readable_text == "TEACHER SECTION ASSOCIATION ID 2"
  
  #return this
  value
  
end


###############################################################################
# THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN
###############################################################################

Then /^I should see (\d+) "(.*?)"$/ do |array_length, array_name|
    @result[array_name].length.should == array_length.to_i
end

Then /^one value in "(.*?)" should be "(.*?)"$/ do |array_name, value|
    assert(@result[array_name].include?(value), "Entity does not contain expected value in array.")
end
