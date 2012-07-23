require 'mongo'
require 'securerandom'

API_DB = PropLoader.getProps['DB_HOST']
API_DB_NAME = PropLoader.getProps['api_database_name']

Given /^that databrowser has been authorized for all ed orgs$/ do
  conn = Mongo::Connection.new(API_DB)
  db = conn[API_DB_NAME]
  appColl = db.collection("application")
  dashboardId = appColl.find_one({"body.name" => "SLC Data Browser"})["_id"]
  puts("The dashboard id is #{dashboardId}") if ENV['DEBUG']
  
  appAuthColl = db.collection("applicationAuthorization")
  edOrgColl = db.collection("educationOrganization")
  
  neededEdOrgs = edOrgColl.find({"body.organizationCategories" => ["Local Education Agency"]})
  neededEdOrgs.each do |edOrg|
    puts("Currently on edOrg #{edOrg.inspect}") if ENV['DEBUG']
    edOrgId = edOrg["_id"]
    edOrgTenant = edOrg["metaData"]["tenantId"]
    existingAppAuth = appAuthColl.find_one({"body.authId" => edOrgId})
    if existingAppAuth == nil 
      newAppAuth = {"_id" => "2012ls-#{SecureRandom.uuid}", "body" => {"authType" => "EDUCATION_ORGANIZATION", "authId" => edOrgId, "appIds" => [dashboardId]}, "metaData" => {"tenantId" => edOrgTenant}}
      puts("About to insert #{newAppAuth.inspect}") if ENV['DEBUG']
      appAuthColl.insert(newAppAuth)
    else
      existingAppAuth["body"]["appIds"].push(dashboardId)
      puts("About to update #{existingAppAuth.inspect}") if ENV['DEBUG']
      appAuthColl.update({"body.authId" => edOrgId}, existingAppAuth)
    end
    end
end
