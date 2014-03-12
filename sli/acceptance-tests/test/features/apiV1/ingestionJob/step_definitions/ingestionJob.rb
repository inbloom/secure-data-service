# all required definitions are in utility files
require_relative '../../../utils/mongo_utils.rb'
require_relative '../../../utils/sli_utils.rb'
require_relative '../../../utils/common_stepdefs.rb'

MONGO_BIN = ENV['MONGO_HOME'] ? ENV['MONGO_HOME']+"/bin/" : ""
DB_HOST = ENV['DB_HOST'] ? ENV['DB_HOST'] : "localhost"

###############################################################################
Given /^the ingestion batch job collection has been reset$/ do
  status = system("#{MONGO_BIN}mongoimport --drop -d ingestion_batch_job -c newBatchJob -h #{DB_HOST} --file test/data/newBatchJob_fixture.json")
  assert(status, "#{$?}")
end

And /^the ingestion batch job collection contains "([^"]*)" records$/ do |count|
  db ||= Mongo::Connection.new(Property['DB_HOST']).db('ingestion_batch_job')
  coll ||= db.collection('newBatchJob')
  entity_count = coll.find().count()
  assert(count.to_i == entity_count)
end