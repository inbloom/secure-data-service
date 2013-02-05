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


require 'rubygems'
require 'bundler/setup'
require 'mongo'
require 'time'

require_relative '../../utils/sli_utils.rb'

#--------------- Defs ---------------------#
@expiration=0

#--------------- Steps ---------------------#
Given /^the user has previously authenticated as "([^"]*)" with password "([^"]*)" in "([^"]*)"$/ do |username, password, realm|
  idpRealmLogin(username, password,realm) 
end

Given /^received a "([^"]*)" access token$/ do |arg1|
  if arg1 == "valid"
    assert(@sessionId != nil)
  end
end

When /^I successfully access resource "([^"]*)" and record expiration$/ do |resourceUri|
  restHttpGet(resourceUri)
  disable_NOTABLESCAN()
  assert(@res.code == 200)
  @previous=@expiration
  mongo_session=coll().find_one({"body.appSession.token"=>@sessionId})
  @expiration=mongo_session["body"]["expiration"]
  enable_NOTABLESCAN()
end

Then /^current session's expiration is in the future$/ do
  @expiration>(Time.now.to_f * 1000.0)
end

Then /^current session's expiration has been extended$/ do
  @expiration>@previous
end


#--------------- Helpers ---------------------#
def coll
  @db ||= Mongo::Connection.new(PropLoader.getProps['DB_HOST']).db('sli')
  @coll ||= @db.collection('userSession')
  return @coll
end
