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

###############################################################################
# BEFORE BEFORE BEFORE BEFORE BEFORE BEFORE BEFORE BEFORE BEFORE BEFORE BEFORE
###############################################################################

###############################################################################
# TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM TRANSFORM
###############################################################################

###############################################################################
# GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN GIVEN
###############################################################################

Given /^I have a connection to Mongo$/ do
  host = PropLoader.getProps['ingestion_db']
  port = PropLoader.getProps['ingestion_db_port']
  @conn = Mongo::Connection.new(host, port)
end

###############################################################################
# WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN
###############################################################################

When /^I drop a control file to purge tenant data as "([^\"]*)" with password "([^\"]*)" to "([^\"]*)"$/ do |user, pass, server|
  steps %Q{
    Given a landing zone
    And I drop the file "Purge.zip" into the landingzone
    And I check for the file "job*.log" every "30" seconds for "600" seconds
  }
end

When /^I get the database name$/ do
  if (@mode == "SANDBOX")
    @tenant_name = PropLoader.getProps['sandbox_tenant']
  else
    @tenant_name = PropLoader.getProps['tenant']
  end

  @tenant_db_name = convertTenantIdToDbName(@tenant_name)

end

###############################################################################
# THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN THEN
###############################################################################

Then /^my tenant database should be cleared$/ do
  step "I get the database name"
  tenant_db = @conn.db(@tenant_db_name)
  coll_names = tenant_db.collection_names
  coll_to_skip = ["system.indexes",
                  "system.js",
                  "system.profile",
                  "system.namespaces",
                  "system.users",
                  "tenant",
                  "securityEvent",
                  "realm",
                  "application",
                  "roles",
                  "customRole"]
  disable_NOTABLESCAN
  coll_names.each do |coll|
    assert(tenant_db["#{coll}"].count == 0, "#{coll} is not empty.") if !coll_to_skip.include?(coll)
  end
  enable_NOTABLESCAN
end

Then /^I will drop the whole database$/ do
  @conn.drop_database(@tenant_db_name)
  tenant_dropped = false
  if (!@conn.database_names.include?(@tenant_db_name) || @conn.db(@tenant_db_name).collection_names.empty?)
    tenant_dropped = true
  end
  assert(tenant_dropped, "Tenant DB not dropped.")
end

Then /^I will clean my tenants recordHash documents from ingestion_batch_job db$/ do
  host = PropLoader.getProps['ingestion_batchjob_db']
  port = PropLoader.getProps['ingestion_batchjob_db_port']
  batchJobconn = Mongo::Connection.new(host, port)
  batchJobDb = batchJobconn.db(PropLoader.getProps['ingestion_batchjob_database_name'])
  batchJobDb['recordHash'].remove("t" => @tenant_name)
end

Then /^I clean my tenant's landing zone$/ do
if RUN_ON_RC
    steps %Q{
        Given I am using local data store
        And I am using default landing zone
        And I am using the tenant "<SANDBOX_TENANT>"
        And I use the landingzone user name "<DEVELOPER_SB_EMAIL>" and password "<DEVELOPER_SB_EMAIL_PASS>" on landingzone server "<LANDINGZONE>" on port "<LANDINGZONE_PORT>"
   }

   Net::SFTP.start(@lz_url, @lz_username, {:password => @lz_password, :port => @lz_port_number}) do |sftp|
        clear_remote_lz(sftp)
      end
  end


end


Then /^I will drop the tenant document from the collection$/ do
  sli_db = @conn.db(PropLoader.getProps['sli_database_name'])
  sli_db['tenant'].remove("body.tenantId" => @tenant_name)
  assert(sli_db['tenant'].find("body.tenantId" => @tenant_name).count == 0, "Tenant document not dropped.")
end

Then /^I will delete the realm for this tenant from the collection$/ do
  sli_db = @conn.db(PropLoader.getProps['sli_database_name'])
  sli_db['realm'].remove("body.tenantId" => @tenant_name)
  assert(sli_db['realm'].find("body.tenantId" => @tenant_name).count == 0, "Realm document not deleted.")
end

Then /^I will delete the applications "([^\"]*)" from the collection$/ do |apps|
  app_names = apps.split(",")
  sli_db = @conn.db(PropLoader.getProps['sli_database_name'])
  app_names.each do |name|
    sli_db['application'].remove("body.name" => name)
    assert(sli_db['application'].find("body.name" => name).count == 0, "The application '#{name}' is not deleted.")
  end
end
