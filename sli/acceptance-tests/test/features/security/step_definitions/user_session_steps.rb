require 'rubygems'
require 'bundler/setup'
require 'mongo'
require 'Time'

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
  assert(@res.code == 200)
  @previous=@expiration
  mongo_session=coll().find_one({"body.appSession.token"=>@sessionId})
  @expiration=mongo_session["body"]["expiration"]
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
