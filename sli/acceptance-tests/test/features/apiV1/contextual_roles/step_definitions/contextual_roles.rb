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
require 'open3'

require_relative '../../../utils/sli_utils.rb'
require_relative '../../../utils/selenium_common.rb'
require_relative '../../utils/api_utils.rb'
require_relative '../../entities/crud/step_definitions/crud_step.rb'
require_relative '../../end_user_stories/CustomEntities/step_definitions/CustomEntities_steps.rb'

DATABASE_HOST = PropLoader.getProps['ingestion_db']
DATABASE_PORT = PropLoader.getProps['ingestion_db_port']

#############################################################################################
# Transform
#############################################################################################

Transform /^<(.*?)>$/ do |human_readable_id|

  case human_readable_id
#Students
    when 'carmen.ortiz URI'
      id = '/v1/students/5246832bf178dea013797aa14e57dcfbea46b17a_id'
    when 'lashawn.taite URI'
      id = '/v1/students/aee5235923f795ae2eeff4e8197624244e754885_id'
    when 'matt.sollars URI'
      id = '/v1/students/153df715388ff1b2293fc8b2f3828816bc2d3c1f_id'
    when 'bert.jakeman URI'
      id = '/v1/students/3d7084654aa96c1fdc68a27664760f6bb1b97b5a_id'
    when 'nate.dedrick URI'
      id = '/v1/students/b98a593e13945f54ecc3f1671127881064ab592d_id'
    when 'mu.mcneill URI'
      id = '/v1/students/2d17703cb29a95bbfdaab47f513cafdc0ef55d67_id'
    when 'jack.jackson URI'
      id = '/v1/students/df54047bf88ecd7e2f6fbf00951196f747c9ccfc_id'

#Education Organizations
    when 'East Daybreak URI'
      id = '/v1/educationOrganizations/2a30827ed4cf5500fb848512d19ad73ed37c4464_id'
    when 'District 9 URI'
      id = '/v1/educationOrganizations/99a4ec9d3ba372993b2860a798b550c77bb73a09_id'

#Following are for DELETES
    when 'pat.sollars URI'
      id = '/v1/students/993283bce14b54bbfc896b8452f26e745ce4c101_id'
    when 'herman.ortiz URI'
      id = '/v1/students/5fb6e796c5a8485c28f1875fda41810eca13db46_id'
    when 'shawn.taite URI'
      id = '/v1/students/edeba6dcfab1d1461896e9581c20ce329604a94c_id'
    when 'jake.bertman URI'
      id = '/v1/students/7b9ae98922c207146f1feb0b799a91b1c02f17eb_id'
    when 'john.johnson URI'
      id = '/v1/students/aceb1e6d159c833db61f72a9dfeee50be2f2691e_id'
    when 'kate.dedrick URI'
      id = '/v1/students/a94fc8d0895f2a00b811d54996a8f3eb8fdc7480_id'

#Following are for CUSTOM
    when 'JACK.JACKSON CUSTOM URI'
      id = '/v1/students/df54047bf88ecd7e2f6fbf00951196f747c9ccfc_id/custom'
    when 'BERT.JAKEMAN CUSTOM URI'
      id = '/v1/students/3d7084654aa96c1fdc68a27664760f6bb1b97b5a_id/custom'
    when 'EAST.DAYBREAK CUSTOM URI'
      id = '/v1/educationOrganizations/2a30827ed4cf5500fb848512d19ad73ed37c4464_id/custom'
    when 'JMACEY CUSTOM URI'
      id = '/v1/staff/7810ac678851ae29a450cc18bd9f47efa37bfaef_id/custom'
    when 'JMACEY SEOAA CUSTOM URI'
      id = '/v1/staffEducationOrgAssignmentAssociations/57edc58caa226f4ab888e51ef8b5531b98800cca_id/custom'

#Following is for a newly created entity
    when 'NEWLY CREATED ENTITY ID'
      id = @newId
  end

  id
end

Transform /^(<[^"]*>)\/([^"]*)$/ do |uri_placeholder1, uri_placeholder2|
  uri = Transform(uri_placeholder1) + "/" + uri_placeholder2
  uri
end

