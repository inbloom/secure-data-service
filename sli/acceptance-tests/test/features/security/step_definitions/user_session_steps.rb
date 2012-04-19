require 'rubygems'
require 'bundler/setup'
require 'mongo'

require_relative '../../utils/sli_utils.rb'

Given /^the user has previously authenticated as "([^"]*)" with password "([^"]*)"$/ do |username, password|
  idpLogin(username, password) 
end

Given /^received a "([^"]*)" access token$/ do |arg1|
  if arg1 == "valid"
    assert(@sessionId != nil)
  end
end

Given /^I try to access the resource "([^"]*)" using the user's credentials$/ do |resourceUri|
  @res = restHttpGet(resourceUri)
end

Then /^the user should get access to the resource$/ do
  puts(@res)
  assert(@res.code == 200)
end

#--------------- Helpers ---------------------#

#--------------- Defs ---------------------#
@db ||= Mongo::Connection.new(PropLoader.getProps['DB_HOST']).db('sli')
@coll ||= @db.collection('userSession')
@appColl ||= @db.collection('application')
@SLI_DEBUG=true

