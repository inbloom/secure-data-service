require 'json'
require 'mongo'

Transform /^district "([^"]*)"$/ do |district|
  id = "4726e42f-b265-372a-3c17-dc8d5d5fb263" if district == "IL-SUNSET"
end

When /^I POST a new admin delegation$/ do
  dataObj = DataProvider.getValidAdminDelegationData()
  data = prepareData("application/json", dataObj)
  @format = "application/json"


  restHttpPost("/adminDelegation", data)

  assert(@res != nil, "Response from POST operation was null")
end


When /^I do not have access to app authorizations for district "([^"]*)"$/ do |arg1|
  #No code necessary, should be handled already
end


Given /^I have a valid admin delegation entity$/ do
  @adminDelegation = DataProvider.getValidAdminDelegationData()
end

Given /^I change "([^"]*)" to true$/ do |field|
  @adminDelegation[field] = true
end

When /^I PUT to admin delegation$/ do
  @format = "application/json"
  data = prepareData(@format, @adminDelegation)
  restHttpPut("/adminDelegation/myEdOrg", data)
  assert(@res != nil, "Response from PUT operation was nil")
end

When /^I have access to app authorizations for district "([^"]*)"$/ do |arg1|
  #No code necessary, should be handled already
end

Then /^I should update app authorizations for (district "[^"]*")$/ do |district|
  @format = "application/json"
  dataObj = {"authId" => "IL-SUNSET", "appIds" => ["78f71c9a-8e37-0f86-8560-7783379d96f7"], "authType" => "EDUCATION_ORGANIZATION"}
  data = prepareData("application/json", dataObj)
  puts("The data is #{data}") if ENV['DEBUG']
  restHttpPut("/applicationAuthorization/" + district, data)
  assert(@res != nil, "Response from PUT operation was nil")
end

Then /^I should get my delegations$/ do
  restHttpGet("/adminDelegation")
  assert(@res != nil, "Response from GET operation was nil")
end

Then /^I should see that "([^"]*)" is "([^"]*)" for "([^"]*)"$/ do |field, value, district|
  list = JSON.parse(@res.body)
  foundIt = false
  list.each do |cur|
    if cur["localEdOrgId"] == district  
      foundIt = true
      assert(cur[field].to_s == value, "Expected field #{field} to be #{value} was #{cur[field].to_s}")
    end
  end
  assert(foundIt, "Never found district #{district}")
end

When /^I should save the old app authorizations for "([^"]*)"/ do |district|
  appAuthColl()
  $appAuths = @coll.find_one({"body.authId" => district})
end

Then /^I put back app authorizations/ do
  if $appAuths != nil
    appAuthColl()
    @coll.remove({"body.authId" => $appAuths["body"]["authId"]})
    @coll.insert($appAuths)
  end
end

def appAuthColl
  @db ||= Mongo::Connection.new(PropLoader.getProps['DB_HOST']).db('sli')
  @coll ||= @db.collection('applicationAuthorization')
  return @coll
end
