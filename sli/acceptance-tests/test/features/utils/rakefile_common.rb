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

require 'ldapstorage'
require 'digest/sha1'
require 'yaml'
require 'rest-client'

def cleanUpLdapUser(user_email)
  ldap = LDAPStorage.new(PropLoader.getProps['ldap_hostname'], PropLoader.getProps['ldap_port'],
                         PropLoader.getProps['ldap_base'], PropLoader.getProps['ldap_admin_user'],
                         PropLoader.getProps['ldap_admin_pass'], PropLoader.getProps['ldap_use_ssl'])

  cleanUpUser(user_email, ldap)
end

def cleanUpMiniSandboxLdapUser(user_email)
  ldap_sb = LDAPStorage.new(PropLoader.getProps['minisb_ldap_hostname'], PropLoader.getProps['minisb_ldap_port'],
                         PropLoader.getProps['minisb_ldap_base'], PropLoader.getProps['minisb_ldap_admin_user'],
                         PropLoader.getProps['minisb_ldap_admin_pass'], PropLoader.getProps['minisb_ldap_use_ssl'])

  cleanUpUser(user_email, ldap_sb)

end

def cleanUpUser(user_email, ldap)
  puts "Attempting to remove user: #{user_email}"
  ldap.get_user_groups(user_email).each do |group_id|
    ldap.remove_user_group(user_email, group_id)
  end 

  ldap.delete_user("#{user_email}")
end

def allLeaAllowApp(appName)
  allLeaAllowAppForTenant(appName, 'Midgar')
  allLeaAllowAppForTenant(appName, 'Hyrule')
end

#
# Replace the doc for the app with the given name in
# "applicationAuthorization" so that the app is authorized for use by
# ALL edOrgs (appearing in "educationOrganization") in the given
# tenant
#
def allLeaAllowAppForTenant(appName, tenantName)
  sleep 1
  disable_NOTABLESCAN()
  conn = Mongo::Connection.new(PropLoader.getProps['DB_HOST'], PropLoader.getProps['DB_PORT'])
  db = conn[PropLoader.getProps['api_database_name']]
  appColl = db.collection("application")
  app = appColl.find_one({"body.name" => appName})
  raise "ERROR: Could not find an application named #{appName}" if app.nil?

  appId = app["_id"]
  puts("The app #{appName} id is #{appId}") if ENV['DEBUG']
  
  dbTenant = conn[convertTenantIdToDbName(tenantName)]
  appAuthColl = dbTenant.collection("applicationAuthorization")
  edOrgColl = dbTenant.collection("educationOrganization")

  neededEdOrgs = [] 
  edOrgColl.find().each do |edorg|
    edorg_entry = {}
    edorg_entry["authorizedEdorg"]= edorg["_id"]
    neededEdOrgs.push(edorg_entry)
  end
  appAuthColl.remove("body.applicationId" => appId)
  newAppAuth = {"_id" => "2012ls-#{SecureRandom.uuid}", "body" => {"applicationId" => appId, "edorgs" => neededEdOrgs}, "metaData" => {"tenantId" => tenantName}}
  appAuthColl.insert(newAppAuth)
  conn.close
  enable_NOTABLESCAN()
end

def authorizeEdorg(appName)
  authorizeEdorgForTenant(appName, 'Midgar')
end

def authorizeEdorgForTenant(appName, tenantName)
  #sleep 1
  puts "Entered authorizeEdorg" if ENV['DEBUG']
  disable_NOTABLESCAN()
  puts "Getting mongo cursor" if ENV['DEBUG']
  conn = Mongo::Connection.new(PropLoader.getProps['DB_HOST'], PropLoader.getProps['DB_PORT'])
  puts "Setting into the sli db" if ENV['DEBUG']
  db = conn[PropLoader.getProps['api_database_name']]
  puts "Setting into the application collection" if ENV['DEBUG']
  appColl = db.collection("application")
  puts "Finding the application with name #{appName}" if ENV['DEBUG']
  appId = appColl.find_one({"body.name" => appName})["_id"]
  puts("The app #{appName} id is #{appId}") if ENV['DEBUG']
  
  dbTenant = conn[convertTenantIdToDbName(tenantName)]
  appAuthColl = dbTenant.collection("applicationAuthorization")
  
  puts("The app #{appName} id is #{appId}")
  neededEdOrgsArray = appAuthColl.find_one({"body.applicationId" => appId})["body"]["edorgs"]
  neededEdOrgs = []
  neededEdOrgsArray.each do |edorg_entry|
    neededEdOrgs.push(edorg_entry["authorizedEdorg"])
  end
  neededEdOrgs.each do |edorg|
    new_edorg = Hash.new
    new_edorg["authorizedEdorg"] = edorg
    appColl.update({"_id" => appId}, {"$push" => {"body.edorgs" => new_edorg}})
  end
  
  conn.close
  enable_NOTABLESCAN()
