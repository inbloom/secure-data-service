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

require 'selenium-webdriver'
require 'mongo'

require_relative '../../../utils/sli_utils.rb'
require_relative '../../../utils/selenium_common.rb'

DATABASE_HOST = PropLoader.getProps['ingestion_db']
DATABASE_PORT = PropLoader.getProps['ingestion_db_port']

#############################################################################################
# Transform
#############################################################################################

Transform /^<(.*?)>$/ do |human_readable_id|
  id = "/v1/students/5246832bf178dea013797aa14e57dcfbea46b17a_id"         if human_readable_id == "carmen.ortiz URI"
  id = "/v1/students/aee5235923f795ae2eeff4e8197624244e754885_id"         if human_readable_id == "lashawn.taite URI"
  id = "/v1/students/153df715388ff1b2293fc8b2f3828816bc2d3c1f_id"         if human_readable_id == "matt.sollars URI"
  id = "/v1/students/3d7084654aa96c1fdc68a27664760f6bb1b97b5a_id"         if human_readable_id == "bert.jakeman URI"
  id = "/v1/students/b98a593e13945f54ecc3f1671127881064ab592d_id"         if human_readable_id == "nate.dedrick URI"
  id = "/v1/students/2d17703cb29a95bbfdaab47f513cafdc0ef55d67_id"         if human_readable_id == "mu.mcneill URI"
  id = "/v1/students/df54047bf88ecd7e2f6fbf00951196f747c9ccfc_id"         if human_readable_id == "jack.jackson URI"
  id
end


#############################################################################################
# After Steps
#############################################################################################

#Undo changes made by update_mongo, remove_from_mongo, and add_to_mongo after the scenario ends
After do |scenario|
  if scenario.passed?
    unless @mongo_changes.nil?
      @mongo_changes.reverse_each do |mongo_change|
        disable_NOTABLESCAN()
        conn = Mongo::Connection.new(DATABASE_HOST,DATABASE_PORT)
        db = conn[mongo_change[:tenant]]
        coll = db.collection(mongo_change[:collection])
        if mongo_change[:operation] == 'remove'
          coll.insert(mongo_change[:value])
        elsif mongo_change[:operation] == 'update'
          if mongo_change[:found]
            coll.update(mongo_change[:query], {'$set' => {mongo_change[:field] => mongo_change[:value]}})
          else
            coll.update(mongo_change[:query], {'$unset' => {mongo_change[:field] => 1}})
          end
        elsif mongo_change[:operation] == 'add'
          entity = mongo_change[:entity]
          query = {'_id' => entity['_id']}
          coll.remove(query)
        end
        conn.close
        enable_NOTABLESCAN()
      end
    end
  end
end
#############################################################################################
# Mongo Steps
#############################################################################################

#The following three methods (update_mongo, remove_from_mongo, add_to_mongo) are singleton operators.
#They also keep a record of the changes they made so that they can be reverted after the scenario is done
#Note: tenant is the database name and query is a query that'd uniquely identify an entity
#      (Generally, a query like {'_id' => id} is enough)

def update_mongo(tenant, collection, query, field, remove, value = nil)
#Note: value is ignored if remove is true (It doesn't matter what the field contains if you're removing it)

  conn = Mongo::Connection.new(DATABASE_HOST,DATABASE_PORT)
  db = conn[tenant]
  coll = db.collection(collection)
  entity = coll.find_one(query)
  entity_iter = entity
  field_list = field.split('.')
  found = true
  field_list.each do |field_entry|
    if entity_iter.is_a? Array
      field_entry = field_entry.to_i
    else
      unless entity_iter.has_key? field_entry
        found = false
        break
      end
    end
    entity_iter = entity_iter[field_entry]
  end
  if !(remove) || found
    entry = {:operation => 'update',
             :tenant => tenant,
             :collection => collection,
             :query => query,
             :field => field,
             :remove => remove,
             :found => found,
             :value => entity_iter,
             :new => value
    }
    if remove
      coll.update(query, {'$unset' => {field => 1}})
    else
      coll.update(query, {'$set' => {field => value}})
    end
    (@mongo_changes ||= []) << entry
  end

  conn.close

