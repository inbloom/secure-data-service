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
  coll = securityEventCollection()
  coll.remove()
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
  db ||= Mongo::Connection.new(PropLoader.getProps['DB_HOST']).db('sli')
  coll ||= db.collection('securityEvent')
  return coll
end

def getMatchingSecEvents(field="body.logMessage", securityeventpattern)
    coll = securityEventCollection()
    secEventCount = coll.find({field => /#{securityeventpattern}/}).count()
    return secEventCount
end

And /^a security event "([^"]*)" should be created for these targetEdOrgs( ONLY)?$/ do |expectedEvent, only, table|
  coll = securityEventCollection()
  secEvent = coll.find({}, :fields => ["body.logMessage", "body.targetEdOrgList"]).sort("body.timeStamp" => :desc).next_document
  secEventMessage      = secEvent["body"]["logMessage"]
  secEventTargetEdOrgs = secEvent["body"]["targetEdOrgList"]
  scenarioTargetEdOrgs = table.hashes.map {|e| e["targetEdOrg"]}
  inScenarioButNotInSecEvents = scenarioTargetEdOrgs - secEventTargetEdOrgs
  assert(secEventMessage == expectedEvent,        "Expected latest Security Event to be [" + expectedEvent + "]. Found [" + secEventMessage + "].");
  assert(inScenarioButNotInSecEvents.length == 0, "Some targetEdOrgs [" + inScenarioButNotInSecEvents.join(",") + "] were not found in latest SecurityEvent [" + secEventMessage + "].");
  if only
      inSecEventsButNotInScenario =   secEventTargetEdOrgs -  scenarioTargetEdOrgs
      assert(inSecEventsButNotInScenario.length == 0, "Some EXTRA targetEdOrgs [" + inSecEventsButNotInScenario.join(",") + "] were found in latest SecurityEvent [" + secEventMessage + "].");
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
  url = PropLoader.getProps['api_server_url']+"/api/rest"+arg1
  @res = RestClient.get(url, nil) {|response, request, result| response}
end

Then /^I should receive a "([^"]*)" response$/ do |arg1|
  assert("#{@res.code}" ==  arg1, "Expected #{arg1}, but got #{@res.code}")
end