Transform /^\[([^{]*)\]$/ do |array_list|
  array = array_list.split(%r{,\s*})
  array
end

Transform /^\[\{([^{]*)\}\]$/ do |array_hash|
  array = []
  hash = {}
  hash_split = array_hash.split(%r{,\s*})
  hash_split.each do |hash_parse|
    hash_key_value = hash_parse.split(%r{\s*:\s*})
    new_hash = {hash_key_value[0] => hash_key_value[1]}
    hash.merge!(new_hash)
  end
  array << hash

  array
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

def update_mongo(db_name, collection, query, field, remove, value = nil)
#Note: value is ignored if remove is true (It doesn't matter what the field contains if you're removing it)

  entry = update_mongo_operation(db_name, collection, query, field, remove, value)

  (@mongo_changes ||= []) << entry
end

def update_mongo_operation(db_name, collection, query, field, remove, value = nil)
  conn = Mongo::Connection.new(DATABASE_HOST,DATABASE_PORT)
  db = conn[db_name]
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
             :tenant => db_name,
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
  end
  conn.close

  return entry

end

def add_to_mongo(db_name, collection, entity)
  conn = Mongo::Connection.new(DATABASE_HOST,DATABASE_PORT)
  db = conn[db_name]
  coll = db.collection(collection)
  entry = {:operation => 'add',
           :tenant => db_name,
           :collection => collection,
           :entity => entity
  }

  coll.insert(entity)
  (@mongo_changes ||= []) << entry
  conn.close
end

def remove_from_mongo(db_name, collection, query)
  entry = remove_from_mongo_operation(db_name, collection, query)

  (@mongo_changes ||= []) << entry

end

def remove_from_mongo_operation(db_name, collection, query)
  conn = Mongo::Connection.new(DATABASE_HOST,DATABASE_PORT)
  db = conn[db_name]
  coll = db.collection(collection)
  entity = coll.find_one(query)
  if entity
    entry = {:operation => 'remove',
             :tenant => db_name,
             :collection => collection,
             :query => query,
             :value => entity
    }
    coll.remove(query)

  end
  conn.close

  return entry
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

Given /^I change all SEOAs of "([^"]*)" to the edorg "([^"]*)"$/ do |staff, edorg|
  tenant = convertTenantIdToDbName @tenant
  disable_NOTABLESCAN()
  conn = Mongo::Connection.new(DATABASE_HOST,DATABASE_PORT)
  db = conn[tenant]
  staff_coll = db.collection('staff')
  edorg_coll = db.collection('educationOrganization')
  staff_id = staff_coll.find_one({'body.staffUniqueStateId' => staff})['_id']
  edorg_id = edorg_coll.find_one({'body.stateOrganizationId' => edorg})['_id']
  seoa_coll = db.collection('staffEducationOrganizationAssociation')
  seoas = seoa_coll.find({'body.staffReference' => staff_id}).to_a
  seoas.each do |seoa|
    query = { '_id' => seoa['_id']}
    update_mongo(tenant, 'staffEducationOrganizationAssociation', query, 'body.educationOrganizationReference', false, edorg_id)
  end

  enable_NOTABLESCAN()
  conn.close

end

Given /^I change the custom role of "([^"]*)" to (add|remove) the "([^"]*)" right$/ do |role, function, right|
  tenant = convertTenantIdToDbName @tenant
  disable_NOTABLESCAN()
  conn = Mongo::Connection.new(DATABASE_HOST,DATABASE_PORT)
  db = conn[tenant]
  add = (function == 'add')
  role_coll = db.collection('customRole')
  custom_roles = role_coll.find_one()
  roles = custom_roles['body']['roles']
  index = roles.index {|entry| entry['names'][0] == role}
  if add
    roles[index]['rights'] << right
  else
    roles[index]['rights'].delete_if {|entry| entry == right}
  end
  update_mongo(tenant,'customRole',{},"body.roles.#{index}.rights", false, roles[index]['rights'])

  enable_NOTABLESCAN()
  conn.close

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
  conn.close
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

Given /^I remove "([^"]*)" from the custom roles$/ do |roleName|
  tenant = convertTenantIdToDbName @tenant
  disable_NOTABLESCAN()
  conn = Mongo::Connection.new(DATABASE_HOST,DATABASE_PORT)
  db = conn[tenant]

  query =  {'body.roles.names' => roleName}

  customRoleColl = db.collection('customRole')
  customRole = customRoleColl.find_one({'body.roles.names' => roleName})

  roles = customRole ['body']['roles']

  roles.each do |role|
   role.delete_if {|key, value| key == 'names' && value == [roleName]}

   if !role.has_key?('names')
     role.store('names', ['DummyValue'])
   end
  end

  remove_from_mongo(tenant, 'customRole', query)
  add_to_mongo(tenant, 'customRole', customRole)

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
                'type'      => 'studentSectionAssociation',
                'body'      => {
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

Given /^I change the type of "([^"]*)" admin role to "(true|false)"$/ do |rolegroup, isAdmin|
  disable_NOTABLESCAN()
  conn = Mongo::Connection.new(DATABASE_HOST,DATABASE_PORT)
  db_name = convertTenantIdToDbName(@tenant)
  db = conn[db_name]
  custom_role_coll = db.collection('customRole')
  custom_role = custom_role_coll.find_one()
  roles =custom_role['body']['roles']

  index = roles.index {|entry| entry['groupTitle'] == rolegroup}
  roles[index]['isAdminRole'] = (isAdmin == 'true')

  update_mongo(db_name,'customRole',{},"body.roles.#{index}", false, roles[index])

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

