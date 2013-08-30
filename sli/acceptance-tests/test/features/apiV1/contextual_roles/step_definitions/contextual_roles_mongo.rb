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

require 'mongo'


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
#Note: query is a query that'd uniquely identify an entity (Generally, a query like {'_id' => id} is enough)

def update_mongo(db_name, collection, query, field, remove, value = nil)
#Note: value is ignored if remove is true (It doesn't matter what the field contains if you're removing it)

  entry = update_mongo_operation(db_name, collection, query, field, remove, value)

  (@mongo_changes ||= []) << entry if entry
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

  (@mongo_changes ||= []) << entry if entry

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
      indirect_ref = false
    when 'teacherSchoolAssociation', 'studentSchoolAssociation', 'disciplineIncident', 'attendance'
      subdoc = false
      edorg_ref = 'body.schoolId'
      indirect_ref = false
    when 'staffEducationOrganizationAssociation'
      subdoc = false
      edorg_ref = 'body.educationOrganizationReference'
      indirect_ref = false
    when 'cohort'
      subdoc = false
      edorg_ref = 'body.educationOrgId'
      indirect_ref = false
    when 'disciplineAction'
      subdoc = false
      edorg_ref = 'body.responsibilitySchoolId'
      indirect_ref = false
    when 'gradebookEntry', 'studentSectionAssociation'
      subdoc = true
      edorg_in_subdoc = false
      edorg_ref = 'body.schoolId'
      indirect_ref = false
    when 'studentAssessment', 'studentGradebookEntry'
      subdoc = false
      indirect_ref = true
      ref_entity = 'studentSchoolAssociation'
      edorg_ref = 'body.schoolId'
      ref_field_in_ref_entity = 'studentId'
      ref_field = 'body.studentId'
    when 'studentCohortAssociation'
      subdoc = true
      indirect_ref = true
      ref_sub_entity = nil
      ref_entity = 'cohort'
      edorg_ref = 'body.educationOrgId'
      ref_field_in_ref_entity = '_id'
      ref_field = 'body.cohortId'
    when 'studentDisciplineIncidentAssociation'
      subdoc = true
      indirect_ref = true
      ref_sub_entity = nil
      ref_entity = 'disciplineIncident'
      edorg_ref = 'body.schoolId'
      ref_field_in_ref_entity = '_id'
      ref_field = 'body.disciplineIncidentId'
    when 'studentParentAssociation', 'studentAcademicRecord', 'reportCard', 'grade'
      subdoc = true
      indirect_ref = true
      ref_entity = 'studentSchoolAssociation'
      edorg_ref = 'body.schoolId'
      ref_field_in_ref_entity = 'studentId'
      ref_field = 'body.studentId'
    when 'studentCompetency'
      subdoc = false
      indirect_ref = true
      ref_entity = 'section'
      edorg_ref = 'body.schoolId'
      ref_sub_entity = 'studentSectionAssociation'
      ref_field_in_ref_entity = '_id'
      ref_field = 'body.studentSectionAssociationId'
    else
      subdoc = false
      edorg_ref = 'body.educationOrganizationId'
      indirect_ref = false
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
    if subdoc && !indirect_ref
      if edorg_in_subdoc
        entities = coll.find({"#{type}.#{edorg_ref}" => edorg},{:fields => ["#{type}.$"]}).to_a
      else
        entities = coll.find({edorg_ref => edorg}).to_a
      end
      entities.each { |entity| entity_ids.add(entity[type][0]['_id'])}
    elsif !indirect_ref && !subdoc
      entities = coll.find({edorg_ref => edorg},{:fields => %w(_id)}).to_a
      entities.each { |entity| entity_ids.add(entity['_id'])}
    elsif indirect_ref && !subdoc
      ref_coll = db.collection(ref_entity)
      ref_entities = ref_coll.find({edorg_ref => edorg}).to_a
      ref_ids = Set.new
      if ref_field_in_ref_entity == '_id'
        if ref_sub_entity.nil?
          ref_entities.each { |entry| ref_ids.add(entry[ref_field_in_ref_entity])}
        else
          ref_entities.each { |entry| ref_ids.add(entry[ref_sub_entity][0][ref_field_in_ref_entity])}
        end
      else
        ref_entities.each { |entry| ref_ids.add(entry['body'][ref_field_in_ref_entity])}
      end
      entities = []
      ref_ids.each { |entry| entities.concat(coll.find({ref_field => entry},{:fields => %w(_id)}).to_a)}
      entities.each { |entity| entity_ids.add(entity['_id'])}
    elsif indirect_ref && subdoc
      ref_coll = db.collection(ref_entity)
      ref_entities = ref_coll.find({edorg_ref => edorg}).to_a
      ref_ids = Set.new
      if ref_field_in_ref_entity == '_id'
        if ref_sub_entity.nil?
          ref_entities.each { |entry| ref_ids.add(entry[ref_field_in_ref_entity])}
        else
          ref_entities.each { |entry| ref_ids.add(entry[ref_sub_entity][0][ref_field_in_ref_entity])}
        end
      else
        ref_entities.each { |entry| ref_ids.add(entry['body'][ref_field_in_ref_entity])}
      end
      entities = []
      ref_ids.each { |entry| entities.concat(coll.find({"#{type}.#{ref_field}" => entry},{:fields => ["#{type}.$"]}).to_a)}
      entities.each { |entity| entity_ids.add(entity[type][0]['_id']) }
    end
  end
  assert(entity_ids.size > 0, "No #{type} found that is associated with the edorgs of #{staff}")
  (entity_ids.to_a.shuffle.take(number.to_i)).each { |entry| (@entity_ids ||= []) << entry }
  conn.close
  enable_NOTABLESCAN()
