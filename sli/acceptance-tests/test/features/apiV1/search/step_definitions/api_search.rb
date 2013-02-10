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

require_relative '../../entities/crud/step_definitions/crud_step.rb'

###############################################################################
## TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM
################################################################################
#
Transform /^<([^"]*)>$/ do |human_readable_id|
  id = human_readable_id
  id = @newId                    if human_readable_id == "ENTITY ID"
 #return the translated value
  id
end

###############################################################################
## THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN
################################################################################
Then /^I should have entity with "(.*?)" and "(.*?)"$/ do |arg1, arg2|
 found_special_char = false
 @result.each do |record|
   id = record["id"]
   if record["id"] == arg1 
     desc = record["description"]
     assert( (desc == arg2), "Charecter encoding is not working with elastic search. Expected '" +arg2+ "' but received '"+desc+"'")
     found_special_char = true
     break
   end
 end
 assert(found_special_char, "Expected record not found for search")
end