Given /^I get (\d+) random ids for "([^"]*)" in "([^"]*)"$/ do |number, type, entity|
  disable_NOTABLESCAN()
  conn = Mongo::Connection.new(DATABASE_HOST,DATABASE_PORT)
  db_name = convertTenantIdToDbName(@tenant)
  db = conn[db_name]
  coll = db.collection(entity)
  if type == 'educationOrganization' && entity == 'educationOrganization'
    entities = coll.find({}, {:fields => %w(_id)}).to_a
  else
    entities = coll.find({'type' => type}, {:fields => %w(_id)}).to_a
  end
  assert(entities.size > 0, "No #{entity} of type #{type} found")
  (entities.shuffle.take(number.to_i)).each { |entry| (@entity_ids ||= []) << entry['_id'] }
  conn.close
  enable_NOTABLESCAN()
end

Given /^I get (\d+) random ids associated with the edorgs for "([^"]*)" of "([^"]*)" in "([^"]*)"$/ do |number, staff, type, collection|
  case type
    when 'studentProgramAssociation'
      subdoc = true
      edorg_in_subdoc = true
      edorg_ref = 'body.educationOrganizationId'
    when 'teacherSchoolAssociation', 'studentSchoolAssociation', 'disciplineIncident', 'attendance'
      subdoc = false
      edorg_ref = 'body.schoolId'
    when 'staffEducationOrganizationAssociation'
      subdoc = false
      edorg_ref = 'body.educationOrganizationReference'
    when 'cohort'
      subdoc = false
      edorg_ref = 'body.educationOrgId'
    when 'disciplineAction'
      subdoc = false
      edorg_ref = 'body.responsibilitySchoolId'
    when 'gradebookEntry'
      subdoc = true
      edorg_in_subdoc = false
      edorg_ref = 'body.schoolId'
    else
      subdoc = false
      edorg_ref = 'body.educationOrganizationId'
  end

  disable_NOTABLESCAN()
  conn = Mongo::Connection.new(DATABASE_HOST,DATABASE_PORT)
  db_name = convertTenantIdToDbName(@tenant)
  db = conn[db_name]
  edorgs = Set.new
  seoa_coll = db.collection('staffEducationOrganizationAssociation')
  staff_coll = db.collection('staff')
  staff_id = staff_coll.find_one({'body.staffUniqueStateId' => staff})['_id']
  seoas = seoa_coll.find('body.staffReference' => staff_id).to_a
  seoas.each {|seoa| edorgs.add(seoa['body']['educationOrganizationReference']) }

  entity_ids = Set.new
  coll = db.collection(collection)
  edorgs.each do |edorg|
    if subdoc
      if edorg_in_subdoc
        entities = coll.find({"#{type}.#{edorg_ref}" => edorg},{:fields => ["#{type}.$"]}).to_a
      else
        entities = coll.find({edorg_ref => edorg}).to_a
      end
      entities.each { |entity| entity_ids.add(entity[type][0]['_id'])}
    else
      entities = coll.find({edorg_ref => edorg},{:fields => %w(_id)}).to_a
      entities.each { |entity| entity_ids.add(entity['_id'])}
    end
  end
  assert(entity_ids.size > 0, "No #{type} found that is associated with the edorgs of #{staff}")
  (entity_ids.to_a.shuffle.take(number.to_i)).each { |entry| (@entity_ids ||= []) << entry }
  conn.close
  enable_NOTABLESCAN()
end

