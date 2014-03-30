# all required definitions are in utility files
require_relative '../../../utils/mongo_utils.rb'
require_relative '../../../utils/sli_utils.rb'
require_relative '../../../utils/common_stepdefs.rb'

MONGO_BIN = ENV['MONGO_HOME'] ? ENV['MONGO_HOME']+"/bin/" : ""
DB_HOST = ENV['DB_HOST'] ? ENV['DB_HOST'] : "localhost"

###############################################################################
Given /^the ingestion batch job collection has been reset$/ do
  importStatus = system("#{MONGO_BIN}mongoimport --drop -d ingestion_batch_job -c newBatchJob -h #{DB_HOST} --file test/data/newBatchJob_fixture.json")
  indexStatus = system("mongo ingestion_batch_job ../config/indexes/ingestion_batch_job_indexes.js")
  assert(importStatus, "#{$?}")
  assert(indexStatus, "#{$?}")
end

And /^the ingestion batch job collection contains "([^"]*)" records$/ do |count|
  db ||= Mongo::Connection.new(Property['DB_HOST']).db('ingestion_batch_job')
  coll ||= db.collection('newBatchJob')
  entity_count = coll.find().count()
  assert(count.to_i == entity_count, "Actual count was #{entity_count}")
end

Then /^I should receive a response with "(.*?)" entities$/ do |count|
  jsonResult = JSON.parse(@res.body)
  assert(count.to_i == jsonResult.size, "Actual count was #{jsonResult.size}")
end

Then /^I should have only one entity with id "(.*?)"$/ do |id|
  jsonResult = JSON.parse(@res.body)

  if @res.body[0,1] == "["
    fail("A JSON array was returned")
  end
  assert(jsonResult["id"] == id, "The entity id does not match")
end

When /^I navigate to ingestion job url "([^\"]*)"$/ do |uri|
  if defined? @queryParams
    uri = uri + "?#{@queryParams.join('&')}"
  end
  restHttpGet(uri)
  assert(@res != nil, "Response from rest-client GET is nil")
  assert(@res.body != nil, "Response body is nil")
  contentType = contentType(@res).gsub(/\s+/,"")
  jsonTypes = ["application/json", "application/json;charset=utf-8", "application/vnd.slc.full+json", "application/vnd.slc+json" "application/vnd.slc.full+json;charset=utf-8", "application/vnd.slc+json;charset=utf-8"].to_set

  @headers=@res.raw_headers.to_hash()  
  if jsonTypes.include? contentType && !@res.body.empty?
    @result = JSON.parse(@res.body)
    assert(@result != nil, "Result of JSON parsing is nil")
    #puts "\n\nDEBUG: common stepdef result from API call is: #{@result}"
  elsif /application\/xml/.match contentType
    doc = Document.new @res.body
    @result = doc.root
  else
    puts "Common stepdefs setting result to null"
    @result = {}
  end
end