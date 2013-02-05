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


require File.expand_path('../common_stepdefs.rb', __FILE__)
require File.expand_path('../rakefile_common.rb', __FILE__)
require 'rubygems'
require 'bundler/setup'

require 'rest-client'
require 'json'
require 'builder'
require 'rexml/document'
require 'yaml'
require 'digest/sha1'
require 'socket'

include REXML

$SLI_DEBUG=ENV['DEBUG'] if ENV['DEBUG']

$SESSION_MAP = {
                "jdoe_IL" => "c88ab6d7-117d-46aa-a207-2a58d1f72796",
                "tbear_IL" => "c77ab6d7-227d-46bb-a207-2a58d1f82896",
                "john_doe_IL" => "a69ab2d7-137d-46ba-c281-5a57d1f22706",
                "ejane_IL" => "4ab8b6d4-51ad-c67a-1b0a-25e8d1f12701",
                "johndoe_NY" => "a49cb2f7-a31d-06ba-f281-515b01f82706",
                "ejane_NY" => "c17ab6d0-0caa-c87f-100a-2ae8d0f22601",
                "linda.kim_IL" => "4cf7a5d4-37a1-ca19-8b13-b5f95131ac85",
                "cgray_IL" => "1cf7a5d4-75a2-ba63-8b53-b5f95131cc48",
                "rbraverman_IL" => "2cf7a5d4-78a1-ca42-8b74-b5f95131ac21",
                "manthony_IL" => "2cfda5e4-74a1-7a4b-8274-a5f5c134ac61",
                "mario.sanchez_NY" => "8cfba5a4-39a1-ca39-8413-b5697131ac85",
                "baduser_SLI" => "4cf7a5d4-37a1-ca55-8b13-b5f95131ac85",
                "nouser_SLI" => "4cf7a5d4-37a1-ca66-8b13-b5f95131ac85",
                "teacher_IL" => "4cf7a5d4-37a1-ca77-8b13-b5f95131ac85",
                "prince_IL" => "4cf7a5d4-37a1-ca88-8b13-b5f95131ac85",
                "root_IL" => "4cf7a5d4-37a1-ca99-8b13-b5f95131ac85",
                "custom_IL" => "20de11c7-56b3-4d8b-bfaa-b61bc5be7671",
                "developer_SLI" => "26c4b55b-5fa8-4287-af3d-98e7b5f98232",
                "operator_SLI" => "a8cf184b-9c7e-4253-9f45-ed4e9f4f596c",
                "bigbro_IL" => "4cf7a5d4-37a1-ca00-8b13-b5f95131ac85",
                "sunsetrealmadmin_SLI" => "d9af321c-5fa8-4287-af3d-98e7b5f9d999",
                "fakerealmadmin_SLI" => "aa391d1c-99a8-4287-af3d-481516234242",
                "anotherfakerealmadmin_SLI" => "910bcfad-5fa8-4287-af3d-98e7b5f9e786",
                "sunsetadmin_SLI" => "4aea375c-0e5d-456a-8b89-23bc03aa5ea2",
                "badadmin_IL" => "5cf7a5d4-57a1-c100-8b13-b5f95131ac85",
                "sampleUser_IL" => "e88cb5c1-771d-46ac-a207-e88cb7c1771d",
                "demo_IL" => "e88cb5c1-771d-46ac-a2c7-2d58d7f12196",
                "eengland_NY" => "ebbec99c-c8cf-4982-b853-3513374d0073",
                "gcanning_NY" => "0a50a4ec-e00f-4944-abac-2abbdb99f7d9",
                "jbarrera_NY" => "2485c0ec-bf37-4b30-b96e-07b98b205bf9",
                "jpratt_NY" => "2b0608b6-5162-4e13-8669-f71e9878a2ef",
                "jsmalley_NY" => "144e272d-cfbd-42a2-a8e7-ee333e77eec6",
                "jcarlyle_NY" => "81198176-7d9f-4fc1-8f4a-9ff9dda0870d",
                "mhahn_NY" => "9e95a2f8-686c-4b0f-9816-9d8dfec3de1d",
                "rlindsey_NY" => "3fe8d3dc-577b-401e-82e0-faa847048ede",
                "sholcomb_NY" => "e6aa1a6f-1ae2-4727-b9d8-131cdfdd239a",
                "llogan_IL" => "6fb146b3-6dac-41c9-ab72-0f4d4832b873",
                "jwashington_IL" => "0b496e6d-471d-4c1b-bd83-bb3fe0d671b6",
                "jvasquez_IL" => "c294f7ee-45ee-4c56-8e72-dad9c926d42b",
                "ckoch_IL" => "a21a9381-e189-408d-b21d-b44d847af83f",
                "rrogers_IL" => "cacd9227-5b14-4685-babe-31230476cf3b",
                "mjohnson_IL" => "29da4ea2-40e1-466a-8f2c-ea357d4f096c",
                "sbantu_IL" => "79abdc40-dcd8-4412-b5db-32f63befcc90",
                "jstevenson_IL" => "9f58b6dc-0880-4e2a-a65f-3aa8b5201fbd",
                "jjackson_IL" => "b7cbbc75-23bf-4005-a545-8a110eefa063",
                "kmelendez_NY" => "d93ef071-39ff-4e41-9619-f8f43d22b4bf",
                "agibbs_NY" => "1dc64dcb-354e-4ab6-be54-e8401caa06a6",
                "charrison_NY" => "8fbd7332-1af4-4524-ae6d-f28ddf600798",
                "mgonzales_IL" => "10229764-a6a0-4367-9862-fd18781c9638",
                "akopel_IL" => "438e472e-a888-46d1-8087-0195f4e37089",
                "msmith_IL" => "5679153f-f1cc-44bd-9bfa-a21a41cd020c",
                "racosta_IL" => "3f165e8d-bb42-4b62-8a2d-92f98dcd6ffc",
                "agillespie_IL" => "ba09eeb3-a50a-4278-b363-22074168421d",
                "wgoodman_IL" => "8c950c56-74f3-4e5d-a02c-d09497fddb1d",
                "ingestionuser_SLI" => "3b22ab4c-1de4-ac99-8b89-23bc03aaa812",
                "sandboxingestionuser_SLI" => "3b22ab4c-1de4-ac99-8b89-23bc03aaa701",
                "sandboxoperator_SLI" => "a8cf185b-9c8e-4254-9f46-ed4e9f4f597c",
                "sandboxadministrator_SLI" => "a8cf186b-9c8e-4253-9f46-ed4e9f4f598c",
                "sandboxdeveloper_SLI" => "a1cf186b-9c8e-4252-9f46-ed4e9f4f597c",
                "anothersandboxdeveloper_SLI" => "be71e33e-00f5-442a-a0c7-3dc5c63a8a02",
                "iladmin_SLI" => "9abf3111-0e5d-456a-8b89-004815162342",
                "stweed_IL" => "2cf7a5d4-75a2-ba63-8b53-b5f95131de48",
                "teach1_SEC" => "00000000-5555-5555-0001-500000000001",
                "teach2_SEC" => "00000000-5555-5555-0001-500000000002",
                "teach3_SEC" => "00000000-5555-5555-0001-500000000003",
                "teach4_SEC" => "00000000-5555-5555-0001-500000000004",
                "teacher10_SEC" => "00000000-5555-5555-0001-500000000010",
                "teacher11_SEC" => "00000000-5555-5555-0001-500000000011",
                "teacher12_SEC" => "00000000-5555-5555-0001-500000000012",
                "staff1_SEC" => "00000000-5555-5555-0001-500000000101",
                "staff2_SEC" => "00000000-5555-5555-0001-500000000102",
                "staff3_SEC" => "00000000-5555-5555-0001-500000000103",
                "staff4_SEC" => "00000000-5555-5555-0001-500000000104",
                "staff5_SEC" => "00000000-5555-5555-0001-500000000105",
                "staff6_SEC" => "00000000-5555-5555-0001-500000000106",
                "staff7_SEC" => "00000000-5555-5555-0001-500000000107",
                "staff8_SEC" => "00000000-5555-5555-0001-500000000108",
                "staff9_SEC" => "00000000-5555-5555-0001-500000000109",
                "staff10_SEC" => "00000000-5555-5555-0001-500000000110",
                "staff11_SEC" => "00000000-5555-5555-0001-500000000111",
                "staff12_SEC" => "00000000-5555-5555-0001-500000000112",
                "staff13_SEC" => "00000000-5555-5555-0001-500000000113",
                "staff14_SEC" => "00000000-5555-5555-0001-500000000114",
                "staff15_SEC" => "00000000-5555-5555-0001-500000000115",
                "staff16_SEC" => "00000000-5555-5555-0001-500000000116",
                "staff17_SEC" => "00000000-5555-5555-0001-500000000117",
                "staff18_SEC" => "00000000-5555-5555-0001-500000000118",
                "staff20_SEC" => "00000000-5555-5555-0001-500000000120",
                "staff21_SEC" => "00000000-5555-5555-0001-500000000121",
                "staff22_SEC" => "00000000-5555-5555-0001-500000000122",
                "linda.kim_Zork" => "08e3cc74-4a5c-4a0e-b8ab-680ee11cc890",
                "linda.kim_Chaos" => "160eb95e-173f-472a-8ed2-b973a4d775a3",
                "linda.kimadmin_IL" => "4cf7a5d4-37a1-ca19-8b13-b5f95131afff",
                "cgrayadmin_IL" => "bd8987d4-75a2-ba63-8b53-424242424242",
                "jstevenson_SIF" => "e4e9d71c-d674-11e1-9ea4-f9fc6188709b",
                "linda.kim_developer-email" => "d0c34964-4a5c-4a0e-b8ab-1fd1a6801888",
                "linda.kim_sandboxadministrator" => "9a87321a-8534-4a0e-b8ab-981ab8716233",
                "unprovisioned_sunset_admin_SLI" => "261d8a09-a181-4e3f-bcaa-241f409afb8b",
                "rrogerslimitedwrite_IL" => "03af65a0-5720-4cbc-ae49-f7bcb46c25f1",
                "mmagic_Midgar" => "7e859615-3487-41b0-a052-cb1beb6fb12d",
                "morion_Midgar" => "11a45527-9141-491d-8ebd-132f1d105625"
                                    
}