Given /^I add (student school association|attendance) for "([^"]*)" in "([^"]*)" that's already expired$/ do |collection, student, edorg|
  disable_NOTABLESCAN()
  conn = Mongo::Connection.new(DATABASE_HOST,DATABASE_PORT)
  db_name = convertTenantIdToDbName(@tenant)
  db = conn[db_name]
  student_coll = db.collection('student')
  edorg_coll = db.collection('educationOrganization')
  student_id = student_coll.find_one({'body.studentUniqueStateId' => student})['_id']
  edorg_id = edorg_coll.find_one({'body.stateOrganizationId' => edorg})['_id']
  entries = {
      'student school association' => {
          '_id' => SecureRandom.uuid,
          'type' => 'studentSchoolAssociation',
          'body' => {
              'schoolYear' => '2010-2011',
              'schoolId' => edorg_id,
              'studentId' => student_id,
              'entryDate' => '2010-10-10',
              'entryGradeLevel' => 'Ninth grade',
              'exitWithdrawDate' => '2011-05-05',
              'exitWithdrawType' => 'Withdrawn due to illness'
          }
      },
      'attendance' => {
          '_id' => SecureRandom.uuid,
          'type' => 'attendance',
          'body' => {
              'schoolYear' => '2010-2011',
              'schoolId' => edorg_id,
              'studentId' => student_id,
              'attendanceEvent' => [
                  {
                      'reason' => 'Alarm did not go off',
                      'event' => 'Tardy',
                      'date' => '2010-12-01'
                  },
                  {
                      'reason' => 'Sick: Had note from doctor',
                      'event' => 'Excused Absence',
                      'date' => '2011-03-15'
                  }
              ]
          }
      }
  }
  @newId = entries[collection]['_id']
  add_to_mongo(db_name,entries[collection]['type'],entries[collection])

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

When /^I log in as "(.*?)"/ do |user|

  expiration_in_seconds = 300
  script_loc = File.dirname(__FILE__) + '/../../../../../../opstools/token-generator/generator.rb'

  disable_NOTABLESCAN()
  conn = Mongo::Connection.new(DATABASE_HOST, DATABASE_PORT)
  db_name = convertTenantIdToDbName @tenant
  db = conn[db_name]
  staff_coll = db.collection('staff')
  seoa_coll = db.collection('staffEducationOrganizationAssociation')
  edorg_coll = db.collection('educationOrganization')

  staff_id = staff_coll.find_one({'body.staffUniqueStateId' => user})['_id']
  seoas = seoa_coll.find({'body.staffReference' => staff_id}).to_a
  assert(seoas.size > 0, "No SEOAs found for #{user}")

  sli_db_name = PropLoader.getProps['sli_database_name']
  sli_db = conn[sli_db_name]
  user_session_coll = sli_db.collection('userSession')
  user_session_coll.remove()

  first = true
  previous_session = 0
  session_id = 0
  seoas.each do |seoa|
    role = seoa['body']['staffClassification']
    edorg = edorg_coll.find_one({'_id' => seoa['body']['educationOrganizationReference']})['body']['stateOrganizationId']
    command = "ruby #{script_loc} -e #{expiration_in_seconds} -c #{@oauthClientId} -u #{user} -r \"#{role}\" -E \"#{edorg}\" -t \"#{@tenant}\" -R \"#{@realm}\" -n #{first}"
    out, status = Open3.capture2(command)
    assert(out.include?("token is"), "Could not get a token for #{user} for realm #{@realm}")
    match = /token is (.*)/.match(out)
    session_id = match[1]
    assert(previous_session == session_id,'Multiple sessions detected') unless first
    previous_session = session_id
    first = false
  end

  @sessionId = session_id
  puts "The generated token is #{@sessionId}"

  conn.close
  enable_NOTABLESCAN()
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
  @realm = 'IL-DAYBREAK'
  all_lea_allow_app_for_tenant('Mobile App', @tenant)
  authorize_edorg_for_tenant('Mobile App', @tenant)
  # restore back current dir
  Dir.chdir(current_dir)
end

#############################################################################################


#############################################################################################
# API Steps
#############################################################################################

Given /^entity type "([^"]*)"$/ do |arg1|
  @currentEntity = arg1
end

Given /^a valid formatted entity json document for a "([^"]*)"$/ do |arg1|
  @format = "application/json"
  step "a valid json document for entity \"#{arg1}\""
end

When /^the response should have the newly created entity$/ do
  response = JSON.parse(@res.body)
  found = false
  if response.is_a? Array
    response.each do |entry|
      if entry['id'] == @newId
        found = true
        break
      end
    end
  else
    found = (response['id'] == @newId)
  end
  assert(found,'Newly created entity not found in the response')
end

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

Then /^the response (should|should not) have the following students$/ do |function, table|
  should = (function == 'should')
  negative = 'not ' if should
  table.hashes.map do |row|
    found = false
    JSON.parse(@res.body).each do |student|
      if student['studentUniqueStateId'] == row['student']
        found = true
        break
      end
    end
    assert(found == should, "Student: #{row['student']} #{negative}found")
  end
