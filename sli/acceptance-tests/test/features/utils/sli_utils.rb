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

def restHtmlPost(id, data, format = @format, cookie = @cookie)
  # Validate Cookie is not nil
  assert(cookie != nil, "Cookie passed into POST was nil")
  
  url = "http://"+PropLoader.getProps['api_server_url']+"/api/rest"+id
  @res = RestClient.post(url, data, {:content_type => format, :cookies => {:iPlanetDirectoryPro => cookie}}){|response, request, result| response } 
  
end

def restHtmlGet()
  #
end

def restHtmlPut()
  #
end

def restHtmlDelete()
  #
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
