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
require_relative '../../../ingestion/features/step_definitions/ingestion_steps.rb'
require_relative '../../../apiV1/utils/api_utils.rb'
require_relative '../../../ingestion/features/step_definitions/clean_database.rb'
require_relative '../../../utils/sli_utils.rb'
require_relative '../../../odin/step_definitions/data_generation_steps.rb'
require_relative '../../../security/step_definitions/securityevent_util_steps.rb'
require 'zip/zip'
require 'archive/tar/minitar'
require 'zlib'
require 'open3'
require 'openssl'
require 'time'
require 'digest/sha1'
require 'set'
include Archive::Tar

SCHEDULER_SCRIPT = File.expand_path(PropLoader.getProps['bulk_extract_scheduler_script'])
TRIGGER_SCRIPT_DIRECTORY = File.expand_path(PropLoader.getProps['bulk_extract_script_directory'])
CRON_OUTPUT_DIRECTORY = PropLoader.getProps['bulk_extract_cron_output_directory']
TRIGGER_SCRIPT = File.expand_path(PropLoader.getProps['bulk_extract_script'])
DELTA_SCRIPT = File.expand_path(PropLoader.getProps['bulk_extract_delta_script'])
DELTA_CONFIG = File.expand_path(PropLoader.getProps['bulk_extract_delta_properties'])
DELTA_KEYSTORE = File.expand_path(PropLoader.getProps['bulk_extract_delta_keystore'])
OUTPUT_DIRECTORY = PropLoader.getProps['bulk_extract_output_directory']
PROPERTIES_FILE = PropLoader.getProps['bulk_extract_properties_file']
KEYSTORE_FILE = PropLoader.getProps['bulk_extract_keystore_file']
JAR_FILE = PropLoader.getProps['bulk_extract_jar_loc']
DATABASE_NAME = PropLoader.getProps['sli_database_name']
DATABASE_HOST = PropLoader.getProps['bulk_extract_db']
DATABASE_PORT = PropLoader.getProps['bulk_extract_port']
INDEPENDENT_ENTITIES = ['graduationPlan']
ENCRYPTED_ENTITIES = ['student', 'parent']
COMBINED_ENTITIES = ['assessment', 'studentAssessment']
COMBINED_SUB_ENTITIES = ['assessmentItem','objectiveAssessment','studentAssessmentItems','studentObjectiveAssessments']

ENCRYPTED_FIELDS = ['loginId', 'studentIdentificationCode','otherName','sex','address','electronicMail','name','telephone','birthData']
MUTLI_ENTITY_COLLS = ['staff', 'educationOrganization']
CLEANUP_SCRIPT = File.expand_path(PropLoader.getProps['bulk_extract_cleanup_script'])

$APP_CONVERSION_MAP = {"19cca28d-7357-4044-8df9-caad4b1c8ee4" => "vavedra9ub",
                       "22c2a28d-7327-4444-8ff9-caad4b1c7aa3" => "pavedz00ua" 
                      }
#Don't hate me, help me find a better solution...
#BTW, I got "Riley Approved" 
$GLOBAL_VARIABLE_MAP = {}
LEA_DAYBREAK_ID_VAL = '1b223f577827204a1c7e9c851dba06bea6b031fe_id'
SEA_IL_ID_VAL = 'b64ee2bcc92805cdd8ada6b7d8f9c643c9459831_id'

CURRENT_STUDENT_QUERY     = <<-jsonDelimiter
  [
  {"$project":{"schools":1}}
  ,{"$unwind":"$schools"}
  ,{"$match":{ "$or":[     {"schools.exitWithdrawDate":{"$exists":true, "$gt": "#{DateTime.now.strftime('%Y-%m-%d')}"}} ,{"schools.exitWithdrawDate":{"$exists":false}}    ]}}
  ,{"$project":{"_id":1, "schools._id":1}}
  ,{"$group":{"_id":"$schools._id", "students":{"$addToSet":"$_id"}}}
  ]
jsonDelimiter

############################################################
# Transform
############################################################

Transform /(.*LEA_DAYBREAK_ID.*)/ do|hrId|
    hrId.sub(/(.*)LEA_DAYBREAK_ID(.*)/, '\1' + LEA_DAYBREAK_ID_VAL + '\2')
end

Transform /(.*SEA_IL_ID.*)/ do|hrId|
  hrId.sub(/(.*)SEA_IL_ID(.*)/, '\1' + SEA_IL_ID_VAL + '\2')
end

Transform /^<(.*?)>$/ do |human_readable_id|
  # entity id transforms
  id = "19cca28d-7357-4044-8df9-caad4b1c8ee4"               if human_readable_id == "app id"
  id = "22c2a28d-7327-4444-8ff9-caad4b1c7aa3"               if human_readable_id == "app id paved"
  id = "vavedra9ub"                                         if human_readable_id == "client id"
  id = "pavedz00ua"                                         if human_readable_id == "client id paved"
  id = "1b223f577827204a1c7e9c851dba06bea6b031fe_id"        if human_readable_id == "IL-DAYBREAK"
  id = "99d527622dcb51c465c515c0636d17e085302d5e_id"        if human_readable_id == "IL-HIGHWIND"
  id = "067098399bb1feee5efe7cfbe91bb34fa352f9a5_id"        if human_readable_id == "IL-SUNSET"
  id = "54b4b51377cd941675958e6e81dce69df801bfe8_id"        if human_readable_id == "ed_org_to_lea2_id"
  id = "880572db916fa468fbee53a68918227e104c10f5_id"        if human_readable_id == "lea2_id"
  id = "1b223f577827204a1c7e9c851dba06bea6b031fe_id"        if human_readable_id == "lea1_id"
  id = "884daa27d806c2d725bc469b273d840493f84b4d_id"        if human_readable_id == "STANDARD-SEA"
  id = "352e8570bd1116d11a72755b987902440045d346_id"        if human_readable_id == "IL-DAYBREAK school"
  id = "a96ce0a91830333ce68e235a6ad4dc26b414eb9e_id"        if human_readable_id == "Orphaned School"
  id = "02bdd6bf0fd5f761e6fc316ca6c763d4bb96c055_id"        if human_readable_id == "11 School District"
  id = "c67b5565b3b6475bae9e042c96cb0b9db6b37b29_id"        if human_readable_id == "10 School District"
  id = "b64ee2bcc92805cdd8ada6b7d8f9c643c9459831_id"        if human_readable_id == "IL"
  id = "352e8570bd1116d11a72755b987902440045d346_id"        if human_readable_id == "South Daybreak Elementary"
  id = "a13489364c2eb015c219172d561c62350f0453f3_id"        if human_readable_id == "Daybreak Central High"
  id = "772a61c687ee7ecd8e6d9ad3369f7883409f803b_id"        if human_readable_id == "East Daybreak Junior High"

  id
end

Transform /^#(-?\d+)$/ do |number|
  number.to_i
end

############################################################
# Given
############################################################
Given /^I trigger a bulk extract$/ do
  bulkExtractTrigger(TRIGGER_SCRIPT, JAR_FILE, PROPERTIES_FILE, KEYSTORE_FILE)
end

Given /^I trigger an extract for tenant "([^"]*)"$/ do |tenant|
  options = " -t#{tenant}"
  bulkExtractTrigger(TRIGGER_SCRIPT, JAR_FILE, PROPERTIES_FILE, KEYSTORE_FILE, options)
end

Given /^I trigger a delta extract$/ do
  options = " -d"
  bulkExtractTrigger(TRIGGER_SCRIPT, JAR_FILE, PROPERTIES_FILE, KEYSTORE_FILE, options)
end

Given /^the extraction zone is empty$/ do
  if (Dir.exists?(OUTPUT_DIRECTORY))
    puts "#{OUTPUT_DIRECTORY} cleaned"
    FileUtils.rm_rf("#{OUTPUT_DIRECTORY}/.", secure: true)
  end
end

Given /^the extract download directory is empty$/ do
  if (Dir.exists?(OUTPUT_DIRECTORY + "decrypt"))
    puts "decrypt dir is #{OUTPUT_DIRECTORY}decrypt"
    FileUtils.rm_rf("#{OUTPUT_DIRECTORY}decrypt", secure: true)
  end
end

Given /^the unpack directory is empty$/ do
  if (Dir.exists?(@unpackDir))
    puts "unpack dir is #{@unpackDir}"
    FileUtils.rm_rf("#{@unpackDir}", secure: true)
  end
end

Given /^I have delta bulk extract files generated for today$/ do
  @pre_generated = "#{File.dirname(__FILE__)}/../../test_data/deltas/Midgar_delta_1.tar"
  bulk_delta_file_entry = {
    _id: "Midgar_delta-19cca28d-7357-4044-8df9-caad4b1c8ee4",
    body: {
      tenantId: "Midgar",
      isDelta: true,
      applicationId: "19cca28d-7357-4044-8df9-caad4b1c8ee4",
      path: "#{@pre_generated}",
      date: Time.now
    },
    metaData: {
      updated: Time.now
    },
    type: "bulkExtractEntity"
  }
  @conn ||= Mongo::Connection.new(DATABASE_HOST, DATABASE_PORT)
  @sliDb ||= @conn.db(DATABASE_NAME)
  @coll ||= @sliDb.collection("bulkExtractFiles")
  @coll.save(bulk_delta_file_entry)
end

Given /^The bulk extract app has been approved for "([^"]*)" with client id "([^"]*)"$/ do |lea, clientId|
  @lea = Hash.new
  @lea["name"] = lea
  @lea["clientId"] = clientId
  puts "stubbed out"
end

Given /^The X509 cert (.*?) has been installed in the trust store and aliased$/ do |cert|
  @lea["cert"] = cert
  puts "Stubbed out"
end

Given /^the bulk extract files in the database are scrubbed/ do
  @conn = Mongo::Connection.new(DATABASE_HOST, DATABASE_PORT)
  @sliDb = @conn.db(DATABASE_NAME)
  @coll = @sliDb.collection("bulkExtractFiles")
  @coll.remove()
end

Given /^I add all the test edorgs$/ do |table|
  table.hashes.map do |row|
    addTestEdorg(row["tenant"], row["Edorg"])
  end
end

Given /^I configurate the bulk extract scheduler script$/ do
  assert(Dir.exists?(TRIGGER_SCRIPT_DIRECTORY), "Bulk Extract script directory #{TRIGGER_SCRIPT_DIRECTORY} does not exist")
  @current_dir = Dir.pwd
  is_jenkins = @current_dir.include?"jenkins"
  puts "pwd: #{@current_dir}"
  @trigger_script_path = TRIGGER_SCRIPT_DIRECTORY

  is_jenkins ? config_path = File.dirname(__FILE__) + '/../../test_data/config/' :  config_path = File.dirname(__FILE__) + '/../../test_data/local/'
  config_file = config_path + 'bulk_extract_scheduling.conf'
  assert(File.exists?(config_file), "Bulk Extract scheduling config file #{config_file} does not exist")

  config_contents = File.read(config_file)

  current_time = Time.now
  current_time.sec >= 55 ? cron_time = current_time + 120 : cron_time = current_time + 60
  cron_time_string = cron_time.strftime("%M %H %-d %-m %w")

  config_contents['* * * * *'] = cron_time_string

  @scheduling_config_path = config_path + 'temp/'
  FileUtils.makedirs(@scheduling_config_path)
  new_config = @scheduling_config_path + File.basename(config_file)

  File.open(new_config,'w') { |file| file.write(config_contents)}

  puts "bulk extract script path: #{@trigger_script_path}"
  puts "bulk extract scheduling config path: #{@scheduling_config_path}"
end

Given /^I clean the bulk extract file system and database$/ do
  steps "Given the extraction zone is empty"
  steps "Given I have an empty delta collection"
  steps "Given I have an empty bulk extract files collection"
end

Given /^There is no SEA for the tenant "(.*?)"$/ do |tenant|
  @tenant_db = @conn.db(convertTenantIdToDbName(tenant))
  collection = @tenant_db.collection('educationOrganization')
  collection.remove({'body.organizationCategories' => 'State Education Agency'})
end

Given /^I get the SEA Id for the tenant "(.*?)"$/ do |tenant|
  @tenant_db = @conn.db(convertTenantIdToDbName(tenant))
  edOrgcollection = @tenant_db.collection('educationOrganization')
  @seaId = edOrgcollection.find_one({'body.organizationCategories' => 'State Education Agency'})["_id"]
  assert (@seaId != nil)
  puts @seaId
end

Given /^none of the following entities reference the SEA:$/ do |table|
  table.hashes.map do |row|
    collection = @tenant_db.collection(row["entity"])
    collection.remove({row["path"] => @seaId})
  end
end

Given /^I clean up the cron extraction zone$/ do
  Dir.chdir
  puts "pwd: #{Dir.pwd}"
  if (Dir.exists?(CRON_OUTPUT_DIRECTORY))
    FileUtils.rm_rf CRON_OUTPUT_DIRECTORY
  end
  assert(!Dir.exists?(CRON_OUTPUT_DIRECTORY), "cron output directory #{CRON_OUTPUT_DIRECTORY} does exist")
  puts "CRON_OUTPUT_DIRECTORY: #{CRON_OUTPUT_DIRECTORY}"
  Dir.chdir(@current_dir)
end

Given /^the tenant "(.*?)" does not have any bulk extract apps for any of its education organizations$/ do |tenant|
  disable_NOTABLESCAN()
  conn = Mongo::Connection.new(DATABASE_HOST, DATABASE_PORT)
  db = conn[DATABASE_NAME]
  app_coll = db.collection('application')
  apps = app_coll.find({'body.isBulkExtract' => true}).to_a
  assert(apps.size > 0, 'Could not find any bulk extract applications')

  apps.each do |app|
    app_id = app['_id']
    puts("The id for a bulk extract app is #{app_id}") if $SLI_DEBUG

    db_tenant = conn[convertTenantIdToDbName(tenant)]
    app_auth_coll = db_tenant.collection('applicationAuthorization')

    app_auth_coll.remove('body.applicationId' => app_id)
  end
  conn.close
  enable_NOTABLESCAN()
end

Given /^all (LEAs|edorgs) in "([^"]*)" are authorized for "([^"]*)"/ do |which_edorg, tenant, application|
  disable_NOTABLESCAN()
  conn = Mongo::Connection.new(DATABASE_HOST, DATABASE_PORT)
  db = conn[DATABASE_NAME]
  app_coll = db.collection('application')
  apps = app_coll.find({'body.name' => application}).to_a
  assert(apps.size > 0, "Could not find any application with the name #{application}")
  assert(apps.size == 1, "Found multiple applications with the name #{application}")

  app_id = apps[0]['_id']
  puts("The id for a #{application} is #{app_id}") if $SLI_DEBUG

  db_tenant = conn[convertTenantIdToDbName(tenant)]
  app_auth_coll = db_tenant.collection('applicationAuthorization')
  ed_org_coll = db_tenant.collection('educationOrganization')

  case which_edorg.downcase
  when 'leas'
    query = {'body.organizationCategories' => {'$in' => ['Local Education Agency']}}
  else
    query = {}
  end

  needed_ed_orgs = []
  ed_org_coll.find(query).each do |edorg|
    needed_ed_orgs.push(edorg['_id'])
  end

  app_auth_coll.remove('body.applicationId' => app_id)
  new_app_auth = {'_id' => "2012ls-#{SecureRandom.uuid}", 'body' => {'applicationId' => app_id, 'edorgs' => needed_ed_orgs}, 'metaData' => {'tenantId' => tenant}}
  app_auth_coll.insert(new_app_auth)

  needed_ed_orgs.each do |edorg|
    app_coll.update({'_id' => app_id}, {'$push' => {'body.authorized_ed_orgs' => edorg}})
  end

  conn.close
  enable_NOTABLESCAN()
end

############################################################
# When
############################################################

When /^I only remove bulk extract file for tenant:"(.*?)", edorg:"(.*?)", app:"(.*?)", date:"(.*?)"$/ do |tenant, edorg, app, date|
  path = File.expand_path(createCleanupFile(@parentDir, tenant, edorg, app, date))
  FileUtils.rm(path)
end

When /^I execute cleanup script for tenant:"(.*?)", edorg:"(.*?)", date:"(.*?)", path:"(.*?)"$/ do |tenant, edorg, date, path|
  @log = "cleanup/out.log"
  puts CLEANUP_SCRIPT
  options = "-t#{tenant} "
  if(!edorg.empty?)
    options += " -e#{edorg}"
  end
  if(!date.empty?)
    options += " -d#{date}"
  end
  if(!path.empty?)
    if path.include?('Daybreak') || path.include?('Sunset')
      path_tenant = 'Midgar'
    elsif path.include?('NY')
      path_tenant = 'Hyrule'
    else
      path_tenant = tenant
    end
    abPath = File.expand_path(@parentDir + path_tenant + "/" +  path)
    options += " -f#{abPath}"
  end
  command  = "echo y | ruby #{CLEANUP_SCRIPT} #{options}"
  @cleanResult, result = Open3.capture2(command)
  puts @cleanResult
end

When /^I should see error message$/ do
  errorMessage = "FATAL:"
  puts @cleanResult
  assert(@cleanResult.to_s.include?(errorMessage), "Result of bulk extract cleanup script should include error message but was " + @cleanResult )
end

When /^I should see warning message$/ do
  errorMessage = "1 files failed"
  puts @cleanResult
  assert(@cleanResult.to_s.include?(errorMessage), "Result of bulk extract cleanup script should include error message but was " + @cleanResult )
end

When /^I get the path to the extract file for the tenant "(.*?)" and application with id "(.*?)" for the lea "(.*?)"$/ do |tenant, appId, lea|
  getExtractInfoFromMongo(build_bulk_query(tenant,appId,lea))
  openDecryptedFile(appId)
  Minitar.unpack(@filePath, @unpackDir)
  assert(File.exists?(@unpackDir + "/metadata.txt"), "Cannot find metadata file in extract")
end

When /^I get the path to the extract file for the tenant "(.*?)" and application with id "(.*?)" for the edOrg "(.*?)"$/ do |tenant, appId, edOrg|
  getExtractInfoFromMongo(build_bulk_query(tenant,appId,edOrg))
  openDecryptedFile(appId)
  Minitar.unpack(@filePath, @unpackDir)
  assert(File.exists?(@unpackDir + "/metadata.txt"), "Cannot find metadata file in extract")
end

When /^I get the path to the extract file for the tenant "(.*?)" and application with id "(.*?)" for sea "(.*?)"$/ do |tenant, appId, sea|
  getExtractInfoFromMongo(build_bulk_query(tenant,appId,sea,false, true))
  openDecryptedFile(appId)
  Minitar.unpack(@filePath, @unpackDir)
  assert(File.exists?(@unpackDir + "/metadata.txt"), "Cannot find metadata file in extract")
end


When /^I retrieve the path to and decrypt the SEA public data extract file for the tenant "(.*?)" and application with id "(.*?)"$/ do |tenant, appId|
  @tenant = tenant
  getExtractInfoFromMongo(build_bulk_query(tenant,appId,nil,false, true))
  openDecryptedFile(appId)
end

When /^I fetch the path to and decrypt the SEA public data extract file for the tenant "(.*?)" and application with id "(.*?)" and edorg with id "(.*?)"$/ do |tenant, appId, edOrgId|
  @tenant = tenant
  getExtractInfoFromMongo(build_bulk_query(tenant,appId,edOrgId,false, true))
  openDecryptedFile(appId)
end

When /^I know the file-length of the extract file$/ do
  @file_size = File.size(@filePath)
end

When /^I retrieve the path to and decrypt the LEA data extract file for the tenant "(.*?)" and application with id "(.*?)"$/ do |tenant, appId|
  getExtractInfoFromMongo(build_bulk_query(tenant,appId))
  openDecryptedFile(appId)
end

When /^I retrieve the path to and decrypt the LEA "(.*?)" data extract file for the tenant "(.*?)" and application with id "(.*?)"$/ do |lea, tenant, appId|
  getExtractInfoFromMongo(build_bulk_query(tenant,appId,lea))
  openDecryptedFile(appId)
end

When /^I fetch the path to and decrypt the LEA data extract file for the tenant "(.*?)" and application with id "(.*?)" and edorg with id "(.*?)"$/ do |tenant, appId, edOrgId|
  @tenant = tenant
  getExtractInfoFromMongo(build_bulk_query(tenant,appId,edOrgId))
  openDecryptedFile(appId)
end

When /^I decrypt the extract file with application with id "(.*?)"$/ do |appId|
  openDecryptedFile(appId)
end

When /^I verify that an extract tar file was created for the tenant "(.*?)"$/ do |tenant|

	puts "Extract FilePath: #{@filePath}"

	assert(File.exists?(@filePath), "Extract file was not created or Output Directory was not found")
end

When /^I verify this tar file is the same as the pre-generated delta file$/ do
   puts "pre-generated file at: #{@pre_generated}"
   puts "served file from API at: #{@filePath}"
   assert(FileUtils.compare_file(@filePath, @pre_generated), "Delta file served from API is different from pre-generated file")
end

When /^there is a metadata file in the extract$/ do
  Minitar.unpack(@filePath, @unpackDir)
	assert(File.exists?(@unpackDir + "/metadata.txt"), "Cannot find metadata file in extract")
end

When /^the extract contains a file for each of the following entities:$/ do |table|
  Minitar.unpack(@filePath, @unpackDir)

	table.hashes.map do |entity|
    exists = File.exists?(@unpackDir + "/" +entity['entityType'] + ".json.gz")
    assert(exists, "Cannot find #{entity['entityType']}.json file in extracts")
	end

  fileList = Dir.entries(@unpackDir)
	assert((fileList.size-3)==table.hashes.size, "Expected " + table.hashes.size.to_s + " extract files, Actual:" + (fileList.size-3).to_s+" and they are: #{fileList}")
end

When /^the extract contains a file for each of the following entities with the appropriate count and does not have certain ids:$/ do |table|
  Minitar.unpack(@filePath, @unpackDir)
	table.hashes.map do |entity|
    exists = File.exists?(@unpackDir + "/" +entity['entityType'] + ".json.gz")
    assert(exists, "Cannot find #{entity['entityType']}.json file in extracts")
    `gunzip #{@unpackDir}/#{entity['entityType']}.json.gz`
    json = JSON.parse(File.read("#{@unpackDir}/#{entity['entityType']}.json"))
    puts json.size
    assert(json.size == entity['count'].to_i, "The number of #{entity['entityType']} should be #{entity['count']}")
    badIdFound = false
    if(!entity['id'].nil?)
      json.each {|e| assert(false, "We shouldn't have found #{entity['id']}") if e['id'] == entity['id']}
    end
	end

  fileList = Dir.entries(@unpackDir)
	assert((fileList.size-3)>=table.hashes.size, "Expected " + table.hashes.size.to_s + " extract files, Actual:" + (fileList.size-3).to_s)
end

When /^a "(.*?)" extract file exists$/ do |collection|
  fn = @unpackDir + "/" + collection + ".json.gz"
  exists = File.exists?(fn)
  assert(exists, "Cannot find #{collection}.json file in extracts as file #{fn}")
end

# This is old, uses hardwired counts, and contains the spurious "a"
# ATs that call this step should use explicit counts or "dbCount"
When /^a the correct number of "([^"]*?)" was extracted from the database$/ do |collection|
  step "the correct number of \"#{collection}\" \"old-hardwired\" was extracted from the database"