end

Then /^"([^"]*)" in the response (should|should not) have restricted data$/ do |student, function|
  should = (function == 'should')
  negative = 'not ' if should
  response = JSON.parse(@res.body)
  index =  response.index {|entry| entry['studentUniqueStateId'] == student}
  assert(response[index].has_key?('schoolFoodServicesEligibility') == should, "Restricted field #{negative}found")
end

Then /^I should see all global entities$/ do
  jsonResult = JSON.parse(@res.body)
  apiSet = Set.new
  dbSet = Set.new

  #Get entity ids from the api call
  jsonResult.each do |data|
    apiSet.add(data["id"])
  end

  #Get entity ids from the database
  conn = Mongo::Connection.new(PropLoader.getProps["ingestion_db"], PropLoader.getProps["ingestion_db_port"])
  db = conn.db(convertTenantIdToDbName("Midgar"))
  coll = db[@currentEntity]

  coll.find.each do |doc|
    dbSet.add(doc["_id"])
  end

  conn.close

  # Global entity special cases.
  # This clearly has to be rewritten.  This is just a placeholder, until the actual logic is installed.
  if (@currentEntity == "educationOrganization" )
    dbSet = apiSet
  elsif (@currentEntity == "learningObjective")
    dbSet = apiSet
  elsif (@currentEntity == "learningStandard")
    dbSet = apiSet
  elsif (@currentEntity == "program")
    dbSet = apiSet
  elsif (@currentEntity == "section")
    dbSet = apiSet
  end
  diffSet = dbSet.difference(apiSet)

  #difference should be 0 between two non-empty sets of entity ids
  assert(apiSet.empty? == false, "Api returned 0 entities of type #{@currentEntity}.")
  assert(dbSet.empty? == false, "No entities of type #{@currentEntity} found in the database.")
  assert(diffSet.empty?, "Did not receive the expected entities:
    \n Number of expected (api) entities: #{apiSet.size}
    \n Number of actual (database) entities: #{dbSet.size}
    \n Outstanding entities: #{diffSet.inspect}")
end

When /^I navigate to GET each id for "([^"]*)"$/ do |uri|
  @return_codes = []
  @entity_ids.each do |id|
    step "I navigate to GET \"#{uri}/#{id}\""
    @return_codes << @res.code
  end
end

Then /^All the return codes should be (\d+)$/ do |code|
  @return_codes.each do |return_code|
    assert(return_code == code.to_i,"Found a return code of #{return_code}. Expecting all to be #{code}.")
  end
end

When /^I set the ([^"]*) to ([^"]*)$/ do |key, value|
  @result = {} if !defined? @result
  value.is_a?(String) ? @result[key] = convert(value) : @result[key] = value
end