def convertTenantIdToDbName(tenantId)
  db_name = Digest::SHA1.hexdigest tenantId
  puts "Tenant: #{tenantId} DB: #{db_name}"
  return db_name
end

def assert(bool, message = 'assertion failure')
  raise message unless bool
end

# Function idpLogin
# Inputs: (String) user = Username to login to the IDP with
# Inputs: (String) passwd = Password associated with the username
# Output: sets @sessionId, a string containing the OAUTH session that can be referenced throughout the Gherkin scenario
# Returns: Nothing, see Output
# Description: Helper function that logs in to the IDP using the supplied credentials
#              and sets the @sessionId variable for use in later stepdefs throughout the scenario
#              It is suggested you assert the @sessionId before returning success from the calling function
def idpLogin(user, passwd)
  idpRealmLogin(user, passwd, "SLI")
end

# Function idpRealmLogin
# Inputs: (Enum/String) realm = ("SLI" "IL" or "NY") Which IDP you want to login with
# Inputs: (String) user = Username to login to the IDP with
# Inputs: (String) passwd = Password associated with the username
# Output: sets @sessionId, a string containing the OAUTH session that can be referenced throughout the Gherkin scenario
# Returns: Nothing, see Output
# Description: Helper function that logs in to the specified IDP using the supplied credentials
#              and sets the @sessionId variable for use in later stepdefs throughout the scenario
#              It is suggested you assert the @sessionId before returning success from the calling function
def idpRealmLogin(user, passwd, realm="SLI")
  puts "realm " + realm
  token = $SESSION_MAP[user+"_"+realm]
  assert(token != nil, "Could not find session for user #{user} in realm #{realm}")
  @sessionId = token
  puts(@sessionId) if $SLI_DEBUG