end

def randomizeRcProdTenant()
  PropLoader.update('tenant', "#{PropLoader.getProps['tenant']}_#{Time.now.to_i}")
end

def randomizeRcSandboxTenant()
  email = PropLoader.getProps['developer_sb_email_imap_registration_user_email']
  email2 = PropLoader.getProps['developer2_sb_email_imap_registration_user_email']
  
  emailParts = email.split("@")
  randomEmail = "#{emailParts[0]}+#{Random.rand(1000)}"+"@"+"#{emailParts[1]}"
  PropLoader.update('developer_sb_email_imap_registration_user_email', randomEmail)
  PropLoader.update('sandbox_tenant', randomEmail)
  
  email2Parts = email2.split("@")
  randomEmail2 = "#{email2Parts[0]}+#{Random.rand(1000)}"+"@"+"#{email2Parts[1]}"
  PropLoader.update('developer2_sb_email_imap_registration_user_email', randomEmail2)
end

def convertTenantIdToDbName(tenantId)
  return Digest::SHA1.hexdigest tenantId
end


def testTls(url, token, client_id, path)
  puts "Loading Key and Certificate for client ID #{client_id}"
  client_cert = OpenSSL::X509::Certificate.new File.read File.expand_path("../keys/#{client_id}.crt", __FILE__)
  private_key = OpenSSL::PKey::RSA.new File.read File.expand_path("../keys/#{client_id}.key", __FILE__)

  headers = {:accept => "application/x-tar", :Authorization => "bearer #{token}"}

  res = RestClient::Request.execute(:method => :get, :url => url, :headers => headers, :ssl_client_cert => client_cert, :ssl_client_key => private_key) {|response, request, result| response }
  puts "Return Code = #{res.code}, Header = #{res.raw_headers}"
  if res.code > 300
    puts "Error Response = #{res.body}"
    return
  end

  file = Dir.pwd + "/#{path}"
  File.delete(file) if File.exists?(file)
  File.open(file, "a") do |outf|
    outf << res.body
  end
  puts "File was saved to #{Dir.pwd + "/#{path}"}"
end

# Property Loader class

class PropLoader
  @@yml = YAML.load_file(File.join(File.dirname(__FILE__),'properties.yml'))
  #@@modified=false

  def self.getProps
    #self.updateHash() unless @@modified
    self.updateHash()
    return @@yml
  end

  def self.update(key, val)
    #@@yml[key] = val
    ENV[key] = val
  end

  private

  def self.updateHash()
    @@yml.each do |key, value|
      @@yml[key] = ENV[key] if ENV[key]
    end
    #@@modified=true
  end
end

##############################################################################
# turn ON --notablescan MongoDB flag, if set in ENV
##############################################################################
def enable_NOTABLESCAN()
  setTableScan true
end



##############################################################################
# turn OFF --notablescan MongoDB flag, if set in ENV
##############################################################################
def disable_NOTABLESCAN()
  setTableScan false
end

def setTableScan(enabled)
  if ENV["TOGGLE_TABLESCANS"]
    puts "Turning --notablescan flag #{enabled}"
    adminconn = Mongo::Connection.new
    admindb = adminconn.db('admin')
    cmd = {setParameter: 1, notablescan: enabled}
    admindb.command(cmd)
    begin
      shards = admindb.command({listShards: 1})["shards"]
    rescue Mongo::OperationFailure => e
      puts "Could not get the shard list"
      shards = []
    end
    shards.each{ |shard|
      hostname, port = shard['host'].split(":")
      shardconn = Mongo::Connection.new(hostname, port)
      sharddb = shardconn['admin']
      sharddb.command(cmd)
      sharddb.get_last_error()
      shardconn.close()
    }
    admindb.get_last_error()
    adminconn.close
  end
end