Given /^a valid json document for entity "([^"]*)"$/ do |entity|
  disable_NOTABLESCAN()
  conn = Mongo::Connection.new(DATABASE_HOST, DATABASE_PORT)
  db_name = convertTenantIdToDbName @tenant
  db = conn[db_name]
  gp_coll = db.collection('gradingPeriod')
  co_coll = db.collection('courseOffering')
  course_coll = db.collection('course')
  session_coll = db.collection('session')
  edorg_coll = db.collection('educationOrganization')
  student_coll = db.collection('student')
  di_coll = db.collection('disciplineIncident')
  section_coll = db.collection('section')
  yt_coll = db.collection('yearlyTranscript')

  gp = gp_coll.find_one({'body.gradingPeriodIdentity.schoolId' => '99a4ec9d3ba372993b2860a798b550c77bb73a09_id'})
  gp_id = gp['_id']
  gp_school_id = gp['body']['gradingPeriodIdentity']['schoolId']

  co = co_coll.find_one({'body.schoolId' => '2a30827ed4cf5500fb848512d19ad73ed37c4464_id'})
  co_id = co['_id']
  co_school_id = co['body']['schoolId']

  course_session = session_coll.find_one({})
  course = course_coll.find_one({})
  course_id = course['_id']
  course_school_id = course_session['body']['schoolId']
  course_session_id = course_session['_id']

  student = student_coll.find_one({'body.studentUniqueStateId' => 'jack.jackson'})
  student_programs = student['studentProgramAssociation']
  edorg = edorg_coll.find_one({'body.stateOrganizationId' => 'District 9'})
  edorg_programs = edorg['body']['programReference']
  student_programs.each do |student_program|
    edorg_programs.delete_if {|edorg_program| edorg_program == student_program['body']['programId']}
  end
  edorg_program_id = edorg_programs.shuffle[0]

  di = di_coll.find_one({'body.schoolId' => '2a30827ed4cf5500fb848512d19ad73ed37c4464_id'})

  gradebook_section = section_coll.find_one({'body.schoolId' => '2a30827ed4cf5500fb848512d19ad73ed37c4464_id'})

  yt = yt_coll.find_one({'body.studentId' => '153df715388ff1b2293fc8b2f3828816bc2d3c1f_id'})

  enable_NOTABLESCAN()
  conn.close

  entity_data = {
    'gradingPeriod' => {
        'gradingPeriodIdentity' => {
            'schoolId' => '2a30827ed4cf5500fb848512d19ad73ed37c4464_id',
            'gradingPeriod' => 'First Six Weeks',
            'schoolYear' => '2011-2012'
        },
        'beginDate' => '2012-07-01',
        'endDate' => '2012-07-31',
        'totalInstructionalDays' => 20
    },

    'course' => {
        'courseTitle' => 'Chinese 1',
        'numberOfParts' => 1,
        'courseCode' => [{
                             'ID' => 'C1',
                             'identificationSystem' => 'School course code',
                             'assigningOrganizationCode' => "Bob's Code Generator"
                         }],
        'courseLevel' => 'Basic or remedial',
        'courseLevelCharacteristics' => ['Advanced Placement'],
        'gradesOffered' => ['Eighth grade'],
        'subjectArea' => 'Foreign Language and Literature',
        'courseDescription' => 'Intro to Chinese',
        'dateCourseAdopted' => '2001-01-01',
        'highSchoolCourseRequirement' => false,
        'courseDefinedBy' => 'LEA',
        'minimumAvailableCredit' => {
            'credit' => 1.0
        },
        'maximumAvailableCredit' => {
            'credit' => 1.0
        },
        'careerPathway' => 'Hospitality and Tourism',
        'schoolId' => '264b869a22b74b4ab5b3b6620b3d31d1a98dc4a0_id',
        'uniqueCourseId' => 'Chinese-1-10'
    },

    'courseOffering' => {
        'schoolId' => course_school_id,
        'localCourseCode' => 'LCCMA1',
        'sessionId' => course_session_id,
        'localCourseTitle' => 'Math 1 - Intro to Mathematics',
        'courseId' => course_id
    },

    'educationOrganization' => {
        'organizationCategories' => ['State Education Agency'],
        'stateOrganizationId' => 'SomeUniqueSchoolDistrict-2422883',
        'nameOfInstitution' => 'Gotham City School District',
        'address' => [
            'streetNumberName' => '111 Ave C',
            'city' => 'Chicago',
            'stateAbbreviation' => 'IL',
            'postalCode' => '10098',
            'nameOfCounty' => 'Wake'
        ]
    },

    'learningObjective' => {
        'academicSubject' => 'Mathematics',
        'objective' => 'Learn Mathematics',
        'objectiveGradeLevel' => 'Fifth grade'
    },

    'learningStandard' => {
        'learningStandardId' => {
            'identificationCode' => 'apiTestLearningStandard'},
        'description' => 'a description',
        'gradeLevel' => 'Ninth grade',
        'contentStandard'=>'State Standard',
        'subjectArea' => 'English'
    },

    'program' => {
        'programId' => 'ACC-TEST-PROG-3',
        'programType' => 'Remedial Education',
        'programSponsor' => 'Local Education Agency',
        'services' => [[
                           {'codeValue' => 'codeValue3'},
                           {'shortDescription' => 'Short description for acceptance test program 3'},
                           {'description' => 'This is a longer description of the services provided by acceptance test program 3. More detail could be provided here.'}]]
    },

    'section' => {
        'uniqueSectionCode' => 'SpanishB09',
        'sequenceOfCourse' => 1,
        'educationalEnvironment' => 'Off-school center',
        'mediumOfInstruction' => 'Independent study',
        'populationServed' => 'Regular Students',
        'schoolId' => co_school_id,
        'courseOfferingId' => co_id
    },

    'session' => {
        'sessionName' => 'Spring 2012',
        'schoolYear' => '2011-2012',
        'term' => 'Spring Semester',
        'beginDate' => '2012-01-01',
        'endDate' => '2012-06-30',
        'totalInstructionalDays' => 80,
        'gradingPeriodReference' => [gp_id],
        'schoolId' => gp_school_id
    },


    'assessment' => {
        'assessmentTitle' => 'Writing Advanced Placement Test',
        'assessmentIdentificationCode' => [{
                                               'identificationSystem' => 'School',
                                               'ID' => '01234B'
                                           }],
        'academicSubject' => 'Mathematics',
        'assessmentCategory' => 'Achievement test',
        'gradeLevelAssessed' => 'Adult Education',
        'contentStandard' => 'LEA Standard',
        'version' => 2
    },


    'school' => {
        'shortNameOfInstitution' => 'SCTS',
        'nameOfInstitution' => 'School Crud Test School',
        'webSite' => 'www.scts.edu',
        'stateOrganizationId' => 'SomeUniqueSchool-24242342',
        'organizationCategories' => ['School'],
        'address' => [
            'addressType' => 'Physical',
            'streetNumberName' => '123 Main Street',
            'city' => 'Lebanon',
            'stateAbbreviation' => 'KS',
            'postalCode' => '66952',
            'nameOfCounty' => 'Smith County'
        ],
        'gradesOffered' => [
            'Kindergarten',
            'First grade',
            'Second grade',
            'Third grade',
            'Fourth grade',
            'Fifth grade'
        ]
    },

    'graduationPlan' => {
        'creditsBySubject' => [{
                                   'subjectArea' => 'English',
                                   'credits' => {
                                       'creditConversion' => 0,
                                       'creditType' => 'Semester hour credit',
                                       'credit' => 6
                                   }
                               }],
        'individualPlan' => false,
        'graduationPlanType' => 'Minimum',
        'educationOrganizationId' => '2a30827ed4cf5500fb848512d19ad73ed37c4464_id',
        'totalCreditsRequired' => {
            'creditConversion' => 0,
            'creditType' => 'Semester hour credit',
            'credit' => 32
        }
    },

    'competencyLevelDescriptor' => {
        'description' => 'Herman tends to throw tantrums',
        'codeValue' => 'Temper Tantrum',
        'performanceBaseConversion' => 'Basic'
    },

    'studentCompetencyObjective' => {
        'objectiveGradeLevel' => 'Kindergarten',
        'objective' => 'Phonemic Awareness',
        'studentCompetencyObjectiveId' => 'SCO-K-1',
        'educationOrganizationId' => '2a30827ed4cf5500fb848512d19ad73ed37c4464_id'
    },

    'staffEducationOrganizationAssociation' => {
        'educationOrganizationReference' => '2a30827ed4cf5500fb848512d19ad73ed37c4464_id',
        'staffReference' => '7810ac678851ae29a450cc18bd9f47efa37bfaef_id',
        'beginDate' => '2000-01-01',
        'positionTitle' => 'Hall monitor',
        'staffClassification' => 'Leader'
    },

    'teacherSchoolAssociation' => {
        'schoolId' => '2a30827ed4cf5500fb848512d19ad73ed37c4464_id',
        'programAssignment' => 'Special Education',
        'teacherId' => '8107c5ce31cec58d4ac0b647e91b786b03091f02_id',
        'instructionalGradeLevels' => ['Twelfth grade'],
        'academicSubjects' => ['Composite']
    },

    'studentSchoolAssociation' => {
        'schoolId' => '2a30827ed4cf5500fb848512d19ad73ed37c4464_id',
        'studentId' => 'df54047bf88ecd7e2f6fbf00951196f747c9ccfc_id',
        'entryDate' => '2000-01-01',
        'exitWithdrawDate' => '2000-12-12',
        'entryGradeLevel' => 'Ninth grade'
    },

    'studentProgramAssociation' => {
        'educationOrganizationId' => '99a4ec9d3ba372993b2860a798b550c77bb73a09_id',
        'studentId' => 'df54047bf88ecd7e2f6fbf00951196f747c9ccfc_id',
        'programId' => edorg_program_id,
        'beginDate' => '2000-01-01',
        'reasonExited' => 'Received a certificate',
    },

    'cohort' => {
        'cohortIdentifier' => 'My new Cohort',
        'educationOrgId' => '99a4ec9d3ba372993b2860a798b550c77bb73a09_id',
        'cohortType' => 'Field Trip',
        'cohortDescription' => 'Trip to a nice field'
    },

    'disciplineIncident' => {
        'schoolId' => '2a30827ed4cf5500fb848512d19ad73ed37c4464_id',
        'incidentIdentifier' => 'Incident A1',
        'incidentDate' => '2013-01-01',
        'incidentTime' => '12:00:00',
        'incidentLocation' => 'Auditorium',
        'behaviors' =>  [[{
                             'codeValue' => 'Code 6'
                         }]]
    },

    'disciplineAction' => {
        'disciplineActionIdentifier' => 'student jailed',
        'responsibilitySchoolId' => '2a30827ed4cf5500fb848512d19ad73ed37c4464_id',
        'disciplines' => [[{
                              'codeValue' => 'Code 6'
                          }]],
        'disciplineIncidentId' => [di['_id']],
        'disciplineDate' => '2013-01-01',
        'studentId' => ['153df715388ff1b2293fc8b2f3828816bc2d3c1f_id'],
        'disciplineActionLength' => 2
    },

    'gradebookEntry' => {
        'gradebookEntryType' => 'Quiz',
        'dateAssigned' => '2013-01-01',
        'sectionId' => gradebook_section['_id'],
        'description' => 'Entry of a quiz'
    },

    'attendance' => {
        'schoolId' => '2a30827ed4cf5500fb848512d19ad73ed37c4464_id',
        'studentId' => '153df715388ff1b2293fc8b2f3828816bc2d3c1f_id',
        'schoolYear' => '2011-2012',
        'attendanceEvent' => [{
                                  'event' => 'Tardy',
                                  'date' => '2011-12-12',
                                  'reason' => 'Woke up late'
                              }]
    },

    'courseTranscript' => {
        'educationOrganizationReference' => ['2a30827ed4cf5500fb848512d19ad73ed37c4464_id'],
        'courseId' => course_id,
        'creditsEarned' => {
            'credit' => 3
        },
        'courseAttemptResult' => 'Fail',
        'studentAcademicRecordId' => yt['studentAcademicRecord'][0]['_id'],
        'gradeLevelWhenTaken' => 'Ninth grade'
    }

  }
  @fields = entity_data[entity]
