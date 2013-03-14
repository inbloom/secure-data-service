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

require 'mongo'
require_relative '../../apiV1/entities/crud/step_definitions/crud_step.rb'

###############################################################################
# BEFORE BEFORE BEFORE BEFORE BEFORE BEFORE BEFORE BEFORE BEFORE BEFORE BEFORE
###############################################################################

Before do
  @type_to_uri = {
      "grade" => "grades",
      "reportCard" => "reportCards",
      "studentAcademicRecord" => "studentAcademicRecords"
  }
  @uri_to_type = @type_to_uri.invert
  @updates = {
      "grade" => {
          "field" => "gradeType",
          "value" => "Mid-Term Grade"
      },
      "reportCard" => {
          "field" => "numberOfDaysAbsent",
          "value" => "17"
      },
      "studentAcademicRecord" => {
          "field" => "cumulativeGradePointsEarned",
          "value" => "2.2"
      }
  }
end

###############################################################################
# GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN
###############################################################################

Given /^I have valid json documents for "([^\"]*)"$/ do |entities|
  @entities = entities.split ","
end

Given /^I POST them$/ do
  @ids = {}
  @entities.each do |entity|
    puts entity
    uri = @type_to_uri[entity]
    steps %Q{
       Given a valid entity json document for a "#{entity}"
       When I navigate to POST "/v1/#{uri}"
       Then I should receive a return code of 201
       And I should receive a new entity URI
    }
    @ids[entity] = @newId
  end
  puts @ids
end

###############################################################################
# THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN
###############################################################################

Then /^I should be able to perform CRUD on them$/ do
  @entities.each do |entity|
    puts entity
    uri = @type_to_uri[entity]
    update_field = @updates[entity]["field"]
    update_value = @updates[entity]["value"]
    steps %Q{
       Given a valid entity json document for a "#{entity}"
       When I navigate to POST "/v1/#{uri}"
       Then I should receive a return code of 201
       And I should receive a new entity URI
       When I navigate to GET "/#{uri}/<NEWLY CREATED ENTITY ID>"
       Then I should receive a return code of 200
       And the response should contain the appropriate fields and values
       And "entityType" should be "#{entity}"
       And I should receive a link named "self" with URI "/#{uri}/<NEWLY CREATED ENTITY ID>"
       When I set the "#{update_field}" to "#{update_value}"
       And I navigate to PUT "/#{uri}/<NEWLY CREATED ENTITY ID>"
       Then I should receive a return code of 204
       And I navigate to GET "/#{uri}/<NEWLY CREATED ENTITY ID>"
       And "#{update_field}" should be "#{update_value}"
       When I navigate to DELETE "/#{uri}/<NEWLY CREATED ENTITY ID>"
       Then I should receive a return code of 204
       And I navigate to GET "/#{uri}/<NEWLY CREATED ENTITY ID>"
       And I should receive a return code of 404
    }
  end
end

Then /^the documents should be sub\-doc'ed in a "([^\"]*)" document$/ do |container_doc|
  disable_NOTABLESCAN
  host = PropLoader.getProps['ingestion_db']
  port = PropLoader.getProps['ingestion_db_port']
  db_name = convertTenantIdToDbName('Midgar')
  conn = Mongo::Connection.new(host, port)
  midgar_db = conn.db(db_name)
  container_coll = midgar_db.collection(container_doc)
  @entities.each do |entity|
    puts entity
    uri = @type_to_uri[entity]
    id = @ids[entity]
    docs = container_coll.find({"#{entity}._id" => id}).to_a
    assert(docs.size == 1, "Cannot find container doc for #{entity} with id #{id}")
    steps "Given a valid entity json document for a \"#{entity}\""
    assert(docs[0]["body"]["schoolYear"] == @fields["schoolYear"], "schoolYear does not match: expected #{@fields["schoolYear"]}, 
      in Mongo #{docs[0]["body"]["schoolYear"]}")
    assert(docs[0]["body"]["studentId"] == @fields["studentId"], "schoolYear does not match: expected #{@fields["studentId"]}, 
      in Mongo #{docs[0]["body"]["studentId"]}")
  end
  enable_NOTABLESCAN
end

Then /^I should DELETE them$/ do
  @entities.each do |entity|
    puts entity
    uri = @type_to_uri[entity]
    id = @ids[entity]
    steps %Q{
       When I navigate to DELETE "/v1/#{uri}/#{id}"
       Then I should receive a return code of 204
    }
  end
end