end

When /^the correct number of "([^"]*?)" "([^"]*?)" was extracted from the database$/ do |collection, expectedCount|
  disable_NOTABLESCAN()
  @tenantDb = @conn.db(convertTenantIdToDbName(@tenant))

  # Flag: get the count from the database
  countDb = false

  # expectedCount is either "old-hardwired" for old tests, or an
  # explicit count, or "dbCount" to take from the database
  if expectedCount == "old-hardwired"
    case collection
	when "school"
	  count = 3
	when "teacher"
	  count = @tenantDb.collection("staff").find({"type" => "teacher" } ).count()
    when "graduationPlan"
      count = 3
    when "gradingPeriod"
      count = 13
    when "staffEducationOrganizationAssociation"
      count = 10
    when "staffCohortAssociation"
      count = 2
    when "staffProgramAssociation"
      count = 6
    when "cohort"
      count = 1
    when "educationOrganization"
      count = 5
    when "staff"
      count = 10
    else
      countDb = true
    end
  elsif expectedCount == "dbCount"
    countDb = true
  else
    count = expectedCount.to_i()
  end
    
  if countDb
    parentCollection = subDocParent(collection)
    if(parentCollection == nil)
      count = @tenantDb.collection(collection).count()
    else
      count = @tenantDb.collection(parentCollection).aggregate([ {"$match" => {"#{collection}" => {"$exists" => true}}}, {"$unwind" => "$#{collection}"}]).size
    end
  end

  Zlib::GzipReader.open(@unpackDir + "/" + collection + ".json.gz") { |extractFile|
    records = JSON.parse(extractFile.read)
    puts "\nCounts Expected: " + count.to_s + " Actual: " + records.size.to_s + "\n"
    assert(records.size == count,"Counts off Expected: " + count.to_s + " Actual: " + records.size.to_s)
  }
  enable_NOTABLESCAN()
end

$schoolSessions= {}
When /I check that the session extract for "(.*?)" has the correct number of records/ do |edOrgId|
  disable_NOTABLESCAN()
  @tenantDb = @conn.db(convertTenantIdToDbName(@tenant))
  query     = <<-jsonDelimiter
  [
  {"$project":{"body.schoolId":1}}
  ,{"$group":{"_id":"$body.schoolId", "sessions":{"$addToSet":"$_id"}}}
  ]
  jsonDelimiter
  puts(query)
  query  = JSON.parse(query)
  result = @tenantDb.collection('session').aggregate(query)
  result.each{ |schoolIdToSessions|
    schoolId = schoolIdToSessions['_id']
    puts schoolId
    sessions = schoolIdToSessions['sessions']
    puts sessions
    $schoolSessions[schoolId] = sessions
  }

  sessionZipFile  = @unpackDir + '/session.json.gz'
  sessionJsnFile  = @unpackDir + '/session.json'
  Minitar.unpack(@filePath, @unpackDir)
  assert(File.exists?(sessionZipFile), "Cannot find #{sessionZipFile} file ")
  File.delete(sessionJsnFile) if File.exist?(sessionJsnFile)
  `gunzip #{sessionZipFile}`
  json = JSON.parse(File.read(sessionJsnFile))

  comment = "Expected session extract for #{edOrgId} to have #{$schoolSessions[edOrgId].size}. Found #{json.size}"
  assert(json.size == $schoolSessions[edOrgId].size, comment)
  puts (comment)
  enable_NOTABLESCAN()
end

$schoolSections = {}
When /I check that the section extract for "(.*?)" has the correct number of records/ do |edOrgId|
  disable_NOTABLESCAN()
  @tenantDb = @conn.db(convertTenantIdToDbName(@tenant))
  query     = <<-jsonDelimiter
  [
  {"$project":{"body.schoolId":1}}
  ,{"$group":{"_id":"$body.schoolId", "sections":{"$addToSet":"$_id"}}}
  ]
  jsonDelimiter
  puts(query)
  query  = JSON.parse(query)
  result = @tenantDb.collection('section').aggregate(query)
  result.each{ |schoolIdToSections|
    schoolId = schoolIdToSections['_id']
    puts schoolId
    sections = schoolIdToSections['sections']
    puts sections
    $schoolSections[schoolId] = sections
  }

  sectionZipFile  = @unpackDir + '/section.json.gz'
  sectionJsnFile  = @unpackDir + '/section.json'
  Minitar.unpack(@filePath, @unpackDir)
  assert(File.exists?(sectionZipFile), "Cannot find #{sectionZipFile} file ")
  File.delete(sectionJsnFile) if File.exist?(sectionJsnFile)
  `gunzip #{sectionZipFile}`
  json = JSON.parse(File.read(sectionJsnFile))

  comment = "Expected section extract for #{edOrgId} to have #{$schoolSections[edOrgId].size}. Found #{json.size}"
  assert(json.size == $schoolSections[edOrgId].size, comment)
  puts (comment)
  enable_NOTABLESCAN()
end

$schoolStaffEdorgAssignment = {}
When /I check that the staffEdorgAssignment extract for "(.*?)" has the correct number of records/ do |edOrgId|
  disable_NOTABLESCAN()
  @tenantDb = @conn.db(convertTenantIdToDbName(@tenant))
  query     = <<-jsonDelimiter
  [
  {"$project":{"body.educationOrganizationReference":1}}
  ,{"$group":{"_id":"$body.educationOrganizationReference", "staffEdorgAssignments":{"$addToSet":"$_id"}}}
  ]
  jsonDelimiter
  puts(query)
  query  = JSON.parse(query)
  result = @tenantDb.collection('staffEducationOrganizationAssociation').aggregate(query)
  puts result
  result.each{ |schoolIdToSections|
    schoolId = schoolIdToSections['_id']
    puts schoolId
    staffEdorgAssignments = schoolIdToSections['staffEdorgAssignments']
    puts staffEdorgAssignments
    $schoolStaffEdorgAssignment[schoolId] = staffEdorgAssignments
  }
  seaZipFile  = @unpackDir + '/staffEducationOrganizationAssociation.json.gz'
  seaJsnFile  = @unpackDir + '/staffEducationOrganizationAssociation.json'
  Minitar.unpack(@filePath, @unpackDir)
  assert(File.exists?(seaZipFile), "Cannot find #{seaZipFile} file ")
  File.delete(seaJsnFile) if File.exist?(seaJsnFile)
  `gunzip #{seaZipFile}`
  json = JSON.parse(File.read(seaJsnFile))

  comment = "Expected section extract for #{edOrgId} to have #{$schoolStaffEdorgAssignment[edOrgId].size}. Found #{json.size}"
  assert(json.size == $schoolStaffEdorgAssignment[edOrgId].size, comment)
  puts (comment)
  enable_NOTABLESCAN()
end

                                          
$schoolStaff = {}
When /I check that the staff extract for "(.*?)" has the correct number of records/ do |edOrgId|
  disable_NOTABLESCAN()
  @tenantDb = @conn.db(convertTenantIdToDbName(@tenant))
  query     = <<-jsonDelimiter
  [
  {"$project":{"body.educationOrganizationReference":1}}
  ,{"$group":{"_id":"$body.educationOrganizationReference", "staffEducationOrganizationAssociation":{"$addToSet":"$_id"}}}
  ]
  jsonDelimiter
  puts(query)
  query  = JSON.parse(query)
  result = @tenantDb.collection('staffEducationOrganizationAssociation').aggregate(query)
                                          puts result
     result.each{ |schoolIdToStaffs|
                                          
     schoolId = schoolIdToStaffs['_id']
     puts schoolId
     schoolStaffs = schoolIdToStaffs['staffEducationOrganizationAssociation']
     puts schoolStaffs
     $schoolStaff[schoolId] = schoolStaffs
     }
     seaZipFile  = @unpackDir + '/staff.json.gz'
     seaJsnFile  = @unpackDir + '/staff.json'
     Minitar.unpack(@filePath, @unpackDir)
     assert(File.exists?(seaZipFile), "Cannot find #{seaZipFile} file ")
     File.delete(seaJsnFile) if File.exist?(seaJsnFile)
     `gunzip #{seaZipFile}`
     json = JSON.parse(File.read(seaJsnFile))
                                          
     comment = "Expected staff extract for #{edOrgId} to have #{$schoolStaff[edOrgId].size}. Found #{json.size}"
     assert(json.size == $schoolStaff[edOrgId].size, comment)
     puts (comment)
     enable_NOTABLESCAN()
end


$schoolParents = {}
When /I check that the parent extract for "(.*?)" has the correct number of records/ do |edOrgId|
  disable_NOTABLESCAN()
  @tenantDb = @conn.db(convertTenantIdToDbName(@tenant))
  query     = <<-jsonDelimiter
  [
  {"$project":{"schools":1,"studentParentAssociation":1}}
  ,{"$unwind":"$schools"},{"$unwind":"$studentParentAssociation"}
  ,{"$match":{}}
  ,{"$project":{"schools._id":1, "studentParentAssociation.body.parentId":1}}
  ,{"$group":{"_id":"$schools._id", "parents":{"$addToSet":"$studentParentAssociation.body.parentId"}}}
  ]
  jsonDelimiter
  puts(query)
  query  = JSON.parse(query)
  result = @tenantDb.collection('student').aggregate(query)
  puts result
  result.each{ |schoolIdToParents|
    schoolId = schoolIdToParents['_id']
    parents = schoolIdToParents['parents']
    $schoolParents[schoolId] = parents
  }

  parentZipFile  = @unpackDir + '/parent.json.gz'
  parentJsnFile  = @unpackDir + '/parent.json'
  Minitar.unpack(@filePath, @unpackDir)
  assert(File.exists?(parentZipFile), "Cannot find #{parentZipFile} file ")
  File.delete(parentJsnFile) if File.exist?(parentJsnFile)
  `gunzip #{parentZipFile}`
  json = JSON.parse(File.read(parentJsnFile))

  comment = "Expected parent extract for #{edOrgId} to have #{$schoolParents[edOrgId].size}. Found #{json.size}"
  assert(json.size == $schoolParents[edOrgId].size, comment)
  puts (comment)
  enable_NOTABLESCAN()
end
                                          

def get_student_schools(query)
  schoolStudents = {}
  query  = JSON.parse(query)
  result = @tenantDb.collection('student').aggregate(query)

  result.each{ |schoolIdToStudents|
    schoolId = schoolIdToStudents['_id']
    students = schoolIdToStudents['students']
    schoolStudents[schoolId] = students
  }

   schoolStudents
end

When /I check that the student extract for "(.*?)" has the correct number of records/ do |edOrgId|
  disable_NOTABLESCAN()
  @tenantDb = @conn.db(convertTenantIdToDbName(@tenant))

  query     = <<-jsonDelimiter
  [
  {"$project":{"schools":1}}
  ,{"$unwind":"$schools"}
  ,{"$match":{}}
  ,{"$project":{"_id":1, "schools._id":1}}
  ,{"$group":{"_id":"$schools._id", "students":{"$addToSet":"$_id"}}}
  ]
  jsonDelimiter
  puts(query)

  schoolStudents = get_student_schools(query)
  studentZipFile  = @unpackDir + '/student.json.gz'
  studentJsnFile  = @unpackDir + '/student.json'
  Minitar.unpack(@filePath, @unpackDir)
  assert(File.exists?(studentZipFile), "Cannot find #{studentZipFile} file ")
  File.delete(studentJsnFile) if File.exist?(studentJsnFile)
  `gunzip #{studentZipFile}`
  json = JSON.parse(File.read(studentJsnFile))

  comment = "Expected student extract for #{edOrgId} to have #{schoolStudents[edOrgId].size}. Found #{json.size}"
  assert(json.size == schoolStudents[edOrgId].size, comment)
  puts (comment)
  enable_NOTABLESCAN()
end



$schoolSSA = {}
When /I check that the studentSchoolAssociation extract for "(.*?)" has the correct number of records/ do |edOrgId|
  disable_NOTABLESCAN()
  @tenantDb = @conn.db(convertTenantIdToDbName(@tenant))
  query     = <<-jsonDelimiter
  [
  {"$project":{"body.schoolId":1}}
  ,{"$group":{"_id":"$body.schoolId", "studentSchoolAssociations":{"$addToSet":"$_id"}}}
  ]
  jsonDelimiter
  puts(query)
  query  = JSON.parse(query)
  result = @tenantDb.collection('studentSchoolAssociation').aggregate(query)
  puts "+++++++++++++++"
  puts result
  result.each{ |schoolIdToSSA|
    schoolId = schoolIdToSSA['_id']
    ssa = schoolIdToSSA['studentSchoolAssociations']
    $schoolSSA[schoolId] = ssa
  }

  studentZipFile  = @unpackDir + '/studentSchoolAssociation.json.gz'
  studentJsnFile  = @unpackDir + '/studentSchoolAssociation.json'
  Minitar.unpack(@filePath, @unpackDir)
  assert(File.exists?(studentZipFile), "Cannot find #{studentZipFile} file ")
  File.delete(studentJsnFile) if File.exist?(studentJsnFile)
  `gunzip #{studentZipFile}`
  json = JSON.parse(File.read(studentJsnFile))

  comment = "Expected student extract for #{edOrgId} to have #{$schoolSSA[edOrgId].size}. Found #{json.size}"
  assert(json.size == $schoolSSA[edOrgId].size, comment)
  puts (comment)
  enable_NOTABLESCAN()
end


When /I check that the attendance extract for "(.*?)" has the correct number of records/ do |edOrgId|
  disable_NOTABLESCAN()
  @tenantDb = @conn.db(convertTenantIdToDbName(@tenant))

  query = <<-jsonDelimiter
  [
  {"$project":{"body.attendanceEvent":1, "body.schoolId":1, "body.schoolYear":1, "body.studentId":1}}
  ,{"$unwind":"$body.attendanceEvent"}
  ,{"$match":{"body.studentId" :{"$in":"PLACE"}}}
  ,{"$group":{"_id":"events", "events":{"$addToSet":"$_id"}}}
  ]
  jsonDelimiter
  puts(query)
  query = JSON.parse(query)
  studentSchools = get_student_schools( CURRENT_STUDENT_QUERY )

  query[2]['$match']['body.studentId']['$in']  = studentSchools[edOrgId]
  result = @tenantDb.collection('attendance').aggregate(query)

  attendanceZipFile  = @unpackDir + '/attendance.json.gz'
  attendanceJsnFile  = @unpackDir + '/attendance.json'
  Minitar.unpack(@filePath, @unpackDir)
  assert(File.exists?(attendanceZipFile), "Cannot find #{attendanceZipFile} file ")
  File.delete(attendanceJsnFile) if File.exist?(attendanceJsnFile)
  `gunzip #{attendanceZipFile}`
  json = JSON.parse(File.read(attendanceJsnFile))

  expected = result[0]['events'].size
  comment = "Expected attendance extract for #{edOrgId} to have #{expected}. Found #{json.size}"
  assert(json.size == expected, comment)
  puts (comment)
  enable_NOTABLESCAN()
end

When /I check that the "(.*?)" extract for "(.*?)" has the correct number of records/ do |entity, edOrgId|
  disable_NOTABLESCAN()
  @tenantDb = @conn.db(convertTenantIdToDbName(@tenant))

  result = @tenantDb.collection(entity).find({'$or' => [{'body.schoolId' => edOrgId}, {'body.educationOrganizationId' => edOrgId}, {'body.educationOrgId' => edOrgId}]}).count()
  puts result

  zipFile  = "#{@unpackDir}/#{entity}.json.gz"
  jsnFile  = "#{@unpackDir}/#{entity}.json"
  Minitar.unpack(@filePath, @unpackDir)
  assert(File.exists?(zipFile), "Cannot find #{zipFile} file ")
  File.delete(jsnFile) if File.exist?(jsnFile)
  `gunzip #{zipFile}`
  json = JSON.parse(File.read(jsnFile))

  expected = result
  comment = "Expected #{entity} extract for #{edOrgId} to have #{expected}. Found #{json.size}"
  assert(json.size == expected, comment)
  puts (comment)
  enable_NOTABLESCAN()
end

When /I check that the "(.*?)" extract for "(.*?)" has "(.*?)" records/ do |entity, edOrgId, expected|

  zipFile  = "#{@unpackDir}/#{entity}.json.gz"
  jsnFile  = "#{@unpackDir}/#{entity}.json"
  Minitar.unpack(@filePath, @unpackDir)
  assert(File.exists?(zipFile), "Cannot find #{zipFile} file ")
  File.delete(jsnFile) if File.exist?(jsnFile)
  `gunzip #{zipFile}`
  json = JSON.parse(File.read(jsnFile))

  comment = "Expected #{entity} extract for #{edOrgId} to have [#{expected}]. Found #{json.size}"
  assert(json.size == expected.to_i, comment)
  puts (comment)
end

When /I check that the disciplineAction extract for "(.*?)" has the correct number of records/ do |edOrgId|
  disable_NOTABLESCAN()
  @tenantDb = @conn.db(convertTenantIdToDbName(@tenant))
  entity = 'disciplineAction'
  studentSchools = get_student_schools( CURRENT_STUDENT_QUERY )

  result = @tenantDb.collection(entity).find({'body.studentId' => {'$in' => studentSchools[edOrgId]}}).count()

  zipFile  = "#{@unpackDir}/#{entity}.json.gz"
  jsnFile  = "#{@unpackDir}/#{entity}.json"
  Minitar.unpack(@filePath, @unpackDir)
  assert(File.exists?(zipFile), "Cannot find #{zipFile} file ")
  File.delete(jsnFile) if File.exist?(jsnFile)
  `gunzip #{zipFile}`
  json = JSON.parse(File.read(jsnFile))

  expected = result
  comment = "Expected #{entity} extract for #{edOrgId} to have #{expected}. Found #{json.size}"
  assert(json.size == expected, comment)
  puts (comment)
  enable_NOTABLESCAN()
end

When /I check that the calendarDate extract for "(.*?)" has the correct number of records/ do |edOrgId|
  disable_NOTABLESCAN()
  @tenantDb = @conn.db(convertTenantIdToDbName(@tenant))
  entity = 'calendarDate'

  result = @tenantDb.collection(entity).find({'body.educationOrganizationId' => edOrgId}).count()

  zipFile  = "#{@unpackDir}/#{entity}.json.gz"
  jsnFile  = "#{@unpackDir}/#{entity}.json"
  Minitar.unpack(@filePath, @unpackDir)
  assert(File.exists?(zipFile), "Cannot find #{zipFile} file ")
  File.delete(jsnFile) if File.exist?(jsnFile)
  `gunzip #{zipFile}`
  json = JSON.parse(File.read(jsnFile))

  expected = result
  comment = "Expected #{entity} extract for #{edOrgId} to have #{expected}. Found #{json.size}"
  assert(json.size == expected, comment)
  puts (comment)
  enable_NOTABLESCAN()
end

$schoolGradingPeriods = {}
When /I check that the gradingPeriod extract for "(.*?)" has the correct number of records/ do |edOrgId|
  disable_NOTABLESCAN()
  @tenantDb = @conn.db(convertTenantIdToDbName(@tenant))
  entity = 'gradingPeriod'

  query = <<-jsonDelimiter
  [
    {"$project":{"body.schoolId":1, "body.gradingPeriodReference":1}}
    ,{"$unwind":"$body.gradingPeriodReference"}
    ,{"$group":{"_id":"$body.schoolId", "gpr":{"$addToSet":"$body.gradingPeriodReference"}}}
  ]
  jsonDelimiter
  puts(query)
  query = JSON.parse(query)
  result = @tenantDb.collection('session').aggregate(query)

  result.each{ |schoolIdToGpr|
    schoolId = schoolIdToGpr['_id']
    gpr = schoolIdToGpr['gpr']
    $schoolGradingPeriods[schoolId] = gpr
  }

  zipFile  = "#{@unpackDir}/#{entity}.json.gz"
  jsnFile  = "#{@unpackDir}/#{entity}.json"
  Minitar.unpack(@filePath, @unpackDir)
  assert(File.exists?(zipFile), "Cannot find #{zipFile} file ")
  File.delete(jsnFile) if File.exist?(jsnFile)
  `gunzip #{zipFile}`
  json = JSON.parse(File.read(jsnFile))

  expected = $schoolGradingPeriods[edOrgId].size
  comment = "Expected #{entity} extract for #{edOrgId} to have #{expected}. Found #{json.size}"
  assert(json.size == expected, comment)
  puts (comment)
  enable_NOTABLESCAN()
end

$schoolTeachers = {}
When /I check that the teacherSchoolAssociation extract for "(.*?)" has the correct number of records/ do |edOrgId|
  disable_NOTABLESCAN()
  @tenantDb = @conn.db(convertTenantIdToDbName(@tenant))
  entity = 'teacherSchoolAssociation'

  query = <<-jsonDelimiter
  [
    {"$project":{"body.schoolId":1, "body.teacherId":1}}
    ,{"$group":{"_id":"$body.schoolId", "gpr":{"$addToSet":"$body.teacherId"}}}
  ]
  jsonDelimiter
  puts(query)
  query = JSON.parse(query)
  result = @tenantDb.collection('teacherSchoolAssociation').aggregate(query)

  result.each{ |schoolIdToGpr|
    schoolId = schoolIdToGpr['_id']
    gpr = schoolIdToGpr['gpr']
    $schoolTeachers[schoolId] = gpr
  }

  zipFile  = "#{@unpackDir}/#{entity}.json.gz"
  jsnFile  = "#{@unpackDir}/#{entity}.json"
  Minitar.unpack(@filePath, @unpackDir)
  assert(File.exists?(zipFile), "Cannot find #{zipFile} file ")
  File.delete(jsnFile) if File.exist?(jsnFile)
  `gunzip #{zipFile}`
  json = JSON.parse(File.read(jsnFile))

  expected = $schoolTeachers[edOrgId].size
  comment = "Expected #{entity} extract for #{edOrgId} to have #{expected}. Found #{json.size}"
  assert(json.size == expected, comment)
  puts (comment)
  enable_NOTABLESCAN()
end

When /I check that the studentGradebookEntry extract for "(.*?)" has the correct number of records/ do |edOrgId|
  disable_NOTABLESCAN()
  @tenantDb = @conn.db(convertTenantIdToDbName(@tenant))
  entity = 'studentGradebookEntry'
  date = DateTime.now.strftime('%Y-%m-%d')
  studentSchools = get_student_schools(CURRENT_STUDENT_QUERY)
  gradebookEntryForEdOrgStudent =  @tenantDb.collection('studentGradebookEntry').find({'body.studentId' => {'$in' => studentSchools[edOrgId]}})

  zipFile  = "#{@unpackDir}/#{entity}.json.gz"
  jsnFile  = "#{@unpackDir}/#{entity}.json"
  Minitar.unpack(@filePath, @unpackDir)
  assert(File.exists?(zipFile), "Cannot find #{zipFile} file ")
  File.delete(jsnFile) if File.exist?(jsnFile)
  `gunzip #{zipFile}`
  json = JSON.parse(File.read(jsnFile))

  expected = gradebookEntryForEdOrgStudent.count()
  comment = "Expected #{entity} extract for #{edOrgId} to have #{expected}. Found #{json.size}"
  assert(json.size == expected, comment)
  puts (comment)
  enable_NOTABLESCAN()
end
                                          
                                          
$studentAssessments = {}
When /I check that the studentAssessment extract for "(.*?)" has the correct number of records/ do |edOrgId|
  disable_NOTABLESCAN()
  @tenantDb = @conn.db(convertTenantIdToDbName(@tenant))
  entity = 'studentAssessment'
  date = DateTime.now.strftime('%Y-%m-%d')
  query     = <<-jsonDelimiter
  [
  {"$project":{"schools":1}}
  ,{"$unwind":"$schools"}
  ,{"$project":{"_id":1, "schools._id":1}}
  ,{"$group":{"_id":"$schools._id", "students":{"$addToSet":"$_id"}}}
  ]
  jsonDelimiter
  puts(query)
  query  = JSON.parse(query)
  result = @tenantDb.collection('student').aggregate(query)
                                          
  result.each{ |schoolIdToStudents|
  schoolId = schoolIdToStudents['_id']
  students = schoolIdToStudents['students']
  $studentAssessments[schoolId] = students
  }
                                          
  assessmentEntryForEdOrgStudent =  @tenantDb.collection('studentAssessment').find({'body.studentId' => {'$in' => $studentAssessments[edOrgId]}})
                                          
  zipFile  = "#{@unpackDir}/#{entity}.json.gz"
  jsnFile  = "#{@unpackDir}/#{entity}.json"
  Minitar.unpack(@filePath, @unpackDir)
  assert(File.exists?(zipFile), "Cannot find #{zipFile} file ")
  File.delete(jsnFile) if File.exist?(jsnFile)
  `gunzip #{zipFile}`
  json = JSON.parse(File.read(jsnFile))
                                          
  expected = assessmentEntryForEdOrgStudent.count()
  comment = "Expected #{entity} extract for #{edOrgId} to have #{expected}. Found #{json.size}"
  assert(json.size == expected, comment)
  puts (comment)
  enable_NOTABLESCAN()
  end

When /I check that the staffCohortAssociation extract for "(.*?)" has the correct number of records/ do |edOrgId|
  disable_NOTABLESCAN()
  @tenantDb = @conn.db(convertTenantIdToDbName(@tenant))
  entity = 'staffCohortAssociation'
  date = DateTime.now.strftime('%Y-%m-%d')
  query = <<-jsonDelimiter
  {
   "$and":[
      {"$or":[{"body.beginDate":{"$lt":"#{date}","$exists":true}},  {"body.beginDate":{"$exists":false}}]},
      {"$or":[{"body.endDate":{"$gt":"#{date}","$exists":true}},    {"body.endDate":{"$exists":false}}]},
      {"body.educationOrganizationReference":"#{edOrgId}"}
   ]
  }
  jsonDelimiter
  query = JSON.parse(query)
  staffForEdOrgRS = @tenantDb.collection('staffEducationOrganizationAssociation').find(query)
  staffForEdOrg = staffForEdOrgRS.map{ |staffEducationOrganizationAssociation|
    staffEducationOrganizationAssociation['body']['staffReference']
  }
  cohortForEdOrgStaff =  @tenantDb.collection('staffCohortAssociation').find({'body.staffId' => {'$in' => staffForEdOrg}})

  zipFile  = "#{@unpackDir}/#{entity}.json.gz"
  jsnFile  = "#{@unpackDir}/#{entity}.json"
  Minitar.unpack(@filePath, @unpackDir)
  assert(File.exists?(zipFile), "Cannot find #{zipFile} file ")
  File.delete(jsnFile) if File.exist?(jsnFile)
  `gunzip #{zipFile}`
  json = JSON.parse(File.read(jsnFile))

  expected = cohortForEdOrgStaff.count()
  comment = "Expected #{entity} extract for #{edOrgId} to have #{expected}. Found #{json.size}"
  assert(json.size == expected, comment)
  puts (comment)
  enable_NOTABLESCAN()
end

When /I check that the staffProgramAssociation extract for "(.*?)" has the correct number of records/ do |edOrgId|
  disable_NOTABLESCAN()
  @tenantDb = @conn.db(convertTenantIdToDbName(@tenant))
  entity = 'staffProgramAssociation'
  date = DateTime.now.strftime('%Y-%m-%d')
  query = <<-jsonDelimiter
  {
   "$and":[
      {"$or":[{"body.beginDate":{"$lt":"#{date}","$exists":true}},  {"body.beginDate":{"$exists":false}}]},
      {"$or":[{"body.endDate":{"$gt":"#{date}","$exists":true}},    {"body.endDate":{"$exists":false}}]},
      {"body.educationOrganizationReference":"#{edOrgId}"}
   ]
  }
  jsonDelimiter
  query = JSON.parse(query)
  staffForEdOrgRS = @tenantDb.collection('staffEducationOrganizationAssociation').find(query)
  staffForEdOrg = staffForEdOrgRS.map{ |staffEducationOrganizationAssociation|
    staffEducationOrganizationAssociation['body']['staffReference']
  }
  programForEdOrgStaff =  @tenantDb.collection('staffProgramAssociation').find({'body.staffId' => {'$in' => staffForEdOrg}})

  zipFile  = "#{@unpackDir}/#{entity}.json.gz"
  jsnFile  = "#{@unpackDir}/#{entity}.json"
  Minitar.unpack(@filePath, @unpackDir)
  assert(File.exists?(zipFile), "Cannot find #{zipFile} file ")
  File.delete(jsnFile) if File.exist?(jsnFile)
  `gunzip #{zipFile}`
  json = JSON.parse(File.read(jsnFile))

  expected = programForEdOrgStaff.count()
  comment = "Expected #{entity} extract for #{edOrgId} to have #{expected}. Found #{json.size}"
  assert(json.size == expected, comment)
  puts (comment)
  enable_NOTABLESCAN()
end

When /^a "(.*?)" was extracted with all the correct fields$/ do |collection|
  disable_NOTABLESCAN()
	Zlib::GzipReader.open(@unpackDir +"/" + collection + ".json.gz") { |extractFile|
	records = JSON.parse(extractFile.read)
	uniqueRecords = Hash.new
	records.each do |jsonRecord|
		assert(uniqueRecords[jsonRecord['id']] == nil, "Record was extracted twice \nJSONRecord:\n" + jsonRecord.to_s)
		uniqueRecords[jsonRecord['id']] = 1

		mongoRecord = getMongoRecordFromJson(jsonRecord)
		assert(mongoRecord != nil, "MongoRecord not found: " + mongoRecord.to_s)

		compareRecords(mongoRecord, jsonRecord)
	end
  enable_NOTABLESCAN()
}
end

When /^I log into "(.*?)" with a token of "(.*?)", a "(.*?)" for "(.*?)" for "(.*?)" in tenant "(.*?)", that lasts for "(.*?)" seconds/ do |client_appName, user, role, edorg, realm, tenant, expiration_in_seconds|

  @edorg = getEntityId(realm)
  @api_version = "v1"

  disable_NOTABLESCAN()
  conn = Mongo::Connection.new(DATABASE_HOST, DATABASE_PORT)
  db = conn[DATABASE_NAME]
  appColl = db.collection("application")
  client_id = appColl.find_one({"body.name" => client_appName})["body"]["client_id"]
  conn.close
  enable_NOTABLESCAN()

  script_loc = File.dirname(__FILE__) + "/../../../../../../opstools/token-generator/generator.rb"
  out, status = Open3.capture2("ruby #{script_loc} -e #{expiration_in_seconds} -c #{client_id} -u #{user} -r \"#{role}\" -E \"#{edorg}\" -t \"#{tenant}\" -R \"#{realm}\" -n true")
  assert(out.include?("token is"), "Could not get a token for #{user} for realm #{realm}")
  match = /token is (.*)/.match(out)
  @sessionId = match[1]
  puts "The generated token is #{@sessionId}"
end

def getEntityId(entity)
  entity_to_id_map = {
      "orphanEdorg" => "54b4b51377cd941675958e6e81dce69df801bfe8_id",
      "IL-Daybreak" => "1b223f577827204a1c7e9c851dba06bea6b031fe_id",
      "IL-Highwind" => "99d527622dcb51c465c515c0636d17e085302d5e_id",
      "District-5"  => "880572db916fa468fbee53a68918227e104c10f5_id",
      "Daybreak Central High" => "a13489364c2eb015c219172d561c62350f0453f3_id"
  }
  return entity_to_id_map[entity]
end

When /^I try to POST to the bulk extract endpoint/ do
  hash = {
    "stuff" => "Random stuff"
  }
  @format = "application/vnd.slc+json"
  restHttpPost("/bulk/extract/tenant",hash.to_json)
end

When /^I use an invalid tenant to trigger a bulk extract/ do
 # command  = "#{TRIGGER_SCRIPT}"
 # if (PROPERTIES_FILE !=nil && PROPERTIES_FILE != "")
 #   command = command + " -Dsli.conf=#{PROPERTIES_FILE}"
 #   "Using extra property: -Dsli.conf=#{PROPERTIES_FILE}"
 #  end
 #  if (KEYSTORE_FILE !=nil && KEYSTORE_FILE != "")
 #   command = command + " -Dsli.encryption.keyStore=#{KEYSTORE_FILE}"
 #   puts "Using extra property: -Dsli.encryption.keyStore=#{KEYSTORE_FILE}"
 #  end
 #  if (JAR_FILE !=nil && JAR_FILE != "")
 #   command = command + " -f#{JAR_FILE}"
 #   puts "Using extra property:  -f#{JAR_FILE}"
 #  end
 #  command = command + " -tNoTenantForYou"
 #  puts "Running: #{command} "
 #  puts runShellCommand(command)
  step "I trigger an extract for tenant \"NoTenantForYou\""
end

When /^I request the latest bulk extract delta using the api$/ do
  puts "stubbed out"
end

When /^I untar and decrypt the "(.*?)" delta tarfile for tenant "(.*?)" and appId "(.*?)" for "(.*?)"$/ do |data_store, tenant, appId, lea|
  sleep 1
  opts = {sort: ["body.date", Mongo::DESCENDING], limit: 1}
  getExtractInfoFromMongo(build_bulk_query(tenant, appId, lea, true), opts)

  openDecryptedFile(appId)
  @fileDir = OUTPUT_DIRECTORY if data_store == "API"
  untar(@fileDir)
  @deltaDir = @fileDir
end

When /^I untar and decrypt the "(.*?)" public delta tarfile for tenant "(.*?)" and appId "(.*?)" for "(.*?)"$/ do |data_store, tenant, appId, lea|
  sleep 1
  opts = {sort: ["body.date", Mongo::DESCENDING], limit: 1}
  query = build_bulk_query(tenant, appId, lea, true, true)
  getExtractInfoFromMongo(query, opts)
  openDecryptedFile(appId)
  @fileDir = OUTPUT_DIRECTORY if data_store == "API"
  untar(@fileDir)
  @deltaDir = @fileDir
end

When /^I POST and validate the following entities:$/ do |table|
  table.hashes.map do |api_params|
    print "Posting new #{api_params['entityType']} .. "
    step "I POST a \"#{api_params['entityName']}\" of type \"#{api_params['entityType']}\""
    step "I should receive a return code of #{api_params['returnCode']}"
    print "OK\n"
  end
end

When /^I POST a "(.*?)" of type "(.*?)"$/ do |entity_name, entity_type|
  response_map, value = nil
  # POST is a special case. We are creating a brand-new entity.
  # Get the entity json body from the map specified in get_post_body_by_entity_name()
  body = get_post_body_by_entity_name(entity_name)
  # Get the endpoint that corresponds to the desired entity
  endpoint = get_entity_endpoint(entity_type)
  puts prepareData(@format, body).to_s
  restHttpPost("/#{@api_version}/#{endpoint}", prepareData(@format, body))
  assert(@res != nil, "Response from rest-client POST is nil")
end

When /^I PUT and validate the following entities:$/ do |table|
  table.hashes.map do |api_params|
    print "Putting #{api_params['entityName']} .."
    step "I PUT the \"#{api_params['field']}\" for a \"#{api_params['entityName']}\" entity to \"#{api_params['value']}\" at \"#{api_params['endpoint']}\""
    step "I should receive a return code of #{api_params['returnCode']}"
    print "OK\n"
  end
end

When /^I PUT the "(.*?)" for a "(.*?)" entity to "(.*?)" at "(.*?)"$/ do |field, entity_name, value, endpoint|
  #the GET body contains field the user may not have access to, changed to use the POST body
  post_body = get_post_body_by_entity_name(entity_name)
  if post_body.nil?
     puts "No POST body found with entityName: " + entity_name + ". Attempting to use GET body."
     response_body = get_response_body(endpoint)
     assert(response_body != nil, "No response from GET request for entity #{entity_name}")
     # If we get a list, just take the first entry. No muss, no fuss.
     response_body = response_body[0] if response_body.is_a?(Array)
     # Modify the response body field with value, will become PUT body
     put_body = update_api_put_field(response_body, field, value)
  else
     put_body = update_api_put_field(post_body, field, value)
  end
  puts prepareData(@format, put_body).to_s
  restHttpPut("/#{@api_version}/#{endpoint}", prepareData(@format, put_body))
  assert(@res != nil, "Response from rest-client PUT is nil")
end

def update_api_put_field(body, field, value)
  # This method allows us to modify custom fields in a way that is
  # compliant with the ed-fi data structure and the type requirements per field.
  # Some would call this hackish. I call it fiendishly clever.
  # Set the GET response body as body and edit the requested field
  case field
   when "postalCode"
    body["address"][0]["postalCode"] = value.to_s
   when "contactPriority"
    body["contactPriority"] = value.to_i
   when "missingEntity"
    body["id"] = value
   when "name.firstName"
    body["name"]["firstName"] = value
   when "name.middleName"
    body["name"]["middleName"] = value
   else
    body[field] = value
  end
  return body
end

When /^I PATCH and validate the following entities:$/ do |table|
  table.hashes.map do |api_params|
    print "Patching #{api_params['entity']} .."
    step "I PATCH the \"#{api_params['fieldName']}\" entity of type \"#{api_params['entityType']}\" to \"#{api_params['value']}\" at endpoint \"#{api_params['endpoint']}\""
    step "I should receive a return code of #{api_params['returnCode']}"
    print "OK\n"
  end
end

When /^I PATCH the "(.*?)" entity of type "(.*?)" to "(.*?)" at endpoint "(.*?)"$/ do |field_name, entity_type, value, endpoint|
  # Get the desired entity from mongo, we will only use the _id
  entity_response_body = get_response_body(endpoint)
  # We will set the PATCH body to ONLY the field_values map we get from get_patch_body_by_entity_name()
  patch_body = get_patch_body_by_entity_name(field_name, value)
  # Get the endpoint that corresponds to the desired entity
  endpoint = get_entity_endpoint(entity_type)
  restHttpPatch("/#{@api_version}/#{endpoint}/#{entity_response_body["id"]}", prepareData(@format, patch_body))
  assert(@res != nil, "Response from rest-client PATCH is nil")
end

When /^I PATCH the "([^"]*)" field of the entity specified by endpoint "([^"]*)" to '([^']*)'$/ do |field_name, endpoint, value|
  # Get the desired entity from mongo, we will only use the _id
  entity_response_body = get_response_body(endpoint)
  STDOUT.puts entity_response_body.to_s
  # We will set the PATCH body based on the evaluated value
  patch_body = { field_name => eval(value) }
  restHttpPatch("/#{@api_version}/#{endpoint}", prepareData(@format, patch_body))
  assert(@res != nil, "Response from rest-client PATCH is nil")
  STDOUT.puts @res.to_s