end

Given /^I get (\d+) random ids of "([^"]*)" in "([^"]*)" associated with the staff of "([^"]*)"$/ do |number, type, collection, staff|
  case type
    when 'staffCohortAssociation', 'staffProgramAssociation'
      subdoc = false
      staff_ref = 'body.staffId'
    when 'teacherSectionAssociation'
      subdoc = true
      staff_ref = 'body.teacherId'
  end

  disable_NOTABLESCAN()
  conn = Mongo::Connection.new(DATABASE_HOST,DATABASE_PORT)
  db_name = convertTenantIdToDbName(@tenant)
  db = conn[db_name]
  edorgs = Set.new
  seoa_coll = db.collection('staffEducationOrganizationAssociation')
  staff_coll = db.collection('staff')
  staff_id = staff_coll.find_one({'body.staffUniqueStateId' => staff})['_id']
  seoas = seoa_coll.find({'body.staffReference' => staff_id}).to_a
  seoas.each {|seoa| edorgs.add(seoa['body']['educationOrganizationReference']) }
  entity_ids = Set.new
  coll = db.collection(collection)

  edorgs.each do |edorg|
    ref_seoas = seoa_coll.find({'body.educationOrganizationReference' => edorg}).to_a
    ref_seoas.each do |ref_seoa|
      if subdoc
        entities = coll.find({"#{type}.#{staff_ref}" => ref_seoa['body']['staffReference']},{:fields => ["#{type}.$"]}).to_a
        entities.each { |entity| entity_ids.add(entity[type][0]['_id'])}
      else
        entities = coll.find({staff_ref => ref_seoa['body']['staffReference']},{:fields => %w(_id)}).to_a
        entities.each { |entity| entity_ids.add(entity['_id'])}
      end
    end
  end
  assert(entity_ids.size > 0, "No #{type} found that is associated with the staff of #{staff}")
  (entity_ids.to_a.shuffle.take(number.to_i)).each { |entry| (@entity_ids ||= []) << entry }
  conn.close
  enable_NOTABLESCAN()
end