end

# Function restHttpPost
# Inputs: (String) id = URL of the desired resource (ex. /students/fe3425e53-f23-f343-53cab3453)
# Inputs: (Object) data = Data object of type @format that you want to create
# Opt. Input: (String) format = defaults to @format that is generally set from the scenario step defs
#                               Can be manually overwritten
# Opt. Input: (String) sessionId = defaults to @sessionId that was created from the idpLogin() function
#                               Can be manually overwritten
# Output: sets @res, the HTML REST response that can be access throughout the remainder of the Gherkin scenario
# Returns: Nothing, see Output
# Description: Helper function that calls the REST API specified in id using POST to create a new object
#              It is suggested you assert the state of the @res response before returning success from the calling function
def restHttpPost(id, data, format = @format, sessionId = @sessionId)
  # Validate SessionId is not nil
  assert(sessionId != nil, "Session ID passed into POST was nil")

  urlHeader = makeUrlAndHeaders('post',id,sessionId,format)
  @res = RestClient.post(urlHeader[:url], data, urlHeader[:headers]){|response, request, result| response }

  puts(@res.code,@res.body,@res.raw_headers) if $SLI_DEBUG
end

def restHttpPostAbs(url, data = nil, format = @format, sessionId = @sessionId)
  # Validate SessionId is not nil
  assert(sessionId != nil, "Session ID passed into POST was nil")

  urlHeader = makeHeaders('post',sessionId,format)
  @res = RestClient.post(url, data, urlHeader[:headers]){|response, request, result| response }

  puts(@res.code,@res.body,@res.raw_headers) if $SLI_DEBUG
