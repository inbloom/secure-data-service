module OauthHelper

class Oauth

  attr_accessor :entry_url, :client_id, :client_secret, :token, :code

  def initialize()
    @client_id = "#{APP_CONFIG['client_id']}"
    @client_secret = "#{APP_CONFIG['client_secret']}"
    @redirect_uri = "#{APP_CONFIG['redirect_uri']}"
  end


  def authorize_url
    get_client().auth_code.authorize_url(:redirect_uri => @redirect_uri)
  end

  def enabled()
    return @client_id != nil && @client_id != ""
  end

  def has_code()
    return @code != nil
  end


  def get_client()
    apiUrl = "#{APP_CONFIG['api_base']}"
    uri = URI.parse(apiUrl)
    apiUrl = "#{uri.scheme}://#{uri.host}:#{uri.port}"
    return OAuth2::Client.new(@client_id, @client_secret, {:site => apiUrl, :token_url => '/api/oauth/token', :authorize_url => '/api/oauth/authorize'})
  end 

  def get_token(code)
    @code = code
    token = get_client().auth_code.get_token(code , {:redirect_uri => @redirect_uri})
    @token = token.token
    return @token
  end

end

end
