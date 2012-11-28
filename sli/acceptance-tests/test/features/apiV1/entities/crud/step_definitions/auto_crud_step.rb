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


require 'rest-client'
require 'json'
require 'builder'
require 'rexml/document'
require 'uri'
include REXML
require_relative '../../../../utils/sli_utils.rb'
require_relative '../../../utils/api_utils.rb'

When /^I navigate to POST for each resource available$/ do
  resources.each do |resource|
    puts "Resource #{resource} ==> POST"
    step "I navigate to POST \"</v1#{resource}>\""
    begin
      result = JSON.parse(@res.body)
    end
    assert(result != nil, "result of parsing result body was nil!")
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
