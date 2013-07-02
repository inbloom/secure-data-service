require 'logger'
require 'net/http'
require 'rexml/document'
require 'json'
require 'httparty'
require 'curl'
require 'uri'


@log = Logger.new(STDOUT)
@log.level = 3

def main()
  profile = ARGV[0]
  user = ARGV[1]
  if ARGV.length>2
    roleOrPassword = ARGV[2]
  else
    roleOrPassword = "IT Administrator"
  end
  if ARGV.length>3
    realm = ARGV[3]
  end

  
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
  mode=profiles[profile]["mode"]

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

  if mode == "prod"
    authUser = user
    authPassword = roleOrPassword
    user = nil
    role = nil
    if realm==nil
      help()
      exit()
    end
  else #sandbox
    realm = nil
    role = roleOrPassword
  end

  puts "Using profile: #{profile}"
  puts "API: #{apiUrl}"
  puts "SIDP: #{sidpUrl}"
  puts "Client ID: #{clientId}"
  puts "Secret: #{secret}"
  puts "Auth User: #{authUser}"
  puts "Auth Password: #{authPassword}"
  puts "User: #{user}"
  puts "Role: #{role}"
  puts "Realm: #{realm}"
  puts "Mode: #{mode}"

  result = startAuth(apiUrl, redirectUrl, clientId, realm)
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

def startAuth(api, redirectUrl, clientId, realm)
  @log.info "Starting Auth process..."
  url = "#{api}/oauth/authorize?response_type=code&redirect_uri=#{redirectUrl}&client_id=#{clientId}"
  if realm != nil
    url = url +"&Realm=#{realm}"
  end
  @log.info "Connecting to API: [#{url}]"
  result = Curl::Easy.http_get(url) do |curl|
    curl.follow_location = false
  end
  location = result.header_str.match("Location: (.*)")[1]
  @log.info location
  samlRequest = URI.unescape(location.match("SAMLRequest=(.*)")[1])
  @log.debug "SAMLRequest: [#{samlRequest}]"
  realm = URI.unescape(location.match("realm=(.*)&")[1])
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
  response = HTTParty.post(url, 
    :body => { :SAMLResponse => samlResponse }
    )
  checkHttpResponse(response)
  json = JSON.parse(response.body)
  code = json["authorization_code"]
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

def help
  puts "Incorrect usage!"
  puts "ruby gettoken.rb <profile> <user> <role or password> <realm>"
  puts "  <profile> is the name of a profile defined in profiles.json"
  puts "  <role or password> is role for sandbox which is optional, if not specied then IT Administator is used. For prod mode you must pass the user's password"
  puts "  <realm> Not used for sandbox, but for prod is required. The realm the user authenticates with."
end

if ARGV.length < 2
  help()
  exit()
end

main()

