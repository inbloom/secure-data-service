=begin
#--

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
#++
# This module is part of using the OAuth2 gem.
module OauthHelper

# The OAuth class implements the various methods needed to handle OAuth as per
# the Oauth2 gem.
class Oauth

  # The final OAuth token that is needed to make API calls.
  attr_accessor :token

  # We use this to check to see if we have the required client_id and
  # shared_secret configured.
  def enabled?
    return !(APP_CONFIG['client_id'].nil? and APP_CONFIG['client_secret'].nil?)
  end

  # This is the internal call used to start the OAuth process and it passes
  # along the client redirect. This creates an authorization code that is used
  # later in #get_token
  def authorize_url
    get_client().auth_code.authorize_url(:redirect_uri => APP_CONFIG['redirect_uri'])
  end


  # This method actually does the job of initializing the OAuth2 client with
  # all of the configured information in +config/config.yml+
  def get_client()
    apiUrl = "#{APP_CONFIG['api_base']}"
    uri = URI.parse(apiUrl)
    apiUrl = "#{uri.scheme}://#{uri.host}:#{uri.port}"
    return OAuth2::Client.new(APP_CONFIG['client_id'], APP_CONFIG['client_secret'], {:site => apiUrl, :token_url => '/api/oauth/token', :authorize_url => '/api/oauth/authorize'})
  end 

  # The final step in this process is to get the acutal OAuth token which is
  # then stored in the @token variable.
  def get_token(code)
    @code = code
    token = get_client().auth_code.get_token(code , {:redirect_uri => APP_CONFIG['redirect_uri']})
    @token = token.token
    @token
  end

  # Simply lets us know if we got an authorization code or not to decide if we
  # should proceed to the #get_token step
  def has_code
    return !@code.nil?
  end

end

end
