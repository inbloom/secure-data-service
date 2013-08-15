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

#Staff
    when 'msmith URI'
      id = '/v1/staff/3a780cebc8f98982f9b7a5d548fecff42ed8f2f1_id'

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

Transform /^(\[?\{.*?\}\]?)$/ do |array_hash|
  string_hash = array_hash.gsub(/[']/,"\"")
  hash = JSON.parse(string_hash)

  hash
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
  @fields = {} unless defined? @fields
  value.is_a?(String) ? @fields[key] = convert(value) : @fields[key] = value
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
  di_coll = db.collection('disciplineIncident')
  section_coll = db.collection('section')
  yt_coll = db.collection('yearlyTranscript')
  assessment_coll = db.collection('assessment')
  cohort_coll = db.collection('cohort')
  student_coll = db.collection('student')
  learn_obj_coll = db.collection('learningObjective')

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

  session_id = session_coll.find_one({'body.schoolId' => '99a4ec9d3ba372993b2860a798b550c77bb73a09_id'})['_id']

  assessment_id = assessment_coll.find_one()['_id']

  cohort_id = cohort_coll.find_one({'body.educationOrgId' => '2a30827ed4cf5500fb848512d19ad73ed37c4464_id'})['_id']

  student_dis = student_coll.find_one({'_id' => '153df715388ff1b2293fc8b2f3828816bc2d3c1f_id'},
                                     {:fields => {'_id' => 0, 'studentDisciplineIncidentAssociation.body.disciplineIncidentId' => 1}})['studentDisciplineIncidentAssociation']
  student_di_ids = []
  student_dis.each {|entry| student_di_ids << entry['body']['disciplineIncidentId']}
  student_di_id = di_coll.find_one({'body.schoolId' => '2a30827ed4cf5500fb848512d19ad73ed37c4464_id', '_id' => {'$nin' => student_di_ids}})['_id']

  program_id = edorg_coll.find_one({'_id' => '99a4ec9d3ba372993b2860a798b550c77bb73a09_id'})['body']['programReference'][0]

  teacher_section_id = section_coll.find_one({'body.schoolId' => '2a30827ed4cf5500fb848512d19ad73ed37c4464_id',
                                              'teacherSectionAssociation.body.teacherId' => {'$ne' => '7810ac678851ae29a450cc18bd9f47efa37bfaef_id'}})['_id']

  learning_objective_id = learn_obj_coll.find_one()['_id']

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
    },

    'studentAssessment'=> {
        'studentId' => '153df715388ff1b2293fc8b2f3828816bc2d3c1f_id',
        'assessmentId' => assessment_id,
        'administrationDate' => '2012-01-09',
        'gradeLevelWhenAssessed' => 'Ungraded'
    },

    'studentCohortAssociation' => {
        'studentId' => '153df715388ff1b2293fc8b2f3828816bc2d3c1f_id',
        'cohortId' => cohort_id,
        'beginDate' => '2012-01-01',
        'endDate' => '2012-01-05'
    },

    'studentDisciplineIncidentAssociation' => {
        'studentId' => '153df715388ff1b2293fc8b2f3828816bc2d3c1f_id',
        'disciplineIncidentId' => student_di_id,
        'studentParticipationCode' => 'Witness'
    },

    'studentParentAssociation' => {
        'studentId' => '153df715388ff1b2293fc8b2f3828816bc2d3c1f_id',
        'parentId' => 'aa3acf92cdebd987fa4b27c762828cde72ccfabc_id',
        'relation' => 'Uncle'
    },

    'studentSectionAssociation' => {
        'studentId' => '153df715388ff1b2293fc8b2f3828816bc2d3c1f_id',
        'sectionId' => gradebook_section['_id'],
        'beginDate' => '2012-01-01',
        'endDate' => '2012-01-02'
    },

    'staffCohortAssociation' => {
        'staffId' => '3a780cebc8f98982f9b7a5d548fecff42ed8f2f1_id',
        'cohortId' => cohort_id,
        'beginDate' => '2008-07-06',
        'studentRecordAccess' => false
    },

    'staffProgramAssociation' => {
        'staffId' => '3a780cebc8f98982f9b7a5d548fecff42ed8f2f1_id',
        'programId' => program_id,
        'beginDate' => '2008-07-06',
        'endDate' => '2012-02-29'
    },

    'teacherSectionAssociation' => {
        'teacherId' => '7810ac678851ae29a450cc18bd9f47efa37bfaef_id',
        'sectionId' => teacher_section_id,
        'classroomPosition' => 'Assistant Teacher'
    },

    'calendarDate' => {
        'date' => '2010-01-01',
        'educationOrganizationId' => '2a30827ed4cf5500fb848512d19ad73ed37c4464_id',
        'calendarEvent' => 'Teacher only day'
    },

    'studentCompetency' => {
        'studentSectionAssociationId' => gradebook_section['studentSectionAssociation'][0]['_id'],
        'objectiveId' => {
            'learningObjectiveId' => learning_objective_id
        },
        'competencyLevel' => {
            'description' => 'Incredibly Incompetent'
        },
        'diagnosticStatement' => 'Fails miserably'
    },

    'parent' => {
        'parentUniqueStateId' => 'new-parent',
        'loginId' => 'new-parent-login',
        'sex' => 'Female',
        'name' => {
            'firstName' => 'Samantha',
            'lastSurname' => 'Samuelson'
        }
    },

    'staff' => {
        'staffUniqueStateId' => 'new-staff',
        'loginId' => 'new-staff-login',
        'sex' => 'Male',
        'name' => {
            'firstName' => 'Seymour',
            'lastSurname' => 'Skinner'
        }
    },

    'teacher' => {
        'staffUniqueStateId' => 'new-teacher',
        'highlyQualifiedTeacher' => true,
        'loginId' => 'new-teacher-login',
        'sex' => 'Female',
        'name' => {
            'firstName' => 'Edna',
            'lastSurname' => 'Krabapple'
        }
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

When /^I change the result field "([^\"]*)" to "(.*?)"$/ do |field, value|
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
