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


require "selenium-webdriver"
require 'approval'
require "mongo"
require 'rumbster'
require 'digest'

require_relative '../../../utils/sli_utils.rb'
require_relative '../../../utils/selenium_common.rb'
require_relative '../../../ingestion/features/step_definitions/ingestion_steps.rb'

PRELOAD_EDORG = "STANDARD-SEA"

Before do
  @explicitWait = Selenium::WebDriver::Wait.new(:timeout => 60)
  @db = Mongo::Connection.new.db(PropLoader.getProps['api_database_name'])
  @edorgId =  "Test_Ed_Org"
  @email = "devldapuser_#{Socket.gethostname}@slidev.org"
  dbName = @email.gsub(/[^A-Za-z0-9]/, '_')
  @tenantDb = Mongo::Connection.new.db(convertTenantIdToDbName(dbName))
end

After do
  begin
    STDOUT.puts "Attempting to delete #{@lz}" if $SLI_DEBUG
    initializeLandingZone(@lz)
  rescue
    if $SLI_DEBUG
      STDOUT.puts "Could not clean out landing zone:  #{@lz}"
      STDOUT.puts "Reason:  #{$!}"
    end
  end

  # This is a hack to clean up the landing zone for the pre-populate sample data scenario
  # It depends on other sandbox LZ provisioning tests to clean up the LZ for the pre-populate test.
  # This assumes test ordering, so it's definitely not the clean way to code.
  # Please update if anyone can think of a better way. Thank you!
  begin
    sample_data_set_lz = @lz[0..@lz.rindex("/")] + sha256(PRELOAD_EDORG) + "/"
    STDOUT.puts "Attempting to delete #{sample_data_set_lz}" if $SLI_DEBUG
    initializeLandingZone(sample_data_set_lz)
  rescue
    if $SLI_DEBUG
      STDOUT.puts "Could not clean out landing zone:  #{sample_data_set_lz}"
      STDOUT.puts "Reason:  #{$!}"
    end
  end
end


Transform /^<([^"]*)>$/ do |human_readable_id|
  id = @email                                       if human_readable_id == "USERID"
  id = "test1234"                                   if human_readable_id == "PASSWORD"
  id = @edorgId                                     if human_readable_id == "EDORG_NAME"
  id = @edorgId                                     if human_readable_id == "SANDBOX_EDORG"
  id = @email                                       if human_readable_id == "DEVELOPER_EMAIL"
  id = "sandbox_edorg_2"                            if human_readable_id == "SANDBOX_EDORG_2"
  id = @email                                       if human_readable_id == "TENANTID"
  id = @edorgId                                     if human_readable_id == "PROD_EDORG"
  id = "district_edorg"                             if human_readable_id == "DISTRICT_EDORG"
  id = "STANDARD-SEA"                               if human_readable_id == "SMALL_SAMPLE_DATASET_EDORG"
  id
end


Given /^LDAP server has been setup and running$/ do
  @ldap = LDAPStorage.new(PropLoader.getProps['ldap_hostname'], PropLoader.getProps['ldap_port'],
                          PropLoader.getProps['ldap_base'], PropLoader.getProps['ldap_admin_user'],
                          PropLoader.getProps['ldap_admin_pass'])
  @email_sender_name= "Administrator"
  @email_sender_address= "noreply@slidev.org"
  @email_conf = {
      :host =>  PropLoader.getProps['email_smtp_host'],
      :port => PropLoader.getProps['email_smtp_port'],
      :sender_name => @email_sender_name,
      :sender_email_addr => @email_sender_address
  }
end

Given /^there is an sandbox account in ldap$/ do
  @sandbox = true
  ApprovalEngine.init(@ldap,Emailer.new(@email_conf),nil,@sandbox)
end

Given /^there is an production Ingestion Admin account in ldap$/ do
  @sandbox = false
  ApprovalEngine.init(@ldap,Emailer.new(@email_conf),nil,true)

end

Given /^the account has a tenantId "([^"]*)"$/ do |tenantId|
#@email="devldapuser_#{Socket.gethostname}@slidev.org"
  @tenantId = tenantId
  removeUser(@email)
  sleep(1)

  user_info = {
      :first => "Provision",
      :last => "test",
      :email => @email,
      :emailAddress => @email,
      :password => "test1234",
      :emailtoken => "token",
      :vendor => "test",
      :status => "submitted",
      :homedir => "/dev/null",
      :uidnumber => "500",
      :gidnumber => "500",
      :tenant => @tenantId
  }

  ApprovalEngine.add_disabled_user(user_info)
  ApprovalEngine.change_user_status(@email, ApprovalEngine::ACTION_ACCEPT_EULA)
  user_info = ApprovalEngine.get_user(@email)
  ApprovalEngine.verify_email(user_info[:emailtoken])
