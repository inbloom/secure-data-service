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


require 'json'
require 'mongo'

require_relative '../../utils/sli_utils.rb'

Given /^the sli securityEvent collection is empty$/ do
  coll = securityEventCollection
  coll.remove
end

Then /^a security event matching "([^"]*)" should be in the sli db$/ do |securityeventpattern|
  disable_NOTABLESCAN()
  secEventCount = getMatchingSecEvents(securityeventpattern);
  enable_NOTABLESCAN()
  assert(secEventCount > 0, "No security events were found with logMessage matching " + securityeventpattern)
end

Then /^"([^"]*)" security event matching "([^"]*)" should be in the sli db$/ do |expected_count, securityeventpattern|
  disable_NOTABLESCAN()
  secEventCount = getMatchingSecEvents(securityeventpattern);
  enable_NOTABLESCAN()
  assert(secEventCount == expected_count.to_i, "Unexpected number of security events found with logMessage matching " + securityeventpattern)
end


Then /^"([^"]*)" security event with field "([^"]*)" matching "([^"]*)" should be in the sli db$/ do |expected_count, field, securityeventpattern|
  disable_NOTABLESCAN()
  secEventCount = getMatchingSecEvents(field, securityeventpattern);
  enable_NOTABLESCAN()
  assert(secEventCount == expected_count.to_i, "Unexpected number of security events found with logMessage matching " + securityeventpattern)
end

def securityEventCollection
  puts "Connecting to Mongo #{Property[:db_host]}:#{Property[:db_port]}"
  db = Mongo::Connection.new(Property[:db_host],Property[:db_port]).db('sli')
  db.collection_names.each { |name| puts name }
  puts "securityEvent count: #{db.collection('securityEvent').count}"
  db.collection('securityEvent')
end

def getMatchingSecEvents(field="body.logMessage", securityeventpattern)
    coll = securityEventCollection()
    secEventCount = coll.find({field => /#{securityeventpattern}/}).count()
    return secEventCount
end

And /^a security event "([^"]*)" should be created for these targetEdOrgs( ONLY)?$/ do |expected_event, only, table|
  latest_event = securityEventCollection.find({}, :fields => ['body.logMessage', 'body.targetEdOrgList']).sort('body.timeStamp' => :desc).next_document
  message      = latest_event['body']['logMessage']
  event_ed_orgs = latest_event['body']['targetEdOrgList'] || []
  expected_ed_orgs = table.hashes.map {|e| e['targetEdOrg']}

  message.should == expected_event
  if only
    event_ed_orgs.should =~ expected_ed_orgs
  else
    event_ed_orgs.should include(*expected_ed_orgs)
  end
end

Then /^I should see a count of "([^"]*)" in the security event collection$/ do |count|
  disable_NOTABLESCAN()
  @result = "true"
    coll = securityEventCollection()
    @entity_count = coll.count().to_i
    #puts @entity_count
    #puts "There are " + @entity_count.to_s + " in the security event collection"
    if @entity_count.to_s != count.to_s
      @result = "false"
    end
  assert(@result == "true", "Some records didn't match successfully.")
  enable_NOTABLESCAN()
end

When /^I GET the url "([^"]*)" using a blank cookie$/ do |arg1|
  url = Property['api_server_url']+"/api/rest"+arg1
  @res = RestClient.get(url, nil) {|response, request, result| response}
end

Then /^I should receive a "([^"]*)" response$/ do |arg1|
  assert("#{@res.code}" ==  arg1, "Expected #{arg1}, but got #{@res.code}")
end

