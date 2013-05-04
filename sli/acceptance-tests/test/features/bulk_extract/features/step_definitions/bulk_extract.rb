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
require 'zip/zip'
require 'archive/tar/minitar'
require 'zlib'
require 'open3'
require 'openssl'
require 'time'
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
ENCRYPTED_ENTITIES = ['student', 'parent']
COMBINED_ENTITIES = ['assessment', 'studentAssessment']
COMBINED_SUB_ENTITIES = ['assessmentItem','objectiveAssessment','studentAssessmentItems','studentObjectiveAssessments']

ENCRYPTED_FIELDS = ['loginId', 'studentIdentificationCode','otherName','sex','address','electronicMail','name','telephone','birthData']
MUTLI_ENTITY_COLLS = ['staff', 'educationOrganization']

$APP_CONVERSION_MAP = {"19cca28d-7357-4044-8df9-caad4b1c8ee4" => "vavedra9ub"}

############################################################
# Transform
############################################################

Transform /^<(.*?)>$/ do |human_readable_id|
  # entity id transforms
  id = "19cca28d-7357-4044-8df9-caad4b1c8ee4"               if human_readable_id == "app id"
  id = "vavedRa9uB"                                         if human_readable_id == "client id"
  id = "1b223f577827204a1c7e9c851dba06bea6b031fe_id"        if human_readable_id == "IL-DAYBREAK"
  id = "54b4b51377cd941675958e6e81dce69df801bfe8_id"        if human_readable_id == "ed_org_to_lea2_id"
  id = "880572db916fa468fbee53a68918227e104c10f5_id"        if human_readable_id == "lea2_id"
  id = "1b223f577827204a1c7e9c851dba06bea6b031fe_id"        if human_readable_id == "lea1_id"
  id = "884daa27d806c2d725bc469b273d840493f84b4d_id"        if human_readable_id == "sea_id"
  id = "352e8570bd1116d11a72755b987902440045d346_id"        if human_readable_id == "IL-DAYBREAK school"
  id = "a96ce0a91830333ce68e235a6ad4dc26b414eb9e_id"        if human_readable_id == "Orphaned School"

  id
end

############################################################
# Scheduler
############################################################
Given /^the current crontab is empty$/ do
    command = "crontab -l"
    result = runShellCommand(command)
    puts "Running: #{command} #{result}"
    command = "crontab -r"
    result = runShellCommand(command)
    puts "Running: #{command} #{result}"
    assert(result.length==0, "current crontab is not empty but #{result}")
end

Given /^the local bulk extract script path and the scheduling config path$/ do
    assert(Dir.exists?(TRIGGER_SCRIPT_DIRECTORY), "Bulk Extract script directory #{TRIGGER_SCRIPT_DIRECTORY} does not exist")
    @current_dir = Dir.pwd
    is_jenkins = @current_dir.include?"jenkins"
    puts "pwd: #{@current_dir}"
    @trigger_script_path = TRIGGER_SCRIPT_DIRECTORY
    @scheduling_config_path = File.dirname(__FILE__) + '/../../test_data/config/'

    if !is_jenkins
        @scheduling_config_path = File.dirname(__FILE__) + '/../../test_data/local/'
    end

    assert(Dir.exists?(@scheduling_config_path), "Bulk Extract scheduling config directory #{@scheduling_config_path} does not exist")

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

Then /^the "(.*?)" has the correct number of SEA public data records$/ do |entity|
  disable_NOTABLESCAN()
	@tenantDb = @conn.db(convertTenantIdToDbName(@tenant))
  SEA = @tenantDB.collection("educationOrganization").find({"body.organizationCategories" => "State Education Agency"})
  @SEA_id = SEA["_id"]

  puts "Comparing SEA " + SEA_id

  query_field = getSEAPublicRefField(entity)
  collection = entity
  if (entity == "school")
    collection = "educationOrganization"
  end

  count = @tenantDb.collection(collection).find({query_field => SEA_id}).count()
	Zlib::GzipReader.open(@unpackDir + "/" + entity + ".json.gz") { |extractFile|
    records = JSON.parse(extractFile.read)
    puts records
    puts "\nCounts Expected: " + count.to_s + " Actual: " + records.size.to_s + "\n"
    assert(records.size == count,"Counts off Expected: " + count.to_s + " Actual: " + records.size.to_s)
  }
end

