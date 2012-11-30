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

require_relative '../../../../utils/sli_utils.rb'
require_relative '../../common.rb'
require_relative '../../../utils/api_utils.rb'





###############################################################################
# GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN
###############################################################################




When /^I navigate to GET with invalid id for each resource available$/ do
  resources.each do |resource|
    badId = "bad1111111111111111111111111111111111111_id"
    uri = "/v1#{resource}/#{badId}"
    puts "GET " + uri
    steps %Q{
      When I navigate to GET \"#{uri}\"
      Then I should receive a return code of 404
    }
  end
end

When /^I navigate to PUT with invalid id for each resource available$/ do
  resources.each do |resource|

    #PUT is not allowed for /home
    if (resource.include? "home") 
      next
    end

    badId = "bad1111111111111111111111111111111111111_id"
    uri = "/v1#{resource}/#{badId}"

    # strip leading "/"
    resource_type = get_resource_type resource

    puts "PUT #{uri}"
    steps %Q{
      Given a valid entity json document for a \"#{resource_type}\"
    }
    # split the steps calls so that @updates will have been populated
    steps %Q{
      When I set the "#{@updates['field']}" to "#{@updates['value']}"
      When I navigate to PUT \"#{uri}\"
      Then I should receive a return code of 404

    }
    #step "I should receive a return code of 404"
  end
end


When /^I navigate to DELETE with invalid id for each resource available$/ do
  resources.each do |resource|
    badId = "bad1111111111111111111111111111111111111_id"
    uri = "/v1#{resource}/#{badId}"
    puts "DELETE " + uri
    steps %Q{
      When I navigate to DELETE \"#{uri}\"
      Then I should receive a return code of 404
    }
  end
end



When /^I navigate to PUT "([^"]*)"$/ do |url|
  @result = @fields if !defined? @result
  @result.update(@fields)
  data = prepareData(@format, @result)
  restHttpPut(url, data)
  assert(@res != nil, "Response from rest-client PUT is nil")
end



Given /^a valid entity json document for a "([^"]*)"$/ do |arg1|
  resource_config = File.expand_path(File.dirname(__FILE__))+ "/resource_config.json"
@entityData = JSON.parse(File.read(resource_config))
  @fields = @entityData[arg1]["POST"]
  @updates = @entityData[arg1]["UPDATE"]
  @naturalKey = @entityData[arg1]["naturalKey"]
end
Then /^I perform CRUD for each resource available$/ do
  resources.each do |resource|
    begin
      #post is not allowed for associations
        post_resource resource
        get_resource resource
        steps %Q{
          When I set the "#{@updates['field']}" to "#{@updates['value']}"
          When I navigate to PUT \"/v1#{resource}/#{@newId}\"
          Then I should receive a return code of 204
        }
        update_natural_key resource
        delete_resource resource
        puts  "|#{get_resource_type(resource)}|"
    rescue =>e
      $stderr.puts"#{resource} ==> #{e}"
    end
  end
end

def resources
  config_path = File.expand_path("../../../../../../../../api/src/main/resources/wadl/v1_resources.json", __FILE__)
  v1_resources = JSON.parse(File.read(config_path))['resources']
  get_resource_paths v1_resources
end

def get_resource_paths resources, base = ""
  paths = []
  resources.each do |resource|
    paths << base + resource['path']
    if resource.has_key? 'subResources'
      #do nothing for now
    end
  end
  paths
end
def post_resource resource
  resource_type = get_resource_type resource
  steps %Q{
          Given a valid entity json document for a \"#{resource_type}\"
          When I navigate to POST \"/v1#{resource}\"
          Then I should receive a return code of 201
          And I should receive an ID for the newly created entity
  }
      assert(@newId != nil, "After POST, URI is nil")

end
def get_resource resource
  resource_type = get_resource_type resource
  steps %Q{
          When I navigate to GET \"/v1#{resource}/#{@newId}\"
          Then I should receive a return code of 200
          And the response should contain the appropriate fields and values
         And "entityType" should be \"#{resource_type}\"
         And I should receive a link named "self" with URI \"/v1#{resource}/#{@newId}\"
  }
