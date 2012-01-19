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
  ret = "4f0c9368-8488-7b01-0000-000059f9ba56" if arg == 'Smallville District'
  ret = "67ce204b-9999-4a11-bfea-000000000006" if arg == 'Small Mouth Bass Middle School'
  ret = "67ce204b-9999-4a11-bfea-000000000007" if arg == 'Don\'t Sweat the Small Stuff Middle School'
  ret = "67ce204b-9999-4a11-bfea-000000000008" if arg == 'La Vie en Small French-Immersion K-8'
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

Given /^I have an aggregation definition for (<[^>]*>)$/ do |agg_def|
  restHttpGet("/aggregationdefinitions/#{agg_def}")
  
  @result = JSON.parse(@res.body)
  
  @map = getFuncByName(@result["map"])
  @reduce = getFuncByName(@result["reduce"])
  @finalize = getFuncByName(@result["finalize"])
  @cleanup = getFuncByName("cleanupBodyAndId")
  
  @params = @result["parameters"]
  @agg_coll = @result["collection"]
  @name = @result["name"]
end

When /^the aggregation is calculated$/ do  
  cmd = {
    "mapreduce" => @agg_coll,
    "map" => @map,
    "reduce" => @reduce,
    "finalize" => @finalize,
    "query" => { "body.assessmentId" => { "$in" => @params }},
    "scope" => { "aggregation_name" => @name, "execution_time" => Time.new },
    "out" => { "reduce" => "aggregation" }
  }
  run(cmd)
end

Then /^I should receive a (district|school) performance level aggregation for (<[^>]*>)/ do |type, id|
  @agg_res = coll.find_one( 
    { "body.groupBy" => 
      { "#{type}Id" => id, "assessmentType" => @name }});
end

Then /^there should be (\d+) (level \w+ performers)$/ do |count, level|
  res_count = Integer(@agg_res['body'][level])
  res_count.should equal Integer(count)
end
