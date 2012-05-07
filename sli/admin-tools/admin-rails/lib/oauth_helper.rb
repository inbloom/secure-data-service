module OauthHelper

class Oauth

  attr_accessor :token

  def enabled?
    return !(APP_CONFIG['client_id'].nil? and APP_CONFIG['client_secret'].nil?)
  end

  def authorize_url
    get_client().auth_code.authorize_url(:redirect_uri => APP_CONFIG['redirect_uri'])
  end

  def get_client()
    apiUrl = "#{APP_CONFIG['api_base']}"
    uri = URI.parse(apiUrl)
    apiUrl = "#{uri.scheme}://#{uri.host}:#{uri.port}"
    return OAuth2::Client.new(APP_CONFIG['client_id'], APP_CONFIG['client_secret'], {:site => apiUrl, :token_url => '/api/oauth/token', :authorize_url => '/api/oauth/authorize'})
  end 

  def get_token(code)
    @code = code
    token = get_client().auth_code.get_token(code , {:redirect_uri => APP_CONFIG['redirect_uri']})
    @token = token.token
    @token
  end
  
  def has_code
    return !@code.nil?
  end

end

end
