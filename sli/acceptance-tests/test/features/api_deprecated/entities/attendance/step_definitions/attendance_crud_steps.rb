require_relative '../../../../utils/sli_utils.rb'
Given /^I am testing the '(\w+)' entity$/ do |type| 
  @entity_type = type 
end

When /^I POST a new entity$/ do
  @result = EntityProvider.get_new_entity(@entity_type)
  uri = EntityProvider.get_entity_uri(@entity_type)
  step "I POST the entity to \"#{uri}\""
end

Then /^I should receive a new entity ID$/ do
  step "I should receive a return code of 201"
  step "I should receive an ID for the newly created entity"
end

When /^I GET the new entity$/ do
  uri = EntityProvider.get_entity_uri(@entity_type)
  step "I navigate to GET \"#{uri}/#{@newId}\""
end

Then /^the response should match the POSTed entity$/ do
  step "I should receive a return code of 200"
  entity = EntityProvider.get_new_entity(@entity_type)
  EntityProvider.verify_entities_match(entity, JSON.parse(@res.body))
end

When /^I GET \w+ existing entity$/ do
  id = EntityProvider.get_existing_entity(@entity_type)["id"]
  uri = EntityProvider.get_entity_uri(@entity_type)
  puts "I navigate to GET \"#{uri}/#{id}\""
  step "I navigate to GET \"#{uri}/#{id}\""
end

Then /^the response should contain the expected entity$/ do
  step "I should receive a return code of 200"
  entity = EntityProvider.get_existing_entity(@entity_type)
  EntityProvider.verify_entities_match(entity, JSON.parse(@res.body))
end

When /^I update fields in the existing entity$/ do
  @update_entity = EntityProvider.get_updated_entity(@entity_type)
end

And /^I PUT the updated entity$/ do
  id = @update_entity["id"]
  uri = EntityProvider.get_entity_uri(@entity_type)
  @result = @update_entity
  step "I PUT the entity to \"#{uri}/#{id}\""
end

Then /^the response should contain the updated fields$/ do
  step "I should receive a return code of 200"
  entity = EntityProvider.get_updated_entity(@entity_type)
  EntityProvider.verify_entities_match(entity, JSON.parse(@res.body))
end

When /^I DELETE an existing entity$/ do
  id = EntityProvider.get_existing_entity(@entity_type)["id"]
  uri = EntityProvider.get_entity_uri(@entity_type)
  step "I navigate to DELETE \"#{uri}/#{id}\""
end
