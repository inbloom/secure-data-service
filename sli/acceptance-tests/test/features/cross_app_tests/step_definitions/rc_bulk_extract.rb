#bulk extract
TRIGGER_SCRIPT = File.expand_path(PropLoader.getProps['bulk_extract_script'])
OUTPUT_DIRECTORY = PropLoader.getProps['bulk_extract_output_directory']
PROPERTIES_FILE = PropLoader.getProps['bulk_extract_properties_file']
KEYSTORE_FILE = PropLoader.getProps['bulk_extract_keystore_file']
JAR_FILE = PropLoader.getProps['bulk_extract_jar_loc']
SSH_USER = PropLoader.getProps['ssh_user']
EXTRACT_TO_DIRECTORY = PropLoader.getProps['extract_to_directory']

require 'archive/tar/minitar'
include Archive::Tar

Given /^the extraction zone is empty$/ do
    if (Dir.exists?(OUTPUT_DIRECTORY))
      puts OUTPUT_DIRECTORY
      FileUtils.rm_rf("#{OUTPUT_DIRECTORY}/.", secure: true)
    end
end

Given /^the production extraction zone is empty$/ do
   executeShellCommand("rm -f \~/.ssh/known_hosts")
   executeShellCommand("ssh #{SSH_USER} sudo rm -rf #{OUTPUT_DIRECTORY}#{convertTenantIdToDbName(PropLoader.getProps['tenant'])}")
end

Given /^there is no bulk extract files in the local directory$/ do
    executeShellCommand("rm -f #{EXTRACT_TO_DIRECTORY}/extract.tar")
    executeShellCommand("rm -rf #{EXTRACT_TO_DIRECTORY}/unpack/")
    executeShellCommand("rm -rf #{EXTRACT_TO_DIRECTORY}/extract/")
    executeShellCommand("rm -rf #{EXTRACT_TO_DIRECTORY}/logs/")
end

def executeShellCommand(command)
    puts command
    `#{command}`
end

When /^the operator triggers a bulk extract for the production tenant$/ do
    executeShellCommand("rm -f \~/.ssh/known_hosts")
    command = getBulkExtractCommand(PropLoader.getProps['tenant'])
    executeShellCommand("ssh #{SSH_USER} sudo #{command}")
end

When /^the operator triggers a delta for the production tenant$/ do
    executeShellCommand("rm -f \~/.ssh/known_hosts")
    command = getBulkExtractCommand(PropLoader.getProps['tenant'], " -d")
    executeShellCommand("ssh #{SSH_USER} sudo #{command}")
end

When /^I store the URL for the latest delta for the (LEA|SEA)$/ do |edorg|
  edorg == 'LEA' ? delta = 'deltaEdOrgs' : delta = 'deltaSea'
  puts "result body from previous API call is #{@res}"
  @delta_uri = JSON.parse(@res)
  @list_url  = @delta_uri[delta][@lea][0]["uri"]
  # @list_irl is in the format https://<url>/api/rest/v1.3/bulk/extract/<lea>/delta/<timestamp>
  # -> strip off everything before v1.3, store: bulk/extract/<lea>/delta/<timestamp>
  @list_url.match(/api\/rest\/v(.*?)\/(.*)$/)
  puts "Bulk Extract Delta URI suffix: #{$2}"
  @list_uri = $2
  # Get the timestamp from the URL
  @list_url.match(/delta\/(.*)$/)
  @delta_file = "delta_#{@lea}_#{$1}.tar"
  # Store directory information for later retrieval
  @download_path = OUTPUT_DIRECTORY + @delta_file
  @fileDir = OUTPUT_DIRECTORY + "decrypt"
  @filePath = @fileDir + "/" + @delta_file
  @unpackDir = @fileDir
  @encryptFilePath = @download_path
end

When /^I PATCH the postalCode for the lea entity to 11999$/ do
  restHttpGet("/v1/educationOrganizations/#{@lea}/schools")
  puts "result from '/v1/educationOrganizations/#{@lea}/schools' is #{@res}"
  assert(@res.code == 200, "Response from GET '/v1/educationOrganizations/#{@lea}/schools' is #{@res.code}, expected 200")
  json = JSON.parse(@res.body)
  if json.is_a? Array
    school_id = json[0]['id']
  else
    school_id = json['id']
  end
  patch_body = {
    "address" => [{"postalCode" => "11999",
    "nameOfCounty" => "Wake",
    "streetNumberName" => "111 Ave A",
    "stateAbbreviation" => "IL",
    "addressType" => "Physical",
    "city" => "Chicago"}]
  }
  @format = "application/json"
  puts "PATCHing body #{patch_body} to /v1/educationOrganizations/#{school_id}"
  restHttpPatch("/v1/educationOrganizations/#{school_id}", prepareData(@format, patch_body), @format)
  puts @res
  assert(@res != nil, "Patch failed: Received no response from API.")
end

When /^I PATCH the endDate for the staffProgramAssociation entity to 2011-05-05$/ do
  restHttpGet("/v1/staffProgramAssociations")
  puts "result from '/v1/staffProgramAssociations' is #{@res}"
  assert(@res.code == 200, "Response from GET '/v1/staffProgramAssociations/' is #{@res.code}, expected 200")
  json = JSON.parse(@res.body)
  if json.is_a? Array
    spa_id = json[0]['id']
  else
    spa_id = json['id']
  end
  patch_body = {
      "endDate" => "2011-05-05"
  }
  @format = "application/json"
  puts "PATCHing body #{patch_body} to /v1/staffProgramAssociations/#{spa_id}"
  restHttpPatch("/v1/staffProgramAssociations/#{spa_id}", prepareData(@format, patch_body), @format)
  puts @res
  assert(@res != nil, "Patch failed: Received no response from API.")