end

# Function restHttpGet
# Inputs: (String) id = URL of the desired resource (ex. /students/fe3425e53-f23-f343-53cab3453)
# Opt. Input: (String) format = defaults to @format that is generally set from the scenario step defs
#                               Can be manually overwritten
# Opt. Input: (String) sessionId = defaults to @sessionId that was created from the idpLogin() function
#                               Can be manually overwritten
# Output: sets @res, the HTML REST response that can be access throughout the remainder of the Gherkin scenario
# Returns: Nothing, see Output
# Description: Helper function that calls the REST API specified in id using GET to retrieve an existing object
#              It is suggested you assert the state of the @res response before returning success from the calling function
def restHttpGet(id, format = @format, sessionId = @sessionId)
  # Validate SessionId is not nil
  assert(sessionId != nil, "Session ID passed into GET was nil")

  urlHeader = makeUrlAndHeaders('get',id,sessionId,format)
  @res = RestClient.get(urlHeader[:url], urlHeader[:headers]){|response, request, result| response }

  puts(@res.code,@res.body,@res.raw_headers) if $SLI_DEBUG
end

def restHttpGetAbs(url, format = @format, sessionId = @sessionId)
  # Validate SessionId is not nil
  assert(sessionId != nil, "Session ID passed into GET was nil")

  urlHeader = makeHeaders('get',sessionId,format)
  @res = RestClient.get(url, urlHeader){|response, request, result| response }

  puts(@res.code,@res.body,@res.raw_headers) if $SLI_DEBUG
end

# Function restHttpPut
# Inputs: (String) id = URL of the desired resource (ex. /students/fe3425e53-f23-f343-53cab3453)
# Inputs: (Object) data = Data object of type @format that you want to update
# Opt. Input: (String) format = defaults to @format that is generally set from the scenario step defs
#                               Can be manually overwritten
# Opt. Input: (String) sessionId = defaults to @sessionId that was created from the idpLogin() function
#                               Can be manually overwritten
# Output: sets @res, the HTML REST response that can be access throughout the remainder of the Gherkin scenario
# Returns: Nothing, see Output
# Description: Helper function that calls the REST API specified in id using PUT to update an existing object
#              It is suggested you assert the state of the @res response before returning success from the calling function
def restHttpPut(id, data, format = @format, sessionId = @sessionId)
  # Validate SessionId is not nil
  assert(sessionId != nil, "Session ID passed into PUT was nil")

  urlHeader = makeUrlAndHeaders('put',id,sessionId,format)
  @res = RestClient.put(urlHeader[:url], data, urlHeader[:headers]){|response, request, result| response }
  puts "\n\n" if $SLI_DEBUG
  puts @res.code if $SLI_DEBUG
  puts "\n\n" if $SLI_DEBUG
  puts @res.body if $SLI_DEBUG
  puts "\n\n" if $SLI_DEBUG
  puts @res.raw_headers if $SLI_DEBUG
  puts "\n\n" if $SLI_DEBUG
  puts(@res.code,@res.body,@res.raw_headers) if $SLI_DEBUG
