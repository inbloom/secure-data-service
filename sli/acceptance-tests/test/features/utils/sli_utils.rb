require 'rest-client'
require 'json'
require 'builder'
require 'rexml/document'
require 'yaml'
include REXML

def assert(bool, message = 'assertion failure')
  raise message unless bool
end

# Function idpLogin
# Inputs: (String) user = Username to login to the IDP with
# Inputs: (String) passwd = Password associated with the username
# Output: sets @cookie, a cookie object that can be referenced throughout the Gherkin scenario
# Returns: Nothing, see Output
# Description: Helper function that logs in to the IDP using the supplied credentials
#              and sets the @cookie variable for use in later stepdefs throughout the scenario
#              It is suggested you assert the @cookie before returning success from the calling function 
def idpLogin(user, passwd)
  url = "http://"+PropLoader.getProps['idp_server_url']+"/idp/identity/authenticate?username="+user+"&password="+passwd
  res = RestClient.get(url){|response, request, result| response }
  @cookie = res.body[res.body.rindex('=')+1..-1]
end

# Function restHttpPost
# Inputs: (String) id = URL of the desired resource (ex. /students/fe3425e53-f23-f343-53cab3453)
# Inputs: (Object) data = Data object of type @format that you want to create
# Opt. Input: (String) format = defaults to @format that is generally set from the scenario step defs
#                               Can be manually overwritten
# Opt. Input: (Cookie) cookie = defaults to @cookie that was created from the idpLogin() function
#                               Can be manually overwritten
# Output: sets @res, the HTML REST response that can be access throughout the remainder of the Gherkin scenario
# Returns: Nothing, see Output
# Description: Helper function that calls the REST API specified in id using POST to create a new object
#              It is suggested you assert the state of the @res response before returning success from the calling function
def restHttpPost(id, data, format = @format, cookie = @cookie)
  # Validate Cookie is not nil
  assert(cookie != nil, "Cookie passed into POST was nil")
  
  url = "http://"+PropLoader.getProps['api_server_url']+"/api/rest"+id
  @res = RestClient.post(url, data, {:content_type => format, :cookies => {:iPlanetDirectoryPro => cookie}}){|response, request, result| response } 
  
end

# Function restHttpGet
# Inputs: (String) id = URL of the desired resource (ex. /students/fe3425e53-f23-f343-53cab3453)
# Opt. Input: (String) format = defaults to @format that is generally set from the scenario step defs
#                               Can be manually overwritten
# Opt. Input: (Cookie) cookie = defaults to @cookie that was created from the idpLogin() function
#                               Can be manually overwritten
# Output: sets @res, the HTML REST response that can be access throughout the remainder of the Gherkin scenario
# Returns: Nothing, see Output
# Description: Helper function that calls the REST API specified in id using GET to retrieve an existing object
#              It is suggested you assert the state of the @res response before returning success from the calling function
def restHttpGet(id, format = @format, cookie = @cookie)
  # Validate Cookie is not nil
  assert(cookie != nil, "Cookie passed into GET was nil")

  url = "http://"+PropLoader.getProps['api_server_url']+"/api/rest"+id
  @res = RestClient.get(url,{:accept => format, :cookies => {:iPlanetDirectoryPro => cookie}}){|response, request, result| response }

end

# Function restHttpPut
# Inputs: (String) id = URL of the desired resource (ex. /students/fe3425e53-f23-f343-53cab3453)
# Inputs: (Object) data = Data object of type @format that you want to update
# Opt. Input: (String) format = defaults to @format that is generally set from the scenario step defs
#                               Can be manually overwritten
# Opt. Input: (Cookie) cookie = defaults to @cookie that was created from the idpLogin() function
#                               Can be manually overwritten
# Output: sets @res, the HTML REST response that can be access throughout the remainder of the Gherkin scenario
# Returns: Nothing, see Output
# Description: Helper function that calls the REST API specified in id using PUT to update an existing object
#              It is suggested you assert the state of the @res response before returning success from the calling function
def restHttpPut(id, data, format = @format, cookie = @cookie)
  # Validate Cookie is not nil
  assert(cookie != nil, "Cookie passed into PUT was nil")
  
  url = "http://"+PropLoader.getProps['api_server_url']+"/api/rest"+id
  @res = RestClient.put(url, data, {:content_type => format, :cookies => {:iPlanetDirectoryPro => cookie}}){|response, request, result| response }

end

# Function restHttpDelete
# Inputs: (String) id = URL of the desired resource (ex. /students/fe3425e53-f23-f343-53cab3453)
# Opt. Input: (String) format = defaults to @format that is generally set from the scenario step defs
#                               Can be manually overwritten
# Opt. Input: (Cookie) cookie = defaults to @cookie that was created from the idpLogin() function
#                               Can be manually overwritten
# Output: sets @res, the HTML REST response that can be access throughout the remainder of the Gherkin scenario
# Returns: Nothing, see Output
# Description: Helper function that calls the REST API specified in id using DELETE to remove an existing object
#              It is suggested you assert the state of the @res response before returning success from the calling function
def restHttpDelete(id, format = @format, cookie = @cookie)
  # Validate Cookie is not nil
  assert(cookie != nil, "Cookie passed into DELETE was nil")

  url = "http://"+PropLoader.getProps['api_server_url']+"/api/rest"+id
  @res = RestClient.delete(url,{:accept => format, :cookies => {:iPlanetDirectoryPro => cookie}}){|response, request, result| response }
end

class PropLoader
  @@yml = YAML.load_file(File.join(File.dirname(__FILE__),'properties.yml'))
  def self.getProps
    if ENV['api_server_url']
      @@yml['api_server_url'] = ENV['api_server_url']
    end
    return @@yml
  end
end
