require 'logger'
require 'net/http'
require 'rexml/document'
require 'json'
require 'httparty'
require 'curl'
require 'uri'


@log = Logger.new(STDOUT)
@log.level = 0

def main()
  profile = ARGV[0]
  user = ARGV[1]
  if ARGV.length>2
    role = ARGV[2]
  else
    role = "IT Administrator"
  end
  puts "Using profile: #{profile}"
  profiles = nil
  open('profiles.json', 'r') { |f| profiles = JSON.load(f) }
  @log.debug "Profiles: #{profiles}"
  @log.debug "Profile Selected is: #{profiles[profile]}"
  authUser=profiles[profile]["authUser"]
  authPassword=profiles[profile]["password"]
  clientId=profiles[profile]["clientId"]
  secret=profiles[profile]["secret"]
  redirectUrl=profiles[profile]["redirect_uri"]
  server=profiles[profile]["server"]
  puts "Server is: #{server}"

  if server == "local"
    apiUrl="http://local.slidev.org:8080/api"
    sidpUrl="http://local.slidev.org:8082/simple-idp"
  elsif server == "sandbox"
    apiUrl="https://api.sandbox.inbloom.org/api"
    sidpUrl="https://sidp.sandbox.inbloom.org/sliidp"
  else
    apiUrl="https://#{server}-api.slidev.org/api"
    sidpUrl="https://#{server}-sidp.slidev.org/sliidp"
  end

  puts "API: #{apiUrl}"
  puts "SIDP: #{sidpUrl}"
  puts "Client ID: #{clientId}"
  puts "Secret: #{secret}"
  puts "Auth User: #{authUser}"
  puts "User: #{user}"
  puts "Role: #{role}"

  result = startAuth(apiUrl, redirectUrl, clientId)
  samlResponse = sidpLogin(sidpUrl, result['samlRequest'], result['realm'], authUser, authPassword, user, role)
  code = postSaml(apiUrl, samlResponse)
  token = getToken(apiUrl, code, redirectUrl, clientId, secret)
  puts "Token is #{token}"
end

def checkHttpResponse(response)
  @log.debug response.body
  if response.code != 200
    @log.error response.body  
    @log.error response.headers.inspect
    @log.error "Response: [#{response.code}], message: [#{response.message}]"
    @log.error "Unexpected response code"
  end
end

def startAuth(api, redirectUrl, clientId)
  @log.info "Starting Auth process..."
  url = "#{api}/oauth/authorize?response_type=code&redirect_uri=#{redirectUrl}&client_id=#{clientId}"
  @log.info "Connecting to API: [#{url}]"
  response = HTTParty.get(url)
  checkHttpResponse(response)
  samlRequest = response.match("SAMLRequest\" value=\"(.*)\"")[1]
  @log.debug "SAMLRequest: [#{samlRequest}]"
  realm = response.match("realm\" value=\"(.*)\"")[1]
  @log.info "Realm: [#{realm}]"
  return {'samlRequest'=>samlRequest, 'realm'=>realm}
end

def sidpLogin(sidp, samlRequest, realm, authUser, authPassword, impUser, impRole)
  @log.info "Logging in to SIDP..."
  url = sidp+"/login"
  @log.info "Logging in to SIDP: [#{url}]"
  response = HTTParty.post(url, 
    :body => { :SAMLRequest => samlRequest, :realm => realm, :user_id => authUser, :password => authPassword })
  checkHttpResponse(response)
  cookie = response.headers['Set-Cookie']
  @log.info "Cookie is: #{cookie}"
  if impUser != nil
    url = sidp+"/impersonate"
    @log.info "SIDP Impersonation: [#{url}]"
    response = HTTParty.post(url, 
      :body => { :SAMLRequest => samlRequest, :realm => realm, :impersonate_user => impUser, :customRoles => impRole, :manualConfig => "true" },
      :headers => {'Cookie' => cookie}
      )
    checkHttpResponse(response)
  end
  samlResponse = response.match("value=\"(.*)\"")[1]
  @log.info "Got SAMLResponse"
  @log.debug "SAMLResponse: [#{samlResponse}]"
  return samlResponse
end