end

def add_to_mongo(tenant, collection, entity)
  conn = Mongo::Connection.new(DATABASE_HOST,DATABASE_PORT)
  db = conn[tenant]
  coll = db.collection(collection)
  entry = {:operation => 'add',
           :tenant => tenant,
           :collection => collection,
           :entity => entity
  }

  coll.insert(entity)
  (@mongo_changes ||= []) << entry
  conn.close
end

def remove_from_mongo(tenant, collection, query)
  conn = Mongo::Connection.new(DATABASE_HOST,DATABASE_PORT)
  db = conn[tenant]
  coll = db.collection(collection)
  entity = coll.find_one(query)
  if entity
    entry = {:operation => 'remove',
             :tenant => tenant,
             :collection => collection,
             :query => query,
             :value => entity
    }
    coll.remove(query)

    (@mongo_changes ||= []) << entry

  end
  conn.close

end

Given /^I (remove|expire) all SEOA expiration dates for "([^"]*)" in tenant "([^"]*)"$/ do | function, staff, tenant|
  tenant = convertTenantIdToDbName tenant
  disable_NOTABLESCAN()
  conn = Mongo::Connection.new(DATABASE_HOST,DATABASE_PORT)
  db = conn[tenant]
  staff_coll = db.collection('staff')
  staff_id = staff_coll.find_one({'body.staffUniqueStateId' => staff})['_id']
  seoa_coll = db.collection('staffEducationOrganizationAssociation')
  seoas = seoa_coll.find({'body.staffReference' => staff_id}).to_a
  remove = (function == 'remove')
  seoas.each do |seoa|
    query = { '_id' => seoa['_id']}
    update_mongo(tenant, 'staffEducationOrganizationAssociation', query, 'body.endDate', remove, '2000-01-01')
  end

  conn.close
  enable_NOTABLESCAN()
end

Given /^I remove the school association with student "([^"]*)" in tenant "([^"]*)"$/ do |studentUniqueStateId, tenant|
  tenant = convertTenantIdToDbName tenant
  disable_NOTABLESCAN()
  query = {'body.studentUniqueStateId' => studentUniqueStateId}
  conn = Mongo::Connection.new(DATABASE_HOST,DATABASE_PORT)
  db = conn[tenant]
  student_coll = db.collection('student')
  student_id = student_coll.find_one(query)['_id']
  ssa_query = {'body.studentId' => student_id}
  remove_from_mongo(tenant, "studentSchoolAssociation", ssa_query)

  enable_NOTABLESCAN()
end

Given /^I modify all SEOA staff classifications for "([^"]*)" in tenant "([^"]*)" to "([^"]*)"$/ do |staff, tenant, value|
  tenant = convertTenantIdToDbName tenant
  disable_NOTABLESCAN()
  conn = Mongo::Connection.new(DATABASE_HOST,DATABASE_PORT)
  db = conn[tenant]
  staff_coll = db.collection('staff')
  staff_id = staff_coll.find_one({'body.staffUniqueStateId' => staff})['_id']
  seoa_coll = db.collection('staffEducationOrganizationAssociation')
  seoas = seoa_coll.find({'body.staffReference' => staff_id}).to_a
  seoas.each do |seoa|
    query = { '_id' => seoa['_id']}
    update_mongo(tenant, 'staffEducationOrganizationAssociation', query, 'body.staffClassification', false, value)
  end

  conn.close
  enable_NOTABLESCAN()

end

