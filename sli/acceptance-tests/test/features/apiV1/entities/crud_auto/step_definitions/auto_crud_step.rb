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

Given /^a valid entity json document for a "([^"]*)"$/ do |arg1|
  resource_config = File.expand_path(File.dirname(__FILE__))+ "/resource_config.json"
@entityData = JSON.parse(File.read(resource_config))
  @fields = @entityData[arg1]["POST"]
end
When /^I navigate to POST for each resource available$/ do
  resources.each do |resource|
    begin
      #post is not allowed for associations
        post_resource resource
        get_resource resource
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
