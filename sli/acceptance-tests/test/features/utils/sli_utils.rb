require File.expand_path('../common_stepdefs.rb', __FILE__)
require 'rubygems'
require 'bundler/setup'

require 'rest-client'
require 'json'
require 'builder'
require 'rexml/document'
require 'yaml'
include REXML

$SLI_DEBUG=ENV['DEBUG'] if ENV['DEBUG']

$SESSION_MAP = {"demo_SLI" => "e88cb6d1-771d-46ac-a207-2e58d7f12196",
                "jdoe_IL" => "c88ab6d7-117d-46aa-a207-2a58d1f72796",
                "tbear_IL" => "c77ab6d7-227d-46bb-a207-2a58d1f82896",
                "john_doe_IL" => "a69ab2d7-137d-46ba-c281-5a57d1f22706",
                "ejane_IL" => "4ab8b6d4-51ad-c67a-1b0a-25e8d1f12701",
                "johndoe_NY" => "a49cb2f7-a31d-06ba-f281-515b01f82706",
                "ejane_NY" => "c17ab6d0-0caa-c87f-100a-2ae8d0f22601",
                "linda.kim_IL" => "4cf7a5d4-37a1-ca19-8b13-b5f95131ac85",
                "cgray_IL" => "1cf7a5d4-75a2-ba63-8b53-b5f95131cc48",
                "rbraverman_IL" => "2cf7a5d4-78a1-ca42-8b74-b5f95131ac21",
                "mario.sanchez_NY" => "8cfba5a4-39a1-ca39-8413-b5697131ac85",
                "educator_SLI" => "4cf7a5d4-37a1-ca11-8b13-b5f95131ac85",
                "leader_SLI" => "4cf7a5d4-37a1-ca22-8b13-b5f95131ac85",
                "administrator_SLI" => "4cf7a5d4-37a1-ca33-8b13-b5f95131ac85",
                "aggregator_SLI" => "4cf7a5d4-37a1-ca44-8b13-b5f95131ac85",
                "baduser_SLI" => "4cf7a5d4-37a1-ca55-8b13-b5f95131ac85",
                "nouser_SLI" => "4cf7a5d4-37a1-ca66-8b13-b5f95131ac85",
                "teacher_IL" => "4cf7a5d4-37a1-ca77-8b13-b5f95131ac85",
                "prince_IL" => "4cf7a5d4-37a1-ca88-8b13-b5f95131ac85",
                "root_IL" => "4cf7a5d4-37a1-ca99-8b13-b5f95131ac85",
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
                 "iladmin_SLI" => "9abf3111-0e5d-456a-8b89-004815162342"}

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

def makeUrlAndHeaders(verb,id,sessionId,format)
  if(verb == 'put' || verb == 'post')
    headers = {:content_type => format}
  else
    headers = {:accept => format}
  end

  headers.store(:Authorization, "bearer "+sessionId)

  url = PropLoader.getProps['api_server_url']+"/api/rest"+id
  puts(url, headers) if $SLI_DEBUG

  return {:url => url, :headers => headers}
end

##############################################################################
##############################################################################
###### After hook(s) #########################################################

After do |scenario| 
  if(ENV['FAILFAST'])
    Cucumber.wants_to_quit = true if scenario.failed?
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
# Property Loader class

class PropLoader
  @@yml = YAML.load_file(File.join(File.dirname(__FILE__),'properties.yml'))
  @@modified=false

  def self.getProps
    self.updateHash() unless @@modified
    return @@yml
  end

  private

  def self.updateHash()
    @@yml.each do |key, value|
      @@yml[key] = ENV[key] if ENV[key]
    end
    @@modified=true
  end
end

