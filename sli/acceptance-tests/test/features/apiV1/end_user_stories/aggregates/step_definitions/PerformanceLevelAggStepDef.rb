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
require_relative '../../../../utils/sli_utils.rb'

Transform /^level (\w+) performers$/ do |level| 
  ret = 'level1' if level == 'one'
  ret = 'level2' if level == 'two'
  ret = 'level3' if level == 'three'
  ret = 'level4' if level == 'four'
  ret 
end

Transform /^<([^>]*)>$/ do |arg|
  ret = "d8db9f83-6bb1-4919-bb53-11e6e7fa9877" if arg == 'District Level 8th Grade EOG'
  ret = "bd9a323a-d6fd-454f-98d9-edf2702d31e1" if arg == 'School Level 8th Grade EOG'
  ret = "9471d57e-e1c8-4f10-8d52-8e422ba2f2ab" if arg == 'Teacher Level Math Scores'
  ret = "1d303c61-88d4-404a-ba13-d7c5cc324bc5" if arg == 'Smallville District'
  ret = "67ce204b-9999-4a11-aaab-000000000005" if arg == 'Small Mouth Bass Middle School'
  ret = "67ce204b-9999-4a11-aaab-000000000006" if arg == 'Don\'t Sweat the Small Stuff Middle School'
  ret = "67ce204b-9999-4a11-aaab-000000000007" if arg == 'La Vie en Small French-Immersion K-8'
  ret = "67ce204b-9999-4a11-aabc-000000000030" if arg == '8888031'
  ret = "67ce204b-9999-4a11-aabc-000000000031" if arg == '8888032'
  ret = "67ce204b-9999-4a11-aabc-000000000032" if arg == '8888033'
  ret = "67ce204b-9999-4a11-aabc-000000000033" if arg == '8888034'
  ret = "67ce204b-9999-4a11-aabc-000000000034" if arg == '8888035'
  ret = "67ce204b-9999-4a11-aabc-000000000035" if arg == '8888036'
  ret = "67ce204b-9999-4a11-aabc-000000000036" if arg == '8888037'
  ret = "67ce204b-9999-4a11-aabc-000000000037" if arg == '8888038'
  ret = "67ce204b-9999-4a11-aabc-000000000038" if arg == '8888039'
  ret = "67ce204b-9999-4a11-aabc-000000000039" if arg == '8888040'
  ret = "67ce204b-9999-4a11-aabc-000000000048" if arg == '8888049'
  ret
end

def db
  @db ||= Mongo::Connection.new(PropLoader.getProps['DB_HOST']).db('sli')
end

def coll
  @coll ||= db.collection('aggregation')
end

def fcoll
  @fcoll ||= db.collection('system.js')
end

def run(cmd)
  #calculate aggregation (map/reduce)
  db.command(cmd)
  #clean up results
  db.command({"$eval" => @cleanup})
end

def getFuncByName(func_name)
  res = fcoll.find_one("_id" => func_name)
  res['value'] #return the function
end

Given /^I am connected to the sli database$/ do
  db
end

Given /^I am using the Smallville School District assessment scores$/ do
  #fixture data is loaded in by rake task
end

Given /^the aggregation table is clear$/ do
  coll.remove()
end

Given /^I have an aggregation definition for (<[^>]*>)$/ do |agg_def|
  restHttpGet("/aggregationDefinitions/#{agg_def}")
  
  @result = JSON.parse(@res.body)
  
  @map = getFuncByName(@result["map"])
  @reduce = getFuncByName(@result["reduce"])
  @finalize = getFuncByName(@result["finalize"])
  @cleanup = getFuncByName("cleanupBodyAndId")
  
  @params = @result["parameters"]
  @queryField = @result["queryField"]
  @query = {}
  @query[@queryField] = {"$in" => @params}

  @agg_coll = @result["collection"]
  @name = @result["name"]
end

When /^the aggregation is calculated$/ do  
  cmd = {
    "mapreduce" => @agg_coll,
    "map" => @map,
    "reduce" => @reduce,
    "finalize" => @finalize,
    "query" => @query,
    "scope" => { "aggregation_name" => @name, "execution_time" => Time.new },
    "out" => { "reduce" => "aggregation" }
  }
  run(cmd)
end

Then /^I should receive a (teacher|district|school) performance level aggregation for (<[^>]*>)/ do |type, id|
  @agg_res = coll.find_one( 
    { "body.groupBy" => 
      { "#{type}Id" => id, "assessmentType" => @name }})
end

Then /^there should be (\d+) (level \w+ performers)$/ do |count, level|
  res_count = Integer(@agg_res['body'][level])
  res_count.should equal Integer(count)
end
