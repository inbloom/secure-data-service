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

require_relative '../../ingestion/rc_test/step_definition/rc_integration_ingestion.rb'

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
  host = (RUN_ON_RC) ? "rcingest01.#{RC_SERVER}" : PropLoader.getProps['ingestion_db']
  @conn = Mongo::Connection.new(host)
end

###############################################################################
# WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN WHEN
###############################################################################

When /^I drop a control file to purge tenant data as "([^\"]*)" with password "([^\"]*)" to "([^\"]*)"$/ do |user, pass, server|
  if RUN_ON_RC
    steps %Q{
      Given I am using local data store
      And I am using default landing zone
      And I use the landingzone user name "#{user}" and password "#{pass}" on landingzone server "#{server}-lz.slidev.org" on port "443"
      And I drop the file "Purge.zip" into the landingzone
      And I check for the file "job*.log" every "30" seconds for "600" seconds
    }
  else
    steps %Q{
      Given I am using local data store
      And I have a local configured landing zone for my tenant
      And I drop the file "Purge.zip" into the landingzone
      And I check for the file "job*.log" every "30" seconds for "600" seconds
    }
  end
end

When /^I get the database name$/ do
  @tenant_name = PropLoader.getProps['tenant']
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
  assert(!@conn.database_names.include?(@tenant_db_name), "Tenant DB not dropped.")
end

Then /^I will drop the tenant document from the collection$/ do
  sli_db = @conn.db(PropLoader.getProps['sli_database_name'])
  sli_db['tenant'].remove("body.tenantId" => @tenant_name)
  assert(sli_db['tenant'].find("body.tenantId" => @tenant_name).count == 0, "Tenant document not dropped.")
end