end

When /^I DELETE and validate the following entities:$/ do |table|
  table.hashes.map do |api_params|
    print "Deleting #{api_params['entity']} .."
    step "I DELETE an \"#{api_params['entity']}\" of id \"#{api_params['id']}\""
    step "I should receive a return code of #{api_params['returnCode']}"
    print " OK (Received expected RetCode: #{api_params['returnCode']})\n"
  end
end

When /^I DELETE an "(.*?)" of id "(.*?)"$/ do |entity, id|
  # Get the endpoint that corresponds to the desired entity
  endpoint = translate_custom_entity_to_endpoint(entity)
  restHttpDelete("/#{@api_version}/#{endpoint}/#{id}")
end

def translate_custom_entity_to_endpoint(endpoint_name)
  endpoint_name_translation_map = {
    "assessments/id/learningObjectives" => "assessments/235e448a14cc25ac0ede32bf35e9a798bf2cbc1d_id/learningObjectives",
    "assessments/id/learningStandards" => "assessments/235e448a14cc25ac0ede32bf35e9a798bf2cbc1d_id/learningStandards",
    "courseOfferings/id/courses" => "courseOfferings/4e22b4b0aac3310de7f4b789d5a31e5e2bd792ec_id/courses",
    "courseOfferings/id/sections" => "courseOfferings/4e22b4b0aac3310de7f4b789d5a31e5e2bd792ec_id/sections",
    "courseOfferings/id/sessions" => "courseOfferings/4e22b4b0aac3310de7f4b789d5a31e5e2bd792ec_id/sessions",
    "courses/id/courseOfferings" => "courses/7f3baa1a1f553809c6539671f08714aed6ec8b0c_id/courseOfferings",
    "courses/id/courseOfferings/sessions" => "courses/7f3baa1a1f553809c6539671f08714aed6ec8b0c_id/courseOfferings/sessions",
    "educationOrganizations/id/courses" => "educationOrganizations/772a61c687ee7ecd8e6d9ad3369f7883409f803b_id/courses",
    "educationOrganizations/id/educationOrganizations" => "educationOrganizations/772a61c687ee7ecd8e6d9ad3369f7883409f803b_id/educationOrganizations",
    "educationOrganizations/id/graduationPlans" => "educationOrganizations/772a61c687ee7ecd8e6d9ad3369f7883409f803b_id/graduationPlans",
    "educationOrganizations/id/schools" => "educationOrganizations/772a61c687ee7ecd8e6d9ad3369f7883409f803b_id/schools",
    "educationOrganizations/id/studentCompetencyObjectives" => "educationOrganizations/772a61c687ee7ecd8e6d9ad3369f7883409f803b_id/studentCompetencyObjectives",
    "learningObjectives/id/childLearningObjectives" => "learningObjectives/18f258460004b33fa9c1249b8c9ed3bd33c41645_id/childLearningObjectives",
    "learningObjectives/id/learningStandards" => "learningObjectives/18f258460004b33fa9c1249b8c9ed3bd33c41645_id/learningStandards",
    "learningObjectives/id/parentLearningObjectives" => "learningObjectives/18f258460004b33fa9c1249b8c9ed3bd33c41645_id/parentLearningObjectives",
    "msollars.studentAssessment" => "studentAssessments/f9643b7abba04ae01586723abed0e38c63e4f975_id",
    "msollars.studentGradebookEntry" => "studentGradebookEntries/7f714f03238d978398fbd4f8abbf9acb3e5775fe_id",
    "msollars.grade" => "grades/f438cf61eda4d45d77f3d7624fc8d089aa95e5ea_id4542ee7a376b1c7813dcdc495368c875bc6b03ed_id",
    "msollars.student" => "students/067198fd6da91e1aa8d67e28e850f224d6851713_id",
    "schools/id/courseOfferings" => "schools/772a61c687ee7ecd8e6d9ad3369f7883409f803b_id/courseOfferings",
    "schools/id/courses" => "schools/772a61c687ee7ecd8e6d9ad3369f7883409f803b_id/courses",
    "schools/id/sections" => "schools/772a61c687ee7ecd8e6d9ad3369f7883409f803b_id/courses",
    "schools/id/sessions" => "schools/772a61c687ee7ecd8e6d9ad3369f7883409f803b_id/courses",
    "schools/id/sessions/gradingPeriods" => "schools/772a61c687ee7ecd8e6d9ad3369f7883409f803b_id/sessions/gradingPeriods",
    "sessions/id/courseOfferings" => "sessions/fe6e1a162e6f6825830d78d72cb55498afaedcd3_id/courseOfferings",
    "sessions/id/courseOfferings/courses" => "sessions/fe6e1a162e6f6825830d78d72cb55498afaedcd3_id/courseOfferings/courses",
    "sessions/id/sections" => "sessions/fe6e1a162e6f6825830d78d72cb55498afaedcd3_id/sections"
  }
  # If the custom endpoint does not exist in the map, assume endpoint_name is
  # just an endpoint type and return its common endpoint name
  # i.e. if you cannot find it, pluralize it
  if endpoint_name_translation_map[endpoint_name] == nil
    endpoint = get_entity_endpoint(endpoint_name) 
  else
    endpoint = endpoint_name_translation_map[endpoint_name]
  end
  assert(endpoint != nil, "No endpoint mapping entry found in endpoint_name_translation_map for type #{endpoint_name}, please add an entry and try again.")
  return endpoint
end

