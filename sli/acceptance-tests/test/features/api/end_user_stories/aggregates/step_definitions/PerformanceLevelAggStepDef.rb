require 'mongo'
require_relative '../../../../utils/sli_utils.rb'

Transform /^level (\w+) performers$/ do |level| 
  ret = 'level1' if level == 'one'
  ret = 'level2' if level == 'two'
  ret = 'level3' if level == 'three'
  ret = 'level4' if level == 'four'
  ret 
end

Transform /^<([^>]*)>$/ do |agg_def|
  ret = "d8db9f83-6bb1-4919-bb53-11e6e7fa9877" if agg_def == 'District Level 8th Grade EOG'
  ret
end

def db
  @db ||= Mongo::Connection.new('localhost').db('sli')
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
  
  @params = @result["parameters"].join(",")
  @agg_coll = @result["collection"]
  @name = @result["name"]
end

When /^the aggregation is calculated$/ do  
  cmd = {
    "mapreduce" => @agg_coll,
    "map" => @map,
    "reduce" => @reduce,
    "finalize" => @finalize,
    "scope" => { "aggregation_name" => @name, "execution_time" => Time.new },
    "out" => { "reduce" => "aggregation" }
  }
  run(cmd)
end

Then /^I should receive a performance level aggregation$/ do 
  smallvilleDistrictId = "4f0c9368-8488-7b01-0000-000059f9ba56"  
  @agg_res = coll.find_one( 
    { "body.groupBy" => 
      { "districtId" => smallvilleDistrictId, "assessmentType" => @name }});
end

Then /^there should be (\d+) (level \w+ performers)$/ do |count, level|
  res_count = Integer(@agg_res['body'][level])
  res_count.should equal Integer(count)
end
