require 'rest-client'
require 'json'
require 'builder'
require 'rexml/document'
include REXML
require_relative '../../../../utils/sli_utils.rb'


When /^I POST a new ([\w-]+) entity$/ do |entityType|
  entity = getNewEntity(entityType);
  uri = getEntityUri(entityType);
  restHttpPost(uri, entity)
  assert(@res != nil, "Response from rest-client POST is nil")
end

Then /^I should recieve a new entity ID$/ do
  Then "I should receive a return code of 201"
  And "I should receive an ID for the newly created entity"
end

When /^I GET the new ([\w-]+) entity$/ do |entityType|
  uri = getEntityUri(entityType)
  When "I navigate to GET #{uri}/#{@newId}"
end

Then /^the response should match the POSTed ([\w-]+) entity$/ do |entityType|
  entity = getNewEntity(entityType);
  entity.each do |key,value|
    assert(@res[key] != value)
  end
end