def get_patch_body_by_entity_name(field, value)
  entity_name_to_patch_body_map = {
    "postalCode" => {
      "address"=>[{"postalCode"=>value.to_s,
                  "nameOfCounty"=>"Wake",
                  "streetNumberName"=>"111 Ave A",
                  "stateAbbreviation"=>"IL",
                  "addressType"=>"Physical",
                  "city"=>"Chicago"
                 }]
    },
    "calendarEvent" => {
            "calendarEvent" => value
    },
    "contactPriority" => {
      "contactPriority" => value.to_i
    },
    "diagnosticStatement" => {
      "diagnosticStatement" => value
    },
    "gradeLevelWhenAssessed" => {
      "gradeLevelWhenAssessed" => value
    },
    "studentLoginId" => {
      "loginId" => value,
      "sex" => "Male"
    },
    "studentLunch" => {
      "schoolFoodServicesEligibility" => value,
    },
    "momLoginId" => {
      "loginId" => value
    },
    "msollars.name" => {
      "name" => {
        "middleName" => value,
        "firstName" => "Matt",
        "lastSurname" => "Sollars"
      }
    },
    "cgray.name" => {
      "name" => {
        "middleName" => value,
        "firstName" => "Charles",
        "lastSurname" => "Gray"
      }
    },
    "cgray.myClass.name" => {
      "name" => {
        "middleName" => value,
        "firstName" => "LilCharlie",
        "lastSurname" => "Gray"
      }
    },
    "cgray.contactRestrictions" => {
      "contactRestrictions" => value
    },
    "dadLoginId" => {
        "loginId" => value
    },
    "patchProgramType" => {
        "programType" => value
    },
    "patchEndDate" => {
        "endDate" => value
    },
    "patchDescription" => {
        "description" => value
    },
    "patchCourseDesc" => {
        "courseDescription" => value
    },
    "patchCourseId" => {
        "courseId" => value
    },
    "patchContentStd" => {
        "contentStandard" => value
    },
    "patchIndividualPlan" => {
        "individualPlan" => value
    },
    "studentParentName" => {
      "name" => {
        "middleName" => "Fatang",
        "lastSurname" => "ZoopBoing",
        "firstName" => "Pang"
      }
    }
  }
  return entity_name_to_patch_body_map[field]
end

def get_response_body(endpoint)
  # Perform GET request and verify we get a response and a response body
  restHttpGet("/#{@api_version}/#{endpoint}")
  assert(@res != nil, "Response from rest-client GET is nil")
  assert(@res.body != nil, "Response body is nil")
  # Store the response in an entity-specific response map
  response_body = JSON.parse(@res)
  # Fail if we do not find the entity in response body from GET request
  assert(response_body != nil, "No response body for #{endpoint} returned by GET request")
  return response_body
end

When /^I request an unsecured latest delta via API for tenant "(.*?)", lea "(.*?)" with appId "(.*?)"$/ do |tenant, lea, app_id |
  @lea = lea
  @app_id = app_id

  query_opts = {sort: ["body.date", Mongo::DESCENDING], limit: 1}
  # Get the edorg and timestamp from bulk extract collection in mongo
  getExtractInfoFromMongo(build_bulk_query(tenant, app_id, lea, true), query_opts)
  # Assemble the API URI and make API call
  restHttpGet("/bulk/extract/#{lea}/delta/#{@timestamp}", 'application/x-tar', @sessionId)
end

When /^I request latest delta via API for tenant "(.*?)", lea "(.*?)" with appId "(.*?)" clientId "(.*?)"$/ do |tenant, lea, app_id, client_id|
  @lea = lea
  @app_id = app_id

  query_opts = {sort: ["body.date", Mongo::DESCENDING], limit: 1}
  # Get the edorg and timestamp from bulk extract collection in mongo
  getExtractInfoFromMongo(build_bulk_query(tenant, app_id, lea, true), query_opts)
  # Set the download path to stream the delta file from API
  @delta_file = "delta_#{lea}_#{@timestamp}.tar"
  @download_path = OUTPUT_DIRECTORY + @delta_file
  @fileDir = OUTPUT_DIRECTORY + "decrypt"
  @filePath = @fileDir + "/" + @delta_file
  @unpackDir = @fileDir
  # Assemble the API URI and make API call
  restTls("/bulk/extract/#{lea}/delta/#{@timestamp}", nil, 'application/x-tar', @sessionId, client_id)
end

When /^I request latest public delta via API for tenant "(.*?)", lea "(.*?)" with appId "(.*?)" clientId "(.*?)"$/ do |tenant, lea, app_id, client_id|
  @lea = lea
  @app_id = app_id

  query_opts = {sort: ["body.date", Mongo::DESCENDING], limit: 1}
  # Get the edorg and timestamp from bulk extract collection in mongo
  getExtractInfoFromMongo(build_bulk_query(tenant, app_id, lea, true, true), query_opts)
  # Set the download path to stream the delta file from API
  @delta_file = "delta_#{lea}_#{@timestamp}.tar"
  @download_path = OUTPUT_DIRECTORY + @delta_file
  @fileDir = OUTPUT_DIRECTORY + "decrypt"
  @filePath = @fileDir + "/" + @delta_file
  @unpackDir = @fileDir
  # Assemble the API URI and make API call
  restTls("/bulk/extract/#{lea}/delta/#{@timestamp}", nil, 'application/x-tar', @sessionId, client_id)
end

When /^I store the URL for the latest delta for LEA "(.*?)"$/ do |lea|
  @delta_uri = JSON.parse(@res)
  @list_url  = @delta_uri["deltaEdOrgs"][lea][0]["uri"]
  # @list_irl is in the format https://<url>/api/rest/v1.3/bulk/extract/<lea>/delta/<timestamp>
  # -> strip off everything before v1.3, store: /v1.3/bulk/extract/<lea>/delta/<timestamp>
  @list_url.match(/api\/rest\/v(.*?)\/(.*)$/)
  @list_uri = $2
  # Get the timestamp from the URL
  @list_url.match(/delta\/(.*)$/)
  @delta_file = "delta_#{lea}_#{$1}.tar"
  # Store directory information for later retrieval
  @download_path = OUTPUT_DIRECTORY + @delta_file
  @fileDir = OUTPUT_DIRECTORY + "decrypt"
  @filePath = @fileDir + "/" + @delta_file
  @unpackDir = @fileDir
  @encryptFilePath = @download_path
end

When /^I request listed delta via API for "(.*?)"$/ do |app_id|
  @app_id = app_id
  restTls("/#{@list_uri}", nil, 'application/x-tar')
end

When /^I download and decrypt the delta$/ do
  # Open the file, decrypt, and check against API
  # The local download_path assumes sli/acceptance-tests/extract
  cleanDir(@download_path)
  download_path = streamBulkExtractFile(@download_path, @res.body)
  @decrypt_path = OUTPUT_DIRECTORY + "decrypt/" + @delta_file
  openDecryptedFile(@app_id, @decrypt_path, @download_path)
  untar(@decrypt_path)
end

When /^I decrypt and save the full extract$/ do
  @filePath = "extract/extract.tar"
  @unpackDir = File.dirname(@filePath) + '/unpack'
  if (!File.exists?("extract"))
      FileUtils.mkdir("extract")
  end

  step "the response is decrypted"
  File.open(@filePath, 'w') {|f| f.write(@plain) }
end

When /^I generate and retrieve the bulk extract delta via API for "(.*?)"$/ do |lea|
  #client_id = $APP_CONVERSION_MAP[app_id]
  step "I trigger a delta extract" 
  # Request path for IL-Daybreak Admins
  if lea == "1b223f577827204a1c7e9c851dba06bea6b031fe_id"
    step "I log into \"SDK Sample\" with a token of \"jstevenson\", a \"Noldor\" for \"IL-DAYBREAK\" for \"IL-Daybreak\" in tenant \"Midgar\", that lasts for \"300\" seconds"
    step "I request latest delta via API for tenant \"Midgar\", lea \"#{lea}\" with appId \"<app id>\" clientId \"<client id>\""
  # Request path for IL-Highwind Admins
  elsif lea == "99d527622dcb51c465c515c0636d17e085302d5e_id"
    step "I log into \"SDK Sample\" with a token of \"lstevenson\", a \"Noldor\" for \"IL-HIGHWIND\" for \"IL-Highwind\" in tenant \"Midgar\", that lasts for \"300\" seconds"
    step "I request latest delta via API for tenant \"Midgar\", lea \"#{lea}\" with appId \"<app id>\" clientId \"<client id>\""
  elsif lea == "884daa27d806c2d725bc469b273d840493f84b4d_id"
    step "I log into \"SDK Sample\" with a token of \"rrogers\", a \"Noldor\" for \"STANDARD-SEA\" for \"IL-Daybreak\" in tenant \"Midgar\", that lasts for \"300\" seconds"
    step "I request latest public delta via API for tenant \"Midgar\", lea \"#{lea}\" with appId \"<app id>\" clientId \"<client id>\""
  # Catch invalid LEA
  else 
    assert(false, "Did not recognize that LEA, cannot request extract")
  end
  step "I should receive a return code of 200"
  step "I download and decrypt the delta"
end

When /^I request the latest bulk extract delta via API for "(.*?)"$/ do |lea|
  print "Logging in as lstevenson in IL-Highwind .. "
  step "I log into \"SDK Sample\" with a token of \"lstevenson\", a \"Noldor\" for \"IL-HIGHWIND\" for \"IL-Highwind\" in tenant \"Midgar\", that lasts for \"300\" seconds"
  print "OK\nRequesting Delta via API .. "
  step "I request latest delta via API for tenant \"Midgar\", lea \"#{lea}\" with appId \"<app id>\" clientId \"<client id>\""
  print "OK\nVerifying return code 200 .. "
  step "I should receive a return code of 200"
  print "OK\nDownloading and decrypting delta tarfile .. "
  step "I download and decrypt the delta"
  print "OK"
end

When /^I run the bulk extract scheduler script$/ do
  command  = "echo 'y' | #{SCHEDULER_SCRIPT} #{@trigger_script_path} #{@scheduling_config_path}"
  result = runShellCommand(command)
  puts "Running: #{command} #{result}"
  raise "Result of bulk extract scheduler script should include Installed new crontab but was #{result}" if !result.include?"Installed new crontab"
  command = "crontab -l"
  result = runShellCommand(command)
  Dir.chdir
  puts "pwd: #{Dir.pwd}"
end

When /^I set the header format to "(.*?)"$/ do |format|
  puts "DEBUG: format is #{format}"
  @format = format
end


############################################################
# Then
############################################################

Then  /^a "(.*?)" was extracted in the same format as the api$/ do |collection|

  Zlib::GzipReader.open(@unpackDir +"/" + collection + ".json.gz") { |extracts|
  collFile = JSON.parse(extracts.read)
  assert(collFile!=nil, "Cannot find #{collection}.json file in extracts")
  compareToApi(collection, collFile)
}
end

Then  /^The "(.*?)" delta was extracted in the same format as the api$/ do |collection|
  # Make a list of all .gz files in the decrypt directory
  #Dir.entries(@fileDir).each do |file|
  #  zipfiles = Array.new
  #  zipfiles << file if file.match(/.gz$/)
  #end

  zipfile = @fileDir + "/" + collection + ".json.gz"

  #Why do we have two different conventions on where to place the untar'd files?
  zipfile = @unpackDir + "/" + collection + ".json.gz" if (!File.exists? zipfile)

  # Loop through each list, extract the file, parse the json, and verify against the api
  Zlib::GzipReader.open(zipfile) { |extracts|
    collFile = JSON.parse(extracts.read)
    assert(collFile!=nil, "Cannot find #{collection}.json file in extracts")
    compareToApi(collection, collFile)
  }
end

Then /^I should not see an extract for tenant "(.*?)"/ do |tenant|
  @conn = Mongo::Connection.new(DATABASE_HOST, DATABASE_PORT)
  @sliDb = @conn.db(DATABASE_NAME)
  @coll = @sliDb.collection("bulkExtractFiles")

  match =  @coll.find_one({"body.tenantId" => tenant})
  assert(!match,"Invalid extract for tenant #{tenant} found")
end

Then /^the extraction zone should still be empty/ do
  if File.exists?(OUTPUT_DIRECTORY)
    entries = Dir.entries(OUTPUT_DIRECTORY)
    puts "Files in #{OUTPUT_DIRECTORY} are: "
    entries.each {|x| puts x}
    assert(entries.size == 2, "Extraction zone is no longer empty.")
  end
end

Then /^each extracted "(.*?)" delta matches the mongo entry$/ do |collection|
  disable_NOTABLESCAN()
  Zlib::GzipReader.open(@unpackDir +"/" + collection + ".json.gz") { |extractFile|
  records = JSON.parse(extractFile.read)
  uniqueRecords = Hash.new
  records.each do |jsonRecord|
    assert(uniqueRecords[jsonRecord['id']] == nil, "Record was extracted twice \nJSONRecord:\n" + jsonRecord.to_s)
    uniqueRecords[jsonRecord['id']] = 1

    mongoRecord = getMongoRecordFromJson(jsonRecord)
    assert(mongoRecord != nil, "MongoRecord not found: " + mongoRecord.to_s)

    compareRecords(mongoRecord, jsonRecord)
  end
  enable_NOTABLESCAN()
}
end

Then /^The bulk extract tarfile should be empty$/ do
  puts "This is a description step"
end

Then /^I should see "(.*?)" entities of type "(.*?)" in the bulk extract tarfile$/ do |count, collection|
  puts "This is a description step"
end

Then /^the extract contains no entity files/ do
  step "the extract contains a file for each of the following entities:",table(%{
    | entityType |
  })
end

Then /^The "(.*?)" entity with id "(.*?)" should belong to LEA with id "(.*?)"$/ do |entity, entityId, leaId|
  # get a mongo cursor for tenant db
  @ingestion_db_name = convertTenantIdToDbName('Midgar')
  @db = @conn[@ingestion_db_name]
  # check the corresponding entity for the LEA reference
  parentEdorgCheck(entity, entityId, leaId)
end

Then /^I should see "(.*?)" bulk extract files$/ do |count|
  count = count.to_i
  #check the number of bulk extracts in mongo
  checkMongoCounts("bulkExtractFiles", count)
  #check the number of tarfiles generated
  directory = @encryptFilePath.split("/")
  directory = directory[0..-2].join("/")
  checkTarfileCounts(directory, count)
end

Then /^I should see "(.*?)" bulk extract SEA-public data file for the tenant "(.*?)" and application with id "(.*?)"$/ do |count, tenant, app_id|
  count = count.to_i
  @tenant = tenant
  query = build_bulk_query(tenant, app_id, nil, false, true)
  checkMongoQueryCounts("bulkExtractFiles", query, count);
  if count != 0
    getExtractInfoFromMongo(query)
    assert(File.exists?(@encryptFilePath), "SEA public data doesn't exist.")
  end
end

Then /^I remove the edorg with id "(.*?)" from the "(.*?)" database/ do |edorg_id, tenant|
  remove_edorg_from_mongo(edorg_id, tenant)
end

Then /^there should be no deltas in mongo$/ do
  checkMongoCounts("bulkExtractFiles", 0)
end

Then /^I should not see SEA data in the bulk extract deltas$/ do
  #verify there is no delta generated
  steps "Then I should see \"0\" bulk extract files"
end

Then /^I failed retrieve the path to and decrypt the extract file for the tenant "(.*?)" and application with id "(.*?)"$/ do |tenant, appId|
  @conn = Mongo::Connection.new(DATABASE_HOST, DATABASE_PORT)
  @sliDb = @conn.db(DATABASE_NAME)
  @coll = @sliDb.collection("bulkExtractFiles")
  match = @coll.find_one(build_bulk_query(tenant,appId))

  assert(match ==nil, "Database should not be updated with bulk extract file location")
end

Then /^I verify "(.*?)" delta bulk extract files are generated for Edorg "(.*?)" in "(.*?)"$/ do |count, lea, tenant|
  count = count.to_i
  @conn ||= Mongo::Connection.new(DATABASE_HOST, DATABASE_PORT)
  @sliDb ||= @conn.db(DATABASE_NAME)
  @coll = @sliDb.collection("bulkExtractFiles")
  query = {"body.tenantId"=>tenant, "body.isDelta"=>true, "body.edorg"=>lea}
  assert(count == @coll.count({query: query}), "Found #{@coll.count({query: query})}, expected #{count}")
end

Then /^I verify the last delta bulk extract by app "(.*?)" for "(.*?)" in "(.*?)" contains a file for each of the following entities:$/ do |appId, lea, tenant, table|
    opts = {sort: ["body.date", Mongo::DESCENDING], limit: 1}
    getExtractInfoFromMongo(build_bulk_query(tenant, appId, lea, true), opts)
    openDecryptedFile(appId)

    step "the extract contains a file for each of the following entities:", table
end


Then /^I verify the last public delta bulk extract by app "(.*?)" for "(.*?)" in "(.*?)" contains a file for each of the following entities:$/ do |appId, lea, tenant, table|
    opts = {sort: ["body.date", Mongo::DESCENDING], limit: 1}
    getExtractInfoFromMongo(build_bulk_query(tenant, appId, lea, true, true), opts)
    openDecryptedFile(appId)
    step "the extract contains a file for each of the following entities:", table
end


def getExtractsForEdOrg(tenant, edOrg)
  tenantId           = Digest::SHA1.hexdigest tenant
  tenantExtractPath  = "#{OUTPUT_DIRECTORY}#{tenantId}"
  tenantTarList      = Dir.glob "#{tenantExtractPath}/*.tar"

  computedJsons = []
  tenantTarList.each {|tenantTarListItem|
    if  tenantTarListItem =~ /\/(.{36,36})-#{edOrg}.*.tar/
      appId = $1
      encryptedTenantTarFile = File.open(tenantTarListItem, 'rb') { |f| f.read}
      decryptedTenantTarStr  = decryptFile(encryptedTenantTarFile, $APP_CONVERSION_MAP[appId])
      decryptedTenantTarFile = StringIO.new(decryptedTenantTarStr);
      Minitar::Input.open(decryptedTenantTarFile) do |inp|
        inp.each do |entry|
          tarComponent = entry.full_name
          if tarComponent =~  /(.*).json.gz/
            jsonFile = $1
            computedJsons << jsonFile
          end
        end
      end
    end
  }
  return computedJsons
end

And /^Only the following extracts exists for edOrg "(.*?)" in tenant "(.*?)"/ do |edOrg, tenant, table|
     allowedJsons  = table.raw.flatten
     computedJsons = getExtractsForEdOrg(tenant, edOrg)
     set1 = Set.new allowedJsons
     set2 = Set.new computedJsons
     symetricDiff = set1 ^ set2  #symmetric difference
     assert(symetricDiff.size == 0, "Expected #{set1.to_a.join(',')}, Found #{set2.to_a.join(',')} in extract for #{edOrg}")
end

And /^There should not be any of the following extracts for edOrg "(.*?)" in tenant "(.*?)"/ do |edOrg, tenant, table|
  unAllowedJsons  = table.raw.flatten
  computedJsons = getExtractsForEdOrg(tenant, edOrg)
  set1 = Set.new unAllowedJsons
  set2 = Set.new computedJsons
  intersect = set1 & set2
  assert(intersect.size == 0, "Found #{set2.to_a.join(',')} in extract for #{edOrg}. None of #{set1.to_a.join(',')} should occur.")
end

Then /^I verify this "(.*?)" file only contains:$/ do |file_name, table|
    step "I verify this \"#{file_name}\" file should contain:", table
    file_entry_count = to_map(get_json_from_file(file_name)).count
    step_entry_count = table.hashes.map.count
    assert(file_entry_count == step_entry_count, "The extracted #{file_name} json file contains #{file_entry_count} entries, but the step expects #{step_entry_count}")
end

Then /^I verify this "(.*?)" file (should|should not) contain:$/ do |file_name, should, table|
    look_for = should.downcase == "should"
    json_map = to_map(get_json_from_file(file_name))
    table.hashes.map do |entity|
        id = entity['id']
        json_entities = json_map[id]
        field, value = entity['condition'].split('=').map{|s| s.strip}
        if ((entity['condition'].nil? || entity['condition'].empty?) && !look_for)
            assert(json_entities.nil?, "Entity with id #{id} should not exist, but it does")
            next
        end
        assert(!json_entities.nil?, "Does not contain an entity with id: #{id}")
        success = false
        json_entities.each {|e|
            success = find_value_in_map(e, field, value)
            break if success
        }
        if (look_for)
            assert(success, "can't find an entity with id #{id} that matches #{entity['condition']}")
        else
            assert(!success, "found an entity with id #{id} that matches #{entity['condition']}, we should not have this entity")
        end
    end
end

Then /^the "(.*?)" file (should|should not) contain a field$/ do |file_name, should, table|
    look_for = should.downcase == "should"
    json_map = to_map(get_json_from_file(file_name))
    table.hashes.map do |entity|
        id = entity['id']
        field = entity['field']
        json_entities = json_map[id]
        json_entities.each do |json_entity|
          assert(json_entity[field].nil?, "Does contain a #{field.to_s} in #{file_name.to_s} with id: #{id.to_s}")
        end
    end
end

Then /^each record in the full extract is present and matches the delta extract$/ do
  @fileDir = Dir.pwd + "/extract/unpack"

  # loop through the list of files in delta directory
  Dir.entries(@deltaDir).each do |deltaFile|
    if deltaFile == "graduationPlan.json.gz"
    else
    next if !deltaFile.include?("gz")
    next if deltaFile.include?("deleted")
    puts "DEBUG: Current delta file is #{deltaFile}"
    # unzip the delta file
    deltaUnzip = Zlib::GzipReader.open(@deltaDir + "/" + deltaFile)
    deltaRecords = JSON.parse(deltaUnzip.read)
    
    # load and unzip the corresponding full extract file
    fullExtractUnzip = Zlib::GzipReader.open(@fileDir + "/" + deltaFile)
    fullExtractRecords = JSON.parse(fullExtractUnzip.read)
    puts "DEBUG: deltaRecords count is #{deltaRecords.length}"
    puts "DEBUG: fullExtractRecords count is #{fullExtractRecords.length}"

    # TODO: Uncomment this assert when the duplicate fix is pushed
    assert(deltaRecords.length == fullExtractRecords.length, "The number of records do not match. Deltas: #{deltaRecords.length}, Full Extract: #{fullExtractRecords.length}")
    
    # Put delta records in a hashmap for searching
    deltaHash = {}
    deltaRecords.each do |deltaRecord|
      deltaHash[deltaRecord["id"]] = deltaRecord
    end
    # Loop through fullExtract records and try to find match in deltaHash
    fullExtractRecords.each do |extractRecord|
      assert(extractRecord == deltaHash[extractRecord["id"]], "Could not find deltaRecord that corresponds to #{extractRecord}")
    end
    end
  end
