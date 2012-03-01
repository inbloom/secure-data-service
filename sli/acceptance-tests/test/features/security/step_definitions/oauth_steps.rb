require 'rubygems'
require 'bundler/setup'
#require 'json'
require 'oauth2'
require 'mongo'
require_relative '../../utils/sli_utils.rb'

def db
  @db ||= Mongo::Connection.new(PropLoader.getProps['DB_HOST']).db('sli')
end

def sessionColl
  @coll ||= db.collection('oauthSession')
end

def appColl
  @appColl ||= db.collection('application')
end

def getToken(client_name)
  db()
  appColl()
  client = @appColl.find_one({"body.name" => client_name})
  
  client = OAuth2::Client.new(client["body"]["client_id"], client["body"]["client_secret"], :site => 'PUT IN URL')
  client.auth_code.authorize_url(:redirect_uri => client["body"]["redirect_uri"])
  @token = client.auth_code.get_token('PUT IN AUTHORIZATION CODE', :redirect_uri => client["body"]["redirect_uri"])
end

Given /^a user has no session information$/ do
  #We don't have to do anything
end

When /^the user tries to access the resource "([^"]*)"$/ do |resourceUri|
  @resource = resourceUri
  if (@token != nil)
    @res = @token.get(resourceUri)
  else
    @res = restHttpGet(resourceUri)
  end
end

When /^I am told to redirect my user to authenticate$/ do
  #I don't believe anything needs to be done for this step
end

When /^the user logs in to the SLI\-IDP Login page as "([^"]*)" with password "([^"]*)"$/ do |username, password|
  idpLogin(username, password) #Will have to change
end

Then /^the user should be granted a valid access token$/ do
  assert(@token != nil) 
end

Then /^the user should have access to the resource$/ do
  @res = @token.get(@resource)
  assert(@res.code == 200)
end

Given /^received a "([^"]*)" access token$/ do |arg1|
  step "the user has a \"#{arg1}\" access token"
end

Given /^received a "([^"]*)" refresh token$/ do |arg1|
  step "the user has a \"#{arg1}\" refresh token"
end

Then /^the user should get access to the resource$/ do
  assert(@res.code == 200)
end
  
Given /^I try to access the resource "([^"]*)" using the user's credentials$/ do |arg1|
  @res = restHttpGet(@resource)
end

Given /^the user submits their "([^"]*)" token$/ do |arg1|
  @res = @token.get(@resource)
end

Given /^the user's access token is denied$/ do
  assert(@res.code == 401)
end

Given /^the user gets a new "([^"]*)" token$/ do |arg1|
  assert(@token != nil)
end

Given /^the user has a "([^"]*)" access token$/ do |arg1|
  if arg1 == "expired"
    #Generate access token, change expiration date to be in the past
      time = Time.new
      t = t - 600 #The current date, minus 10 minutes
      coll.update({"accessToken.value" => @token.token}, {"$set" => {"accessToken.expiration" => t}})
  elsif arg1 == "valid"
    assert(@token != nil)
  end
end

Given /^the user has a "([^"]*)" refresh token$/ do |arg1|
  if arg1 == "expired"
    time = Time.new
    t = t - 600 #The current date, minus 10 minutes
    coll.update({"accessToken.refreshToken.value" => @token.refresh_token}, {"$set" => {"accessToken.refreshToken.expiration" => t}})
  elsif arg1 == "valid"
    assert(@token.refresh_token != nil)
  end
end

Given /^the user's "([^"]*)" token is denied$/ do |arg1|
  assert(@res.code == 401) #Or should this be HTTP response 403?
end

Given /^the user is redirected to authenticate$/ do
  pending # express the regexp above with the code you wish you had
end

Given /^the user has a made up access token$/ do
  @token = OAuth2::AccessToken::new(nil, "bad-access-token")
end

Given /^the user has a made up refresh token$/ do
  token_hash = {}
  token_hash["refresh_token"] = "bad-refresh-token"
  token_hash["access_token"] = @token.token
  
  @token = OAuth2::AccessToken::from_hash(nil, token_hash)
end

Given /^the user has previously authenticated as "([^"]*)" with password "([^"]*)"$/ do |username, password|
  idpLogin(username, password) #Will need to implement a separate method for the new login process.
end

When /^the user submits their access token$/ do
  @token.get(@resource)
end

Given /^a user visits the resource "([^"]*)"$/ do |arg1|
  step "the user tries to access the resource \"(#{arg1})\""
end