Then /^Then I verify that the "(.*?)" reference an SEA only$/ do |entity| 
  count = @tenantDb.collection(collection).find({query_field => SEA_id}).count()
  Zlib::GzipReader.open(@unpackDir + "/" + entity + ".json.gz") { |extractFile|
    records = JSON.parse(extractFile.read)
    puts "\nCounts Expected: " + count.to_s + " Actual: " + records.size.to_s + "\n"
    assert(records.size == count,"Counts off Expected: " + count.to_s + " Actual: " + records.size.to_s)
  }
end

And /^I clean up the cron extraction zone$/ do
    Dir.chdir
    puts "pwd: #{Dir.pwd}"
    if (Dir.exists?(CRON_OUTPUT_DIRECTORY))
        FileUtils.rm_rf CRON_OUTPUT_DIRECTORY
    end
    assert(!Dir.exists?(CRON_OUTPUT_DIRECTORY), "cron output directory #{CRON_OUTPUT_DIRECTORY} does exist")
    puts "CRON_OUTPUT_DIRECTORY: #{CRON_OUTPUT_DIRECTORY}"
    Dir.chdir(@current_dir)
end

Then /^I run the bulk extract scheduler script$/ do
    command  = "echo 'y' | #{SCHEDULER_SCRIPT} #{@trigger_script_path} #{@scheduling_config_path}"
    result = runShellCommand(command)
    puts "Running: #{command} #{result}"
    raise "Result of bulk extract scheduler script should include Installed new crontab but was #{result}" if !result.include?"Installed new crontab"
    command = "crontab -l"
    result = runShellCommand(command)
    Dir.chdir
    puts "pwd: #{Dir.pwd}"
end

When /^I am willing to wait upto (\d+) seconds for the bulk extract scheduler cron job to start and complete$/ do |limit|
    @maxTimeout = limit.to_i
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

And /^I clear crontab$/ do
    command = "crontab -r"
    result = runShellCommand(command)
    puts "Running: #{command} #{result}"
    assert(result.length==0, "current crontab is not empty but #{result}")
end


############################################################
# Given
############################################################
Given /^I trigger a bulk extract$/ do
  bulkExtractTrigger(TRIGGER_SCRIPT, JAR_FILE, PROPERTIES_FILE, KEYSTORE_FILE)
end

Given /^I trigger a delta extract$/ do
  options = " -d"
  bulkExtractTrigger(TRIGGER_SCRIPT, JAR_FILE, PROPERTIES_FILE, KEYSTORE_FILE, options)
end

Given /^the extraction zone is empty$/ do
  if (Dir.exists?(OUTPUT_DIRECTORY))
    puts OUTPUT_DIRECTORY
    FileUtils.rm_rf("#{OUTPUT_DIRECTORY}/.", secure: true)
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

############################################################
# When
############################################################

When /^I get the path to the extract file for the tenant "(.*?)" and application with id "(.*?)"$/ do |tenant, appId|
  getExtractInfoFromMongo(build_bulk_query(tenant,appId))
end

When /^I retrieve the path to and decrypt the SEA public data extract file for the tenant "(.*?)" and application with id "(.*?)"$/ do |tenant, appId|
  getExtractInfoFromMongo(build_bulk_query(tenant,appId, nil, false, true))
  openDecryptedFile(appId) 
end

When /^I know the file-length of the extract file$/ do
  @file_size = File.size(@filePath)
end

When /^I retrieve the path to and decrypt the extract file for the tenant "(.*?)" and application with id "(.*?)"$/ do |tenant, appId|
  getExtractInfoFromMongo(build_bulk_query(tenant,appId))
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
  exists = File.exists?(@unpackDir + "/" + collection + ".json.gz")
	assert(exists, "Cannot find #{collection}.json file in extracts")

end

When /^a the correct number of "(.*?)" was extracted from the database$/ do |collection|
  disable_NOTABLESCAN()
	@tenantDb = @conn.db(convertTenantIdToDbName(@tenant))

	case collection
	when "school"
	  count = @tenantDb.collection("educationOrganization").find({"type" => "school" } ).count()
	when "teacher"
	  count = @tenantDb.collection("staff").find({"type" => "teacher" } ).count()
	else
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

When /^I log into "(.*?)" with a token of "(.*?)", a "(.*?)" for "(.*?)" in tenant "(.*?)", that lasts for "(.*?)" seconds/ do |client_appName, user, role, realm, tenant, expiration_in_seconds|

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
  out, status = Open3.capture2("ruby #{script_loc} -e #{expiration_in_seconds} -c #{client_id} -u #{user} -r \"#{role}\" -t \"#{tenant}\" -R \"#{realm}\"")
  match = /token is (.*)/.match(out)
  @sessionId = match[1]
  puts "The generated token is #{@sessionId}"
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
 options = " -tNoTenantForYou"
 bulkExtractTrigger(TRIGGER_SCRIPT, JAR_FILE, PROPERTIES_FILE, KEYSTORE_FILE, options)
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
end