end

Then /^I reingest the SEA so I can continue my other tests$/ do
  step "I am using local data store"
  step "I post \"deltas_update_sea.zip\" file as the payload of the ingestion job"
  step "the landing zone for tenant \"Midgar\" edOrg \"Daybreak\" is reinitialized"
  step "zip file is scp to ingestion landing zone"
  step "a batch job for file \"deltas_update_sea.zip\" is completed in database"
  step "a batch job log has been created"
  step "I should not see an error log file created"
  step "I should not see a warning log file created"
end

Then /^I ingested "(.*?)" dataset$/ do |dataset|
  step "I am using local data store"
  step "I post \"#{dataset}\" file as the payload of the ingestion job"
  step "the landing zone for tenant \"Midgar\" edOrg \"Daybreak\" is reinitialized"
  step "zip file is scp to ingestion landing zone"
  step "a batch job for file \"#{dataset}\" is completed in database"
  step "a batch job log has been created"
  step "I should not see an error log file created"
end

Then /^the "(.*?)" has the correct number of SEA public data records "(.*?)"$/ do |entity, field|
  disable_NOTABLESCAN()

	@tenantDb = @conn.db(convertTenantIdToDbName(@tenant))
  SEA = @tenantDb.collection("educationOrganization").find_one({'body.organizationCategories' => {"$in" => ['State Education Agency']}})
  @SEA_id = SEA["_id"]

  puts "Comparing SEA " + @SEA_id

  query_field = "body." + field
  collection = entity
  count = 0

  if INDEPENDENT_ENTITIES.include? entity
    query = {"$or" => [{query_field => @SEA_id}, {query_field => {"$exists" => false}}]}
  else
    query = {query_field => @SEA_id}
  end

  case entity
  when "educationOrganization"
    #adding 1 because SEA is not part of the this mongo query
    count = 1
  else
      count += @tenantDb.collection(collection).find(query).count()
  end

	Zlib::GzipReader.open(@unpackDir + "/" + entity + ".json.gz") { |extractFile|
    records = JSON.parse(extractFile.read)
    puts records

    puts "\nCounts Expected: " + count.to_s + " Actual: " + records.size.to_s + "\n"
    assert(records.size == count,"Counts off Expected: " + count.to_s + " Actual: " + records.size.to_s)
  }
 enable_NOTABLESCAN()
end

Then /^I verify that the "(.*?)" reference an SEA only "(.*?)"$/ do |entity, query|
  query_field = query.split(".")
  Zlib::GzipReader.open(@unpackDir + "/" + entity + ".json.gz") { |extractFile|
    records = JSON.parse(extractFile.read)
    records.each do |record|
      next if entity == 'educationOrganization' && record['organizationCategories'][0] == 'State Education Agency'

      field = record
      query_field.each do |key|
        field = field[key]
      end

      next if INDEPENDENT_ENTITIES.include?(entity) && field == nil

      assert(field == @SEA_id, 'Incorrect reference ' + field + ' expected ' + @SEA_id)
    end
  }
end

Then /^I verify that ([^ ]*?) "(.*?)" does not contain the reference field "(.*?)"$/ do |total, entity, query|
  query_field = query.split(".")
  count = 0
  Zlib::GzipReader.open(@unpackDir + "/" + entity + ".json.gz") { |extractFile|
    records = JSON.parse(extractFile.read)
    records.each do |record|
      next if (entity == "educationOrganization" || entity == "school") && (record["organizationCategories"][0] == "State Education Agency")

      field = record
      query_field.each do |key|
        field = field[key]
      end
      count += 1 if (field ==nil) 
    end
  }

  assert(count == total, "Incorrect number of #{entity} with no EdOrg references. Expected: #{total}, Actual: #{count}")
end

Then /^I verify that extract does not contain a file for the following entities:$/ do |table|
  table.hashes.map do |row|
    exists = File.exists?(@unpackDir + "/" + row["entity"] + ".json.gz")
    assert(!exists, "Found " + row["entity"] + ".json file in extracts")
  end
end


Then /^I have a fake bulk extract tar file for the following tenants and different dates:$/ do |table|
  @parentDir = "extract/cleanup/"
  testData = "#{File.dirname(__FILE__)}/../../test_data/cleanup/cleanup.tar"

  unless File.directory?(@parentDir)
    FileUtils.mkdir_p(@parentDir)
  end

  table.hashes.map do |row|
    subDir = @parentDir + "/" + row["tenant"] + "/" + row["Edorg"] + "/"
    puts subDir
    FileUtils.mkdir_p(subDir);
    destFile = createCleanupFile(@parentDir, row["tenant"], row["Edorg"], row["app"], row["date"])
    puts destFile
    FileUtils.cp(testData, destFile)
    addFakeBEEntry(row["tenant"], row["Edorg"], row["app"], false, false, row["date"], File.expand_path(destFile))
  end
end

Then /^I clean up the cleanup script test data$/ do
  disable_NOTABLESCAN()
  Dir[@parentDir + "/**/*"].each do |item|
    path = File.expand_path(item)
    puts path
    coll = @conn.db('sli').collection('bulkExtractFiles')
    coll.remove({"body.path" => path})
  end
  FileUtils.rm_rf(@parentDir)
  enable_NOTABLESCAN()
end

Then /^I should not see the following tenant bulk extract file:$/ do |table|
  disable_NOTABLESCAN()
  table.hashes.map do |row|
    destFile = File.expand_path(createCleanupFile(@parentDir, row["tenant"], row["Edorg"], row["app"], row["date"]))
    assert(!File.exist?(destFile), "File " + destFile + " was not removed")
    checkMongoQueryCounts("bulkExtractFiles",  {"body.path" => destFile}, 0)
  end
  enable_NOTABLESCAN()
end

Then /^I should see the following tenant bulk extract file:$/ do |table|
  disable_NOTABLESCAN()
  table.hashes.map do |row|
    destFile = File.expand_path(createCleanupFile(@parentDir, row["tenant"], row["Edorg"], row["app"], row["date"]))
    assert(File.exist?(destFile), "File " + destFile + " was removed")
    checkMongoQueryCounts("bulkExtractFiles",  {"body.path" => destFile}, 1)
  end
  enable_NOTABLESCAN()
end

Then /^the following test tenant and edorg are clean:$/ do |table|
  disable_NOTABLESCAN()
  table.hashes.map do |row|
    remove_edorg_from_mongo(row["Edorg"], row["tenant"])
  end
  enable_NOTABLESCAN()
end

Then /^I am willing to wait up to ([^ ]*) seconds for the bulk extract scheduler cron job to start and complete$/ do |limit|
  @maxTimeout = limit
  puts "Waited timeout for #{limit.to_i} seconds"
  intervalTime = 1
  @maxTimeout ? @maxTimeout : @maxTimeout = 900
  iters = (1.0*@maxTimeout/intervalTime).ceil
  iters.times do |i|
    if Dir.exists?(CRON_OUTPUT_DIRECTORY)
      puts "Bulk extract scheduler cron job took approx. #{(i+1)*intervalTime} seconds to start and complete"
      found = true
      break
    else
      sleep(intervalTime)
    end
  end

  assert(Dir.exists?(CRON_OUTPUT_DIRECTORY), "Timeout: cron job output directory #{CRON_OUTPUT_DIRECTORY} does not exist")

  outdir = Dir.new(CRON_OUTPUT_DIRECTORY)
  outdir.each do |filename|
    puts "Bulk extracted file by cron job: #{filename}" if filename!="." && filename!=".."
  end
  Dir.chdir(@current_dir)
end

Then /^I clean up the scheduler jobs/ do
  config_file = @scheduling_config_path + 'bulk_extract_scheduling.conf'
  File.delete(config_file) if File.exists? config_file

  output, status = Open3.capture2('crontab -l')
  assert(status.exitstatus == 0, 'Crontab listing could not be fetched')

  puts 'Current crontab entries:'
  puts output

  new_cron = ''
  output.each_line do |line|
    new_cron += line unless line.include?('Bulk Extract scheduling') || line.include?('local_bulk_extract.sh')
  end

  puts 'New crontab entries:'
  puts new_cron

  cron_file = @scheduling_config_path + 'job.cron'

  File.open(cron_file,'w') { |file| file.write(new_cron)}

  output, status = Open3.capture2("cat #{cron_file} | crontab -")

  assert(status.exitstatus == 0, 'Crontab could not be set')

  puts output

  File.delete(cron_file) if File.exists? cron_file

end

Then /^I save some IDs from all the extract files to "(.*?)" so I can delete them later$/ do |variable| 
  id_map = $GLOBAL_VARIABLE_MAP[variable]
  id_map ||= {} 
  sample_size = 10 
  skip_types = ["deleted"]
  [@fileDir, @unpackDir].each do |dir|
    if File.exists? dir 
      Dir.entries(dir).each { |f| 
        if matched = f.match(/(.*).json.gz/) 
          next unless skip_types.find_index(matched[1]).nil?
          Zlib::GzipReader.open("#{dir}/#{f}") { |extracts|
            extracted = JSON.parse(extracts.read)
            (extracted.shuffle.take(sample_size)).each { |extractRecord|
                id_map[matched[1]] ||= []
                id_map[matched[1]] << extractRecord["id"]
            }
          }
        end
      }
    end
  end
  $GLOBAL_VARIABLE_MAP[variable] = id_map
end

Then /^I delete one random entity from the my saved "(.*?)" except for:$/ do |variable, table|
  id_map = $GLOBAL_VARIABLE_MAP[variable]
  assert(!id_map.nil?, "Did you run the day 0 ingestion step to populate the IDs that I need to delete?")
  exceptions = []
  table.hashes.map do |row|
    exceptions << row["type"]
  end
  db_name = convertTenantIdToDbName('Midgar') 
  saved_for_later = []
  deleted = []
  # loop through the sample IDs we saved for each entity type, and as soon
  # as one delete went through, good enough...
  id_map.each_pair { |type, ids|
    next unless exceptions.find_index(type).nil?
    # delete those things last, since there are other entities hang off them
    saved_for_later << [type, ids] if type == "student" || type == "teacher" || type == "staff"
    deleted << delete_loop(type, ids, db_name)
  }

  saved_for_later.each { |pair|
    deleted << delete_loop(pair[0], pair[1], db_name) 
  }

  $GLOBAL_VARIABLE_MAP[variable] = deleted
end

Then /^I verify this delete file by app "(.*?)" for "(.*?)" contains one single delete from all types in "(.*?)" except:$/ do |appId, lea, variable, table|
  opts = {sort: ["body.date", Mongo::DESCENDING], limit: 1}
  getExtractInfoFromMongo(build_bulk_query("Midgar", appId, lea, true), opts)
  openDecryptedFile(appId)
  Minitar.unpack(@filePath, @unpackDir)

  deleted = $GLOBAL_VARIABLE_MAP[variable]
  puts deleted
  exceptions = []
  table.hashes.map do |row|
    exceptions << row["entityType"]
  end

  json_map = to_map(get_json_from_file("deleted"))
  deleted.each { |entry|
    type = entry[0]
    id = entry[1]
    in_delete_file = json_map[id]
    assert(!in_delete_file.nil?, "delete file does not contain #{type} #{id}") 
  }
end

Then /^the delete file in the delta extract should have one purge entry/ do
  json = get_json_from_file('deleted')
  count = 0
  json.each {|entry| count += 1 if entry['entityType'] == 'purge'}
  assert(count == 1, 'An incorrect number of purge entries was found in the delete file.')
end

############################################################
# Functions
############################################################
def delete_loop(type, ids, db)
  endpoint = get_entity_endpoint(type)
  success = false
  conn = Mongo::Connection.new(DATABASE_HOST, DATABASE_PORT)
  sliDb = conn.db(db)
  coll = sliDb.collection("deltas")
  while (!success && id = ids.pop) do
    5.times {
        begin
            restHttpDelete("/v1/#{endpoint}/#{id}")
            break
        rescue RestClient::RequestTimeout
            puts "Timed out while trying to delete #{type} #{id}"
        end
    }

    if (@res.code == 204) 
      success = true
      deleted_id = [type, id]
    elsif (@res.code == 404)
      # It's possible this entity has been cascadingly deleted
      # so let's take a look at the delta collection and make sure
      # it's really deleted
      query = {}
      query["_id"] = id
      query["c"] = case type
                   when "teacher"
                       "staff"
                   when "school"
                       "educationOrganization"
                   else
                       type
                   end
      query["d"] = {"$exists"=>1}
      assertWithPolling("can not find any delta on #{type} #{id}", 3) {!coll.find_one(query).nil?}
      item = coll.find_one(query)
      if item["u"].nil? || item["d"] >= item["u"]
        deleted_id = [item["c"], item["_id"]]
        success = true 
      end
    end
  end 
  assert(success, "Failed to delete any entities for #{type}")
  conn.close if conn != nil
  deleted_id
end

def get_entity_endpoint(type)
  case type
    when "competencyLevelDescriptor"
      "competencyLevelDescriptor"
    when "gradebookEntry"
      "gradebookEntries"
    when "staff"
      "staff"
    when "staffEducationOrganizationAssociation"
      "staffEducationOrgAssignmentAssociations"
    when "studentCompetency"
      "studentCompetencies"
    when "studentGradebookEntry"
      "studentGradebookEntries"
    else
      type+"s"
  end
end

# checks the map for field that has a value.  If it encounters and array, it'll iterate over it's contents.
# e.g. find_value_in_map(attendance_entity, "attendanceEvent.reason", "test")
def find_value_in_map(entity, fields, value)
  find_in_map(entity, fields) do |x|
    x.to_s == value
  end
end

def find_in_map(entity, fields)
  if entity.is_a? Array
    entity.each do |x|
      found = find_in_map(x, fields, &Proc.new)
      return found if found
    end
    return false
  end

  fields = fields.split('.').map { |x| x.strip } if not fields.is_a? Array
  f = fields[0]

  return false if entity[f].nil?
  if fields.size == 1
    found = yield entity[f]
    return found
  else
    found = find_in_map(entity[f], fields.slice(1..fields.size), &Proc.new)
    return found
  end
end

def bulkExtractTrigger(trigger_script, jar_file, properties_file, keystore_file, options="")
  command = "#{trigger_script}"
  if (properties_file !=nil && properties_file != "")
    command = command + " -Dsli.conf=#{properties_file}"
    puts "Using extra property: -Dsli.conf=#{properties_file}"
  end
  if (keystore_file !=nil && keystore_file != "")
    command = command + " -Dsli.encryption.keyStore=#{keystore_file}"
    puts "Using extra property: -Dsli.encryption.keyStore=#{keystore_file}"
  end
  if (jar_file !=nil && jar_file != "")
    command = command + " -f#{jar_file}"
    puts "Using extra property:  -f#{jar_file}"
  end
  command = command + options
  puts "Running: #{command}"
  result = `#{command}`
  puts result
  assert($?.exitstatus == 0, "Nonzero exit code from bulk extract: #{$?.exitstatus}")
end

def getExtractInfoFromMongo(query, query_opts={})
  @conn = Mongo::Connection.new(DATABASE_HOST, DATABASE_PORT)
  @sliDb = @conn.db(DATABASE_NAME)
  @coll = @sliDb.collection("bulkExtractFiles")
  match = @coll.find_one(query, query_opts)

  assert(match !=nil, "Database was not updated with bulk extract file location")

  edorg = match['body']['edorg'] || ""
  @encryptFilePath = match['body']['path']
  @unpackDir = File.dirname(@encryptFilePath) + "/#{edorg}/unpack"
  @fileDir = File.dirname(@encryptFilePath) + "/#{edorg}/decrypt/"
  @filePath = @fileDir + File.basename(@encryptFilePath)
  @timestamp = match['body']['date'] || ""
  @timestamp = @timestamp.utc.iso8601(3)
  @tenant = query["body.tenantId"]

  if $SLI_DEBUG
    puts "query is #{query}"
    puts "query opts is #{query_opts}"
    puts "encryptFilePath is #{@encryptFilePath}"
    puts "unpackDir is #{@unpackDir}"
    puts "fileDir is #{@fileDir}"
    puts "filePath is #{@filePath}"
    puts "timestamp is #{@timestamp}"
  end
end

def getMongoRecordFromJson(jsonRecord)
	@tenantDb = @conn.db(convertTenantIdToDbName(@tenant))
	case jsonRecord['entityType']
  	when "stateEducationAgency", "localEducationAgency", "school"
  	  collection = "educationOrganization"
  	when "teacher"
  	  collection = "staff"
  	else
      collection = jsonRecord['entityType']
  end
  parent = subDocParent(collection)
  if (parent == nil)
    return @tenantDb.collection(collection).find_one("_id" => jsonRecord['id'])
  else
    #Collection is a subdoc, gets record from parent
    superdoc = @tenantDb.collection(parent).find_one("#{collection}._id" => jsonRecord['id'])
    superdoc[collection].each do |subdoc|
      if (subdoc["_id"] == jsonRecord['id'])
        return subdoc
      end
    end
  end
end

def	compareRecords(mongoRecord, jsonRecord)
	if !MUTLI_ENTITY_COLLS.include?(jsonRecord['entityType'])
	  assert(mongoRecord['type']==jsonRecord['entityType'], "Record types do not match for records \nMONGORecord:\n" + mongoRecord.to_s + "\nJSONRecord:\n" + jsonRecord.to_s)
	end
	jsonRecord.delete('id')
	jsonRecord.delete('entityType')

    if (ENCRYPTED_ENTITIES.include?(mongoRecord['type']))
        compareEncryptedRecords(mongoRecord, jsonRecord)
    elsif(mongoRecord['type'] == 'attendance')
      compareAttendances(mongoRecord, jsonRecord)
    elsif (!COMBINED_ENTITIES.include?(mongoRecord['type']))
	    assert(mongoRecord['body'].eql?(jsonRecord), "Record bodies do not match for records \nMONGORecord:\n" + mongoRecord['body'].to_s + "\nJSONRecord:\n" + jsonRecord.to_s )
    end
end

def compareAttendances(mongoRecord, jsonRecord)
      assert(mongoRecord['body']['attendanceEvent']==jsonRecord['schoolYearAttendance'][0]['attendanceEvent'], "Record types do not match for records \nMONGORecord:\n" + mongoRecord.to_s + "\nJSONRecord:\n" + jsonRecord.to_s)
      assert(mongoRecord['body']['schoolYear']==jsonRecord['schoolYearAttendance'][0]['schoolYear'], "Record types do not match for records \nMONGORecord:\n" + mongoRecord.to_s + "\nJSONRecord:\n" + jsonRecord.to_s)
      jsonRecord.delete('schoolYearAttendance')
      mongoRecord['body'].delete('schoolYear')
      mongoRecord['body'].delete('attendanceEvent')
      assert(mongoRecord['body'].eql?(jsonRecord), "Record bodies do not match for records \nMONGORecord:\n" + mongoRecord['body'].to_s + "\nJSONRecord:\n" + jsonRecord.to_s )
end

def compareEncryptedRecords(mongoRecord, jsonRecord)
    assert(get_nested_keys(mongoRecord['body']).eql?(get_nested_keys(jsonRecord)),
      "Record fields do not match for records \nMONGORecord:\n" + get_nested_keys(mongoRecord['body']).to_s + "\nJSONRecord:\n" + get_nested_keys(jsonRecord).to_s)

    assert(removeEncryptedFields(mongoRecord['body']).eql?(removeEncryptedFields(jsonRecord)),
      "Record bodies do not match for records \nMONGORecord:\n" + mongoRecord['body'].to_s + "\nJSONRecord:\n" + jsonRecord.to_s )
end

def removeEncryptedFields(record)
    record.delete_if{ |key,value| ENCRYPTED_FIELDS.include?(key)}
end


def get_nested_keys(hash, keys=Array.new)
  hash.each do |k, v|
   keys << k
   case v
    when Array
      v.each {|vv| (Hash.try_convert(vv)!=nil)?get_nested_keys(vv, keys): keys.sort }
    when Hash
      get_nested_keys(v,keys)
    end
   end
   keys.sort
end

def entityToUri(entity)

  uri = String.new(entity)

  case entity
  when "staff", "competencyLevelDescriptor"
  when "gradebookEntry", "studentGradebookEntry", "studentCompetency"
    uri[-1] = "ies"
  when "staffEducationOrganizationAssociation"
    uri = "staffEducationOrgAssignmentAssociations"
  else
    uri += "s"
  end

  uri
end

def compareToApi(collection, collFile, sample_size=10)
  found = false
  uri = entityToUri(collection)

  (collFile.shuffle.take(sample_size)).each do |extractRecord|

    id = extractRecord["id"]

    #Make API call and get JSON for the collection
    @format = "application/vnd.slc+json"
    restHttpGet("/v1/#{uri}/#{id}")

    assert(@res != nil, "Response from rest-client GET is nil")
    assert(@res.code != 404, "Response from rest-client GET is not 200 (Got a #{@res.code})")
    if @res.code == 200
      apiRecord = JSON.parse(@res.body)
      assert(apiRecord != nil, "Result of JSON parsing is nil")
      apiRecord.delete("links")
      if COMBINED_ENTITIES.include?(collection)
        COMBINED_SUB_ENTITIES.each do |entity|
          if entity.include? "student"
            identifier = String.new(entity[7..-2])
            identifier[0] = identifier[0].downcase
            extractRecord[entity].sort_by! { |hsh| hsh[identifier]["identificationCode"] } if extractRecord.has_key? entity
            apiRecord[entity].sort_by! { |hsh| hsh[identifier]["identificationCode"] } if apiRecord.has_key? entity
          else
            extractRecord[entity].sort_by! { |hsh| hsh["identificationCode"] } if extractRecord.has_key? entity
            apiRecord[entity].sort_by! { |hsh| hsh["identificationCode"] } if apiRecord.has_key? entity
          end
        end
      end
      assert(extractRecord.eql?(apiRecord), "Extract record doesn't match API record.\nExtractRecord:\n" +extractRecord.to_s + "\nAPIRecord:\n" + apiRecord.to_s)
      found = true
    end
  end

  assert(found, "No API records for #{collection} were fetched successfully.")
end

