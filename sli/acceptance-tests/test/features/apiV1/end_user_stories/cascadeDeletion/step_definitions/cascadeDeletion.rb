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
  value = "schools"                              if human_readable_text == "SCHOOL URI"
  value = "sections"                             if human_readable_text == "SECTION URI"
  value = "teacherSectionAssociations"           if human_readable_text == "TEACHER SECTION ASSOCIATION URI"
  
  #IDs
  value = "bcfcc33f-f4a6-488f-baee-b92fbd062e8d" if human_readable_text == "TEACHER ID"
  value = "a189b6f2-cc17-4d66-8b0d-0478dcf0cdfb" if human_readable_text == "SCHOOL ID"
  value = "8ed12459-eae5-49bc-8b6b-6ebe1a56384f" if human_readable_text == "SECTION ID"
  value = "ba1908a8-e7b9-4984-93e8-4266d2d8675a" if human_readable_text == "TEACHER SECTION ASSOCIATION ID"
  
  #return this
  value
  
end