When /^I POST a "(.*?)" of type "(.*?)"$/ do |field, entity|
  response_map = getEntityBodyFromApi(entity, @api_version, "PUT")
  response_map, value = nil
  # POST is a special case. We are creating a brand-new entity. 
  # Get entity body from the map specified by prepareBody()
  body = prepareBody("POST", value, response_map)
  # Get the endpoint that corresponds to the desired entity
  endpoint = getEntityEndpoint(entity)
  restHttpPost("/#{@api_version}/#{endpoint}", prepareData(@format, body["POST"][field]))
  assert(@res != nil, "Response from rest-client POST is nil")
end

When /^I PUT the "(.*?)" for a "(.*?)" entity to "(.*?)"$/ do |field, entity, value|
  # Get the desired entity from mongo
  response_map = getEntityBodyFromApi(entity, @api_version, "PUT")
  # Modify the response body field with value, will become PUT body 
  put_body = updateApiPutField(response_map, field, value)
  # Get the endpoint that corresponds to the desired entity
  endpoint = getEntityEndpoint(entity)
  restHttpPut("/#{@api_version}/#{endpoint}/#{put_body["id"]}", prepareData(@format, put_body))
  assert(@res != nil, "Response from rest-client PUT is nil")
end

def updateApiPutField(body, field, value)
  # Set the GET response body as body and edit the requested field
  body["address"][0]["postalCode"] = value if field == "postalCode"
  body["loginId"] = value if field == "loginId"
  body["id"] = value if field == "missingEntity"
  return body
end

When /^I PATCH the "(.*?)" for a "(.*?)" entity to "(.*?)"$/ do |field, entity, value|
  # Get the desired entity from mongo, we will only use the _id
  response_map = getEntityBodyFromApi(entity, @api_version, "PATCH")
  # We will set the PATCH body to ONLY the field_values map we get from prepareBody()
  patch_body = prepareBody("PATCH", value, response_map)
  # Get the endpoint that corresponds to the desired entity
  endpoint = getEntityEndpoint(entity)
  restHttpPatch("/#{@api_version}/#{endpoint}/#{patch_body["GET"]["id"]}", prepareData(@format, patch_body["PATCH"][field]))
  assert(@res != nil, "Response from rest-client PATCH is nil")
end

When /^I DELETE an "(.*?)" of id "(.*?)"$/ do |entity, id|
  # Get the endpoint that corresponds to the desired entity
  endpoint = getEntityEndpoint(entity)
  restHttpDelete("/#{@api_version}/#{endpoint}/#{id}")
end

When /^I request latest delta via API for tenant "(.*?)", lea "(.*?)" with appId "(.*?)" clientId "(.*?)"$/ do |tenant, lea, app_id, client_id|
  @lea = lea
  @app_id = app_id
  @client_id = client_id

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
  restTls("/bulk/extract/#{lea}/delta/#{@timestamp}", nil, 'application/x-tar')
end

When /^I download and decrypt the delta$/ do
  # Open the file, decrypt, and check against API
  download_path = streamBulkExtractFile(@download_path, @res.body)
  @decrypt_path = OUTPUT_DIRECTORY + "decrypt/" + @delta_file
  openDecryptedFile(@app_id, @decrypt_path, @download_path)
  untar(@decrypt_path)
end

When /^I generate and retrieve the bulk extract delta via API for "(.*?)"$/ do |lea|
  step "I trigger a delta extract"
  step "I log into \"SDK Sample\" with a token of \"jstevenson\", a \"Noldor\" for \"IL-Daybreak\" in tenant \"Midgar\", that lasts for \"300\" seconds"
  step "I request latest delta via API for tenant \"Midgar\", lea \"#{lea}\" with appId \"<app id>\" clientId \"<client id>\""
  step "I should receive a return code of 200"
  step "I download and decrypt the delta"
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
  puts "stubbed out"
end

Then /^I should see "(.*?)" entities of type "(.*?)" in the bulk extract tarfile$/ do |count, collection|
  count = count.to_i
  puts "stubbed out"
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