def decryptFile(file, client_id)
  private_key = OpenSSL::PKey::RSA.new File.read "./test/features/utils/keys/#{client_id}.key"
  assert(file.length >= 512, "File is less than 512 Bytes: #{file.length}")
  encryptediv = file[0,256]
  encryptedsecret = file[256,256]
  encryptedmessage = file[512,file.length - 512]

  decrypted_iv = private_key.private_decrypt(encryptediv)
  decrypted_secret = private_key.private_decrypt(encryptedsecret)

  aes = OpenSSL::Cipher.new('AES-128-CBC')
  aes.decrypt
  aes.key = decrypted_secret
  aes.iv = decrypted_iv
  @plain = aes.update(encryptedmessage) + aes.final
  if $SLI_DEBUG
    puts("Decrypted iv type is #{decrypted_iv.class}")
    puts("Cipher is #{aes}")
    puts("Plain text length is #{@plain.length}")
    puts "length #{@res.body.length}" if @res != nil
  end
  @plain   #return @plain
end

def untar(filePath)
  puts "Untarring #{@filePath}"
  `tar -xf #{@filePath} -C #{@fileDir}`
end

def parentEdorgCheck(entity, entityId, leaId)
  found = false
  # Note: You need a mongo cursor to the correct DB
  collection = @db[entity]
  edorg = collection.find("_id" => entityId)
  edorg.each do |row|
    if row['body']['parentEducationAgencyReference'] == leaId
      found = true
      puts "The parent LEA of edOrg #{entityId} is #{leaId}"
    end
  end
end

def checkMongoCounts(collection, count)
  @db = @conn["sli"]
  collection = @db[collection]
  assert(collection.count == count, "Found #{collection.count} bulkExtract mongo entries, expected #{count}")
end

def checkMongoQueryCounts(collection, query, count)
  @db = @conn["sli"]
  collection = @db[collection]
  match = collection.find(query)
  assert(match != nil, "No BE record found in db")
  assert(match.count == count, "Found #{match.count} bulkExtract mongo entries, expected #{count}")
end

def checkTarfileCounts(directory, count)
  entries = Dir.entries(directory)
  # loop thru files in directory and incr when we see a *.tar file
  tarfile_count = 0
  entries.each do |file|
    tarfile_count += 1 if file.match(/.tar$/)
  end
  assert(count == tarfile_count, "Found #{tarfile_count} tarfiles, expected #{count}")
end

def openDecryptedFile(appId, filePath=@filePath, encryptFilePath=@encryptFilePath)
  file = File.open(encryptFilePath, 'rb') { |f| f.read}
  decryptFile(file, $APP_CONVERSION_MAP[appId])
  FileUtils.mkdir_p(File.dirname(filePath)) if !File.exists?(File.dirname(filePath))
  File.open(filePath, 'w') {|f| f.write(@plain) }
end

def to_map(json)
  map = {}
  json.each { |e|
    map[e['id']] ||= []
    map[e['id']] << e
  }
  map
end

def get_field_value(json_entity, field)
  return nil if json_entity.nil?
  field_list = field.split(".").map {|s| s.strip}
  entity = json_entity
  field_list.each { |f|
    if (entity.is_a? Array)
        entity = entity.sort()[0]
    end
    entity = entity[f]
  }
  entity.to_s.strip
end

def streamBulkExtractFile(download_file, apiBody)
  download_file ||= Dir.pwd + "/Final.tar"
  f = File.open(download_file, 'a') {|f| f.write(apiBody)}
  return download_file
end

