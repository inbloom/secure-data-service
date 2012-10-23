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

require_relative '../../associations/crud/step_definitions/assoc_crud.rb'
require 'mongo'

###############################################################################
# BEFORE BEFORE BEFORE BEFORE BEFORE BEFORE BEFORE BEFORE BEFORE BEFORE BEFORE
###############################################################################

Before do
  @midgar_db_name = convertTenantIdToDbName('Midgar')
end

###############################################################################
# TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM
###############################################################################

Transform /^<([^"]*)>$/ do |human_readable_id|

  #general
  id = @newId                                 if human_readable_id == "NEW ID"
  id = "74cf790e-84c4-4322-84b8-fca7206f1085" if human_readable_id == "MARVIN MILLER"
  id = "ceffbb26-1327-4313-9cfc-1c3afd38122e" if human_readable_id == "8TH GRADE ENGLISH SEC 6"

  #return the translated value
  id
end

###############################################################################
# GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN
###############################################################################

Given /^a valid json document for a ([^"]*)$/ do |entity|
  @fields = deep_copy($entity_data[entity])
end

###############################################################################
# WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN
###############################################################################

When /^I look at "([^\"]*)" in the "([^\"]*)"$/ do |id, coll|
  conn = Mongo::Connection.new(PropLoader.getProps['ingestion_db'])
  mdb = conn.db(@midgar_db_name)
  @doc = mdb.collection(coll).find("_id" => id).to_a
  assert(!@doc.nil?, "Cannot find the document with _id=#{id} in #{coll}")
  assert(@doc.size == 1, "Number of entities returned != 1 (received #{@doc.size}")
end

###############################################################################
# THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN
###############################################################################

Then /^I should receive a new ID$/ do
  step "I should receive a new ID for the association I just created"
end

Then /^I (should|should not) find "([^\"]*)" in "([^\"]*)"$/ do |should_or_not, id, field|
  check = should_or_not == "should"? true : false
  found = false
  @doc[0][field].each do |row|
    if row["_id"] == id
      found = true
      break
    end
  end
  assert(found == check, "Failed should / should not check")
end

###############################################################################
# DATA DATA DATA DATA DATA DATA DATA DATA DATA DATA DATA DATA DATA DATA DATA
###############################################################################

$entity_data = {
  "studentSectionAssociation" => {
    "studentId" => "74cf790e-84c4-4322-84b8-fca7206f1085",
    "sectionId" => "ceffbb26-1327-4313-9cfc-1c3afd38122e",
    "repeatIdentifier" => "Repeated, counted in grade point average",
    "beginDate" => "2011-12-01",
    "endDate" => "2012-01-01",
    "homeroomIndicator" => true
  }
}