Given /^I remove all SEOAs for "([^"]*)" in tenant "([^"]*)"/ do |staff, tenant|
  tenant = convertTenantIdToDbName tenant
  disable_NOTABLESCAN()
  conn = Mongo::Connection.new(DATABASE_HOST,DATABASE_PORT)
  db = conn[tenant]
  staff_coll = db.collection('staff')
  staff_id = staff_coll.find_one({'body.staffUniqueStateId' => staff})['_id']
  seoa_coll = db.collection('staffEducationOrganizationAssociation')
  seoas = seoa_coll.find({'body.staffReference' => staff_id}).to_a
  seoas.each do |seoa|
    query = { '_id' => seoa['_id']}
    remove_from_mongo(tenant, 'staffEducationOrganizationAssociation', query)
  end

  conn.close
  enable_NOTABLESCAN()

end

Given /^I remove the SEOA with role "([^"]*)" for staff "([^"]*)" in "([^"]*)"$/ do |role, staff, edOrg|
  tenant = convertTenantIdToDbName @tenant
  disable_NOTABLESCAN()
  conn = Mongo::Connection.new(DATABASE_HOST,DATABASE_PORT)
  db = conn[tenant]

  staff_coll = db.collection('staff')
  staff_id = staff_coll.find_one({'body.staffUniqueStateId' => staff})['_id']

  edOrg_coll = db.collection('educationOrganization')
  edOrg_id = edOrg_coll.find_one({'body.stateOrganizationId' => edOrg})['_id']


  seoa_coll = db.collection('staffEducationOrganizationAssociation')
  seoas = seoa_coll.find({'body.staffReference' => staff_id, 'body.educationOrganizationReference' => edOrg_id, 'body.staffClassification' => role}).to_a
  seoas.each do |seoa|
    query = { '_id' => seoa['_id']}
    remove_from_mongo(tenant, 'staffEducationOrganizationAssociation', query)
  end

  conn.close
  enable_NOTABLESCAN()

end

Given /^I remove the teacherSectionAssociation for "([^"]*)"$/ do |staff|
  tenant = convertTenantIdToDbName @tenant
  disable_NOTABLESCAN()
  conn = Mongo::Connection.new(DATABASE_HOST,DATABASE_PORT)
  db = conn[tenant]

  staff_coll = db.collection('staff')
  staff_id = staff_coll.find_one({'body.staffUniqueStateId' => staff})['_id']

  section_coll = db.collection('section')
  sections = section_coll.find({'teacherSectionAssociation.body.teacherId' => staff_id}).to_a

  sections.each do |section|
    query = {'_id' => section['_id']}
    update_mongo(tenant, "section", query, "teacherSectionAssociation",true)
  end

  conn.close
  enable_NOTABLESCAN()
end

Given /^the only SEOA for "([^"]*)" is as a "([^"]*)" in "([^"]*)"$/ do |staff, role, edorg|
  disable_NOTABLESCAN()
  conn = Mongo::Connection.new(DATABASE_HOST,DATABASE_PORT)
  db_tenant = convertTenantIdToDbName @tenant
  db = conn[db_tenant]

  staff_coll = db.collection('staff')
  staff_id = staff_coll.find_one({'body.staffUniqueStateId' => staff})['_id']

  edorg_coll = db.collection('educationOrganization')
  edorg_id = edorg_coll.find_one({'body.stateOrganizationId' => edorg})['_id']


  seoa_coll = db.collection('staffEducationOrganizationAssociation')
  seoas = seoa_coll.find({'body.staffReference' => staff_id}).to_a
  first = true
  seoas.each do |seoa|
    query = { '_id' => seoa['_id']}
    if first
      update_mongo(db_tenant, 'staffEducationOrganizationAssociation', query, 'body.educationOrganizationReference', false, edorg_id)
      update_mongo(db_tenant, 'staffEducationOrganizationAssociation', query, 'body.staffClassification', false, role)
    else
      remove_from_mongo(db_tenant, 'staffEducationOrganizationAssociation', query)
    end
    first = false
  end

  conn.close
  enable_NOTABLESCAN()
end