def get_post_body_by_entity_name(entity_name)
  json_bodies_by_name = {
    "newAssessment" => {
      "nomenclature" => "Nomenclature",
      "identificationCode" => "2013-First grade Assessment 1.OA-1 Sub",
      "percentOfAssessment" => 50,
      "assessmentItemRefs" => [
        "8e47092935b521fb6aba9fdec94a4f961f04cd45_id45d9971816ec629ae4f5317639f6ad67629f8e6c_id",
        "8e47092935b521fb6aba9fdec94a4f961f04cd45_id3d8a30344f8180e851802ea2e29039eca200fbac_id"
      ],
      "assessmentId" => "8e47092935b521fb6aba9fdec94a4f961f04cd45_id",
      "assessmentPerformanceLevel" => [
        {
          "performanceLevelDescriptor" => [
            {"codeValue" => "code1"},
          ],
          "assessmentReportingMethod" => "Number score",
          "minimumScore" => 0,
          "maximumScore" => 50
        }
      ],
      "learningObjectives" => [
        "18f258460004b33fa9c1249b8c9ed3bd33c41645_id",
        "c19a0f38f598ba8d6d8b7968efd1861f754dcc04_id",
        "43ebe40d85cb70c4dc00ed94ee9f68cfae0c5d1a_id",
        "ff84d3d1c6594847234ab13f8cc8bcd2a45bb75d_id",
        "8f8b1ff4fd3459d3ab1bc54c9deb3581820e0bac_id"
      ],
      "maxRawScore" => 50
    },
    "newEducationOrganization" => {
      "organizationCategories" => ["School"],
      "stateOrganizationId" => "SomeUniqueSchoolDistrict-2422883",
      "nameOfInstitution" => "Gotham City School District",
      "address" => [
                "streetNumberName" => "222 Ave D",
                "city" => "Chicago",
                "stateAbbreviation" => "IL",
                "postalCode" => "10098",
                "nameOfCounty" => "Hooray"
                ],
      "parentEducationAgencyReference" => "1b223f577827204a1c7e9c851dba06bea6b031fe_id"
    },
    "invalidEducationOrganization" => {
      "organizationCategories" => ["School"],
      "educationOrgIdentificationCode" => [
          {
            "identificationSystem" => "School",
            "ID" => "Daybreak Podunk High"
          }],
      "stateOrganizationId" => "SchoolInAnInvalidDistrict",
      "nameOfInstitution" => "Donkey School Wrong District",
      "address" => [
                "streetNumberName" => "999 Ave FAIL",
                "city" => "Chicago",
                "stateAbbreviation" => "IL",
                "postalCode" => "10098",
                "nameOfCounty" => "Whoami"
                ],
      "parentEducationAgencyReference" => "ffffffffffffffffffffffffffffffffffffffff_id"
    },
    "newStaff" => {
      "loginId" => "new-staff-1@fakemail.com",
      "otherName" => [{
          "middleName" => "Groban",
          "generationCodeSuffix" => "II",
          "lastSurname" => "Tome",
          "personalTitlePrefix" => "Mrs",
          "firstName" => "Marisa",
          "otherNameType" => "Nickname"
      }],
      "sex" => "Female",
      "staffUniqueStateId" => "new-staff-1",
      "hispanicLatinoEthnicity" => false,
      "oldEthnicity" => "Black, Not Of Hispanic Origin",
      "yearsOfPriorTeachingExperience" => 12,
      "entityType" => "staff",
      "race" => ["White"],
      "yearsOfPriorProfessionalExperience" => 2,
      "address" => [{
          "streetNumberName" => "411 Pesci Ct",
          "postalCode" => "60601",
          "stateAbbreviation" => "IL",
          "addressType" => "Home",
          "city" => "Chicago"
      }],
      "name" => {
          "middleName" => "Cheryl",
          "lastSurname" => "Thome",
          "firstName" => "Marissa"
      },
      "electronicMail" => [{
          "emailAddress" => "new-staff-1@fakemail.com",
          "emailAddressType" => "Home/Personal"
      }],
      "highestLevelOfEducationCompleted" => "Bachelor's",
      "credentials" => [{
          "credentialField" => [{
              "description" => "Mathematics"
          }],
          "level" => "All Level (Grade Level PK-12)",
          "teachingCredentialBasis" => "5-year bachelor's degree",
          "teachingCredentialType" => "Master",
          "credentialType" => "Endorsement",
          "credentialExpirationDate" => "2017-06-24",
          "credentialIssuanceDate" => "2000-09-22"
      }],
      "birthDate" => "1972-01-18",
      "telephone" => [{
          "primaryTelephoneNumberIndicator" => true,
          "telephoneNumber" => "(060)555-3642",
          "telephoneNumberType" => "Unlisted"
      }],
      "staffIdentificationCode" => [{
          "identificationSystem" => "Health Record",
          "ID" => "17502"
      }]
    },
    "newStaffDaybreakAssociation" => { 
      "staffClassification" => "LEA Administrative Support Staff",
      "educationOrganizationReference" => "1b223f577827204a1c7e9c851dba06bea6b031fe_id",
      "positionTitle" => "IT Administrator",
      "staffReference" => "e9f3401e0a034e20bb17663dd7d18ece6c4166b5_id",
      "endDate" => "2014-05-22",
      "entityType" => "staffEducationOrganizationAssociation",
      "beginDate" => "2013-08-28"
    },
    "newStaffHighwindAssociation" => { 
      "staffClassification" => "LEA Administrative Support Staff",
      "educationOrganizationReference" => "99d527622dcb51c465c515c0636d17e085302d5e_id",
      "positionTitle" => "IT Administrator",
      "staffReference" => "e9f3401e0a034e20bb17663dd7d18ece6c4166b5_id",
      "endDate" => "2014-05-22",
      "entityType" => "staffEducationOrganizationAssociation",
      "beginDate" => "2013-08-28"
    },
    "newTeacherEdorgAssociation" => {
      "staffClassification" => "Teacher",
      "educationOrganizationReference" => "a13489364c2eb015c219172d561c62350f0453f3_id",
      "positionTitle" => "IT Administrator",
      "staffReference" => "2472b775b1607b66941d9fb6177863f144c5ceae_id",
      "endDate" => "2014-05-22",
      "entityType" => "staffEducationOrganizationAssociation",
      "beginDate" => "2013-08-26"
    },
    "newTeacher" => {
      "loginId" => "new-teacher-1@fakemail.com",
      "otherName" => [{
          "middleName" => "Geraldo",
          "generationCodeSuffix" => "II",
          "lastSurname" => "Robbespierre",
          "personalTitlePrefix" => "Mr",
          "firstName" => "Marc",
          "otherNameType" => "Other Name"
      }],
      "sex" => "Male",
      "staffUniqueStateId" => "new-teacher-1",
      "hispanicLatinoEthnicity" => false,
      "highlyQualifiedTeacher" => true,
      "oldEthnicity" => "Black, Not Of Hispanic Origin",
      "yearsOfPriorTeachingExperience" => 9,
      "entityType" => "teacher",
      "race" => ["Black - African American"],
      "yearsOfPriorProfessionalExperience" => 10,
      "address" => [{
          "streetNumberName" => "10 South Street",
          "postalCode" => "60601",
          "stateAbbreviation" => "IL",
          "addressType" => "Home",
          "city" => "Chicago"
      }],
      "teacherUniqueStateId" => "new-teacher-1",
      "name" => {
          "middleName" => "Mervin",
          "lastSurname" => "Maroni",
          "firstName" => "Marcos"
      },
      "electronicMail" => [{
          "emailAddress" => "new-teacher-1@fakemail.com",
          "emailAddressType" => "Home/Personal"
      }],
      "highestLevelOfEducationCompleted" => "No Degree",
      "credentials" => [{
          "credentialField" => [{
              "description" => "Physics"
          }],
          "level" => "Elementary (Grade Level PK-5)",
          "teachingCredentialBasis" => "Met state testing requirement",
          "teachingCredentialType" => "Provisional",
          "credentialType" => "Registration",
          "credentialExpirationDate" => "2017-10-27",
          "credentialIssuanceDate" => "2007-07-02"
      }],
      "birthDate" => "1962-09-30",
      "telephone" => [{
          "primaryTelephoneNumberIndicator" => true,
          "telephoneNumber" => "(319)555-1789",
          "telephoneNumberType" => "Emergency 2"
      }],
      "staffIdentificationCode" => [{
          "identificationSystem" => "Health Record",
          "ID" => "18511"
      }]
    },
    "newTeacherSchoolAssociation" => {
      "academicSubjects" => ["Transportation, Distribution and Logistics"],
      "schoolId" => "a13489364c2eb015c219172d561c62350f0453f3_id",
      "entityType" => "teacherSchoolAssociation",
      "programAssignment" => "Regular Education",
      "teacherId" => "2472b775b1607b66941d9fb6177863f144c5ceae_id",
      "instructionalGradeLevels" => ["Adult Education"]
    },
    "newParentMother" => {
      "entityType" => "parent",
      "parentUniqueStateId" => "new-mom-1",
      "loginId" => "new-mom@bazinga.org",
      "sex" => "Female",
      "telephone" => [],
      "address" => [{
        "streetNumberName" => "5440 Bazinga Win St.",
        "postalCode" => "60601",
        "stateAbbreviation" => "IL",
        "addressType" => "Home",
        "city" => "Chicago"
      }],
      "electronicMail" => [{
        "emailAddress" => "new-mom@bazinga.org",
        "emailAddressType" => "Home/Personal"
      }],
      "name" => {
       "middleName" => "Capistrano",
       "lastSurname" => "Samsonite",
       "firstName" => "Mary"
      },
    },
    "newStudentMotherAssociation" => {
      "entityType" => "studentParentAssociation",
      "parentId" => "41edbb6cbe522b73fa8ab70590a5ffba1bbd51a3_id",
      "studentId" => "9bf3036428c40861238fdc820568fde53e658d88_id",
      "relation" => "Mother",
      "contactPriority" => 3
    },
    "newParentFather" => {
      "entityType" => "parent",
      "parentUniqueStateId" => "new-dad-1",
      "loginId" => "new-dad@bazinga.org",
      "sex" => "Male",
      "telephone" => [],
      "address" => [{
        "streetNumberName" => "5440 Bazinga Win St.",
        "postalCode" => "60601",
        "stateAbbreviation" => "IL",
        "addressType" => "Home",
        "city" => "Chicago"
      }],
      "electronicMail" => [{
        "emailAddress" => "new-mom@bazinga.org",
        "emailAddressType" => "Home/Personal"
      }],
      "name" => {
       "middleName" => "Badonkadonk",
       "lastSurname" => "Samsonite",
       "firstName" => "Keith"
      },
    },
    "newStudentFatherAssociation" => {
      "entityType" => "studentParentAssociation",
      "parentId" => "41f42690a7c8eb5b99637fade00fc72f599dab07_id",
      "studentId" => "9bf3036428c40861238fdc820568fde53e658d88_id",
      "relation" => "Father",
      "contactPriority" => 3
    },
    "newDaybreakStudent" => {
      "loginId" => "new-student-min@bazinga.org",
      "sex" => "Male",
      "entityType" => "student",
      "race" => ["White"],
      "languages" => [{
          "language" => "English"
      }],
      "studentUniqueStateId" => "nsmin-1",
      "profileThumbnail" => "1201 thumb",
      "name" => {
          "middleName" => "Robot",
          "lastSurname" => "Samsonite",
          "firstName" => "Sammy"
      },
      "address" => [{
          "streetNumberName" => "1024 Byte Street",
          "postalCode" => "60601",
          "stateAbbreviation" => "IL",
          "addressType" => "Home",
          "city" => "Chicago"
      }],
      "birthData" => {
          "birthDate" => "1998-10-22"
      }
    },
    "newHighwindStudent" => {
      "loginId" => "new-hw-student1@bazinga.org",
      "sex" => "Female",
      "entityType" => "student",
      "race" => ["White"],
      "languages" => [{
          "language" => "English"
      }],
      "studentUniqueStateId" => "hwmin-1",
      "profileThumbnail" => "1301 thumb",
      "name" => {
          "middleName" => "Beth",
          "lastSurname" => "Markham",
          "firstName" => "Caroline"
      },
      "address" => [{
          "streetNumberName" => "128 Bit Road",
          "postalCode" => "60611",
          "stateAbbreviation" => "IL",
          "addressType" => "Home",
          "city" => "Chicago"
      }],
      "birthData" => {
          "birthDate" => "1998-02-12"
      }
    },
    "HwStudentSchoolAssociation" => {
      "exitWithdrawDate" => "2014-05-22",
      "entityType" => "studentSchoolAssociation",
      "entryDate" => "2013-08-27",
      "entryGradeLevel" => "Third grade",
      "schoolYear" => "2013-2014",
      "educationalPlans" => [],
      "schoolChoiceTransfer" => false,
      "entryType" => "Other",
      "studentId" => "b8b0a8d439591b9e073e8f1115ff1cf1fd4125d6_id",
      "repeatGradeIndicator" => false,
      "schoolId" => "1b5de2516221069fd8f690349ef0cc1cffbb6dca_id",
    },
    "DbStudentSchoolAssociation" => {
      "exitWithdrawDate" => "2014-05-22",
      "entityType" => "studentSchoolAssociation",
      "entryDate" => "2013-08-27",
      "entryGradeLevel" => "Eleventh grade",
      "schoolYear" => "2013-2014",
      "educationalPlans" => [],
      "schoolChoiceTransfer" => true,
      "entryType" => "Other",
      "studentId" => "9bf3036428c40861238fdc820568fde53e658d88_id",
      "repeatGradeIndicator" => true,
      "schoolId" => "a13489364c2eb015c219172d561c62350f0453f3_id",
    },
    "newCourseOffering" => {
      "schoolId" => "a13489364c2eb015c219172d561c62350f0453f3_id",
      "sessionId" => "bfeaf9315f04797a41dbf1663d18ead6b6fb1309_id",
      "courseId" => "06ccb498c620fdab155a6d70bcc4123b021fa60d_id",
      "localCourseCode" => "101 English",
      "localCourseTitle" => "Eleventh grade English"
    },
    "newSection" => {
      "uniqueSectionCode" => "English00101",
      "sequenceOfCourse" => 1,
      "educationalEnvironment" => "Classroom",
      "mediumOfInstruction" => "Face-to-face instruction",
      "populationServed" => "Regular Students",
      "schoolId" => "a13489364c2eb015c219172d561c62350f0453f3_id",
      "sessionId" => "bfeaf9315f04797a41dbf1663d18ead6b6fb1309_id",
      "courseOfferingId" => "38edd8479722ccf576313b4640708212841a5406_id"
    },
    "newStudentSectionAssociation" => {
      "entityType" => "studentSectionAssociation",
      "sectionId" => "4030207003b03d055bba0b5019b31046164eff4e_id",
      "studentId" => "9bf3036428c40861238fdc820568fde53e658d88_id",
      "beginDate" => "2013-08-27",
      "homeroomIndicator" => true,
      "repeatIdentifier" => "Repeated, counted in grade point average"
    },
    "newGradebookEntry" => {
      "gradingPeriodId" => "21b8ac38bf886e78a879cfdb973a9352f64d07b9_id",
      "sectionId" => "4030207003b03d055bba0b5019b31046164eff4e_id",
      "dateAssigned" => "2014-02-21",
      "description" => "Gradebook Entry of type: Homework, assigned on: 2014-02-21",
      "gradebookEntryType" => "Homework",
      "entityType" => "gradebookEntry",
      "learningObjectives" => ["f8323e42a3438c198f7d7b41336512b74155f3af_id",
                             "d469f0079144395720985c432f6bd9475c5f5a28_id",
                             "12ebed0aa9b9e0fc406278fb8184a9569dd71600_id",
                             "ea27f2c3cd548cf82682a75e29182462da366912_id",
                             "5b1d4e75f457644b1bd00f7ef05caafa605adaec_id"]
    },
    "newStudentAssessment" => {
      "studentId" => "9bf3036428c40861238fdc820568fde53e658d88_id",
      "assessmentId" => "8e6fceafe05daef1da589a1709ee278ba51d337a_id",
      "administrationDate" => "2013-09-24",
      "specialAccommodations" => ["Large Print"],
      "administrationEndDate" => "2013-09-25",
      "gradeLevelWhenAssessed" => "Eleventh grade",
      "performanceLevelDescriptors" => [
        [{
          "codeValue" => "30 code"
        }]
      ],
      "administrationEnvironment" => "Classroom",
      "retestIndicator" => "Primary Administration",
      "studentObjectiveAssessments" => [{
        "entityType" => "studentAssessment",
        "performanceLevelDescriptors" => [
          [{
            "codeValue" => "code1"
          }]
        ],
        "scoreResults" => [{
          "result" => "32",
          "assessmentReportingMethod" => "Scale score"
        }],
        "objectiveAssessment" => {
          "nomenclature" => "Nomenclature",
          "identificationCode" => "2013-Eleventh grade Assessment 2.OA-0",
          "percentOfAssessment" => 50,
          "assessmentId" => "8e6fceafe05daef1da589a1709ee278ba51d337a_id",
          "assessmentPerformanceLevel" => [{
            "performanceLevelDescriptor" => [{
              "codeValue" => "code1"
            }],
            "assessmentReportingMethod" => "Number score",
            "minimumScore" => 0,
            "maximumScore" => 50
          }],
          "learningObjectives" => [
            "1b0d13e233ef61ffafb613a8cc6930dfc0d29b92_id",
            "8b6407c747e3de04c8e8365b1aa202f1dc3510c6_id",
            "ea27f2c3cd548cf82682a75e29182462da366912_id",
            "b2c4add05d75ba5144203d8dc3e1c5cb79b58c7b_id",
            "f515c869a5b8507f7462dafd65c20710fc300182_id"
          ],
          "maxRawScore" => 50
        }
      }],
      "reasonNotTested" => "Not appropriate (ARD decision)",
      "serialNumber" => "30 code",
      "scoreResults" => [{
        "result" => "32",
        "assessmentReportingMethod" => "Scale score"
      }],
      "linguisticAccommodations" => ["Bilingual Dictionary"],
      "administrationLanguage" => {
         "language" => "English"
      },
      "studentAssessmentItems" => [{
        "rawScoreResult" => 82,
        "responseIndicator" => "Effective response",
        "assessmentResponse" => "false",
        "assessmentItemResult" => "Incorrect",
        "assessmentItem" => {
          "identificationCode" => "2013-Eleventh grade Assessment 2#1",
          "assessmentId" => "8e6fceafe05daef1da589a1709ee278ba51d337a_id",
          "correctResponse" => "true",
          "itemCategory" => "True-False",
          "maxRawScore" => 10
        }
      }, {
        "rawScoreResult" => 29,
        "responseIndicator" => "Nonscorable response",
        "assessmentResponse" => "false",
        "assessmentItemResult" => "Correct",
        "assessmentItem" => {
            "identificationCode" => "2013-Eleventh grade Assessment 2#4",
            "assessmentId" => "8e6fceafe05daef1da589a1709ee278ba51d337a_id",
            "correctResponse" => "false",
            "itemCategory" => "True-False",
            "maxRawScore" => 10
        }
      }, {
        "rawScoreResult" => 58,
        "responseIndicator" => "Nonscorable response",
        "assessmentResponse" => "false",
        "assessmentItemResult" => "Correct",
        "assessmentItem" => {
          "identificationCode" => "2013-Eleventh grade Assessment 2#2",
          "assessmentId" => "8e6fceafe05daef1da589a1709ee278ba51d337a_id",
          "correctResponse" => "false",
          "itemCategory" => "True-False",
          "maxRawScore" => 10
        }
      },
      {
        "rawScoreResult" => 16,
        "responseIndicator" => "Ineffective response",
        "assessmentResponse" => "false",
        "assessmentItemResult" => "Incorrect",
        "assessmentItem" => {
          "identificationCode" => "2013-Eleventh grade Assessment 2#3",
          "assessmentId" => "8e6fceafe05daef1da589a1709ee278ba51d337a_id",
          "correctResponse" => "true",
          "itemCategory" => "True-False",
          "maxRawScore" => 10
        }
      }]
    },
    "newDaybreakCourse" => {
      "courseDefinedBy" => "National Organization",
      "courseDescription" => "this is a course for Sixth grade",
      "courseLevelCharacteristics" => ["Core Subject"],
      "dateCourseAdopted" => "2012-06-19",
      "highSchoolCourseRequirement" => false,
      "uniqueCourseId" => "new-science-1",
      "entityType" => "course",
      "courseCode" => [{
          "identificationSystem" => "School course code",
          "ID" => "new-science-1"
      }],
      "gradesOffered" => ["Sixth grade"],
      "maximumAvailableCredit" => {
          "credit" => 3.0
      },
      "minimumAvailableCredit" => {
          "credit" => 3.0
      },
      "subjectArea" => "Critical Reading",
      "courseLevel" => "Basic or remedial",
      "courseTitle" => "Sixth grade Science",
      "numberOfParts" => 1,
      "schoolId" => "1b223f577827204a1c7e9c851dba06bea6b031fe_id",
      "courseGPAApplicability" => "Weighted",
      "careerPathway" => "Arts, A/V Technology and Communications"
    },
    "newGrade" => {
      "schoolYear" => "2013-2014",
      "studentSectionAssociationId" => "4030207003b03d055bba0b5019b31046164eff4e_id78468628f357b29599510341f08dfd3277d9471e_id",
      "sectionId" => "4030207003b03d055bba0b5019b31046164eff4e_id",
      "letterGradeEarned" => "A",
      "studentId" => "9bf3036428c40861238fdc820568fde53e658d88_id",
      "numericGradeEarned" => 96,
      "gradeType" => "Final",
      "performanceBaseConversion" => "Advanced",
      "entityType" => "grade",
      "diagnosticStatement" => "Student has Advanced understanding of subject."
    },
    "newReportCard" => {
      "gpaGivenGradingPeriod" => 4.0,
      "numberOfDaysTardy" => 5,
      "entityType" => "reportCard",
      "studentCompetencyId" => ["0b60ada34879ae92d702b8deba8ffa4b0304bd4f_id", "a2d49222a65539f8658a53262619ccd743eadeaa_id", "85d510ed1e6a021582511f2ea3f593cc215a2f03_id", "efbac13e68205e055e0b62dcb688db655d1f1993_id", "add666959932195cb58f6bb23a04cdf9c4f33b80_id", "269a5ed956c61131644b852007c25938d5e52dbe_id", "d378378182c655ddcd807c4ea8a6f1dd9856bc54_id", "55418b178d1b94246aa85dce397c96a064d8b131_id", "a257d6fbe7da025ed044246cbd26b5a4d3e7980d_id", "97d5881972febe96ff3b8898c517b86862b846a6_id", "9b878efa5294c11cd28b34ff8b261eaf0721d1cb_id", "e3eb0b9c4d81d2d05f73fe812f1448f6b154e788_id", "18da02af03074e79c38178da6af667fb92b765f0_id", "84cac53e0dba7443a1d38296006c2298b61b3f27_id", "a98d764081246bcc505d16597e46932651f71388_id", "36c93cc301c35a053dbc527b9ff95470bf941b3c_id", "43b22d9ccd4ee38fa414cac155295b5f3a0497d7_id", "df625d78063c3a19427f31582cc01ce45e4926bc_id", "5a606626e43fd425f4c2795fa59fc558b02d9e96_id", "3d490c9268eb505c2019f393019c45c0a860f19d_id"],
      "schoolYear" => "2013-2014",
      "gradingPeriodId" => "21b8ac38bf886e78a879cfdb973a9352f64d07b9_id",
      "studentId" => "9bf3036428c40861238fdc820568fde53e658d88_id",
      "gpaCumulative" => 3.8,
      "numberOfDaysInAttendance" => 137.0,
      "grades" => ["1417cec726dc51d43172568a9c332ee1712d73d4_idcd83575df61656c7d8aebb690ae0bb3ff129a857_id"],
      "numberOfDaysAbsent" => 1.0
    },
    "newStudentAcademicRecord" => {
      "gradeValueQualifier" => "90-100%=A, 80-90%=B",
      "projectedGraduationDate" => "2013-08-18",
      "academicHonors" => [{
          "honorAwardDate" => "2000-07-28",
          "honorsDescription" => "Honor Desc BBB",
          "academicHonorsType" => "Scholarships",

      }],
      "cumulativeCreditsEarned" => {
          "credit" => 3.0
      },
      "reportCards" => ["1417cec726dc51d43172568a9c332ee1712d73d4_id77bc827b90835ef0df42154428ac3153f0ddc746_id"],
      "entityType" => "studentAcademicRecord",
      "schoolYear" => "2013-2014",
      "studentId" => "9bf3036428c40861238fdc820568fde53e658d88_id",
      "sessionId" => "bfeaf9315f04797a41dbf1663d18ead6b6fb1309_id",
      "classRanking" => {
          "classRankingDate" => "2013-10-19",
          "percentageRanking" => 99,
          "totalNumberInClass" => 8,
          "classRank" => 10
      },
      "cumulativeGradePointAverage" => 3.8,
      "recognitions" => [{
          "recognitionType" => "Other",
          "recognitionAwardDate" => "2013-10-25",
          "recognitionDescription" => "Recognition Desc BBB"
      }],
      "cumulativeGradePointsEarned" => 0.0
    },
    "newAttendanceEvent" => {
      "studentId" => "9bf3036428c40861238fdc820568fde53e658d88_id",
      "schoolId" => "a13489364c2eb015c219172d561c62350f0453f3_id",
      "entityType" => "attendance",
      "schoolYearAttendance" => [{
        "schoolYear" => "2013-2014",
        "attendanceEvent" => [{
          "reason" => "Missed school bus",
          "event" => "Tardy",
          "date" => "2013-08-30"
        }, {
          "reason" => "Excused: sick",
          "event" => "Excused Absence",
          "date" => "2013-12-19"
        }, {
          "reason" => "Missed school bus",
          "event" => "Tardy",
          "date" => "2014-05-19"
        }]
      }]
    },
    "newCalendarDate" => {
      "calendarEvent" => "Instructional day",
      "date" => "2015-04-02",
      "educationOrganizationId" => "352e8570bd1116d11a72755b987902440045d346_id"
    },
    "newCalendarDate2" => {
          "calendarEvent" => "Instructional day",
          "date" => "2015-04-02",
          "educationOrganizationId" => "772a61c687ee7ecd8e6d9ad3369f7883409f803b_id"
    },
    "newBECalendarDate" => {
           "calendarEvent" => "Instructional day",
           "date" => "2015-04-02",
           "educationOrganizationId" => "884daa27d806c2d725bc469b273d840493f84b4d_id"
    },
    "newCohort" => {
      "academicSubject" => "Communication and Audio/Visual Technology",
      "cohortType" => "Extracurricular Activity",
      "cohortScope" => "School",
      "educationOrgId" => "a13489364c2eb015c219172d561c62350f0453f3_id",
      "entityType" => "cohort",
      "cohortDescription" => "New Cohort 1 at Edorg Daybreak Central High",
      "cohortIdentifier" => "new-cohort-1"
    },
    "newStaffCohortAssociation" => {
      "staffId" => "2472b775b1607b66941d9fb6177863f144c5ceae_id",
      "cohortId" => "cb99a7df36fadf8885b62003c442add9504b3cbd_id",
      "beginDate" => "2013-01-15",
      "endDate" => "2014-03-29",
      "studentRecordAccess" => true
    },
    "newStudentCohortAssociation" => {
      "studentId" => "9bf3036428c40861238fdc820568fde53e658d88_id",
      "cohortId" => "cb99a7df36fadf8885b62003c442add9504b3cbd_id",
      "beginDate" => "2013-01-25",
      "endDate" => "2014-03-29"
    },
    "DbGradingPeriod" => {
      "endDate" => "2015-05-29",
      "gradingPeriodIdentity" => {
          "schoolYear" => "2014-2015",
          "gradingPeriod" => "End of Year",
          "schoolId" => "1b223f577827204a1c7e9c851dba06bea6b031fe_id"
      },
      "entityType" => "gradingPeriod",
      "beginDate" => "2014-09-02",
      "totalInstructionalDays" => 180
    },
    "DbSession" => {
      "schoolYear" => "2014-2015",
      "sessionName" => "2014-2015 Year Round session: IL-DAYBREAK",
      "term" => "Year Round",
      "gradingPeriodReference" => ["1dae9e8450e2e77dd0b06dee3fd928c1bfda4d49_id"],
      "endDate" => "2015-05-29",
      "schoolId" => "1b223f577827204a1c7e9c851dba06bea6b031fe_id",
      "entityType" => "session",
      "beginDate" => "2014-09-02",
      "totalInstructionalDays" => 180
    },
    "newProgram" => {
      "services" => [
          [{"codeValue" => "srv:136"}]
      ],
      "programId" => "12345",
      "programSponsor" => "State Education Agency",
      "entityType" => "program",
      "programType" => "Regular Education"
    },
    "newStudentProgramAssociation" => {
      "services" => [
        [{"description" => "Reading Intervention"}]
      ],
      "programId" => "0ee2b448980b720b722706ec29a1492d95560798_id",
      "studentId" => "9bf3036428c40861238fdc820568fde53e658d88_id",
      "endDate" => "2014-05-22",
      "reasonExited" => "Reached maximum age",
      "entityType" => "studentProgramAssociation",
      "beginDate" => "2013-08-26",
      "educationOrganizationId" => "1b223f577827204a1c7e9c851dba06bea6b031fe_id"
    },
    "newStaffProgramAssociation" => {

    },
    "newStudentCompetency" => {
      "competencyLevel" => { "codeValue" => "Barely Competent" },
      "objectiveId" => { "learningObjectiveId" => "c9b6e99895327de1b01f29ced552f6a3515d8455_id" },
      "diagnosticStatement" => "passed with flying colors",
      "studentSectionAssociationId" => "4030207003b03d055bba0b5019b31046164eff4e_id78468628f357b29599510341f08dfd3277d9471e_id",
    },
    "newDisciplineIncident" => {

    },
    "newDisciplineAction" => {

    },
    "newStudentDiscIncidentAssoc" => {

    },
    "newSession" => {
      "schoolYear" => "2014-2015",
      "sessionName" => "New SEA session",
      "term" => "Year Round",
      "gradingPeriodReference" => ["1dae9e8450e2e77dd0b06dee3fd928c1bfda4d49_id"],
      "endDate" => "2015-06-11",
      "schoolId" => "884daa27d806c2d725bc469b273d840493f84b4d_id",
      "beginDate" => "2014-08-12",
      "totalInstructionalDays" => 180
    },
    "newGradingPeriod" => {
      "endDate"=>"2014-05-22",
      "gradingPeriodIdentity"=>{
          "schoolYear"=>"2013-2014",
          "gradingPeriod"=>"End of Year",
          "schoolId"=>"884daa27d806c2d725bc469b273d840493f84b4d_id"
      },
      "entityType"=>"gradingPeriod",
      "beginDate"=>"2013-07-20",
      "totalInstructionalDays"=>180
    },
    "newLearningObjective" => {
      "objectiveGradeLevel" => "Fourth grade",
      "objective" => "New Generic Learning Objective 1",
      "academicSubject" => "Writing",
      "description" => "Description"
    },
    "newLearningStandard" => {
      "subjectArea" => "Science",
      "courseTitle" => "Science",
      "contentStandard" => "State Standard",
      "description" => "Description",
      "learningStandardId" => { "identificationCode" => "NEW-LS-GK-1" },
      "gradeLevel" => "Kindergarten"
    },
    "newCompetencyLevelDescriptor" => {
      "description" => "Description",
      "codeValue" => "NEW-CLD-CodeValue",
      "performanceBaseConversion" => "Advanced"
    },
    "newStudentCompetencyObjective" => {
      "objectiveGradeLevel" => "Kindergarten",
      "objective" => "Phonemic Awareness",
      "studentCompetencyObjectiveId" => "NEW-JS-K-1",
      "description" => "Description",
      "educationOrganizationId" => "884daa27d806c2d725bc469b273d840493f84b4d_id"
    },
    "newSEACourse" => {
      "courseDescription" => "new SEA course",
      "uniqueCourseId" => "new-sea-1",
      "courseCode" => [{
          "identificationSystem" => "School course code",
          "ID" => "new-science-1"
      }],
      "courseTitle" => "Sixth grade Science",
      "numberOfParts" => 1,
      "schoolId" => "884daa27d806c2d725bc469b273d840493f84b4d_id"
    },
    "newSEACourseOffering" => {
      "schoolId" => "884daa27d806c2d725bc469b273d840493f84b4d_id",
      "sessionId" => "bfeaf9315f04797a41dbf1663d18ead6b6fb1309_id",
      "courseId" => "877e4934a96612529535581d2e0f909c5288131a_id",
      "localCourseCode" => "101 English",
      "localCourseTitle" => "New SEA English"
    },
    "newAssessment" => {
      "assessmentIdentificationCode" => [
          { "identificationSystem" => "State", "ID" => "New Assessment 1" }
      ],
      "assessmentTitle" => "New Assessment Title 1",
      "gradeLevelAssessed" => "Eighth grade",
      "academicSubject" => "English",
      "version" => 2,
      "contentStandard" => "State Standard"
    },
    "newGraduationPlan" => {
      "creditsBySubject" => [{
          "subjectArea" => "English",
          "credits" => {
              "creditConversion" => 0,
              "creditType" => "Semester hour credit",
              "credit" => 6
           }
      }],
      "individualPlan" => false,
      "graduationPlanType" => "Recommended",
      "educationOrganizationId" => "884daa27d806c2d725bc469b273d840493f84b4d_id",
      "totalCreditsRequired" => {
          "creditConversion" => 0,
          "creditType" => "Semester hour credit",
          "credit" => 32
      }
    },
    "expiredStaff" => {
      "staffUniqueStateId" => "ex-staff-member1",
      "sex" => "Male",
      "hispanicLatinoEthnicity" => false,
      "highestLevelOfEducationCompleted" => "Bachelor's",
      "name" => {
        "firstName" => "Exavier",
        "middleName" => "D.",
        "lastSurname" => "StaffGuy"
      }
    },
    "expiredStaffEdorgAssociation" => {
      "staffClassification" => "IT Administrator",
      "educationOrganizationReference" => "772a61c687ee7ecd8e6d9ad3369f7883409f803b_id",
      "positionTitle" => "Educator",
      "staffReference" => "bfddb715a20bb2996b8769abfc1813d029bfdf29_id",
      "endDate" => "2013-05-22",
      "entityType" => "staffEducationOrganizationAssociation",
      "beginDate" => "2012-08-26"
    },
    "expiredTeacher" => {
      "loginId" => "expired-teacher-1@fakemail.com",
      "sex" => "Male",
      "staffUniqueStateId" => "exp-teacher-1",
      "hispanicLatinoEthnicity" => false,
      "highlyQualifiedTeacher" => true,
      "yearsOfPriorTeachingExperience" => 9,
      "entityType" => "teacher",
      "yearsOfPriorProfessionalExperience" => 10,
      "address" => [{
          "streetNumberName" => "10 South Street",
          "postalCode" => "60601",
          "stateAbbreviation" => "IL",
          "addressType" => "Home",
          "city" => "Chicago"
      }],
      "teacherUniqueStateId" => "expired-teacher-1",
      "name" => {
          "middleName" => "Mervin",
          "lastSurname" => "Maroni",
          "firstName" => "Marcos"
      },
      "electronicMail" => [
        { "emailAddress" => "expired-teacher-work@fakemail.com",
          "emailAddressType" => "Work"
        },
        {
          "emailAddress" => "expired-teacher-home@fakemail.com",
          "emailAddressType" => "Home/Personal"
        }
      ],
      "highestLevelOfEducationCompleted" => "Master's",
      "birthDate" => "1962-09-30",
      "telephone" => [{
          "primaryTelephoneNumberIndicator" => true,
          "telephoneNumber" => "(319)555-1789",
          "telephoneNumberType" => "Emergency 2"
      }],
      "staffIdentificationCode" => [{
          "identificationSystem" => "Health Record",
          "ID" => "82421"
      }]
    },
    "expiredTeacherEdorgAssociation" => {
      "staffClassification" => "Teacher",
      "educationOrganizationReference" => "772a61c687ee7ecd8e6d9ad3369f7883409f803b_id",
      "positionTitle" => "Educator",
      "staffReference" => "2ff51e81ecbd9c4160a19be629d0ccb4cb529796_id",
      "endDate" => "2013-05-22",
      "entityType" => "staffEducationOrganizationAssociation",
      "beginDate" => "2012-08-26"
    },
    "expiredTeacherSchoolAssociation" => {
      "academicSubjects" => ["Transportation, Distribution and Logistics"],
      "schoolId" => "772a61c687ee7ecd8e6d9ad3369f7883409f803b_id",
      "entityType" => "teacherSchoolAssociation",
      "programAssignment" => "Regular Education",
      "teacherId" => "2ff51e81ecbd9c4160a19be629d0ccb4cb529796_id",
      "instructionalGradeLevels" => ["Adult Education"]
    },
    "expiredTeacherSectionAssociation" => {
      "teacherId" => "2ff51e81ecbd9c4160a19be629d0ccb4cb529796_id",
      "sectionId" => "eb8663fe6856b49684a778446a0a1ad33238a86d_id",
      "classroomPosition" => "Teacher of Record",
      "endDate" => "2013-05-22",
      "beginDate" => "2012-08-26"
    },
    "expiredStudent" => {
      "loginId" => "expired-student-1@bazinga.org",
      "sex" => "Male",
      "entityType" => "student",
      "race" => ["White"],
      "languages" => [{
          "language" => "English"
      }],
      "studentUniqueStateId" => "expired-student-1",
      "profileThumbnail" => "expired-student-1 thumb",
      "name" => {
          "middleName" => "Tera",
          "lastSurname" => "Byte",
          "firstName" => "Jim"
      },
      "address" => [{
          "streetNumberName" => "2048 Byte Street",
          "postalCode" => "60601",
          "stateAbbreviation" => "IL",
          "addressType" => "Home",
          "city" => "Chicago"
      }],
      "birthData" => {
          "birthDate" => "1997-03-02"
      }
    },
    "expiredStudentSchoolAssociation" => {
      "exitWithdrawDate" => "2013-05-22",
      "entityType" => "studentSchoolAssociation",
      "entryDate" => "2012-08-27",
      "entryGradeLevel" => "Seventh grade",
      "schoolYear" => "2012-2013",
      "educationalPlans" => [],
      "schoolChoiceTransfer" => true,
      "entryType" => "Other",
      "studentId" => "b13887c5f555d6675d1f71de3b0fa6ad3b67f8aa_id",
      "repeatGradeIndicator" => true,
      "schoolId" => "772a61c687ee7ecd8e6d9ad3369f7883409f803b_id"
    },
    "expiredStudentSectionAssociation" => {
      "entityType" => "studentSectionAssociation",
      "sectionId" => "eb8663fe6856b49684a778446a0a1ad33238a86d_id",
      "studentId" => "b13887c5f555d6675d1f71de3b0fa6ad3b67f8aa_id",
      "endDate" => "2012-12-13",
      "beginDate" => "2012-08-27",
      "homeroomIndicator" => true,
      "repeatIdentifier" => "Repeated, counted in grade point average"
    },
    "jstevenson.staffProgramAssociation" => {
      "programId" => "904888b5ed47e46e2be7f3536a581e044fb18835_id",
      "staffId" => "dfec28d34c75a4d307d1e85579e26a81630f6a47_id",
      "endDate" => "2015-06-04",
      "beginDate" => "2012-09-09"
    },
    "msollars.studentProgramAssociation" => {
      "services" => [
        [{"description" => "Reading Intervention"}]
      ],
      "programId" => "904888b5ed47e46e2be7f3536a581e044fb18835_id",
      "studentId" => "067198fd6da91e1aa8d67e28e850f224d6851713_id",
      "endDate" => "2014-06-22",
      "reasonExited" => "Reached maximum age",
      "entityType" => "studentProgramAssociation",
      "beginDate" => "2013-05-26",
      "educationOrganizationId" => "772a61c687ee7ecd8e6d9ad3369f7883409f803b_id"
    },
    "expiredStudentProgramAssociation" => {
      "services" => [
        [{"description" => "Reading Intervention"}]
      ],
      "programId" => "904888b5ed47e46e2be7f3536a581e044fb18835_id",
      "studentId" => "b13887c5f555d6675d1f71de3b0fa6ad3b67f8aa_id",
      "endDate" => "2013-06-22",
      "reasonExited" => "Reached maximum age",
      "entityType" => "studentProgramAssociation",
      "beginDate" => "2013-05-26",
      "educationOrganizationId" => "772a61c687ee7ecd8e6d9ad3369f7883409f803b_id"
    },
    "expiredStudentCohortAssociation" => {
      "studentId" => "b13887c5f555d6675d1f71de3b0fa6ad3b67f8aa_id",
      "cohortId" => "b4f9ddccc4c5c47a00541ee7c6d67fcb287316ce_id",
      "beginDate" => "2012-01-25",
      "endDate" => "2013-03-29"
    },
    "msollars.studentCompetency" => {
      "competencyLevel" => { "codeValue" => "Advanced" },
      "objectiveId" => { "learningObjectiveId" => "c9b6e99895327de1b01f29ced552f6a3515d8455_id" },
      "diagnosticStatement" => "passed with flying colors",
      "studentSectionAssociationId" => "eb8663fe6856b49684a778446a0a1ad33238a86d_id9abdc5ad23afda9fca17a667c1af0f472000f2cb_id",
    },
    "msollars.studentAcademicRecord" => {
      "gradeValueQualifier" => "90-100%=A, 80-90%=B",
      "projectedGraduationDate" => "2013-08-18",
      "academicHonors" => [{
          "honorAwardDate" => "2000-07-28",
          "honorsDescription" => "Honor Desc BBB",
          "academicHonorsType" => "Scholarships",

      }],
      "cumulativeCreditsEarned" => {
          "credit" => 3.0
      },
      "reportCards" => ["1417cec726dc51d43172568a9c332ee1712d73d4_id77bc827b90835ef0df42154428ac3153f0ddc746_id"],
      "entityType" => "studentAcademicRecord",
      "schoolYear" => "2013-2014",
      "studentId" => "067198fd6da91e1aa8d67e28e850f224d6851713_id",
      "sessionId" => "bfeaf9315f04797a41dbf1663d18ead6b6fb1309_id",
      "classRanking" => {
          "classRankingDate" => "2013-10-19",
          "percentageRanking" => 99,
          "totalNumberInClass" => 8,
          "classRank" => 10
      },
      "cumulativeGradePointAverage" => 3.8,
      "recognitions" => [{
          "recognitionType" => "Other",
          "recognitionAwardDate" => "2013-10-25",
          "recognitionDescription" => "Recognition Desc BBB"
      }],
      "cumulativeGradePointsEarned" => 0.0
    },
    "msollars.grade" => {
      "schoolYear" => "2012-2013",
      "studentSectionAssociationId" => "2982f5d3840b0a46bf152c7b7243c0db8dda694f_id06f4aa0f6d84ae7ab290709fc348754cbd232cb5_id",
      "sectionId" => "2982f5d3840b0a46bf152c7b7243c0db8dda694f_id",
      "letterGradeEarned" => "A",
      "studentId" => "067198fd6da91e1aa8d67e28e850f224d6851713_id",
      "numericGradeEarned" => 96,
      "gradeType" => "Final",
      "performanceBaseConversion" => "Advanced",
      "entityType" => "grade",
      "diagnosticStatement" => "Student has Advanced understanding of subject."
    },
    "msollars.reportCard" => {
      "gpaGivenGradingPeriod" => 4.0,
      "numberOfDaysTardy" => 5,
      "entityType" => "reportCard",
      "studentCompetencyId" => ["0b60ada34879ae92d702b8deba8ffa4b0304bd4f_id", "a2d49222a65539f8658a53262619ccd743eadeaa_id", "85d510ed1e6a021582511f2ea3f593cc215a2f03_id", "efbac13e68205e055e0b62dcb688db655d1f1993_id", "add666959932195cb58f6bb23a04cdf9c4f33b80_id", "269a5ed956c61131644b852007c25938d5e52dbe_id", "d378378182c655ddcd807c4ea8a6f1dd9856bc54_id", "55418b178d1b94246aa85dce397c96a064d8b131_id", "a257d6fbe7da025ed044246cbd26b5a4d3e7980d_id", "97d5881972febe96ff3b8898c517b86862b846a6_id", "9b878efa5294c11cd28b34ff8b261eaf0721d1cb_id", "e3eb0b9c4d81d2d05f73fe812f1448f6b154e788_id", "18da02af03074e79c38178da6af667fb92b765f0_id", "84cac53e0dba7443a1d38296006c2298b61b3f27_id", "a98d764081246bcc505d16597e46932651f71388_id", "36c93cc301c35a053dbc527b9ff95470bf941b3c_id", "43b22d9ccd4ee38fa414cac155295b5f3a0497d7_id", "df625d78063c3a19427f31582cc01ce45e4926bc_id", "5a606626e43fd425f4c2795fa59fc558b02d9e96_id", "3d490c9268eb505c2019f393019c45c0a860f19d_id"],
      "schoolYear" => "2013-2014",
      "gradingPeriodId" => "21b8ac38bf886e78a879cfdb973a9352f64d07b9_id",
      "studentId" => "067198fd6da91e1aa8d67e28e850f224d6851713_id",
      "gpaCumulative" => 3.8,
      "numberOfDaysInAttendance" => 137.0,
      "grades" => ["1417cec726dc51d43172568a9c332ee1712d73d4_idcd83575df61656c7d8aebb690ae0bb3ff129a857_id"],
      "numberOfDaysAbsent" => 1.0
    },
    "msollars.studentGradebookEntry" => {
      "studentSectionAssociationId" => "2982f5d3840b0a46bf152c7b7243c0db8dda694f_id06f4aa0f6d84ae7ab290709fc348754cbd232cb5_id",
      "gradebookEntryId" => "eb8663fe6856b49684a778446a0a1ad33238a86d_id1e0f700db933cda9dc1adf5f04d1204d2a9c2ddf_id",
      "letterGradeEarned" => "F",
      "sectionId" => "2982f5d3840b0a46bf152c7b7243c0db8dda694f_id",
      "studentId" => "067198fd6da91e1aa8d67e28e850f224d6851713_id",
      "numericGradeEarned" => 59,
      "dateFulfilled" => "2013-04-25",
      "diagnosticStatement" => "Diagnostic Statement"
    },
    "msollars.student" => {
      "loginId" => "4859@fakemail.com",
      "sex" => "Female",
      "studentCharacteristics" => [{
        "endDate" => "2014-08-01",
        "beginDate" => "20013-04-20",
        "designatedBy" => "Teacher",
        "characteristic" => "Unaccompanied Youth"
      }],
      "oldEthnicity" => "White, Not Of Hispanic Origin",
      "race" => ["Black - African American"],
      "languages" => [{
        "language" => "Norwegian"
      }],
      "schoolFoodServicesEligibility" => "Full price",
      "studentUniqueStateId" => "800000025",
      "name" => {
        "middleName" => "Aida",
        "lastSurname" => "Sollars",
        "firstName" => "Matt"
      },
      "birthData" => {
        "birthDate" => "2002-07-21"
      },
      "homeLanguages" => [{
        "language" => "Cambodian (Khmer)"
      }],
      "learningStyles" => {
        "visualLearning" => 36,
        "auditoryLearning" => 39,
        "tactileLearning" => 12
      },
      "limitedEnglishProficiency" => "NotLimited",
      "studentIdentificationCode" => [{
        "identificationCode" => "abcde",
        "identificationSystem" => "District",
        "assigningOrganizationCode" => "School"
      }],
      "address" => [{
        "streetNumberName" => "707 Elm Street",
        "postalCode" => "60601",
        "stateAbbreviation" => "IL",
        "addressType" => "Home",
        "city" => "Chicago"
      }],
      "displacementStatus" => "Status BBB",
      "electronicMail" => [{
        "emailAddress" => "4859@fakemail.com",
        "emailAddressType" => "Home/Personal"
      }],
      "telephone" => [{
        "telephoneNumber" => "(115)555-5072"
      }]
  },
    "msollars.studentAssessment" => {
      "studentId" => "067198fd6da91e1aa8d67e28e850f224d6851713_id",
      "assessmentId" => "8e6fceafe05daef1da589a1709ee278ba51d337a_id",
      "administrationDate" => "2013-09-24",
      "specialAccommodations" => ["Large Print"],
      "administrationEndDate" => "2012-09-25",
      "gradeLevelWhenAssessed" => "Eleventh grade",
      "performanceLevelDescriptors" => [
        [{
          "codeValue" => "30 code"
        }]
      ],
      "administrationEnvironment" => "Classroom",
      "retestIndicator" => "Primary Administration",
      "studentObjectiveAssessments" => [{
        "entityType" => "studentAssessment",
        "performanceLevelDescriptors" => [
          [{
            "codeValue" => "code1"
          }]
        ],
        "scoreResults" => [{
          "result" => "32",
          "assessmentReportingMethod" => "Scale score"
        }],
        "objectiveAssessment" => {
          "nomenclature" => "Nomenclature",
          "identificationCode" => "2013-Eleventh grade Assessment 2.OA-0",
          "percentOfAssessment" => 50,
          "assessmentId" => "8e6fceafe05daef1da589a1709ee278ba51d337a_id",
          "assessmentPerformanceLevel" => [{
            "performanceLevelDescriptor" => [{
              "codeValue" => "code1"
            }],
            "assessmentReportingMethod" => "Number score",
            "minimumScore" => 0,
            "maximumScore" => 50
          }],
          "learningObjectives" => [
            "1b0d13e233ef61ffafb613a8cc6930dfc0d29b92_id",
            "8b6407c747e3de04c8e8365b1aa202f1dc3510c6_id",
            "ea27f2c3cd548cf82682a75e29182462da366912_id",
            "b2c4add05d75ba5144203d8dc3e1c5cb79b58c7b_id",
            "f515c869a5b8507f7462dafd65c20710fc300182_id"
          ],
          "maxRawScore" => 50
        }
      }],
      "reasonNotTested" => "Not appropriate (ARD decision)",
      "serialNumber" => "30 code",
      "scoreResults" => [{
        "result" => "32",
        "assessmentReportingMethod" => "Scale score"
      }],
      "linguisticAccommodations" => ["Bilingual Dictionary"],
      "administrationLanguage" => {
         "language" => "English"
      },
      "studentAssessmentItems" => [{
        "rawScoreResult" => 82,
        "responseIndicator" => "Effective response",
        "assessmentResponse" => "false",
        "assessmentItemResult" => "Incorrect",
        "assessmentItem" => {
          "identificationCode" => "2013-Eleventh grade Assessment 2#1",
          "assessmentId" => "8e6fceafe05daef1da589a1709ee278ba51d337a_id",
          "correctResponse" => "true",
          "itemCategory" => "True-False",
          "maxRawScore" => 10
        }
      }, {
        "rawScoreResult" => 29,
        "responseIndicator" => "Nonscorable response",
        "assessmentResponse" => "false",
        "assessmentItemResult" => "Correct",
        "assessmentItem" => {
            "identificationCode" => "2013-Eleventh grade Assessment 2#4",
            "assessmentId" => "8e6fceafe05daef1da589a1709ee278ba51d337a_id",
            "correctResponse" => "false",
            "itemCategory" => "True-False",
            "maxRawScore" => 10
        }
      }, {
        "rawScoreResult" => 58,
        "responseIndicator" => "Nonscorable response",
        "assessmentResponse" => "false",
        "assessmentItemResult" => "Correct",
        "assessmentItem" => {
          "identificationCode" => "2013-Eleventh grade Assessment 2#2",
          "assessmentId" => "8e6fceafe05daef1da589a1709ee278ba51d337a_id",
          "correctResponse" => "false",
          "itemCategory" => "True-False",
          "maxRawScore" => 10
        }
      },
      {
        "rawScoreResult" => 16,
        "responseIndicator" => "Ineffective response",
        "assessmentResponse" => "false",
        "assessmentItemResult" => "Incorrect",
        "assessmentItem" => {
          "identificationCode" => "2013-Eleventh grade Assessment 2#3",
          "assessmentId" => "8e6fceafe05daef1da589a1709ee278ba51d337a_id",
          "correctResponse" => "true",
          "itemCategory" => "True-False",
          "maxRawScore" => 10
        }
      }]
    },
    "cgray.studentAssessment" => {
      "studentId" => "fdd8ee3ee44133f489e47d2cae109e886b041382_id",
      "assessmentId" => "8e6fceafe05daef1da589a1709ee278ba51d337a_id",
      "administrationDate" => "2013-09-24",
      "specialAccommodations" => ["Large Print"],
      "administrationEndDate" => "2013-09-25",
      "gradeLevelWhenAssessed" => "Eleventh grade",
      "performanceLevelDescriptors" => [
        [{
          "codeValue" => "30 code"
        }]
      ],
      "administrationEnvironment" => "Classroom",
      "retestIndicator" => "Primary Administration",
      "studentObjectiveAssessments" => [{
        "entityType" => "studentAssessment",
        "performanceLevelDescriptors" => [
          [{
            "codeValue" => "code1"
          }]
        ],
        "scoreResults" => [{
          "result" => "32",
          "assessmentReportingMethod" => "Scale score"
        }],
        "objectiveAssessment" => {
          "nomenclature" => "Nomenclature",
          "identificationCode" => "2013-Eleventh grade Assessment 2.OA-0",
          "percentOfAssessment" => 50,
          "assessmentId" => "8e6fceafe05daef1da589a1709ee278ba51d337a_id",
          "assessmentPerformanceLevel" => [{
            "performanceLevelDescriptor" => [{
              "codeValue" => "code1"
            }],
            "assessmentReportingMethod" => "Number score",
            "minimumScore" => 0,
            "maximumScore" => 50
          }],
          "learningObjectives" => [
            "1b0d13e233ef61ffafb613a8cc6930dfc0d29b92_id",
            "8b6407c747e3de04c8e8365b1aa202f1dc3510c6_id",
            "ea27f2c3cd548cf82682a75e29182462da366912_id",
            "b2c4add05d75ba5144203d8dc3e1c5cb79b58c7b_id",
            "f515c869a5b8507f7462dafd65c20710fc300182_id"
          ],
          "maxRawScore" => 50
        }
      }],
      "reasonNotTested" => "Not appropriate (ARD decision)",
      "serialNumber" => "30 code",
      "scoreResults" => [{
        "result" => "32",
        "assessmentReportingMethod" => "Scale score"
      }],
      "linguisticAccommodations" => ["Bilingual Dictionary"],
      "administrationLanguage" => {
         "language" => "English"
      },
      "studentAssessmentItems" => [{
        "rawScoreResult" => 82,
        "responseIndicator" => "Effective response",
        "assessmentResponse" => "false",
        "assessmentItemResult" => "Incorrect",
        "assessmentItem" => {
          "identificationCode" => "2013-Eleventh grade Assessment 2#1",
          "assessmentId" => "8e6fceafe05daef1da589a1709ee278ba51d337a_id",
          "correctResponse" => "true",
          "itemCategory" => "True-False",
          "maxRawScore" => 10
        }
      }]
    },
    "cgray.grade" => {
      "schoolYear" => "2012-2013",
      "studentSectionAssociationId" => "95ea4c7b7ad507cf6947c82114ff56de92ffda78_id7f04bc9f666bb9a3ec08c232e239d14e7856929b_id",
      "sectionId" => "95ea4c7b7ad507cf6947c82114ff56de92ffda78_id",
      "letterGradeEarned" => "A",
      "studentId" => "fdd8ee3ee44133f489e47d2cae109e886b041382_id",
      "numericGradeEarned" => 96,
      "gradeType" => "Final",
      "performanceBaseConversion" => "Advanced",
      "entityType" => "grade",
      "diagnosticStatement" => "Student has Advanced understanding of subject."
    },
    "cgray.studentGradebookEntry" => {
      "studentSectionAssociationId" => "95ea4c7b7ad507cf6947c82114ff56de92ffda78_id7f04bc9f666bb9a3ec08c232e239d14e7856929b_id",
      "gradebookEntryId" => "d0bf8bb1e3418c8c7578a89403d6ffea5cb9c1a6_id58a523f121d74d87d45c9a26a686decdbce622ef_id",
      "letterGradeEarned" => "A",
      "sectionId" => "95ea4c7b7ad507cf6947c82114ff56de92ffda78_id",
      "studentId" => "fdd8ee3ee44133f489e47d2cae109e886b041382_id",
      "numericGradeEarned" => 99,
      "dateFulfilled" => "2013-04-25",
      "diagnosticStatement" => "Diagnostic Statement"
    },
    "cgray.parent" => {
      "entityType" => "parent",
      "parentUniqueStateId" => "cgray",
      "loginId" => "cgray@bazinga.org",
      "sex" => "Male",
      "telephone" => [
        {"primaryTelephoneNumberIndicator" => true,
         "telephoneNumber" => "(666)555-1776",
         "telephoneNumberType" => "Emergency 2"
        },
        {"primaryTelephoneNumberIndicator" => false,
         "telephoneNumber" => "(666)555-1777",
         "telephoneNumberType" => "Mobile"
        }
      ],
      "address" => [{
        "streetNumberName" => "256 Charles Gray Has His Own Street St.",
        "postalCode" => "66666",
        "stateAbbreviation" => "IL",
        "addressType" => "Home",
        "city" => "Chicago"
      }],
      "electronicMail" => [
        {"emailAddress" => "9468@fakemail.com",
         "emailAddressType" => "Home/Personal"
        },
        {"emailAddress" => "cgray@Midgar.edu",
         "emailAddressType" => "Work"
        }
      ],
      "name" => {
       "middleName" => "Ivan",
       "lastSurname" => "Gray",
       "firstName" => "Charles"
      },
    },
    "cgray.parent.notMe" => {
      "entityType" => "parent",
      "parentUniqueStateId" => "cgray",
      "loginId" => "cgray@bazinga.org",
      "sex" => "Male",
      "telephone" => [
        {"primaryTelephoneNumberIndicator" => true,
         "telephoneNumber" => "(666)555-1776",
         "telephoneNumberType" => "Emergency 2"
        },
        {"primaryTelephoneNumberIndicator" => false,
         "telephoneNumber" => "(666)555-1777",
         "telephoneNumberType" => "Mobile"
        }
      ],
      "address" => [{
        "streetNumberName" => "256 Charles Gray Has His Own Street St.",
        "postalCode" => "66666",
        "stateAbbreviation" => "IL",
        "addressType" => "Home",
        "city" => "Chicago"
      }],
      "electronicMail" => [
        {"emailAddress" => "9468@fakemail.com",
         "emailAddressType" => "Home/Personal"
        },
        {"emailAddress" => "cgray@Midgar.edu",
         "emailAddressType" => "Work"
        }
      ],
      "name" => {
       "middleName" => "Ivan",
       "lastSurname" => "Gray",
       "firstName" => "Charles"
      },
    },
    "cgray.studentParentAssociation.myClass" => {
      "entityType" => "studentParentAssociation",
      "parentId" => "1fe86fe9c45680234f1caa3b494a1c4b42838954_id",
      "studentId" => "fdd8ee3ee44133f489e47d2cae109e886b041382_id",
      "relation" => "Father",
      "contactPriority" => 1
    },
    "cgray.studentSchoolAssociation.myClass" => {
      "exitWithdrawDate" => "2014-05-22",
      "entityType" => "studentSchoolAssociation",
      "entryDate" => "2013-08-27",
      "entryGradeLevel" => "Ninth grade",
      "schoolYear" => "2013-2014",
      "educationalPlans" => [],
      "schoolChoiceTransfer" => false,
      "entryType" => "Other",
      "studentId" => "fdd8ee3ee44133f489e47d2cae109e886b041382_id",
      "repeatGradeIndicator" => false,
      "schoolId" => "f43e124e966084ce15bdba9b4e9befc92adf09ea_id",
    },
    "cgray.studentSectionAssociation.myClass" => {
      "entityType" => "studentSectionAssociation",
      "sectionId" => "cee6195d1c5e2605bea2f3c34d264442c78638d2_id",
      "studentId" => "fdd8ee3ee44133f489e47d2cae109e886b041382_id",
      "beginDate" => "2013-08-27",
      "homeroomIndicator" => true,
      "repeatIdentifier" => "Repeated, counted in grade point average"
    },
    "cgray.studentParentAssociation.notMyKid" => {
      "entityType" => "studentParentAssociation",
      "parentId" => "1fe86fe9c45680234f1caa3b494a1c4b42838954_id",
      "studentId" => "75a1710a115cd94dde09ccd950a11a05b7843ab2_id",
      "relation" => "Father",
      "contactPriority" => 2
    },
    "cgray.studentParentAssociation.mySchool" => {
      "entityType" => "studentParentAssociation",
      "parentId" => "1fe86fe9c45680234f1caa3b494a1c4b42838954_id",
      "studentId" => "6b41180a6ba41031f50f3b50c97ef5f9387666c3_id",
      "relation" => "Father",
      "contactPriority" => 3
    },
    "cgray.studentParentAssociation.newLea" => {
      "entityType" => "studentParentAssociation",
      "parentId" => "1fe86fe9c45680234f1caa3b494a1c4b42838954_id",
      "studentId" => "f07bc57c18f13e8bb692660a7fab0ca92817598c_id",
      "relation" => "Father",
      "contactPriority" => 1
    },
  }
  return json_bodies_by_name[entity_name]
