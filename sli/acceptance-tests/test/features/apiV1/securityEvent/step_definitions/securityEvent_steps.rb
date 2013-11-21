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
require_relative '../../../utils/sli_utils.rb'
require_relative '../../utils/api_utils.rb'
require_relative '../../../security/step_definitions/admin_delegation_crud_steps.rb'

###############################################################################
# TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM
###############################################################################

Transform /^<([^"]*)>$/ do |human_readable_id|
  #securityEvent data
  if human_readable_id == "ENTITY TYPE" then
    "securityEvent"
  elsif human_readable_id == "ENTITY URI" then
    "/securityEvent"
  elsif human_readable_id == "IL_OR_IL_SUNSET" then
    ["IL", "IL-SUNSET"]
  elsif human_readable_id == "IL_SUNSET_LONGWOOD" then
    ["IL", "IL-SUNSET", "IL-LONGWOOD"]
  elsif human_readable_id == "IL_SUNSET_OR_IL_LONGWOOD_OR_SUNSET_CENTRAL" then
    ["IL-SUNSET", "IL-LONGWOOD", "Sunset Central High School"]
  elsif human_readable_id == "IL"
    ["IL"]
  else
    raise "unrecognized pattern for transform, #{human_readable_id}"
  end
end

###########################
# Regular step definitions
###########################

# use the transform above to produce a list of edOrg state unique ids instead of a string
# for the matched parameter
Then /^each securityEvent's targetEdOrgList should contain at least one of "([^"]+)"/ do |edOrgList|
  @result.each do |securityEvent|
    assert(securityEvent.has_key?('targetEdOrgList'), "the entity should have a targetEdOrgList attribute")

    edOrgNamesIncluded = 0
    edOrgList.each do |edOrgName|
      if securityEvent['targetEdOrgList'].include?(edOrgName) then
        edOrgNamesIncluded += 1
      end
    end

    assert(edOrgNamesIncluded > 0,
      "Did not see any of #{edOrgList} ed org names in targetEdOrgList for security event with id #{securityEvent['id']}")
  end
end