Given /^I add a SEOA for "([^"]*)" in "([^"]*)" as a "([^"]*)"$/ do |staff, edOrg, role|
  disable_NOTABLESCAN()
  conn = Mongo::Connection.new(DATABASE_HOST,DATABASE_PORT)
  db_tenant = convertTenantIdToDbName @tenant
  db = conn[db_tenant]

  staff_coll = db.collection('staff')
  staff_id = staff_coll.find_one({'body.staffUniqueStateId' => staff})['_id']

  edorg_coll = db.collection('educationOrganization')
  edorg_id = edorg_coll.find_one({'body.stateOrganizationId' => edOrg})['_id']

  seoa = {'_id' => SecureRandom.uuid,
          'type' => 'staffEducationOrganizationAssociation',
          'body' => {'staffClassification' => role,
                     'educationOrganizationReference' => edorg_id,
                     'staffReference' => staff_id,
                     'beginDate' => '2000-01-01'}  }

  add_to_mongo(db_tenant, 'staffEducationOrganizationAssociation', seoa)
  conn.close
  enable_NOTABLESCAN()
end

Given /^the following student section associations in ([^ ]*) are set correctly$/ do |tenant, table|
  disable_NOTABLESCAN()
  conn = Mongo::Connection.new(DATABASE_HOST,DATABASE_PORT)
  tenant_hash = convertTenantIdToDbName(tenant)
  db = conn[tenant_hash]
  student_coll = db.collection('student')
  staff_coll = db.collection('staff')
  section_coll = db.collection('section')
  edorg_coll = db.collection('educationOrganization')

  table.hashes.map do |row|
    add = (row['enrolledInAnySection?'] == 'yes')
    student_id = student_coll.find_one({'body.studentUniqueStateId' => row['student']})['_id']
    teacher_id = staff_coll.find_one({'body.staffUniqueStateId' => row['teacher']})['_id']
    edorg_id = edorg_coll.find_one({'body.stateOrganizationId' => row['edorg']})['_id']

    sections = section_coll.find({'body.schoolId' => edorg_id,
                                  'teacherSectionAssociation.body.teacherId' => teacher_id,
                                  'studentSectionAssociation.body.studentId' => student_id}).to_a

    found = (sections.size > 0)

    if !found && add
      section = section_coll.find_one({'body.schoolId' => edorg_id,
                                       'teacherSectionAssociation.body.teacherId' => teacher_id},
                                      {:fields => %w(_id studentSectionAssociation)})
      query = { '_id' => section['_id']}
      entry = {
                '_id'       => "#{section['_id']}#{SecureRandom.uuid}",
                'body'      => {
                            'type'              => 'studentSectionAssociation',
                            'sectionId'         => section['_id'],
                            'studentId'         => student_id,
                            'homeroomIndicator' => section['studentSectionAssociation'][0]['body']['homeroomIndicator'],
                            'repeatIdentifier'  => section['studentSectionAssociation'][0]['body']['repeatIdentifier'],
                            'beginDate'         => section['studentSectionAssociation'][0]['body']['beginDate'],
                            'endDate'           => section['studentSectionAssociation'][0]['body']['endDate']
                                },
                'metaData'  => section['studentSectionAssociation'][0]['metaData']
              }
      (value = section['studentSectionAssociation']) << entry
      update_mongo(tenant_hash, 'section', query, 'studentSectionAssociation', false, value)
    elsif found && !add
      sections.each do |section|
        query = { '_id' => section['_id']}
        value = section['studentSectionAssociation']
        value.delete_if {|entry| entry['body']['studentId'] == student_id}
        update_mongo(tenant_hash, 'section', query, 'studentSectionAssociation', false, value)
      end
    end


  end

  conn.close
  enable_NOTABLESCAN()
end