Then /^I verify "(.*?)" delta bulk extract files are generated for LEA "(.*?)" in "(.*?)"$/ do |count, lea, tenant|
  count = count.to_i 
  @conn ||= Mongo::Connection.new(DATABASE_HOST, DATABASE_PORT)
  @sliDb ||= @conn.db(DATABASE_NAME)
  @coll = @sliDb.collection("bulkExtractFiles")
  query = {"body.tenantId"=>tenant, "body.isDelta"=>true, "body.edorg"=>lea}
  assert(count == @coll.count({query: query})) 
end

Then /^I verify the last delta bulk extract by app "(.*?)" for "(.*?)" in "(.*?)" contains a file for each of the following entities:$/ do |appId, lea, tenant, table| 
    opts = {sort: ["body.date", Mongo::DESCENDING], limit: 1}
    getExtractInfoFromMongo(build_bulk_query(tenant, appId, lea, true), opts)
    openDecryptedFile(appId) 
    
    step "the extract contains a file for each of the following entities:", table

end

Then /^I verify this "(.*?)" file (should|should not) contains:$/ do |file_name, should, table|
    look_for = should.downcase == "should"
    json_file_name = @unpackDir + "/#{file_name}.json"
    exists = File.exists?(json_file_name)
    unless exists
      exists = File.exists?(json_file_name+".gz") 
      assert(exists, "Cannot find #{file_name}.json.gz file in extracts")
      `gunzip -c #{json_file_name}.gz > #{json_file_name}`
    end
    json = JSON.parse(File.read("#{json_file_name}"))

    json_map = to_map(json)
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
            # we may have multiple entities with the same id in the delete file
            json_value = get_field_value(e, field)
            if (json_value.to_s == value.to_s) 
                success = true
                break
            end
        }
        if (look_for)
            assert(success, "can't find an entity with id #{id} that matches #{entity['condition']}")
        else
            assert(!success, "found an entity with id #{id} that matches #{entity['condition']}, we should not have this entity")
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

Then /^the "(.*?)" has the correct number of SEA public data records$/ do |entity|
  disable_NOTABLESCAN()
	@tenantDb = @conn.db(convertTenantIdToDbName(@tenant))
  SEA = @tenantDB.collection("educationOrganization").find({"body.organizationCategories" => "State Education Agency"})
  @SEA_id = SEA["_id"]

  puts "Comparing SEA " + SEA_id

  query_field = getSEAPublicRefField(entity)
  collection = entity
  if (entity == "school")
    collection = "educationOrganization"
  end

  count = @tenantDb.collection(collection).find({query_field => SEA_id}).count()
	Zlib::GzipReader.open(@unpackDir + "/" + entity + ".json.gz") { |extractFile|
    records = JSON.parse(extractFile.read)
    puts records
    puts "\nCounts Expected: " + count.to_s + " Actual: " + records.size.to_s + "\n"
    assert(records.size == count,"Counts off Expected: " + count.to_s + " Actual: " + records.size.to_s)
  }
end

Then /^Then I verify that the "(.*?)" reference an SEA only$/ do |entity| 
  count = @tenantDb.collection(collection).find({query_field => SEA_id}).count()
  Zlib::GzipReader.open(@unpackDir + "/" + entity + ".json.gz") { |extractFile|
    records = JSON.parse(extractFile.read)
    puts "\nCounts Expected: " + count.to_s + " Actual: " + records.size.to_s + "\n"
    assert(records.size == count,"Counts off Expected: " + count.to_s + " Actual: " + records.size.to_s)
  }
end

Then /^I verify that extract does not contain a file for the following entities:$/ do |table|
  table.hashes.map do |row|
    exists = File.exists?(@unpackDir + "/" + row["entity"] + ".json.gz")
    assert(!exists, "Found " + row["entity"] + ".json file in extracts")
  end
end

############################################################
# Hooks
############################################################
After do
  @conn.close if @conn != nil
end

############################################################
# Functions
############################################################

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
  puts runShellCommand(command)
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

def compareToApi(collection, collFile)
  found = false
  uri = entityToUri(collection)
    
  collFile.each do |extractRecord|
    
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

def getEntityEndpoint(entity)
  entity_to_endpoint_map = {
    "educationOrganization" => "educationOrganizations",
    "invalidEntry" => "school",
    "orphanEdorg" => "educationOrganizations",
    "parent" => "parents",
    "patchEdOrg" => "educationOrganizations",
    "school" => "educationOrganizations",
    "wrongSchoolURI" => "schoolz"
  }
  return entity_to_endpoint_map[entity]
end