end

When /^I PATCH the (postalCode|name) for the current edorg entity to (.*?)$/ do |field, value|
  patch_body = {
      'postalCode' => {
          'address' => [{'postalCode' => value,
                         'nameOfCounty' => 'Wake',
                         'streetNumberName' => '111 Ave A',
                         'stateAbbreviation' => 'IL',
                         'addressType' => 'Physical',
                         'city' => 'Chicago'}]},
      'name' => {
          'nameOfInstitution' => value
      }
  }
  @format = "application/json"
  puts "PATCHing body #{patch_body[field]} to /v1/educationOrganizations/#{@lea}"
  restHttpPatch("/v1/educationOrganizations/#{@lea}", prepareData(@format, patch_body[field]), @format)
  puts @res
  assert(@res != nil, "Patch failed: Received no response from API.")
end

When /^the operator triggers a bulk extract for tenant "(.*?)"$/ do |tenant|

  command  = "sh #{TRIGGER_SCRIPT}"
  if (PROPERTIES_FILE !=nil && PROPERTIES_FILE != "")
    command = command + " -Dsli.conf=#{PROPERTIES_FILE}"
    puts "Using extra property: -Dsli.conf=#{PROPERTIES_FILE}"
  end
  if (KEYSTORE_FILE !=nil && KEYSTORE_FILE != "")
    command = command + " -Dsli.encryption.keyStore=#{KEYSTORE_FILE}"
    puts "Using extra property: -Dsli.encryption.keyStore=#{KEYSTORE_FILE}"
  end
  if (JAR_FILE !=nil && JAR_FILE != "")
    command = command + " -f#{JAR_FILE}"
    puts "Using extra property:  -f#{JAR_FILE}"
  end
  
  command = command + " -t#{tenant}"
  puts "Running: #{command} "
  puts runShellCommand(command)

end

def getBulkExtractCommand(tenant, options="")
   command  = "sh #{TRIGGER_SCRIPT}"
   if (PROPERTIES_FILE !=nil && PROPERTIES_FILE != "")
     command = command + " -Dsli.conf=#{PROPERTIES_FILE}"
     puts "Using extra property: -Dsli.conf=#{PROPERTIES_FILE}"
   end
   if (KEYSTORE_FILE !=nil && KEYSTORE_FILE != "")
     command = command + " -Dsli.encryption.keyStore=#{KEYSTORE_FILE}"
     puts "Using extra property: -Dsli.encryption.keyStore=#{KEYSTORE_FILE}"
   end
   if (JAR_FILE !=nil && JAR_FILE != "")
     command = command + " -f#{JAR_FILE}"
     puts "Using extra property:  -f#{JAR_FILE}"
   end

   command = command + " -t#{tenant}" + "#{options}"
   puts "Running: #{command} "
   return command
end

Then /^I get the client ID and shared secret for the app$/ do
  @driver.find_element(:xpath, "//tbody/tr[1]/td[1]").click
  @client_id = @driver.find_element(:xpath, '//tbody/tr[2]/td/dl/dd[1]').text
  @shared_secret = @driver.find_element(:xpath, '//tbody/tr[2]/td/dl/dd[2]').text  
  puts "client_id: " + client_id
  puts "Shared Secret ID: " + shared_secret  
  assert(client_id != '', "Expected non empty client Id, got #{client_id}")
  assert(shared_secret != '', "Expected non empty shared secret Id, got #{shared_secret}")
end

Then /^I authorize a session with the app$/ do |user|
  puts API_URL+"/api/oauth/authorize?response_type=code&client_id=#{@client_id}"
  @driver.get(API_URL+"/api/oauth/authorize?response_type=code&client_id=#{@client_id}")
end

Then /^ I capture the authorization and start a session$/ do
  auth_response = JSON.parse(@driver.get_body_text)['authorization_code']

  @driver.get(API_URL+"api/oauth/token?response_type=code&client_id=#{@client_id}&client_secret=#{@shared_secret}&code=#{auth_response}&redirect_uri=http://device")
  @auth_token = JSON.parse(@driver.get_body_text)['access_token']

end 

Then /^I validate the bulk extract file is correct$/ do
  
end

Then /^there is a metadata file in the extract$/ do
  Minitar.unpack(@filePath, @unpackDir)
  assert(File.exists?(@unpackDir + "/metadata.txt"), "Cannot find metadata file in extract")
end

Then /^the extract contains a file for each of the following entities:$/ do |table|
  Minitar.unpack(@filePath, @unpackDir)

  table.hashes.map do |entity|
  exists = File.exists?(@unpackDir + "/" +entity['entityType'] + ".json.gz")
  assert(exists, "Cannot find #{entity['entityType']}.json file in extracts")
  end

  fileList = Dir.entries(@unpackDir)
  puts "Files in upackDir:  #{fileList}"
  assert((fileList.size-3)==table.hashes.size, "Expected " + table.hashes.size.to_s + " extract files, Actual:" + (fileList.size-3).to_s)
end