Given /^I change the type of "([^"]*)" to "([^"]*)"$/ do |user, type|
  disable_NOTABLESCAN()
  conn = Mongo::Connection.new(DATABASE_HOST,DATABASE_PORT)
  db_name = convertTenantIdToDbName(@tenant)
  db = conn[db_name]
  staff_coll = db.collection('staff')
  staff_id = staff_coll.find_one({'body.staffUniqueStateId' => user})['_id']

  query = { '_id' => staff_id}
  update_mongo(db_name, 'staff', query, 'type', false, type)

  conn.close
  enable_NOTABLESCAN()
end

Given /^"([^"]*)" is not associated with any (program|cohort) that belongs to "([^"]*)"$/ do |student, collection, staff|
  disable_NOTABLESCAN()
  conn = Mongo::Connection.new(DATABASE_HOST,DATABASE_PORT)
  db_name = convertTenantIdToDbName(@tenant)
  db = conn[db_name]
  student_coll = db.collection('student')
  staff_coll = db.collection('staff')
  association_coll = db.collection("staff#{collection.capitalize}Association")

  student = student_coll.find_one({'body.studentUniqueStateId' => student})
  staff_id = staff_coll.find_one({'body.staffUniqueStateId' => staff})['_id']

  staff_entities = association_coll.find({'body.staffId' => staff_id}, {:fields => {'_id' => 0,
                                                                                    "body.#{collection}Id" => 1}}).to_a

  query = { '_id' => student['_id']}
  value = student["student#{collection.capitalize}Association"]
  unless value.nil?
    value.delete_if {|entry| staff_entities.include?({'body' => {"#{collection}Id" => entry['body']["#{collection}Id"]}})}
    update_mongo(db_name, 'student', query, "student#{collection.capitalize}Association", false, value)
  end


  conn.close
  enable_NOTABLESCAN()
end

Given /^I expire all section associations that "([^"]*)" has with "([^"]*)"$/ do |student, teacher|
  disable_NOTABLESCAN()
  conn = Mongo::Connection.new(DATABASE_HOST,DATABASE_PORT)
  db_name = convertTenantIdToDbName(@tenant)
  db = conn[db_name]
  student_coll = db.collection('student')
  teacher_coll = db.collection('staff')
  section_coll = db.collection('section')
  teacher_id = teacher_coll.find_one({'body.staffUniqueStateId' => teacher})['_id']
  student_id = student_coll.find_one({'body.studentUniqueStateId' => student})['_id']

  sections = section_coll.find({'teacherSectionAssociation.body.teacherId' => teacher_id,
                                'studentSectionAssociation.body.studentId' => student_id}).to_a

  sections.each do |section|
    query = { '_id' => section['_id']}
    value = section['studentSectionAssociation']
    index = value.index {|entry| entry['body']['studentId'] == student_id}
    update_mongo(db_name, 'section', query, "studentSectionAssociation.#{index}.body.endDate", false, '2000-01-01')
  end

  conn.close
  enable_NOTABLESCAN()
end

#############################################################################################
# OAUTH Steps
#############################################################################################

Given /^the testing device app key has been created$/ do
  @oauthClientId = "EGbI4LaLaL"
  @oauthClientSecret = "iGdeAGCugi4VwZNtMJR062nNKjB7gRKUjSB0AcZqpn8Beeee"
  @oauthRedirectURI = "http://device"
end

When /^I navigate to the API authorization endpoint with my client ID$/ do
  @driver.get PropLoader.getProps['api_server_url'] + "/api/oauth/authorize?response_type=code&client_id=#{@oauthClientId}"
end

Then /^I should be redirected to the realm choosing page$/ do
  assertWithWait("Failed to navigate to Realm chooser") {@driver.title.index("Choose your realm") != nil}
end

When /^I select "(.*?)" from the dropdown and click go$/ do |arg1|
  select = Selenium::WebDriver::Support::Select.new(@driver.find_element(:tag_name, "select"))
  select.select_by(:text, arg1)
  assertWithWait("Could not find the Go button")  { @driver.find_element(:id, "go") }
  @driver.find_element(:id, "go").click
end

Then /^I should receive a json response containing my authorization code$/ do
  assertWithWait("Could not find text 'authorization_code' on page") {@driver.page_source.include?("authorization_code")}
  @oauthAuthCode = @driver.page_source.match(/"authorization_code":"(?<Code>[^"]*)"/)[:Code]
end

Then /^I should receive a response page with http error code 403$/ do
  assertWithWait("Could not find text 'HTTP Error 403' on page") {@driver.page_source.include?("HTTP Error 403")}
end

When /^I navigate to the API token endpoint with my client ID, secret, authorization code, and redirect URI$/ do
  @driver.get PropLoader.getProps['api_server_url'] + "/api/oauth/token?response_type=code&client_id=#{@oauthClientId}" +
  "&client_secret=#{@oauthClientSecret}&code=#{@oauthAuthCode}&redirect_uri=#{@oauthRedirectURI}"
end

Then /^I should receive a json response containing my authorization token$/ do
  assertWithWait("Could not find text 'authorization_token' on page") {@driver.page_source.include?("access_token")}

  @sessionId = @driver.page_source.match(/"access_token":"(?<Token>[^"]*)"/)[:Token]
  puts "sessionId = #@sessionId"