module DataProvider
  def self.getValidRealmData()
    return {
       "tenantId" => "bliss",
       "admin" => false,
       "idp" => {"id" => "http://path.to.nowhere", "redirectEndpoint" => "http://path.to.nowhere/somewhere/else"},
       "saml" => {"field" => []},
       "name" => "a_new_realm",
       "edOrg" => "ba987125-a8ed-eafd-bf75-c98a2fcc3dfg",
       "mappings"=> {"role"=>[{"sliRoleName"=>"Educator","clientRoleName"=>["Math teacher","Sci Teacher","Enforcer of Conformity"]},{"sliRoleName"=>"Leader","clientRoleName"=>["Fearless Leader","Imperator","First Consul"]}]}
    }
  end
  
  def self.getValidAppData()
    return {
      "installed" => true,
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
      "localEdOrgId" => "IL-SUNSET"
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

  def CreateEntityHash.createBaseSchool()
    data = Hash[
        "nameOfInstitution" => "school name",
        "stateOrganizationId" => "12345678",
        "gradesOffered" => ["First grade", "Second grade"],
        "address"=>[],
        "organizationCategories" => ["School"],
        "schoolCategories" => ["Elementary School"],
        ]
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

  def self.get_new_entity(type)
    case type
      when 'attendance', 'v1attendance'
        {
            "studentId" => "7a86a6a7-1f80-4581-b037-4a9328b9b650",
            "schoolId" => "2058ddfb-b5c6-70c4-3bee-b43e9e93307d",
            "schoolYearAttendance" =>
                [{
                     "schoolYear" => "2011-2012",
                     "attendanceEvent" =>
                         [{

                              "date" => "2012-02-24",
                              "event" => "Tardy",
                              "reason" => "missed the bus"

                          }]
                 }]
        }
    end
  end

  def self.get_entity_uri(type)
    case type
    when 'attendance'
      "/attendances"
    when 'v1attendance'
      "/v1/attendances"
    end
  end

  def self.get_existing_entity(type)
    case type
    when 'attendance', 'v1attendance'
      {
          "id" => "530f0704-c240-4ed9-0a64-55c0308f91ee",
          "studentId" => "74cf790e-84c4-4322-84b8-fca7206f1085",
          "schoolId" => "fdacc41b-8133-f12d-5d47-358e6c0c791c",
          "schoolYearAttendance" =>
              [
                  {"schoolYear" => "2011-2012",
                   "attendanceEvent" =>
                       [
                           {"date" => "2011-09-06", "event" => "In Attendance"},
                           {"date" => "2011-09-07", "event" => "In Attendance"},
                           {"date" => "2011-09-08", "event" => "In Attendance"},
                           {"date" => "2011-09-09", "event" => "In Attendance"},
                           {"date" => "2011-09-12", "event" => "In Attendance"},
                           {"date" => "2011-09-13", "event" => "In Attendance"},
                           {"date" => "2011-09-14", "event" => "In Attendance"},
                           {"date" => "2011-09-15", "event" => "In Attendance"},
                           {"date" => "2011-09-16", "event" => "Tardy", "reason" => "Missed school bus"},
                           {"date" => "2011-09-19", "event" => "In Attendance"},
                           {"date" => "2011-09-20", "event" => "In Attendance"},
                           {"date" => "2011-09-21", "event" => "In Attendance"},
                           {"date" => "2011-09-22", "event" => "In Attendance"},
                           {"date" => "2011-09-23", "event" => "In Attendance"},
                           {"date" => "2011-09-26", "event" => "In Attendance"},
                           {"date" => "2011-09-27", "event" => "In Attendance"},
                           {"date" => "2011-09-28", "event" => "In Attendance"},
                           {"date" => "2011-09-29", "event" => "In Attendance"},
                           {"date" => "2011-09-30", "event" => "In Attendance"},
                           {"date" => "2011-10-03", "event" => "In Attendance"},
                           {"date" => "2011-10-04", "event" => "In Attendance"},
                           {"date" => "2011-10-05", "event" => "In Attendance"},
                           {"date" => "2011-10-06", "event" => "In Attendance"},
                           {"date" => "2011-10-07", "event" => "In Attendance"},
                           {"date" => "2011-10-11", "event" => "In Attendance"},
                           {"date" => "2011-10-12", "event" => "In Attendance"},
                           {"date" => "2011-10-13", "event" => "In Attendance"},
                           {"date" => "2011-10-14", "event" => "In Attendance"},
                           {"date" => "2011-10-17", "event" => "In Attendance"},
                           {"date" => "2011-10-18", "event" => "In Attendance"},
                           {"date" => "2011-10-19", "event" => "In Attendance"},
                           {"date" => "2011-10-20", "event" => "In Attendance"},
                           {"date" => "2011-10-21", "event" => "In Attendance"},
                           {"date" => "2011-10-24", "event" => "In Attendance"},
                           {"date" => "2011-10-25", "event" => "In Attendance"},
                           {"date" => "2011-10-26", "event" => "In Attendance"},
                           {"date" => "2011-10-27", "event" => "In Attendance"},
                           {"date" => "2011-10-28", "event" => "In Attendance"},
                           {"date" => "2011-10-31", "event" => "In Attendance"},
                           {"date" => "2011-11-01", "event" => "In Attendance"},
                           {"date" => "2011-11-02", "event" => "In Attendance"},
                           {"date" => "2011-11-03", "event" => "In Attendance"},
                           {"date" => "2011-11-04", "event" => "In Attendance"},
                           {"date" => "2011-11-07", "event" => "In Attendance"},
                           {"date" => "2011-11-08", "event" => "In Attendance"},
                           {"date" => "2011-11-09", "event" => "In Attendance"},
                           {"date" => "2011-11-10", "event" => "In Attendance"},
                           {"date" => "2011-11-14", "event" => "In Attendance"},
                           {"date" => "2011-11-15", "event" => "In Attendance"},
                           {"date" => "2011-11-16", "event" => "In Attendance"},
                           {"date" => "2011-11-17", "event" => "In Attendance"},
                           {"date" => "2011-11-18", "event" => "Excused Absence", "reason" => "Absent excused"},
                           {"date" => "2011-11-21", "event" => "In Attendance"},
                           {"date" => "2011-11-22", "event" => "In Attendance"},
                           {"date" => "2011-11-23", "event" => "In Attendance"},
                           {"date" => "2011-11-28", "event" => "Tardy", "reason" => "Missed school bus"},
                           {"date" => "2011-11-29", "event" => "In Attendance"},
                           {"date" => "2011-11-30", "event" => "In Attendance"},
                           {"date" => "2011-12-01", "event" => "In Attendance"},
                           {"date" => "2011-12-02", "event" => "In Attendance"},
                           {"date" => "2011-12-05", "event" => "In Attendance"},
                           {"date" => "2011-12-06", "event" => "In Attendance"},
                           {"date" => "2011-12-07", "event" => "In Attendance"},
                           {"date" => "2011-12-08", "event" => "In Attendance"},
                           {"date" => "2011-12-09", "event" => "Excused Absence", "reason" => "Absent excused"},
                           {"date" => "2011-12-12", "event" => "In Attendance"},
                           {"date" => "2011-12-13", "event" => "In Attendance"},
                           {"date" => "2011-12-14", "event" => "In Attendance"},
                           {"date" => "2011-12-15", "event" => "In Attendance"},
                           {"date" => "2011-12-16", "event" => "In Attendance"},
                           {"date" => "2011-12-19", "event" => "In Attendance"},
                           {"date" => "2011-12-20", "event" => "In Attendance"},
                           {"date" => "2011-12-21", "event" => "Excused Absence", "reason" => "Absent excused"},
                           {"date" => "2011-12-22", "event" => "In Attendance"},
                           {"date" => "2011-12-23", "event" => "In Attendance"},
                           {"date" => "2011-12-28", "event" => "In Attendance"},
                           {"date" => "2011-12-29", "event" => "In Attendance"},
                           {"date" => "2012-01-03", "event" => "In Attendance"},
                           {"date" => "2012-01-04", "event" => "In Attendance"},
                           {"date" => "2012-01-05", "event" => "In Attendance"},
                           {"date" => "2012-01-06", "event" => "In Attendance"},
                           {"date" => "2012-01-09", "event" => "In Attendance"},
                           {"date" => "2012-01-10", "event" => "In Attendance"},
                           {"date" => "2012-01-11", "event" => "In Attendance"},
                           {"date" => "2012-01-12", "event" => "In Attendance"},
                           {"date" => "2012-01-13", "event" => "In Attendance"},
                           {"date" => "2012-01-16", "event" => "In Attendance"},
                           {"date" => "2012-01-17", "event" => "In Attendance"},
                           {"date" => "2012-01-18", "event" => "In Attendance"},
                           {"date" => "2012-01-19", "event" => "In Attendance"},
                           {"date" => "2012-01-20", "event" => "In Attendance"},
                           {"date" => "2012-01-23", "event" => "In Attendance"},
                           {"date" => "2012-01-24", "event" => "In Attendance"},
                           {"date" => "2012-01-25", "event" => "In Attendance"},
                           {"date" => "2012-01-26", "event" => "In Attendance"},
                           {"date" => "2012-01-27", "event" => "In Attendance"},
                           {"date" => "2012-01-30", "event" => "Excused Absence", "reason" => "Absent excused"},
                           {"date" => "2012-01-31", "event" => "In Attendance"},
                           {"date" => "2012-02-01", "event" => "In Attendance"},
                           {"date" => "2012-02-02", "event" => "In Attendance"},
                           {"date" => "2012-02-03", "event" => "In Attendance"},
                           {"date" => "2012-02-06", "event" => "In Attendance"},
                           {"date" => "2012-02-07", "event" => "In Attendance"},
                           {"date" => "2012-02-08", "event" => "In Attendance"},
                           {"date" => "2012-02-09", "event" => "In Attendance"},
                           {"date" => "2012-02-10", "event" => "In Attendance"},
                           {"date" => "2012-02-13", "event" => "In Attendance"},
                           {"date" => "2012-02-14", "event" => "In Attendance"},
                           {"date" => "2012-02-15", "event" => "In Attendance"},
                           {"date" => "2012-02-16", "event" => "Tardy", "reason" => "Missed school bus"},
                           {"date" => "2012-02-17", "event" => "In Attendance"},
                           {"date" => "2012-02-20", "event" => "In Attendance"},
                           {"date" => "2012-02-21", "event" => "In Attendance"},
                           {"date" => "2012-02-22", "event" => "In Attendance"},
                           {"date" => "2012-02-23", "event" => "In Attendance"},
                           {"date" => "2012-02-24", "event" => "In Attendance"},
                           {"date" => "2012-02-27", "event" => "In Attendance"},
                           {"date" => "2012-02-28", "event" => "In Attendance"},
                           {"date" => "2012-02-29", "event" => "In Attendance"},
                           {"date" => "2012-03-01", "event" => "In Attendance"},
                           {"date" => "2012-03-02", "event" => "In Attendance"},
                           {"date" => "2012-03-05", "event" => "In Attendance"},
                           {"date" => "2012-03-06", "event" => "In Attendance"},
                           {"date" => "2012-03-07", "event" => "In Attendance"},
                           {"date" => "2012-03-08", "event" => "In Attendance"},
                           {"date" => "2012-03-09", "event" => "In Attendance"},
                           {"date" => "2012-03-19", "event" => "In Attendance"},
                           {"date" => "2012-03-20", "event" => "In Attendance"},
                           {"date" => "2012-03-21", "event" => "In Attendance"},
                           {"date" => "2012-03-22", "event" => "In Attendance"},
                           {"date" => "2012-03-23", "event" => "In Attendance"},
                           {"date" => "2012-03-26", "event" => "In Attendance"},
                           {"date" => "2012-03-27", "event" => "In Attendance"},
                           {"date" => "2012-03-28", "event" => "In Attendance"},
                           {"date" => "2012-03-29", "event" => "In Attendance"},
                           {"date" => "2012-03-30", "event" => "In Attendance"},
                           {"date" => "2012-04-02", "event" => "In Attendance"},
                           {"date" => "2012-04-03", "event" => "In Attendance"},
                           {"date" => "2012-04-04", "event" => "In Attendance"},
                           {"date" => "2012-04-05", "event" => "In Attendance"},
                           {"date" => "2012-04-06", "event" => "In Attendance"},
                           {"date" => "2012-04-09", "event" => "In Attendance"},
                           {"date" => "2012-04-10", "event" => "In Attendance"},
                           {"date" => "2012-04-11", "event" => "In Attendance"},
                           {"date" => "2012-04-12", "event" => "In Attendance"},
                           {"date" => "2012-04-13", "event" => "In Attendance"},
                           {"date" => "2012-04-16", "event" => "In Attendance"},
                           {"date" => "2012-04-17", "event" => "In Attendance"},
                           {"date" => "2012-04-18", "event" => "In Attendance"},
                           {"date" => "2012-04-19", "event" => "In Attendance"},
                           {"date" => "2012-04-20", "event" => "In Attendance"},
                           {"date" => "2012-04-23", "event" => "In Attendance"},
                           {"date" => "2012-04-24", "event" => "In Attendance"},
                           {"date" => "2012-04-25", "event" => "In Attendance"},
                           {"date" => "2012-04-26", "event" => "In Attendance"},
                           {"date" => "2012-04-27", "event" => "In Attendance"},
                           {"date" => "2012-04-30", "event" => "In Attendance"},
                           {"date" => "2012-05-01", "event" => "In Attendance"},
                           {"date" => "2012-05-02", "event" => "In Attendance"},
                           {"date" => "2012-05-03", "event" => "In Attendance"},
                           {"date" => "2012-05-04", "event" => "In Attendance"},
                           {"date" => "2012-05-07", "event" => "In Attendance"},
                           {"date" => "2012-05-08", "event" => "Tardy", "reason" => "Missed school bus"},
                           {"date" => "2012-05-09", "event" => "In Attendance"},
                           {"date" => "2012-05-10", "event" => "In Attendance"},
                           {"date" => "2012-05-11", "event" => "In Attendance"},
                           {"date" => "2012-05-14", "event" => "In Attendance"},
                           {"date" => "2012-05-15", "event" => "In Attendance"},
                           {"date" => "2012-05-16", "event" => "In Attendance"},
                           {"date" => "2012-05-17", "event" => "In Attendance"},
                           {"date" => "2012-05-18", "event" => "In Attendance"},
                           {"date" => "2012-05-21", "event" => "In Attendance"},
                           {"date" => "2012-05-22", "event" => "In Attendance"},
                           {"date" => "2012-05-23", "event" => "In Attendance"},
                           {"date" => "2012-05-24", "event" => "In Attendance"},
                           {"date" => "2012-05-25", "event" => "In Attendance"},
                           {"date" => "2012-05-28", "event" => "In Attendance"},
                           {"date" => "2012-05-29", "event" => "In Attendance"},
                           {"date" => "2012-05-30", "event" => "In Attendance"},
                           {"date" => "2012-05-31", "event" => "In Attendance"},
                           {"date" => "2012-06-01", "event" => "In Attendance"
                           }
                       ]
                  }
              ]
      }
    end
  end

  def self.get_updated_entity(type)
    case type
    when 'attendance', 'v1attendance'
      { 
        "id" => "530f0704-c240-4ed9-0a64-55c0308f91ee",
        "studentId" => "6a98d5d3-d508-4b9c-aec2-59fce7e16825",
        "schoolId" => "fdacc41b-8133-f12d-5d47-358e6c0c791c",
        "schoolYearAttendance" =>
            [
                {"schoolYear" => "2011-2012",
                 "attendanceEvent" =>
                     [
                         {"date" => "2011-09-06", "event" => "In Attendance"},
                         {"date" => "2011-09-07", "event" => "In Attendance"},
                         {"date" => "2011-09-08", "event" => "In Attendance"},
                         {"date" => "2011-09-09", "event" => "In Attendance"},
                         {"date" => "2011-09-12", "event" => "In Attendance"},
                         {"date" => "2011-09-13", "event" => "In Attendance"},
                         {"date" => "2011-09-14", "event" => "In Attendance"},
                         {"date" => "2011-09-15", "event" => "In Attendance"},
                         {"date" => "2011-09-16", "event" => "In Attendance"},
                         {"date" => "2011-09-19", "event" => "In Attendance"},
                         {"date" => "2011-09-20", "event" => "In Attendance"},
                         {"date" => "2011-09-21", "event" => "In Attendance"},
                         {"date" => "2011-09-22", "event" => "In Attendance"},
                         {"date" => "2011-09-23", "event" => "In Attendance"},
                         {"date" => "2011-09-26", "event" => "In Attendance"},
                         {"date" => "2011-09-27", "event" => "In Attendance"},
                         {"date" => "2011-09-28", "event" => "In Attendance"},
                         {"date" => "2011-09-29", "event" => "In Attendance"},
                         {"date" => "2011-09-30", "event" => "In Attendance"},
                         {"date" => "2011-10-03", "event" => "In Attendance"},
                         {"date" => "2011-10-04", "event" => "In Attendance"},
                         {"date" => "2011-10-05", "event" => "In Attendance"},
                         {"date" => "2011-10-06", "event" => "In Attendance"},
                         {"date" => "2011-10-07", "event" => "In Attendance"},
                         {"date" => "2011-10-11", "event" => "Tardy", "reason" => "Missed school bus"},
                         {"date" => "2011-10-12", "event" => "In Attendance"},
                         {"date" => "2011-10-13", "event" => "In Attendance"},
                         {"date" => "2011-10-14", "event" => "In Attendance"},
                         {"date" => "2011-10-17", "event" => "In Attendance"},
                         {"date" => "2011-10-18", "event" => "In Attendance"},
                         {"date" => "2011-10-19", "event" => "In Attendance"},
                         {"date" => "2011-10-20", "event" => "In Attendance"},
                         {"date" => "2011-10-21", "event" => "Tardy", "reason" => "Missed school bus"},
                         {"date" => "2011-10-24", "event" => "In Attendance"},
                         {"date" => "2011-10-25", "event" => "In Attendance"},
                         {"date" => "2011-10-26", "event" => "In Attendance"},
                         {"date" => "2011-10-27", "event" => "In Attendance"},
                         {"date" => "2011-10-28", "event" => "In Attendance"},
                         {"date" => "2011-10-31", "event" => "In Attendance"},
                         {"date" => "2011-11-01", "event" => "In Attendance"},
                         {"date" => "2011-11-02", "event" => "In Attendance"},
                         {"date" => "2011-11-03", "event" => "In Attendance"},
                         {"date" => "2011-11-04", "event" => "In Attendance"},
                         {"date" => "2011-11-07", "event" => "In Attendance"},
                         {"date" => "2011-11-08", "event" => "In Attendance"},
                         {"date" => "2011-11-09", "event" => "In Attendance"},
                         {"date" => "2011-11-10", "event" => "In Attendance"},
                         {"date" => "2011-11-14", "event" => "In Attendance"},
                         {"date" => "2011-11-15", "event" => "In Attendance"},
                         {"date" => "2011-11-16", "event" => "In Attendance"},
                         {"date" => "2011-11-17", "event" => "In Attendance"},
                         {"date" => "2011-11-18", "event" => "In Attendance"},
                         {"date" => "2011-11-21", "event" => "In Attendance"},
                         {"date" => "2011-11-22", "event" => "In Attendance"},
                         {"date" => "2011-11-23", "event" => "In Attendance"},
                         {"date" => "2011-11-28", "event" => "In Attendance"},
                         {"date" => "2011-11-29", "event" => "In Attendance"},
                         {"date" => "2011-11-30", "event" => "In Attendance"},
                         {"date" => "2011-12-01", "event" => "In Attendance"},
                         {"date" => "2011-12-02", "event" => "In Attendance"},
                         {"date" => "2011-12-05", "event" => "In Attendance"},
                         {"date" => "2011-12-06", "event" => "In Attendance"},
                         {"date" => "2011-12-07", "event" => "Excused Absence", "reason" => "Absent excused"},
                         {"date" => "2011-12-08", "event" => "In Attendance"},
                         {"date" => "2011-12-09", "event" => "In Attendance"},
                         {"date" => "2011-12-12", "event" => "In Attendance"},
                         {"date" => "2011-12-13", "event" => "In Attendance"},
                         {"date" => "2011-12-14", "event" => "In Attendance"},
                         {"date" => "2011-12-15", "event" => "In Attendance"},
                         {"date" => "2011-12-16", "event" => "In Attendance"},
                         {"date" => "2011-12-19", "event" => "In Attendance"},
                         {"date" => "2011-12-20", "event" => "In Attendance"},
                         {"date" => "2011-12-21", "event" => "In Attendance"},
                         {"date" => "2011-12-22", "event" => "In Attendance"},
                         {"date" => "2011-12-23", "event" => "In Attendance"},
                         {"date" => "2011-12-28", "event" => "In Attendance"},
                         {"date" => "2011-12-29", "event" => "In Attendance"},
                         {"date" => "2012-01-03", "event" => "In Attendance"},
                         {"date" => "2012-01-04", "event" => "In Attendance"},
                         {"date" => "2012-01-05", "event" => "In Attendance"},
                         {"date" => "2012-01-06", "event" => "In Attendance"},
                         {"date" => "2012-01-09", "event" => "In Attendance"},
                         {"date" => "2012-01-10", "event" => "In Attendance"},
                         {"date" => "2012-01-11", "event" => "In Attendance"},
                         {"date" => "2012-01-12", "event" => "In Attendance"},
                         {"date" => "2012-01-13", "event" => "In Attendance"},
                         {"date" => "2012-01-16", "event" => "In Attendance"},
                         {"date" => "2012-01-17", "event" => "In Attendance"},
                         {"date" => "2012-01-18", "event" => "In Attendance"},
                         {"date" => "2012-01-19", "event" => "In Attendance"},
                         {"date" => "2012-01-20", "event" => "In Attendance"},
                         {"date" => "2012-01-23", "event" => "In Attendance"},
                         {"date" => "2012-01-24", "event" => "In Attendance"},
                         {"date" => "2012-01-25", "event" => "In Attendance"},
                         {"date" => "2012-01-26", "event" => "In Attendance"},
                         {"date" => "2012-01-27", "event" => "In Attendance"},
                         {"date" => "2012-01-30", "event" => "In Attendance"},
                         {"date" => "2012-01-31", "event" => "In Attendance"},
                         {"date" => "2012-02-01", "event" => "In Attendance"},
                         {"date" => "2012-02-02", "event" => "In Attendance"},
                         {"date" => "2012-02-03", "event" => "In Attendance"},
                         {"date" => "2012-02-06", "event" => "In Attendance"},
                         {"date" => "2012-02-07", "event" => "In Attendance"},
                         {"date" => "2012-02-08", "event" => "In Attendance"},
                         {"date" => "2012-02-09", "event" => "In Attendance"},
                         {"date" => "2012-02-10", "event" => "In Attendance"},
                         {"date" => "2012-02-13", "event" => "In Attendance"},
                         {"date" => "2012-02-14", "event" => "In Attendance"},
                         {"date" => "2012-02-15", "event" => "In Attendance"},
                         {"date" => "2012-02-16", "event" => "In Attendance"},
                         {"date" => "2012-02-17", "event" => "In Attendance"},
                         {"date" => "2012-02-20", "event" => "In Attendance"},
                         {"date" => "2012-02-21", "event" => "In Attendance"},
                         {"date" => "2012-02-22", "event" => "In Attendance"},
                         {"date" => "2012-02-23", "event" => "In Attendance"},
                         {"date" => "2012-02-24", "event" => "In Attendance"},
                         {"date" => "2012-02-27", "event" => "In Attendance"},
                         {"date" => "2012-02-28", "event" => "In Attendance"},
                         {"date" => "2012-02-29", "event" => "In Attendance"},
                         {"date" => "2012-03-01", "event" => "In Attendance"},
                         {"date" => "2012-03-02", "event" => "In Attendance"},
                         {"date" => "2012-03-05", "event" => "In Attendance"},
                         {"date" => "2012-03-06", "event" => "In Attendance"},
                         {"date" => "2012-03-07", "event" => "In Attendance"},
                         {"date" => "2012-03-08", "event" => "Tardy", "reason" => "Missed school bus"},
                         {"date" => "2012-03-09", "event" => "In Attendance"},
                         {"date" => "2012-03-19", "event" => "In Attendance"},
                         {"date" => "2012-03-20", "event" => "In Attendance"},
                         {"date" => "2012-03-21", "event" => "In Attendance"},
                         {"date" => "2012-03-22", "event" => "In Attendance"},
                         {"date" => "2012-03-23", "event" => "In Attendance"},
                         {"date" => "2012-03-26", "event" => "In Attendance"},
                         {"date" => "2012-03-27", "event" => "In Attendance"},
                         {"date" => "2012-03-28", "event" => "In Attendance"},
                         {"date" => "2012-03-29", "event" => "In Attendance"},
                         {"date" => "2012-03-30", "event" => "In Attendance"},
                         {"date" => "2012-04-02", "event" => "In Attendance"},
                         {"date" => "2012-04-03", "event" => "In Attendance"},
                         {"date" => "2012-04-04", "event" => "In Attendance"},
                         {"date" => "2012-04-05", "event" => "In Attendance"},
                         {"date" => "2012-04-06", "event" => "In Attendance"},
                         {"date" => "2012-04-09", "event" => "Tardy", "reason" => "Missed school bus"},
                         {"date" => "2012-04-10", "event" => "In Attendance"},
                         {"date" => "2012-04-11", "event" => "In Attendance"},
                         {"date" => "2012-04-12", "event" => "In Attendance"},
                         {"date" => "2012-04-13", "event" => "In Attendance"},
                         {"date" => "2012-04-16", "event" => "In Attendance"},
                         {"date" => "2012-04-17", "event" => "In Attendance"},
                         {"date" => "2012-04-18", "event" => "In Attendance"},
                         {"date" => "2012-04-19", "event" => "In Attendance"},
                         {"date" => "2012-04-20", "event" => "In Attendance"},
                         {"date" => "2012-04-23", "event" => "In Attendance"},
                         {"date" => "2012-04-24", "event" => "In Attendance"},
                         {"date" => "2012-04-25", "event" => "In Attendance"},
                         {"date" => "2012-04-26", "event" => "In Attendance"},
                         {"date" => "2012-04-27", "event" => "In Attendance"},
                         {"date" => "2012-04-30", "event" => "In Attendance"},
                         {"date" => "2012-05-01", "event" => "In Attendance"},
                         {"date" => "2012-05-02", "event" => "In Attendance"},
                         {"date" => "2012-05-03", "event" => "In Attendance"},
                         {"date" => "2012-05-04", "event" => "In Attendance"},
                         {"date" => "2012-05-07", "event" => "In Attendance"},
                         {"date" => "2012-05-08", "event" => "In Attendance"},
                         {"date" => "2012-05-09", "event" => "In Attendance"},
                         {"date" => "2012-05-10", "event" => "In Attendance"},
                         {"date" => "2012-05-11", "event" => "In Attendance"},
                         {"date" => "2012-05-14", "event" => "In Attendance"},
                         {"date" => "2012-05-15", "event" => "In Attendance"},
                         {"date" => "2012-05-16", "event" => "In Attendance"},
                         {"date" => "2012-05-17", "event" => "In Attendance"},
                         {"date" => "2012-05-18", "event" => "In Attendance"},
                         {"date" => "2012-05-21", "event" => "In Attendance"},
                         {"date" => "2012-05-22", "event" => "In Attendance"},
                         {"date" => "2012-05-23", "event" => "In Attendance"},
                         {"date" => "2012-05-24", "event" => "In Attendance"},
                         {"date" => "2012-05-25", "event" => "In Attendance"},
                         {"date" => "2012-05-28", "event" => "In Attendance"},
                         {"date" => "2012-05-29", "event" => "In Attendance"},
                         {"date" => "2012-05-30", "event" => "In Attendance"},
                         {"date" => "2012-05-31", "event" => "In Attendance"},
                         {"date" => "2012-06-01", "event" => "Excused Absence", "reason" => "Absent excused"}
                     ]
                }
            ]
      }
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