end
def delete_resource resource
      steps %Q{
          When I navigate to DELETE \"/v1#{resource}/#{@newId}\"
          Then I should receive a return code of 204
          And I navigate to GET \"/v1#{resource}/#{@newId}\"
          Then I should receive a return code of 404
      }
end
def get_resource_type resource
  resource_type = resource[1..-1]
  if resource_type.include? "staffEducationOrgAssignmentAssociation" 
    resource_type = "staffEducationOrganizationAssociation"
  elsif resource_type.sub!(%r/ies\z/,"y").nil?
    resource_type.sub!(%r/s\z/,"")
  end
  resource_type
end
def update_natural_key resource
  if @naturalKey.nil? == false
    @naturalKey.each do |nKey,nVal|
      steps %Q{
          When I set the "#{nKey}" to "#{nVal}"
          When I navigate to PUT \"/v1#{resource}/#{@newId}\"
          Then I should receive a return code of 409 
      }
    end
  end
end

Given /^my contextual access is defined by table:$/ do |table|
  @ctx={}
  table.hashes.each do |hash|
    @ctx[hash["Context"]]=hash["Ids"]
  end
end

Given /^the expected staff rewrite results are defined by table:$/ do |table|
  # table is a Cucumber::Ast::Table
  @state_staff_expected_results={}
  table.hashes.each do |hash|
    @state_staff_expected_results[hash["Entity Resource URI"]]=hash
  end
end


Given /^the staff queries and rewrite rules work$/ do
 

  puts "Given entity URI \"<resource>\""
  puts "Given parameter \"limit\" is \"0\""
  puts "When I navigate to GET \"/v1</Resource URI>\""
  puts "Then I should receive a return code of 200"  
  puts "And each entity's \"entityType\" should be \"<Resource Type>\""
  puts "And I should receive a collection of \"<Count>\" entities"
  puts "And uri was rewritten to \"#<Rewrite URI>\""

  resources.each do |resource_as_uri|
    if (resource_as_uri.include? "home") 
      next
    end

    resource = resource_as_uri[1..-1]
    resource_type = get_resource_type resource_as_uri

    puts "Evaluating: #{resource}"

    row_data = @state_staff_expected_results[resource]
    assert(!row_data.nil?, "No entry in expected staff rewrite results table for resource uri: #{resource}")

    
    step "entity URI \"#{resource}\""
    step "parameter \"limit\" is \"0\""
    step "I navigate to GET \"/v1#{resource_as_uri}\""
    step "I should receive a return code of 200"  
    step "each entity's \"entityType\" should be \"#{resource_type}\""
  
    entity_count = row_data["Count"]
    step "I should receive a collection of \"#{entity_count}\" entities"
    
    rewrite_url = row_data["Rewrite URI"]
    step "uri was rewritten to \"#{rewrite_url}\""
  end
end


Given /^entity URI "([^"]*)"$/ do |arg1|
  @entityUri = arg1
end

Then /^uri was rewritten to "(.*?)"$/ do |expectedUri|
  version = "v1"
  root = expectedUri.match(/\/(.+?)\/|$/)[1]
  expected = version+expectedUri
  actual = @headers["x-executedpath"][0]

  #First, make sure the paths of the URIs are the same
  expectedPath = expected.gsub("@ids", "[^/]+")

  assert(actual.match(expectedPath), "Rewriten URI path didn't match, expected:#{expectedPath}, actual:#{actual}")

  #Then, validate the list of ids are the same
  ids = []
  if @ctx.has_key? root
    idsString = actual.match(/v1\/[^\/]*\/([^\/]*)\/?/)[1]
    actualIds = idsString.split(",")
    expectedIds = @ctx[root].split(",")
    
    assert(actualIds.length == expectedIds.length,"Infered Context IDs not equal: expected:#{expectedIds.inspect}, actual:#{actualIds.inspect}")
    expectedIds.each do |id|
      assert(actualIds.include?(id),"Infered Context IDs not equal: expected:#{expectedIds.inspect}, actual:#{actualIds.inspect}")
    end
  end
end