# if(@sandbox==false)
#ApprovalEngine.change_user_status(@email,ApprovalEngine::ACTION_APPROVE)
#end
#ApprovalEngine.change_user_status(@email,"approve",true)
#clear_edOrg()
#clear_tenant()
end


Given /^there is no corresponding tenant in mongo$/ do
  clear_tenant
end

Given /^there is no corresponding ed\-org in mongo$/ do
  clear_edOrg
end

Given /^there is no corresponding ed\-org "(.*?)" in mongo$/ do |edorg|
  @edorgId=edorg
  clear_edOrg
end

Given /^the account has a edorg of "(.*?)"$/ do |edorgId|
  @edorgName = edorgId
  user_info = ApprovalEngine.get_user(@email).merge( {:edorg => edorgId })
  ApprovalEngine.update_user_info(user_info)
end

Given /^there is already a tenant with tenantId "(.*?)" in mongo$/ do |tenantId|
  clear_tenant
  create_tenant(tenantId,"sandbox_edorg_2")
end

Given /^there is already a edorg with stateOrganizationId "(.*?)" in mongo$/ do |stateOrganizationId|
  clear_edOrg
  create_edOrg(stateOrganizationId)
end

Given /^there is no landing zone for the "(.*?)" in mongo$/ do |edorgId|
  disable_NOTABLESCAN()
  found = false
  tenant_coll = @db['tenant']
  tenant_coll.find("body.tenantId" => @tenantId).each do |tenant|
    tenant['body']['landingZone'].each do |landing_zone|
      if landing_zone.include?(@tenantId) and landing_zone.include?(edorgId)
        found = true
        puts landing_zone
      end
    end
  end
  assert(found==false,"there is a landing zone for #{edorgId} in mongo")
  enable_NOTABLESCAN()
end

Given /^there is no landing zone for the user in LDAP$/ do
  user_info = ApprovalEngine.get_user(@email)
  assert(user_info[:homedir].include?("dev/null"),"the user landing zone is already set to #{user_info[:homedir]}")
end

Given /^there is a landing zone for the "(.*?)" in mongo$/ do |edorgId|
  clear_tenant
  create_tenant(@tenantId,edorgId)
end

Given /^there is a landing zone for the "(.*?)" in LDAP$/ do |edorg|
  user_info = ApprovalEngine.get_user(@email).merge( {:homedir => "/ingestion/lz/inbound/"+@tenantId+"/"+@edorgId})
  puts user_info
  ApprovalEngine.update_user_info(user_info)
end


When /^the developer provision a "(.*?)" Landing zone with edorg is "(.*?)"$/ do |env,edorgId|
  step "I provision with \"#{env}\" high\-level ed\-org to \"#{edorgId}\""
end

When /^the Ingestion Admin go to the provisioning application web page$/ do
  step "the developer go to the provisioning application web page"
end


Then /^a tenant with tenantId "([^"]*)" created in Mongo$/ do |tenantId|
  disable_NOTABLESCAN()
  sleep 3 # Used to deal with wait between clicking button and checking mongo
  tenant_coll=@db["tenant"]
  assert( tenant_coll.find("body.tenantId" => tenantId).count >0 ,"the tenantId #{tenantId} is not created in mongo")
  enable_NOTABLESCAN()
end

Then /^the directory structure for the landing zone is stored for tenant in mongo$/ do
  disable_NOTABLESCAN()
  found =false
  tenant_coll=@db["tenant"]
  tenant_entity = tenant_coll.find("body.tenantId" => @tenantId)
  tenant_entity.each do |tenant|
    tenant['body']['landingZone'].each do |landing_zone|
      if check_lz_path(landing_zone['path'], @tenantId, @edorgName)
        found=true
        puts landing_zone['path']
        @lz = landing_zone['path']
      end
    end
  end
  enable_NOTABLESCAN()
  assert(found,"the directory structure for the landing zone is not stored in mongo")
