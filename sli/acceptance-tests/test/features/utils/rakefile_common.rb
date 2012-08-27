=begin

Copyright 2012 Shared Learning Collaborative, LLC

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

require 'ldapstorage'

def cleanUpLdapUser(user_email)
  ldap = LDAPStorage.new(PropLoader.getProps['ldap_hostname'], PropLoader.getProps['ldap_port'],
                         PropLoader.getProps['ldap_base'], PropLoader.getProps['ldap_admin_user'],
                         PropLoader.getProps['ldap_admin_pass'])

  ldap.get_user_groups(user_email).each do |group_id|
    ldap.remove_user_group(user_email, group_id)
  end 

  ldap.delete_user({:email => "#{user_email}"})
end

def allLeaAllowApp(appName)
  conn = Mongo::Connection.new(PropLoader.getProps['DB_HOST'])
  db = conn[PropLoader.getProps['api_database_name']]
  appColl = db.collection("application")
  appId = appColl.find_one({"body.name" => appName})["_id"]
  puts("The app #{appName} id is #{appId}") if ENV['DEBUG']
  
  appAuthColl = db.collection("applicationAuthorization")
  edOrgColl = db.collection("educationOrganization")

  neededEdOrgs = edOrgColl.find({"body.organizationCategories" => ["Local Education Agency"]})
  neededEdOrgs.each do |edOrg|
    puts("Currently on edOrg #{edOrg.inspect}") if ENV['DEBUG']
    edOrgId = edOrg["_id"]
    edOrgTenant = edOrg["metaData"]["tenantId"]
    existingAppAuth = appAuthColl.find_one({"body.authId" => edOrgId})
    if existingAppAuth == nil 
      newAppAuth = {"_id" => "2012ls-#{SecureRandom.uuid}", "body" => {"authType" => "EDUCATION_ORGANIZATION", "authId" => edOrgId, "appIds" => [appId]}, "metaData" => {"tenantId" => edOrgTenant}}
      puts("About to insert #{newAppAuth.inspect}") if ENV['DEBUG']
      appAuthColl.insert(newAppAuth)
    elsif !(existingAppAuth["body"]["appIds"].include?(appId))
      existingAppAuth["body"]["appIds"].push(appId)
      puts("About to update #{existingAppAuth.inspect}") if ENV['DEBUG']
      appAuthColl.update({"body.authId" => edOrgId}, existingAppAuth)
    else
      puts("App already approved for district #{edOrgId}, skipping") if ENV['DEBUG']
    end
  end
end

# Property Loader class

class PropLoader
  @@yml = YAML.load_file(File.join(File.dirname(__FILE__),'properties.yml'))
  @@modified=false

  def self.getProps
    self.updateHash() unless @@modified
    return @@yml
  end

  private

  def self.updateHash()
    @@yml.each do |key, value|
      @@yml[key] = ENV[key] if ENV[key]
    end
    @@modified=true
  end
end