end

############################################################################################
#Steps for POST
############################################################################################

Given /^I create a student entity with restricted data$/ do
  @fields = CreateEntityHash.createBaseStudentRandomId()
  @fields["economicDisadvantaged"] = true
  @lastStudentId = @fields['studentUniqueStateId']
end

Given /^I create a student entity without restricted data$/ do
  @fields = CreateEntityHash.createBaseStudentRandomId()
  @lastStudentId = @fields['studentUniqueStateId']
end

Then /^I remove the posted student$/ do
  tenant = convertTenantIdToDbName @tenant
  remove_from_mongo_operation(tenant, 'student', {"_id" => @lastStudentId})
end


Then /^I remove the new entity from "([^"]*)"$/ do |collection|
  db_name = convertTenantIdToDbName @tenant
  if collection.include? '.'
    disable_NOTABLESCAN
    conn = Mongo::Connection.new(DATABASE_HOST, DATABASE_PORT)
    db = conn[db_name]
    coll_split = collection.split('.')
    coll = db.collection(coll_split[0])
    query = {"#{coll_split[1]}._id" => @newId}
    entity = coll.find_one(query)
    subdocs = entity[coll_split[1]]
    subdocs.delete_if {|entry| entry['_id'] == @newId}
    update_mongo_operation(db_name, coll_split[0], query, coll_split[1],false, subdocs)
    conn.close
    enable_NOTABLESCAN
  else
    remove_from_mongo_operation(db_name, collection, {"_id" => @newId})
  end
