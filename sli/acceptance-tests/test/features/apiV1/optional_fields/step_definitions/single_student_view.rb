require 'rest-client'
require 'json'
require_relative '../../../utils/sli_utils.rb'
require_relative '../../entities/common.rb'

###############################################################################
# TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM
###############################################################################

Transform /^<(.+)>$/ do |template|
  id = template
  id = "74cf790e-84c4-4322-84b8-fca7206f1085" if template == "STUDENT_ID"
  id
end