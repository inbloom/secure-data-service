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

Transform /endpoints for the role "([^\"]*)"/ do |arg1|
  array = ["Manage Developer Accounts", "Change Password"] if arg1 == "Sandbox Operator"
  array = ["Approve Application Registration", "Change Password", "Manage Administrator Accounts"] if arg1 == "Production Operator"
  array = ["Register Application", "Change Password"] if arg1 == "Production Developer"
  array = ["Delegate Administration", "Authorize Applications", "Change Password", "Manage Administrator Accounts"] if arg1 == "LEA Admin"
  #NOTE: previous admin delegation tests delegate app auth to this SEA Admin
  array = ["Authorize Applications", "Change Password", "Manage Administrator Accounts"] if arg1 == "SEA Admin"
  array = ["Change Password", "Create Landing Zone"] if arg1 == "Ingestion User"
  array = ["Create Custom Roles", "Manage Realm", "Change Password"] if arg1 == "LEA and Realm Admin"
  array
end
 
When /^I make an API call to get my available apps$/ do
  @format = "application/json"
  restHttpGet("/userapps")
  assert(@res != nil, "Response from rest-client GET is nil")
end

When /^I make an API call to get my available apps filtered by admin$/ do
  @format = "application/json"
  restHttpGet("/userapps?is_admin=true")
  assert(@res != nil, "Response from rest-client GET is nil")
end

Then /^I receive a JSON object listing all the apps that my SEA\/LEA have approved$/ do
  assert(@res.code == 200, "Response code not expected: expected 200 but received "+@res.code.to_s)
  @result = JSON.parse(@res.body)
  assert(@result != nil, "Result of JSON parsing is nil")
  assert(@result.length > 6, "around 5 non-admin apps")
end

Then /^I receive a JSON object listing all the admin apps that my SEA\/LEA have approved$/ do
  assert(@res.code == 200, "Response code not expected: expected 200 but received "+@res.code.to_s)
  @result = JSON.parse(@res.body)
  assert(@result != nil, "Result of JSON parsing is nil")
  puts @result.inspect if ENV['DEBUG']
  assert(@result.length < 6, "needed around 6 admin apps, found #{@result.length}") #important thing is this is less than the result of the size of the list of all apps
end

Then /^I receive a JSON object listing all the admin apps$/ do
  assert(@res.code == 200, "Response code not expected: expected 200 but received "+@res.code.to_s)
  @result = JSON.parse(@res.body)
  assert(@result != nil, "Result of JSON parsing is nil")
end

Then /^I receive a JSON object listing all the apps approved for "(.*?)"$/ do |district|
  assert(@res.code == 200, "Response code not expected: expected 200 but received "+@res.code.to_s)
  @apps ||= {}
  @result = JSON.parse(@res.body)
  @apps[district] = @result
  assert(@apps[district] != nil, "Result of JSON parsing is nil")
end

Then /^I receive a JSON object listing all the apps approved for both "(.*?)" and "(.*?)"$/ do |dist1, dist2|
  assert(@res.code == 200, "Response code not expected: expected 200 but received "+@res.code.to_s)
  @result = JSON.parse(@res.body)
  assert(@result!= nil, "Result of JSON parsing is nil")
  app_names = []
  @apps[dist1].each { |app| app_names.push(app['name']) }
  @apps[dist2].each { |app| app_names.push(app['name']) }

  result_names = []
  @result.each { |app| result_names.push(app['name']) }
  result_names.each { |app_name| assert(app_names.include?(app_name), "App list contains #{app_name}") }
  
  assert(result_names.length == app_names.to_set.length, "List of apps should be the same.")
end


And /^the object includes an app URL, admin URL, image URL, description, title, vendor, version, display method, is admin app$/ do
    assert(@result[0]["application_url"] != nil, "Contains app URL")
    assert(@result[0]["administration_url"] != nil, "Contains administrative URL")
    assert(@result[0]["image_url"] != nil, "Contains is image URL")
    assert(@result[0]["description"] != nil, "Contains description")
    assert(@result[0]["name"] != nil, "Contains title")
    assert(@result[0]["vendor"] != nil, "Contains vendor")
    assert(@result[0]["version"] != nil, "Contains version")
    assert(@result[0]["behavior"] != nil, "Contains display method")
    assert(@result[0]["is_admin"] != nil, "Contains is admin app")

end

And /^the list contains the admin app$/ do
    @result.each do |app|
        if app["name"] == "Admin Apps"
            @admin_app = app
        end
    end
    assert(@admin_app != nil, "Admin app found")
end

Then /^the list contains and app named "(.*?)"$/ do |app_name|
    @result.each do |app|
        if app["name"] == app_name
            @found_app = app
        end
    end
    assert(@found_app != nil, "Did not find an app named #{app_name}")

end

Then /^the list does not contain and app named "(.*?)"$/ do |app_name|
    @result.each do |app|
        if app["name"] == app_name
            @found_app = app
        end
    end
    assert(@found_app == nil, "Found an app named #{app_name}")
end

And /^none of the apps are admin apps$/ do
    @result.each do |app|
        assert(app["is_admin"] == false, "#{app['name']} is non-admin app") 
    end
end

And /^the resulting list is empty$/ do
    assert(@result.length == 0, "list is empty")
end

Then /^the admin app endpoints contains (endpoints for the role ".*?")$/ do |arg1|
  @admin_app["endpoints"].each do |endpoint|
    assert(arg1.include?(endpoint["name"]), "Endpoint #{endpoint["name"]} was not expected to be returned") 
  end
  assert(@admin_app["endpoints"].length == arg1.length, "Expected endpoints counts do not match: expected #{arg1.length} actual: #{@admin_app["endpoints"].length}")
end

