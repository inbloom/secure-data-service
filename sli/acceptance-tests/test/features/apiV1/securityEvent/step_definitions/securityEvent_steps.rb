require 'rest-client'
require 'json'
require 'builder'
require 'rexml/document'
include REXML
require_relative '../../../utils/sli_utils.rb'
require_relative '../../utils/api_utils.rb'

###############################################################################
# TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM
###############################################################################

Transform /^<([^"]*)>$/ do |human_readable_id|
  #securityEvent data
  id = "securityEvent"                          if human_readable_id == "ENTITY TYPE"
  id = "/securityEvent"                         if human_readable_id == "ENTITY URI"
  #return the translated value
  id
end
