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


require 'rubygems'
require 'mongo'
require 'pp'
require 'rest-client'

require_relative '../../../utils/sli_utils.rb'
require_relative '../../../ingestion/features/step_definitions/ingestion_steps.rb'

############################################################
# ENVIRONMENT CONFIGURATION
############################################################

SIF_DB_NAME = PropLoader.getProps['sif_database_name']
SIF_DB = PropLoader.getProps['sif_db']
SIF_ZIS_ADDRESS_TRIGGER = PropLoader.getProps['sif_zis_address_trigger']

############################################################
# STEPS: BEFORE
############################################################

Before do
  @conn = Mongo::Connection.new(SIF_DB)
  @mdb = @conn.db(SIF_DB_NAME)

  @postUri = SIF_ZIS_ADDRESS_TRIGGER
  @format = 'application/xml;charset=utf-8'

  # TODO change these to xml strings
  @newEntities = {
    'SchoolInfo' => {
      'id' => 'S1',
      'name' => 'New School Name',
      'address' => 'New School Address'
    },
    'LEAInfo' => {
      'id' => 'LEA1',
      'name' => 'New LEA Name'
    },
    'SEAInfo' => {
      'id' => 'SEA1',
      'name' => 'New SEA Name'
    }
  }
  @existingEntities = {
    'SchoolInfo' => {
      'id' => 'S1',
      'name' => 'Updated School Name',
      'address' => 'Updated School Address'
    },
    'LEAInfo' => {
      'id' => 'LEA1',
      'name' => 'Updated LEA Name'
    },
    'SEAInfo' => {
      'id' => 'SEA1',
      'name' => 'Updated SEA Name'
    }
  }

  # default
  @entities = @newEntities
end


############################################################
# STEPS: GIVEN
############################################################

Given /^this is a new entity$/ do
  @entities = @newEntities
end

Given /^this is an update to an existing entity$/ do
  @entities = @existingEntities
end

Given /^the following collections are clean in datastore:$/ do |table|
  # table is a Cucumber::Ast::Table
  pending # express the regexp above with the code you wish you had
end

############################################################
# STEPS: WHEN
############################################################


When /^I POST a\(n\) "(.*?)" SIF message$/ do |messageType|
  postMessage(@entities[messageType])
end

# TODO fill in the code here
def postMessage(message)
  puts "POSTing message: #{message}"

  headers = {:content_type => @format}
  @res = RestClient.post(@postUri, message, headers){|response, request, result| response }
  puts(@res.code,@res.body,@res.raw_headers)

  pp @res
end

############################################################
# STEPS: THEN
############################################################

############################################################
# STEPS: AFTER
############################################################

After do
  @conn.close if @conn != nil
end

############################################################
# END
############################################################
