require 'rest-client'
require 'json'
require 'builder'
require 'rexml/document'
require 'yaml'
include REXML

$SLI_DEBUG=ENV['DEBUG'] if ENV['DEBUG'] 

def assert(bool, message = 'assertion failure')
  raise message unless bool
end

def webdriverDebugMessage(driver, message="Webdriver could not achieve expected results")
  return "Debug Informaton\nCurrent Page: "+driver.title+"\nCurrent URL : "+driver.current_url+"\n\n"+message
end

# Function idpLogin
# Inputs: (String) user = Username to login to the IDP with
# Inputs: (String) passwd = Password associated with the username
# Output: sets @sessionId, a cookie object that can be referenced throughout the Gherkin scenario
# Returns: Nothing, see Output
# Description: Helper function that logs in to the IDP using the supplied credentials
#              and sets the @sessionId variable for use in later stepdefs throughout the scenario
#              It is suggested you assert the @sessionId before returning success from the calling function 
def idpLogin(user, passwd)
  url = "http://"+PropLoader.getProps['idp_server_url']+"/idp/identity/authenticate?username="+user+"&password="+passwd
  res = RestClient.get(url){|response, request, result| response }
  @sessionId = res.body[res.body.rindex('=')+1..-1]
  puts(@sessionId) if $SLI_DEBUG
end

# Function restHttpPost
# Inputs: (String) id = URL of the desired resource (ex. /students/fe3425e53-f23-f343-53cab3453)
# Inputs: (Object) data = Data object of type @format that you want to create
# Opt. Input: (String) format = defaults to @format that is generally set from the scenario step defs
#                               Can be manually overwritten
# Opt. Input: (Cookie) cookie = defaults to @sessionId that was created from the idpLogin() function
#                               Can be manually overwritten
# Output: sets @res, the HTML REST response that can be access throughout the remainder of the Gherkin scenario
# Returns: Nothing, see Output
# Description: Helper function that calls the REST API specified in id using POST to create a new object
#              It is suggested you assert the state of the @res response before returning success from the calling function
def restHttpPost(id, data, format = @format, cookie = @sessionId)
  # Validate Cookie is not nil
  assert(cookie != nil, "Cookie passed into POST was nil")
  
  url = PropLoader.getProps['api_server_url']+"/api/rest"+id
  @res = RestClient.post(url, data, {:content_type => format, :sessionId => cookie}){|response, request, result| response } 
  puts(@res.code,@res.body,@res.raw_headers) if $SLI_DEBUG
end

# Function restHttpGet
# Inputs: (String) id = URL of the desired resource (ex. /students/fe3425e53-f23-f343-53cab3453)
# Opt. Input: (String) format = defaults to @format that is generally set from the scenario step defs
#                               Can be manually overwritten
# Opt. Input: (Cookie) cookie = defaults to @sessionId that was created from the idpLogin() function
#                               Can be manually overwritten
# Output: sets @res, the HTML REST response that can be access throughout the remainder of the Gherkin scenario
# Returns: Nothing, see Output
# Description: Helper function that calls the REST API specified in id using GET to retrieve an existing object
#              It is suggested you assert the state of the @res response before returning success from the calling function
def restHttpGet(id, format = @format, cookie = @sessionId)
  # Validate Cookie is not nil
  assert(cookie != nil, "Cookie passed into GET was nil")

  url = PropLoader.getProps['api_server_url']+"/api/rest"+id
  @res = RestClient.get(url,{:accept => format,  :sessionId => cookie}){|response, request, result| response }
  puts(@res.code,@res.body,@res.raw_headers) if $SLI_DEBUG
end

# Function restHttpPut
# Inputs: (String) id = URL of the desired resource (ex. /students/fe3425e53-f23-f343-53cab3453)
# Inputs: (Object) data = Data object of type @format that you want to update
# Opt. Input: (String) format = defaults to @format that is generally set from the scenario step defs
#                               Can be manually overwritten
# Opt. Input: (Cookie) cookie = defaults to @sessionId that was created from the idpLogin() function
#                               Can be manually overwritten
# Output: sets @res, the HTML REST response that can be access throughout the remainder of the Gherkin scenario
# Returns: Nothing, see Output
# Description: Helper function that calls the REST API specified in id using PUT to update an existing object
#              It is suggested you assert the state of the @res response before returning success from the calling function
def restHttpPut(id, data, format = @format, cookie = @sessionId)
  # Validate Cookie is not nil
  assert(cookie != nil, "Cookie passed into PUT was nil")
  
  url = PropLoader.getProps['api_server_url']+"/api/rest"+id
  @res = RestClient.put(url, data, {:content_type => format,  :sessionId => cookie}){|response, request, result| response }
  puts(@res.code,@res.body,@res.raw_headers) if $SLI_DEBUG
end

# Function restHttpDelete
# Inputs: (String) id = URL of the desired resource (ex. /students/fe3425e53-f23-f343-53cab3453)
# Opt. Input: (String) format = defaults to @format that is generally set from the scenario step defs
#                               Can be manually overwritten
# Opt. Input: (Cookie) cookie = defaults to @sessionId that was created from the idpLogin() function
#                               Can be manually overwritten
# Output: sets @res, the HTML REST response that can be access throughout the remainder of the Gherkin scenario
# Returns: Nothing, see Output
# Description: Helper function that calls the REST API specified in id using DELETE to remove an existing object
#              It is suggested you assert the state of the @res response before returning success from the calling function
def restHttpDelete(id, format = @format, cookie = @sessionId)
  # Validate Cookie is not nil
  assert(cookie != nil, "Cookie passed into DELETE was nil")

  url = PropLoader.getProps['api_server_url']+"/api/rest"+id
  @res = RestClient.delete(url,{:accept => format,  :sessionId => cookie}){|response, request, result| response }
  puts(@res.code,@res.body,@res.raw_headers) if $SLI_DEBUG
end

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