end

def prepareBody(verb, value, response_map)
  field_data = {
    "GET" => response_map,
    "POST" => {
    }
  }
  return field_data
end

def remove_edorg_from_mongo(edorg_id, tenant)
  tenant_db = @conn.db(convertTenantIdToDbName(tenant))
  collection = tenant_db.collection('educationOrganization')
  collection.remove({'body.stateOrganizationId' => edorg_id})
end

def cleanDir(directory)
  puts "download_path is #{directory}"
  `ls -al #{directory}`
end

def build_bulk_query(tenant, appId, edOrg=nil, delta=false, publicData=false)
  query = {"body.tenantId"=>tenant, "body.applicationId" => appId, "body.isDelta" => delta, "body.isPublicData" => publicData}
  query.merge!({"body.edorg"=>edOrg}) unless edOrg.nil?
  query
end

def createCleanupFile(baseDir, tenant, edorg, app, date)
  date = Time.parse(date)
  date.gmtime
  dateString = date.strftime("%Y-%m-%d-%H-%M-%S")
  puts dateString
  return baseDir + "/" + tenant + "/" + edorg + "/" + edorg + "-" + app + "-" + dateString + ".tar"
end

def addFakeBEEntry(tenant, edorg, app, isDelta, isPublic, date, path)
  edorg_id = getEdorgId(tenant, edorg)
  query = {"type" => "bulkExtractEntity", "body" =>{"tenantId"=>tenant, "edorg" => edorg_id,
           "applicationId" => app, "isDelta" => isDelta, "isPublicData" => isPublic,
           "path" => path, "date" => Time.iso8601(date)}}
  sliDB = @conn.db("sli")
  coll = sliDB.collection('bulkExtractFiles')
  coll.insert(query)

end

def addTestEdorg(tenant, edorg)
  tenantDB = @conn.db(convertTenantIdToDbName(tenant))
  edorg_id = getEdorgId(tenant, edorg)
  edorgQuery = {"type" => "localEducationAgency", "_id"=>edorg_id, "body" => {"stateOrganizationId" => edorg}}
  edorgColl = tenantDB.collection("educationOrganization")
  edorgColl.insert(edorgQuery)
end

def getEdorgId(tenant, edorg)
  return tenant + "-" + edorg
end

def get_json_from_file(file_name)
    json_file_name = @unpackDir + "/#{file_name}.json"
    exists = File.exists?(json_file_name)
    unless exists
      exists = File.exists?(json_file_name+".gz")
      assert(exists, "Cannot find #{file_name}.json.gz file in extracts")
      `gunzip -c #{json_file_name}.gz > #{json_file_name}`
    end
    json = JSON.parse(File.read("#{json_file_name}"))
    json
end

############################################################
# After Hooks
############################################################

After do
  @conn.close if @conn != nil
end