end

Then /^the user gets a success message$/ do
  success = @driver.find_element(:id,"successMessage")
  assert(success!=nil,"didnt get a success message")
end

Then /^the Ingestion Admin gets a success message$/ do
  step "the user gets a success message"
end

When /^the developer is authenticated to Simple IDP as user "([^"]*)" with pass "([^"]*)"$/ do |user, pass|
  step "I submit the credentials \"#{user}\" \"#{pass}\" for the \"Simple\" login page"
end

Then /^the Ingestion Admin is authenticated to Simple IDP as user "(.*?)" with pass "(.*?)"$/ do |user, pass|
  step "I submit the credentials \"#{user}\" \"#{pass}\" for the \"Simple\" login page"
end


When /^the developer go to the provisioning application web page$/ do
  url =PropLoader.getProps['admintools_server_url']+"/landing_zone"
  @driver.get url
end

When /^I provision a Landing zone$/ do
  @driver.find_element(:id, "provisionButton").click
end

When /^the Ingestion Admin provision a Landing zone$/ do
  step "I provision a Landing zone"
end


When /^I provision with "([^"]*)" high\-level ed\-org to "([^"]*)"$/ do |env,edorgName|
  if(env=="sandbox")
    @driver.find_element(:id, "custom").click
    @driver.find_element(:id, "custom_ed_org").send_keys edorgName
  end
  @driver.find_element(:id, "provisionButton").click
  @edorgName=edorgName
end

Then /^I get the success message$/ do
  assertWithWait("No success message") {@driver.find_element(:id, "successMessage") != nil}
end

Then /^the user gets an error message$/ do
  already_provisioned = @driver.find_element(:id,"alreadyProvisioned")
  assert(already_provisioned!=nil,"didnt get an already provisioned message")
end

Then /^an ed\-org is created in Mongo with the "([^"]*)" is "([^"]*)"$/ do |key1, value1|
  step "I am logged in using \"operator\" \"operator1234\" to realm \"SLI\""
  uri="/v1/educationOrganizations"
  uri=uri+"?"+URI.escape(key1)+"="+URI.escape(value1)
  restHttpGet(uri)
  assert(@res.length>0,"didnt see a top level ed org with #{key1} is #{value1}")
end

Then /^a request to provision a landing zone is made$/ do
  # this request is made by landing zone app in admin tools
end

Then /^the directory structure for the landing zone is stored in ldap$/ do
  user=@ldap.read_user(@email)
  assert(check_lz_path(user[:homedir], @tenantId, @edorgName),"the landing zone path is not stored in ldap")
end

When /^the developer selects to preload "(.*?)"$/ do |sample_data_set|
  if sample_data_set.downcase.include? "small"
    sample_data_set="small"
  else
    sample_data_set="medium"
  end
  @explicitWait.until{@driver.find_element(:id,"ed_org_STANDARD-SEA").click}
  select = Selenium::WebDriver::Support::Select.new(@explicitWait.until{@driver.find_element(:id,"sample_data_select")})
  select.select_by(:value, sample_data_set)
  @explicitWait.until{@driver.find_element(:id,"provisionButton").click}
  @edorgName = PRELOAD_EDORG
end

Then /^the "(.*?)" data to preload is stored for the tenant in mongo$/ do |sample_data_set|
  disable_NOTABLESCAN()
  tenant_collection =@db["tenant"]
  preload_tenant=tenant_collection.find("body.tenantId"=> @tenantId,"body.landingZone.educationOrganization" => PRELOAD_EDORG,"body.landingZone.preload.files" => [sample_data_set])
  assert(preload_tenant.count()>0,"the #{sample_data_set} data to preload is not stored for tenant #{@tenantId} in mongo, instead tenant was #{tenant_collection.find("body.tenantId"=> @tenantId)}")
  enable_NOTABLESCAN()
  #preload_tenant.each  do |tenant|
  #  puts tenant
  #end
end

Then /^the user gets a success message indicating preloading has been triggered$/ do
  step "the user gets a success message"
end

Given /^user's landing zone is still provisioned from the prior preloading$/ do
  disable_NOTABLESCAN()
  tenant_coll = @db["tenant"]
  preload_tenant=tenant_coll.find("body.tenantId" => @tenantId, "body.landingZone.educationOrganization" => PRELOAD_EDORG)
  assert(preload_tenant.count()>0, "the user's landing zone is not provisioned from the prior preloading")
  enable_NOTABLESCAN()
end

Then /^I go to my landing zone$/ do
  disable_NOTABLESCAN()
  tenant_collection =@db["tenant"]
  tenant=tenant_collection.find("body.tenantId"=> @tenantId).next
  puts "tenant is #{tenant}"
  @landing_zone_path = tenant["body"]["landingZone"][0]["path"] + "/"
  @source_file_name = "preload"
  enable_NOTABLESCAN()
end

Then /^I clean the landing zone$/ do
  begin
    STDOUT.puts "Attempting to delete #{@lz}" if $SLI_DEBUG
    initializeLandingZone(@lz)
  rescue
    if $SLI_DEBUG
      STDOUT.puts "Could not clean out landing zone:  #{@lz}"
      STDOUT.puts "Reason:  #{$!}"
    end
  end
end

def check_lz_path(path, tenant, edOrg)
  path.include?(tenant + "/" + sha256(edOrg)) || path.include?(tenant + "/" + edOrg)
end

def sha256(to_hash)
  Digest::SHA256.hexdigest(to_hash)
end

def removeUser(email)
  if ApprovalEngine.user_exists?(email)
    ApprovalEngine.remove_user(email)
  end
end
def clear_edOrg
  disable_NOTABLESCAN()
  edOrg_coll=@tenantDb["educationOrganization"]
  edOrg_coll.remove("body.stateOrganizationId"=>@edorgId)
  assert(edOrg_coll.find("body.stateOrganizationId"=>@edorgId).count==0,"edorg with stateOrganizationId #{@edorgId} still exist in mongo")
  enable_NOTABLESCAN()
end

def clear_tenant
  disable_NOTABLESCAN()
  tenant_coll=@db["tenant"]
  tenant_coll.remove("body.tenantId"=>@tenantId)
  assert(tenant_coll.find('body.tenantId'=> @tenantId).count()==0,"tenant with tenantId #{@tenantId} still exist in mongo")
  enable_NOTABLESCAN()
end

def assertText(text)
  @explicitWait.until{@driver.find_element(:tag_name,"body")}
  body = @driver.find_element(:tag_name, "body")
  assert(body.text.include?(text), "Cannot find the text \"#{text}\"")
end

def create_tenant (tenantId,edorgId)
  tenant_entity =
      {
          "_id" => "2012lr-80a2ba9a-b9b6-11e1-a6ba-68a86d3e6628",
          "type" => "tenant",
          "body" => {
              "tenantId" => tenantId,
              "landingZone" => [
                  {
                      "ingestionServer" => "Mac.local",
                      "educationOrganization" => edorgId,
                      "desc" => nil,
                      "path" => "/ingestion/lz/inbound/"+tenantId+"/"+edorgId,
                      "userNames" => nil
                  }
              ]
          },
          "metaData" => {
              "tenantId" => "SLI",
              "createdBy" => tenantId
          }
      }
      disable_NOTABLESCAN()
  tenant_coll = @db['tenant']
  tenant_coll.save(tenant_entity)
  enable_NOTABLESCAN()
end

def create_edOrg (stateOrganizationId)
  edorg_entity={
      "_id" => "2012fy-a82073df-b9ba-11e1-a6ba-68a86d3e6628",
      "type" => "educationOrganization",
      "body" => {
          "organizationCategories" => [
              "State Education Agency"
          ],
          "address" => [
              {
                  "postalCode" => "27713",
                  "streetNumberName" => "unknown",
                  "stateAbbreviation" => "NC",
                  "city" => "unknown"
              }
          ],
          "stateOrganizationId" => stateOrganizationId,
          "nameOfInstitution" => stateOrganizationId
      },
      "metaData" => {
          "tenantId" => @tenantId
      }
  }
  disable_NOTABLESCAN()
  edorg_coll = @tenantDb["educationOrganization"]
  edorg_coll.save(edorg_entity)
  enable_NOTABLESCAN()
end