end

When /^I change the field "([^\"]*)" to "([^\"]*)"$/ do |field, value|
  @patch_body = Hash.new if !defined?(@patch_body)
  @patch_body["#{field}"] = value
end

When /^I clear out the patch request$/ do
  @patch_body = {}
end

# Build the teacher hash
def teacherHashPush(key, value)
  @teacher = Hash.new unless defined? @teacher
  @teacher[key] = value
end

############################################################################################
#Steps for POST
############################################################################################

When /^I change the result field "([^\"]*)" to "([^\"]*)"$/ do |field, value|
  @fields = Hash.new if !defined?(@patch_body)
  @fields["#{field}"] = value
end

When /^I navigate to GET the new entity$/ do

steps %Q{
    When I navigate to GET "/v1/students?studentUniqueStateId=#{@lastStudentId}"
    }
end

When /^I navigate to GET the new student entity$/ do
  step "I navigate to GET \"/v1/students/#{@lastStudentId}\""
end


When /^I navigate to PUT the new entity$/ do

id = @result['id']
steps %Q{
    When I navigate to PUT "/v1/students/#{id}"
    }
end

############################################################################################
#Steps for DELETE
############################################################################################

When /^I attempt to delete the new entity$/ do
  restHttpDelete("/v1/students/#{@result['id']}")
end
