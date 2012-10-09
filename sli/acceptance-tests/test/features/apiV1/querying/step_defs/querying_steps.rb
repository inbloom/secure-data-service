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
require_relative '../../entities/common.rb'
require_relative '../../utils/api_utils.rb'


###############################################################################
# TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM
###############################################################################

Transform /^<(.*)>$/ do |resource_name|
   data = resource_name
end

###############################################################################
# WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN
###############################################################################
When /^I query "([^"]*)" of "([^"]*)" to demonstrate "([^"]*)"$/ do |resource_name, school_id, test_type|
  if school_id == ""
    step "I navigate to GET \"/<#{resource_name}>\""
  else
    step "I navigate to GET \"/<schools/#{school_id}/#{resource_name}>\""
  end
end