end

def restHttpPatch(id, data, format = @format, sessionId = @sessionId)
  # Validate SessionId is not nil
  assert(sessionId != nil, "Session ID passed into PUT was nil")

  urlHeader = makeUrlAndHeaders('patch',id,sessionId,format)
  @res = RestClient.patch(urlHeader[:url], data, urlHeader[:headers]){|response, request, result| response }

  puts(@res.code,@res.body,@res.raw_headers) if $SLI_DEBUG
end

# Function restHttpDelete
# Inputs: (String) id = URL of the desired resource (ex. /students/fe3425e53-f23-f343-53cab3453)
# Opt. Input: (String) format = defaults to @format that is generally set from the scenario step defs
#                               Can be manually overwritten
# Opt. Input: (String) sessionId = defaults to @sessionId that was created from the idpLogin() function
#                               Can be manually overwritten
# Output: sets @res, the HTML REST response that can be access throughout the remainder of the Gherkin scenario
# Returns: Nothing, see Output
# Description: Helper function that calls the REST API specified in id using DELETE to remove an existing object
#              It is suggested you assert the state of the @res response before returning success from the calling function
def restHttpDelete(id, format = @format, sessionId = @sessionId)
  # Validate SessionId is not nil
  assert(sessionId != nil, "Session ID passed into DELETE was nil")

  urlHeader = makeUrlAndHeaders('delete',id,sessionId,format)
  @res = RestClient.delete(urlHeader[:url], urlHeader[:headers]){|response, request, result| response }

  puts(@res.code,@res.body,@res.raw_headers) if $SLI_DEBUG
end

def restHttpDeleteAbs(url, format = @format, sessionId = @sessionId)
  # Validate SessionId is not nil
  assert(sessionId != nil, "Session ID passed into DELETE was nil")

  urlHeader = makeHeaders('delete',sessionId,format)
  @res = RestClient.delete(url, urlHeader[:headers]){|response, request, result| response }

  puts(@res.code,@res.body,@res.raw_headers) if $SLI_DEBUG
end

def makeUrlAndHeaders(verb,id,sessionId,format)
  headers = makeHeaders(verb, sessionId, format)

  url = PropLoader.getProps['api_server_url']+"/api/rest"+id
  puts(url, headers) if $SLI_DEBUG

  return {:url => url, :headers => headers}
end

def makeHeaders(verb,sessionId,format)
  if(verb == 'put' || verb == 'post' || verb == 'patch')
    headers = {:content_type => format}
  else
    headers = {:accept => format}
  end

  headers.store(:Authorization, "bearer "+sessionId)
  if !headers.has_key?(:content_type)
    headers.store("Content-Type", "application/json")
  end
  return headers
end


##############################################################################
##############################################################################
###### After hook(s) #########################################################

Before do
  enable_NOTABLESCAN()
end

at_exit do
  disable_NOTABLESCAN()
end

After do |scenario|
  if scenario.failed?
    begin
      conn = Mongo::Connection.new("jenkins.slidev.org")
      db = conn.db("test_job_failures")
      failures = db.collection("failure")
      title = scenario.is_a?(Cucumber::Ast::OutlineTable::ExampleRow)? scenario.scenario_outline.feature.name.split("\n")[0] : scenario.feature.name.split("\n")[0]
      scenarioName = scenario.is_a?(Cucumber::Ast::OutlineTable::ExampleRow)? scenario.scenario_outline.name : scenario.name
      host = Socket.gethostname.include?("cislave")? Socket.gethostname : "local"
      
      filepath = scenario.is_a?(Cucumber::Ast::OutlineTable::ExampleRow)? scenario.scenario_outline.feature.file : scenario.feature.file
      component = filepath.match(/test[\\\/]features[\\\/]([^\\\/]+)[\\\/]/)[1]
      
      failureHash = {
       "timestamp" => Time.now.to_f,
       "feature" => title,
       "scenario" => scenarioName,
       "component" => component,
       "hostname" => host
      }
      failures.insert(failureHash)
      db.get_last_error()
    rescue
      # If couldn't report failure, swallow the exception and continue
    ensure
      conn.close if conn != nil
    end
    Cucumber.wants_to_quit = true if !ENV['FAILSLOW']
  end