def postSaml(api, samlResponse)
  @log.info "Posting SAML Assertion..."
  url = api + "/rest/saml/sso/post" 
  @log.info "Posting SAML to API: [#{url}]"
  result = Curl::Easy.http_post(url, Curl::PostField.content('SAMLResponse', samlResponse)) do |curl|
    curl.follow_location = false
  end
  @log.debug result.header_str
  code = result.header_str.match("Location.*code=(.*)")[1]
  @log.info "auth code is: [#{code}]"
  enc_code = URI.escape(code.strip())
  return enc_code
end  

def getToken(api, code, redirectUrl, clientId, secret)
  @log.info "Getting token..."
  url = "#{api}/oauth/token?code=#{code}&redirect_uri=#{redirectUrl}&client_id=#{clientId}&client_secret=#{secret}&grant_type=authorization_code"
  @log.info "Getting token from API: [#{url}]"
  response = HTTParty.get(url)
  checkHttpResponse(response)
  json = JSON.parse(response.body)
  token = json["access_token"]
  @log.info "token is: [#{token}]"
  return token
end

def get(url, token)
  @log.info "Calling API: [#{url}]"
  response = HTTParty.get(url, :headers => {'Authorization' => 'Bearer ' +token})
  checkHttpResponse(response)
  json = JSON.parse(response.body)
  @log.debug "Response as JSON: #{json}"
  return json
end

def post(url, token, body)
  @log.info "Posting to: [#{url}]"
  @log.debug "Posting body: #{body}"
  response = HTTParty.post(url, :body => body, :headers => 
    {'Authorization' => 'Bearer ' +token,
     'Content-Type' => 'application/json'})
  if response.code != 201
    @log.error response.body  
    @log.error response.headers.inspect
    @log.error "Response: [#{response.code}], message: [#{response.message}]"
    @log.error "Unexpected response code"
    exit()
  else
    @log.info "Post successful"
    @log.debug response.headers.inspect
    location = response.headers["location"]
    newId = location.match(".*\/(.*)")[1]
    @log.info "New entity: [#{newId}]"
    return location
  end
end

def apiHome(apiUrl, token)
  json = get("#{apiUrl}/rest/v1/home", token)
  return json
end

def getMyId(apiUrl, token)
  json = apiHome(apiUrl, token)
  link = getLink(json, "self")
  me = link.match("staff/(.*)")[1]
  if me == nil
    me = link.match("teacher/(.*)")[1]
  end
  @log.info "Users id is: [#{me}]"
  return me
end

def sessionCheck(apiUrl, token)
  json = get("#{apiUrl}/rest/system/session/check", token)
end

def getMyEdOrg(apiUrl, token, myId)
  json = get("#{apiUrl}/rest/v1/staff/#{myId}/staffEducationOrgAssignmentAssociations/educationOrganizations", token)  
  myEdOrg = json[0]
  return myEdOrg
end

def printJsonObject(hash)
  hash.each do |key, value|
    if key != "links"
      puts "    #{key}: #{value}"
    end
  end
end

def getLink(json, name)
  json["links"].each do |link|
    if link["rel"] == name
      return link["href"]
    end
  end
end


def createEdOrg(apiUrl, token, parentEdOrgId)
  puts "City: "
  city = gets.chomp
  puts "State: "
  state = gets.chomp
  puts "State Organization ID: "
  stateOrganizationId = gets.chomp
  puts "Name of Institution: "
  nameOfInstitution = gets.chomp
  puts "Type of EdOrg:"
  puts "1) LEA"
  puts "2) School"
  choice = Integer(gets.chomp)
  orgCategory = choice==1 ? "Local Education Agency" : "School"
  json = { "accountabilityRatings" => [],
           "organizationCategories" => ["#{orgCategory}"],
           "address" => [{
                "streetNumberName" => "123 Street",
                "postalCode" => "12345",
                "stateAbbreviation" => "#{state}",
                "city" => "#{city}"
           }],
          "educationOrgIdentificationCode" => [],
          "parentEducationAgencyReference" => "#{parentEdOrgId}",
          "programReference" => [],
          "stateOrganizationId" => "#{stateOrganizationId}",
          "entityType" => "educationOrganization",
          "telephone" => [],
          "nameOfInstitution" => "#{nameOfInstitution}"
    }
  post("#{apiUrl}/rest/v1/educationOrganizations", token, json.to_json) 
end

if ARGV.length < 2
  puts "Incorrect usage!"
  puts "ruby gettoken.rb <profile> <user> <role>"
  puts "<role> is optional, if not specied then IT Administator is used"
  exit()
end

main()