end

Then /^I should be able to use the token to make valid API calls$/ do
  restHttpGet("/system/session/check", "application/json")
  assert(@res != nil, "Response from rest-client GET is nil")
  assert(@res != nil, "Response is nil")
  data = JSON.parse(@res.body)
  assert(data != nil, "Response body is nil")
  assert(data['authenticated'] == true,
  "Session debug context 'authentication.authenticated' is not true")
end

def all_lea_allow_app_for_tenant(app_name, tenant_name)
  sleep 1
  disable_NOTABLESCAN()
  conn = Mongo::Connection.new(PropLoader.getProps['DB_HOST'], PropLoader.getProps['DB_PORT'])
  db = conn[PropLoader.getProps['api_database_name']]
  app_coll = db.collection("application")
  app = app_coll.find_one({"body.name" => app_name})
  raise "ERROR: Could not find an application named #{app_name}" if app.nil?

  app_id = app["_id"]

  db_tenant = conn[convertTenantIdToDbName(tenant_name)]
  app_auth_coll = db_tenant.collection("applicationAuthorization")
  ed_org_coll = db_tenant.collection("educationOrganization")

  needed_ed_orgs = []
  ed_org_coll.find().each do |edorg|
    needed_ed_orgs.push(edorg["_id"])
  end

  app_auth_coll.remove("body.applicationId" => app_id)
  new_app_auth = {"_id" => "2012ls-#{SecureRandom.uuid}", "body" => {"applicationId" => app_id, "edorgs" => needed_ed_orgs}, "metaData" => {"tenantId" => tenant_name}}
  app_auth_coll.insert(new_app_auth)
  conn.close
  enable_NOTABLESCAN()
end

def authorize_edorg_for_tenant(app_name, tenant_name)
  #sleep 1
  puts "Entered authorizeEdorg" if ENV['DEBUG']
  disable_NOTABLESCAN()
  puts "Getting mongo cursor" if ENV['DEBUG']
  conn = Mongo::Connection.new(PropLoader.getProps['DB_HOST'], PropLoader.getProps['DB_PORT'])
  puts "Setting into the sli db" if ENV['DEBUG']
  db = conn[PropLoader.getProps['api_database_name']]
  puts "Setting into the application collection" if ENV['DEBUG']
  app_coll = db.collection("application")
  puts "Finding the application with name #{app_name}" if ENV['DEBUG']
  app_id = app_coll.find_one({"body.name" => app_name})["_id"]
  puts("The app #{app_name} id is #{app_id}") if ENV['DEBUG']

  db_tenant = conn[convertTenantIdToDbName(tenant_name)]
  app_auth_coll = db_tenant.collection("applicationAuthorization")

  puts("The app #{app_name} id is #{app_id}")
  needed_ed_orgs = app_auth_coll.find_one({"body.applicationId" => app_id})["body"]["edorgs"]
  needed_ed_orgs.each do |edorg|
    app_coll.update({"_id" => app_id}, {"$push" => {"body.authorized_ed_orgs" => edorg}})
  end

  conn.close
  enable_NOTABLESCAN()
end