Given /^I get (\d+) random ids for parents associated with the students of "([^"]*)"$/ do |number, staff|
  disable_NOTABLESCAN()
  conn = Mongo::Connection.new(DATABASE_HOST,DATABASE_PORT)
  db_name = convertTenantIdToDbName(@tenant)
  db = conn[db_name]
  edorgs = Set.new
  seoa_coll = db.collection('staffEducationOrganizationAssociation')
  staff_coll = db.collection('staff')
  staff_id = staff_coll.find_one({'body.staffUniqueStateId' => staff})['_id']
  seoas = seoa_coll.find({'body.staffReference' => staff_id}).to_a
  seoas.each {|seoa| edorgs.add(seoa['body']['educationOrganizationReference']) }
  entity_ids = Set.new
  student_coll = db.collection('student')
  ssa_coll = db.collection('studentSchoolAssociation')

  edorgs.each do |edorg|
    students = ssa_coll.find({'body.schoolId' => edorg},{:fields => %w(body.studentId)}).to_a
    students.each do |student_ref|
      student = student_coll.find_one({'_id' => student_ref['body']['studentId']},{:fields => %w(studentParentAssociation)})
      student['studentParentAssociation'].each { |entry| entity_ids.add(entry['body']['parentId']) }
    end
  end
  assert(entity_ids.size > 0, "No parent found that is associated with the students of #{staff}")
  (entity_ids.to_a.shuffle.take(number.to_i)).each { |entry| (@entity_ids ||= []) << entry }
  conn.close
  enable_NOTABLESCAN()
end

