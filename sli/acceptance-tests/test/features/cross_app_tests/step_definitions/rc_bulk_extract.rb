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
   `rm -f \~/.ssh/known_hosts`
   ssh_command = "ssh #{SSH_USER} sudo rm -rf #{OUTPUT_DIRECTORY}#{convertTenantIdToDbName(PropLoader.getProps['tenant'])}"
   puts ssh_command
   `#{ssh_command}`
end

Given /^there is no bulk extract files in the local directory$/ do
    command = "rm -f #{EXTRACT_TO_DIRECTORY}/extract.tar"
    puts command
   `#{command}`
    command = "rm -rf #{EXTRACT_TO_DIRECTORY}/unpack/"
    puts command
    `#{command}`
    command = "rm -f #{EXTRACT_TO_DIRECTORY}/extract/"
    puts command
    `#{command}`
end

When /^the operator triggers a bulk extract for the production tenant$/ do
    `rm -f \~/.ssh/known_hosts`
    command = getBulkExtractCommand(PropLoader.getProps['tenant'])
    ssh_command = "ssh #{SSH_USER} sudo #{command}"
    puts ssh_command
    `#{ssh_command}`
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

def getBulkExtractCommand(tenant)
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
  assert((fileList.size-3)==table.hashes.size, "Expected " + table.hashes.size.to_s + " extract files, Actual:" + (fileList.size-3).to_s)
end