Given /^I import the odin setup application and realm data$/ do
  @ci_realm_store_path = File.dirname(__FILE__) + '/../../../../../../../tools/jmeter/odin-ci/'
  @local_realm_store_path = File.dirname(__FILE__) + '/../../../../../../../tools/jmeter/odin-local-setup/'
  #get current working dir
  current_dir = Dir.getwd
  # Get current server environment (ci or local) from properties.yml
  app_server = PropLoader.getProps['app_bootstrap_server']
  # Drop in ci specific app-auth fixture data
  if app_server == "ci"
    puts "\b\bDEBUG: We are setting CI environment app auth data"
    Dir.chdir(@ci_realm_store_path)
    `sh ci-jmeter-realm.sh`
    disable_NOTABLESCAN()
    conn = Mongo::Connection.new(DATABASE_HOST,DATABASE_PORT)
    db = conn['sli']
    coll = db.collection('realm')
    coll.update({'_id' => '45b02cb0-1bad-4606-a936-094331bd47fe'}, {'$set' => {'body.idp.id' => PropLoader.getProps['ci_idp_redirect_url']}})
    coll.update({'_id' => '45b02cb0-1bad-4606-a936-094331bd47fe'}, {'$set' => {'body.idp.redirectEndpoint' => PropLoader.getProps['ci_idp_redirect_url']}})
    conn.close
    enable_NOTABLESCAN()
    # Drop in local specific app-auth fixture data
  elsif app_server == "local"
    puts "\b\bDEBUG: We are setting LOCAL environment app auth data"
    Dir.chdir(@local_realm_store_path)
    `sh local-jmeter-realm.sh`
  else
    puts "\n\nWARNING: No App server context set, assuming CI environment.."
    Dir.chdir(@ci_realm_store_path)
    `sh ci-jmeter-realm.sh`
    disable_NOTABLESCAN()
    conn = Mongo::Connection.new(DATABASE_HOST,DATABASE_PORT)
    db = conn['sli']
    coll = db.collection('realm')
    coll.update({'_id' => '45b02cb0-1bad-4606-a936-094331bd47fe'}, {'$set' => {'body.idp.id' => PropLoader.getProps['ci_idp_redirect_url']}})
    coll.update({'_id' => '45b02cb0-1bad-4606-a936-094331bd47fe'}, {'$set' => {'body.idp.redirectEndpoint' => PropLoader.getProps['ci_idp_redirect_url']}})
    conn.close
    enable_NOTABLESCAN()
  end
  @tenant = 'Midgar'
  all_lea_allow_app_for_tenant('Mobile App', @tenant)
  authorize_edorg_for_tenant('Mobile App', @tenant)
  # restore back current dir
  Dir.chdir(current_dir)
end

#############################################################################################


#############################################################################################
# API Steps
#############################################################################################

Then /^I should get and store the link named "(.*?)"$/ do |mylink|
  @result = JSON.parse(@res.body)
  assert(@result != nil, "Response contains no data")
  found = false
  if !@result.nil? && !@result.empty?
    @result["links"].each do |link|
      if link["rel"] == mylink
        found = true
        teacherHashPush(mylink, link["href"])
      end
    end
  end
  assert(found, "Could not find the link #{mylink} in the URI Response: #{@result}")
end

Then /^I should extract the "(.*?)" id from the "(.*?)" URI$/ do |resource, link|
  value = @teacher[link].match(/#{resource}\/(.*?_id)/)
  teacherHashPush("id", $1)
end

Then /^the response (should|should not) have restricted student data$/ do |function|
  apiRecord = JSON.parse(@res.body)
  assert(apiRecord != nil, "Result of JSON parsing is nil")
  should = (function == 'should')
  negative = 'not ' if should
  assert(apiRecord.has_key?("schoolFoodServicesEligibility") == should, "Restricted field #{negative}found")
end

Then /^the response (should|should not) have general student data$/ do |function|
  apiRecord = JSON.parse(@res.body)
  assert(apiRecord != nil, "Result of JSON parsing is nil")
  should = (function == 'should')
  negative = 'not ' if should
  assert(apiRecord.has_key?("studentUniqueStateId") == should, "General field studentUniqueStateId #{negative}found")
  assert(apiRecord.has_key?("name") == should, "General field name #{negative}found")
  assert(apiRecord.has_key?("birthData") == should, "General field birthData #{negative}found")
  assert(apiRecord.has_key?("hispanicLatinoEthnicity") == should, "General field hispanicLatinoEthnicity #{negative}found")
end


#############################################################################################

# Build the teacher hash
def teacherHashPush(key, value)
  @teacher = Hash.new unless defined? @teacher
  @teacher[key] = value
end