def getEntityId(entity)
  entity_to_id_map = {
    "orphanEdorg" => "54b4b51377cd941675958e6e81dce69df801bfe8_id",
    "IL-Daybreak" => "1b223f577827204a1c7e9c851dba06bea6b031fe_id",
    "District-5"  => "880572db916fa468fbee53a68918227e104c10f5_id",
    "Daybreak Central High" => "a13489364c2eb015c219172d561c62350f0453f3_id"
  }
  return entity_to_id_map[entity]
end

def getEntityBodyFromApi(entity, api_version, verb)
  return {entity=>nil} if verb == "POST"
  entity_to_uri_map = {
    "school" => "educationOrganizations/#{@edorg}/schools?limit=1",
    "educationOrganization" => "educationOrganizations",
    "courseOffering" => "courseOfferings",
    "orphanEdorg" => "educationOrganizations/54b4b51377cd941675958e6e81dce69df801bfe8_id",
    "parent" => "schools/a13489364c2eb015c219172d561c62350f0453f3_id/studentSchoolAssociations/students/studentParentAssociations/parents",
    "studentParentAssociation" => "schools/a13489364c2eb015c219172d561c62350f0453f3_id/studentSchoolAssociations/students/studentParentAssociations",
    "patchEdOrg" => "educationOrganizations/a13489364c2eb015c219172d561c62350f0453f3_id",
    "section" => "sections",
    "staffEducationOrganizationAssociation" => "staffEducationOrgAssignmentAssociations",
    "staffProgramAssociation" => "staffProgramAssociations",
    "studentCohortAssocation" => "studentCohortAssociations",
    "studentDisciplineIncidentAssociation" => "studentDisciplineIncidentAssociations",
    "studentProgramAssociation" => "studentProgramAssociations",
    "studentSectionAssociation" => "studentSectionAssociations",
    "teacherSchoolAssociation" => "teacherSchoolAssociations",
  }
  # Perform GET request and verify we get a response and a response body
  puts "Calling restHttpGet to retreive entity response body for modification."
  restHttpGet("/#{api_version}/#{entity_to_uri_map[entity]}")
  assert(@res != nil, "Response from rest-client GET is nil")
  assert(@res.body != nil, "Response body is nil")
  # Make sure we actually hit the entity
  puts "Ensuring the GET request returned 200"
  step "I should receive a return code of 200"
  puts "GET request: 200 (OK)"
  # Store the response in an entity-specific response map
  response_map = JSON.parse(@res)
  # Fail if we do not find the entity in response body from GET request
  assert(response_map != nil, "No response body for #{entity} returned by GET request")
  return response_map if verb == "DELETE"
  return response_map if verb == "PATCH"
  return response_map[0]
end

def prepareBody(verb, value, response_map)
  field_data = {
    "GET" => response_map,
    "POST" => {
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
        "studentId" => "b4da0130d823cb5c88c5b9e6eb1a5f0f32180697_id",
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
        "studentId" => "b4da0130d823cb5c88c5b9e6eb1a5f0f32180697_id",
        "relation" => "Father",
        "contactPriority" => 3
      },
    },
    "PATCH" => {
      "postalCode" => {
        "address"=>[{"postalCode"=>value,
                    "nameOfCounty"=>"Wake",
                    "streetNumberName"=>"111 Ave A",
                    "stateAbbreviation"=>"IL",
                    "addressType"=>"Physical",
                    "city"=>"Chicago"
                   }]
      },
      "studentParentName" => {
        "name" => {
          "middleName" => "Fatang",
          "lastSurname" => "ZoopBoing",
          "firstName" => "Pang"
        },
      }
    }
  }
  return field_data
end

def remove_edorg_from_mongo(edorg_id, tenant)
  tenant_db = @conn.db(convertTenantIdToDbName(tenant))
  collection = tenant_db.collection('educationOrganization')
  collection.remove({'body.stateOrganizationId' => edorg_id})
end

def build_bulk_query(tenant, appId, lea=nil, delta=false, publicData=false)
  query = {"body.tenantId"=>tenant, "body.applicationId" => appId, "body.isDelta" => delta, "body.isPublicData" => publicData}
  query.merge!({"body.edorg"=>lea}) unless lea.nil?
  query
end

def getSEAPublicRefField(entity)
  query
  case entity
  when "school","educationOrganization"
    query_field = "body.parentEducationAgencyReference"
  when "course","courseOffering", "session"#, "gradingPeriod"
      query_field = "body.schoolId"
  when "graduationPlan"
      query_field = "body.educationOrganizationId"
  end
  return query
end

After('@scheduler') do
  command = "crontab -r"
  puts "blah blah blah"
  result = runShellCommand(command)
  puts "Running: #{command} #{result}"
end
