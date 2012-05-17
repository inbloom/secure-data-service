
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
When /^I query "([^"]*)" to demonstrate "([^"]*)"$/ do |resource_name, test_type|
  step "I navigate to GET \"/<#{resource_name}>\""
end