end

Around('@LDAP_Reset_developer-email') do |scenario, block|
  block.call
  if scenario.failed?
    ldap = LDAPStorage.new(PropLoader.getProps['ldap_hostname'], PropLoader.getProps['ldap_port'],
                          PropLoader.getProps['ldap_base'], PropLoader.getProps['ldap_admin_user'],
                          PropLoader.getProps['ldap_admin_pass'], PropLoader.getProps['ldap_use_ssl'])
    ldap.update_user_info({:email=> "developer-email@slidev.org", :password=>"test1234"})
  end
end

Around('@LDAP_Reset_sunsetadmin') do |scenario, block|
  block.call
  if scenario.failed?
    ldap = LDAPStorage.new(PropLoader.getProps['ldap_hostname'], PropLoader.getProps['ldap_port'],
                          PropLoader.getProps['ldap_base'], PropLoader.getProps['ldap_admin_user'],
                          PropLoader.getProps['ldap_admin_pass'], PropLoader.getProps['ldap_use_ssl'])
    ldap.update_user_info({:email=> "sunsetadmin", :password=>"sunsetadmin1234", :emailtoken => "sunsetadminderpityderp1304425892"})
  end
end

##############################################################################
##############################################################################
### Step Def Util methods ###

def convert(value)
  if /^true$/.match value
    true;
  elsif /^false$/.match value
    false;
  elsif /^\d+\.\d+$/.match value
    Float(value)
  elsif /^\d+$/.match value
    Integer(value)
  else
    value
  end
end

def prepareData(format, hash)
  if format == "application/json"
    hash.to_json
  elsif format == "application/vnd.slc+json"
    hash.to_json
  elsif format == "application/xml"
    hash.to_s
  elsif format == "application/json;charset=utf-8"
    hash.to_json
  elsif format == "application/vnd.slc+json;charset=utf-8"
    hash.to_json
  elsif format == "application/xml;charset=utf-8"
    hash.to_s
  else
    assert(false, "Unsupported MIME type")
  end
end

def contentType(response)
  headers = @res.raw_headers
  assert(headers != nil, "Headers are nil")
  assert(headers['content-type'] != nil, "There is no content-type set in the response")
  headers['content-type'][0]
end

#return boolean
def findLink(id, type, rel, href)
  found = false
  uri = type+id
  restHttpGet(uri)
  assert(@res != nil, "Response from rest-client GET is nil")
  assert(@res.code == 200, "Return code was not expected: #{@res.code.to_s} but expected 200")
  if @format == "application/json" or @format == "application/vnd.slc+json"
    dataH=JSON.parse(@res.body)
    dataH["links"].each do |link|
      if link["rel"]==rel and link["href"].include? href
        found = true
        break
      end
    end
  elsif @format == "application/xml"
    assert(false, "application/xml is not supported")
  else
    assert(false, "Unsupported MIME type")
  end
  return found
end


########################################################################
########################################################################

def runShellCommand(command)
  `#{command}`
end

########################################################################
########################################################################

module DataProvider
  def self.getValidRealmData()
    return {
       "uniqueIdentifier" => "Whatever",
       "tenantId" => "bliss",
       "admin" => false,
       "idp" => {"id" => "http://path.to.nowhere", "redirectEndpoint" => "http://path.to.nowhere/somewhere/else"},
       "saml" => {"field" => []},
       "name" => "a_new_realm",
       "edOrg" => "NC-KRYPTON"
    }
  end

  def self.getValidCustomRoleData()
    return {
      "realmId" => "",
      "roles" => [{"groupTitle" => "Educator", "isAdminRole" => false, "names" => ["Educator", "Math Teacher", "English Teacher"], "rights" => ["READ_GENERAL", "WRITE_GENERAL"]}],
      "customRights" => ["RIGHT_TO_REMAIN_SILENT", "INALIENABLE_RIGHT"]
    }
  end

  def self.getValidAppData()
    return {
      "installed" => false,
      "redirect_uri" => "https://slidev.org",
      "description" => "Prints hello world.",
      "name" => "Hello World",
      "is_admin" => true,
      "behavior" => "Full Window App",
      "administration_url" => "https://slidev.org/admin",
      "image_url" => "https://slidev.org/img",
      "application_url" => "https://slidev.org/image",
      "registration" => {},
      "version" => "3.14",
      "vendor" => "Acme",
      "authorized_ed_orgs" => []
    }
  end

  def self.getValidAdminDelegationData()
    return {
      "viewSecurityEventsEnabled" => false,
      "appApprovalEnabled" => false,
      "localEdOrgId" => "b2c6e292-37b0-4148-bf75-c98a2fcc905f"
      }
  end