Given /^I add "([^"]*)" for "([^"]*)" in "([^"]*)" that's already expired$/ do |collection, student, edorg|
  disable_NOTABLESCAN()
  conn = Mongo::Connection.new(DATABASE_HOST,DATABASE_PORT)
  db_name = convertTenantIdToDbName(@tenant)
  db = conn[db_name]
  student_coll = db.collection('student')
  edorg_coll = db.collection('educationOrganization')
  di_coll = db.collection('disciplineIncident')
  yt_coll = db.collection('yearlyTranscript')
  course_coll = db.collection('course')
  student_id = student_coll.find_one({'body.studentUniqueStateId' => student})['_id']
  edorg_id = edorg_coll.find_one({'body.stateOrganizationId' => edorg})['_id']
  di_id = di_coll.find_one({'body.schoolId' => edorg_id})['_id']
  sar_id = yt_coll.find_one({'body.studentId' => student_id})['studentAcademicRecord'][0]['_id']
  course_id = course_coll.find_one()['_id']
  entries = {
      'studentSchoolAssociations' => {
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
      'attendances' => {
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
      },
      'disciplineActions' => {
          '_id' => SecureRandom.uuid,
          'type' => 'disciplineAction',
          'body' => {
              'disciplineDate' => '2010-01-01',
              'disciplines' => [[{
                                     'codeValue' => 'Code 8'
                                 }]],
              'studentId' => [student_id],
              'responsibilitySchoolId' => edorg_id,
              'assignmentSchoolId' => edorg_id,
              'disciplineActionIdentifier' => 'DA 12',
              'disciplineIncidentId' => [di_id]
          }
      },
      'courseTranscripts' => {
          '_id' => SecureRandom.uuid,
          'type' => 'courseTranscript',
          'body' => {
              'educationOrganizationReference' => [edorg_id],
              'creditsEarned' => {
                  'credit' => 3
              },
              'courseAttemptResult' => 'Withdrawn',
              'studentAcademicRecordId' => sar_id,
              'studentId' => student_id,
              'gradeType' => 'Semester',
              'finalLetterGradeEarned' => 'B-',
              'courseId' => course_id
          }
      }
  }
  @newId = entries[collection]['_id']
  add_to_mongo(db_name,entries[collection]['type'],entries[collection])

  conn.close
  enable_NOTABLESCAN()
end

Given /^I add subdoc "([^"]*)" for "([^"]*)" and "([^"]*)" in "([^"]*)" that's already expired$/ do |subdoc, studentId, refEntity, edorg|
  disable_NOTABLESCAN()
  conn = Mongo::Connection.new(DATABASE_HOST,DATABASE_PORT)
  db_name = convertTenantIdToDbName(@tenant)
  db = conn[db_name]
  student_coll = db.collection('student')
  edorg_coll = db.collection('educationOrganization')

  student = student_coll.find_one({'body.studentUniqueStateId' => studentId})
  student_id = student['_id']
  edorg_id = edorg_coll.find_one({'body.stateOrganizationId' => edorg})['_id']
  refId = createEntity(db_name, refEntity, edorg, student_id)
  entries = {
      'studentProgramAssociation' => {
          '_id' => student_id + refId,
          'type' => 'studentProgramAssociation',
          'body' => {
              'programId' => refId,
              'studentId' => student_id,
              'educationOrganizationId' => edorg_id,
              'endDate' => '2010-11-11',
              'beginDate'  => '2011-11-11'
          }
      },
      'studentCohortAssociation' => {
          '_id' => student_id + refId,
          'type' => 'studentCohortAssociation',
          'body' => {
              'cohortId' => refId,
              'studentId' => student_id,
              'endDate' => '2011-11-11',
              'beginDate' => '2010-11-11'
          }
      },
      'studentDisciplineIncidentAssociation' => {
          '_id' => student_id + refId,
          'type' => 'studentDisciplineIncidentAssociation',
          'body' => {
              'disciplineIncidentId' => refId,
              'studentParticipationCode' => 'Perpetrator',
              'studentId' => student_id
          }
      },
      'studentSectionAssociation' => {
          '_id' => refId + student_id,
          'type' => 'studentSectionAssociation',
          'body' => {
              'sectionId' => refId,
              'studentId' => student_id,
              'endDate' => '2011-11-11',
              'beginDate' => '2010-11-11'
          }
      },
      'gradebookEntry' => {
          '_id' => refId +SecureRandom.uuid + '_id',
          'type' => 'gradebookEntry',
          'body' => {
              'gradingPeriodId' => 'blabla',
              'sectionId' => refId,
              'dateAssigned' => '2011-11-30',
              'description' => 'Gradebook Entry of type=> Homework, assigned on=> 2013-11-30',
              'gradebookEntryType' => 'Homework',
              'learningObjectives' => [
              ]
        }
      },
      'reportCard' => {
          'type' => 'reportCard',
          '_id' => refId + SecureRandom.uuid + '_id',
          'body' => {
              'schoolYear' => '2010-2011',
              'gradingPeriodId' => '72a3b7ac34035f49a0138369f9fc12a350c7b812_id',
              'gpaGivenGradingPeriod' => 2,
              'studentId' => student_id,
              'gpaCumulative' => 2,
              'numberOfDaysInAttendance' => 117,
              'numberOfDaysTardy' => 21,
              'grades' => [
              ],
              'numberOfDaysAbsent' => 3,
              'studentCompetencyId' => [
              ]
          }
      }
  }
  superCollections = {
      'studentProgramAssociation' => 'student',
      'studentDisciplineIncidentAssociation' => 'student',
      'studentSectionAssociation' => 'section',
      'studentCohortAssociation' => 'student',
      'gradebookEntry' => 'section',
      'reportCard' => 'yearlyTranscript'
  }

  queries = {
      'student' => {'body.studentUniqueStateId' => studentId},
      'section' => {'_id' => refId},
      'yearlyTranscript' => {'_id' => refId}
  }

  @newId = entries[subdoc]['_id']
  superCollection = superCollections[subdoc]
  superDoc = db.collection(superCollection).find_one(queries[superCollection])

  puts @newId
  puts refId
  puts student_id
  (superDoc[subdoc] ||= []) << entries[subdoc]
  update_mongo(db_name, superCollection , queries[superCollection], subdoc, false, superDoc[subdoc])

  conn.close
  enable_NOTABLESCAN()
end

def createEntity(db_name, entity, edorgId, student_id)
  id = SecureRandom.uuid + '_id'
  entities = {
      'section' => {
          '_id' => id,
      'body' => {
        'educationalEnvironment' => 'Classroom',
        'sessionId' => 'test',
        'courseOfferingId' => 'test',
        'populationServed' => 'Regular Students',
        'sequenceOfCourse' => 1,
        'mediumOfInstruction' => 'Face-to-face instruction',
        'uniqueSectionCode' => '74',
        'schoolId' => edorgId,
        'creditConversion' => 1,
        'creditType' => 'Other',
        'credit' => 4,
        'gradeBookEntry' => [],
        'studentSectionAssociation' => []
      }
    },
    'program' => {
        '_id' => id,
        'body' => {
            'programId' => '14'
        }
    },
    'yearlyTranscript' => {
        '_id' => id,
        'body' => {
            'schoolYear' => '2010-2011',
            'studentId' => student_id
        },
        'grade' => [ ],
        'reportCard' => [ ],
        'studentAcademicRecord' => [ ]

    }
  }
  add_to_mongo(db_name, entity, entities[entity])
  return id
end