end

module CreateEntityHash
  def CreateEntityHash.createBaseStudent()
    data = Hash[
        "studentUniqueStateId" => "123456",
        "name" => Hash[
          "firstName" => "fname",
          "lastSurname" => "lname",
          "middleName" => "mname"],
        "sex" => "Male",
        "birthData" => Hash[
          "birthDate" => "2012-01-01"
          ],
        "learningStyles" => Hash[
          "visualLearning" => 30,
          "auditoryLearning" => 40,
          "tactileLearning" => 30
          ]
       ]

    return data
  end

  def CreateEntityHash.createBaseStudentRandomId()
    data = CreateEntityHash.createBaseStudent
    data['studentUniqueStateId'] = (0...8).map{65.+(rand(25)).chr}.join
    return data
  end

  def CreateEntityHash.createBaseStudentDefinedId(id)
    data = CreateEntityHash.createBaseStudent
    data['studentUniqueStateId'] = id
    return data
  end

  def CreateEntityHash.createBaseSchool()
    data = Hash[
        "nameOfInstitution" => "school name",
        "stateOrganizationId" => "12345678",
        "gradesOffered" => ["First grade", "Second grade"],
      "address"=>[
      "streetNumberName" => "111 Ave C",
      "city" => "Chicago",
      "stateAbbreviation" => "IL",
      "postalCode" => "10098",
      "nameOfCounty" => "Wake"
      ],
        "organizationCategories" => ["School"],
        "schoolCategories" => ["Elementary School"],
        ]
    return data
  end

  def CreateEntityHash.createBaseSchoolRandomId()
    data = CreateEntityHash.createBaseSchool
    data['stateOrganizationId'] = (0...8).map{65.+(rand(25)).chr}.join
    return data
  end
end

module EntityProvider

  def self.verify_entities_match(expected, response)
    if expected.is_a?(Hash)
      expected.each { |key, value| verify_entities_match(value, response[key]) }
    elsif expected.is_a?(Array)
      assert( expected.size == response.size )
      expected.zip(response).each { |ex, res| verify_entities_match(ex, res) }
    else
      assert( expected == response )
    end
  end

end

######################
######################
### Create uuids that can be used thusly:  @db['collection'].find_one( '_id' => id_from_juuid("e5420397-908e-11e1-9a9d-68a86d2267de"))
def id_from_juuid(uuid)
  hex = uuid.gsub(/[-]+/, '')
  msb = hex[0, 16]
  lsb = hex[16, 16]
  msb = msb[14, 2] + msb[12, 2] + msb[10, 2] + msb[8, 2] + msb[6, 2] + msb[4, 2] + msb[2, 2] + msb[0, 2]
  lsb = lsb[14, 2] + lsb[12, 2] + lsb[10, 2] + lsb[8, 2] + lsb[6, 2] + lsb[4, 2] + lsb[2, 2] + lsb[0, 2]
  hex = msb + lsb;
  BSON::Binary.new([hex].pack('H*'), BSON::Binary::SUBTYPE_UUID)
end

######################
### create a deep copy of entity data used in API CRUD tests
def deep_copy(o)
  Marshal.load(Marshal.dump(o))
end

### asserts something with a timeout
def assertWithPolling(msg, total_wait_sec, &blk)
  passed = false
  total_wait_sec.times { |x|
    begin
      sleep(1)
      assert(yield, msg)
      passed = true
      break
    rescue MiniTest::Assertion
      $stderr.puts "not yet statisfied after #{x} seconds"
    rescue 
      $stderr.puts "not yet statisfied after #{x} seconds"
    end
  }

  assert(yield, msg) unless passed
end
