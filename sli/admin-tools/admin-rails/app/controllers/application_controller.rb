require "active_resource/base"

class ApplicationController < ActionController::Base
  protect_from_forgery
  before_filter :check_login
  
  rescue_from ActiveResource::UnauthorizedAccess do |exception|
    logger.info { "Unauthorized Access: Redirecting..." }
    redirect_to exception.response['WWW-Authenticate'] + "?RelayState=#{current_url}&RealmName=Shared%20Learning%20Infrastructure"
  end
  
  rescue_from ActiveResource::ForbiddenAccess do |exception|
    logger.info { "Forbidden access."}
    raise exception
  end
  
  rescue_from ActiveResource::ServerError do |exception|
    logger.error {"Exception on server, clearing your session."}
    SessionResource.auth_id = nil
  end


  def callback
    puts "Looking for redirect"
    redirect_to flash[:redirect]
    return
    if params[:state]
      redirectUrl = CGI::unescape(params[:state])
      #TODO: disable redirects to other domains
      redirect_to redirectUrl
      return
    end
    respond_to do |format|
      format.html {render :text => "", :status => :no_content}
    end
  end
  
  def begin_authenticate(authentication)
    redirect_to authentication + "?RelayState=" + current_url
  end
  
  def current_url
    "http://" + request.host_with_port + request.fullpath
  end
  
  def check_login
    client_id = "#{APP_CONFIG['client_id']}"
    client_secret = "#{APP_CONFIG['client_secret']}"
    
    puts "Checking for token"

    if cookies['token'] != nil
      puts "found token"
      puts cookies['token']
      return
    end

    #puts "No token"
    #    OAuth2::Response.register_parser(:facebook, 'text/plain') do |body|
    #     token_key, token_value, expiration_key, expiration_value = body.split(/[=&]/)
    #      {token_key => token_value, expiration_key => expiration_value, :mode => :query, :param_name => 'access_token'}
    #    end

  if params[:code]
    puts "We have a code, let's get a token" 
    client = OAuth2::Client.new('admin', 'sb70uDUEYK1IkE5LB2xdBkTJRIQNhBnaOYu1ig5EZW3UwpP4', {:site => 'http://shalka.slidev.org:8080', :token_url => '/api/oauth/token'})
    token = client.auth_code.get_token( params[:code], {:redirect_uri => 'http://shalka.slidev.org:3000/callback'})
    puts "Got access token"
    puts token.token
    cookies['token'] = token.token
    return
  end

  puts "We need to request auth url"
  #client = OAuth2::Client.new('243758069043602', 'e1c4851285ed90552aa439181936af1b', :site => 'https://graph.facebook.com/oauth/access_token')
  client = OAuth2::Client.new('admin', 'sb70uDUEYK1IkE5LB2xdBkTJRIQNhBnaOYu1ig5EZW3UwpP4', {:site => 'http://shalka.slidev.org:8080', :authorize_url => '/api/oauth/authorize'})
  flash[:redirect] = current_url
  puts "Setting redirect to #{current_url}"
  authorize_url =  client.auth_code.authorize_url(:redirect_uri => 'http://shalka.slidev.org:3000/callback')
  puts "URL is #{authorize_url}"
  redirect_to authorize_url
  return

    # Check our session for a valid api key, if not, redirect out
    if cookies['iPlanetDirectoryPro'] != nil
      logger.debug 'We have a cookie set.'
      SessionResource.auth_id = cookies['iPlanetDirectoryPro']
      Rails.logger.debug { "SessionResource.auth_id set to #{SessionResource.auth_id}" }
      # Get the state unique id and state to identify and key logging
      session[:full_name] = Check.new(SessionResource.auth_id).full_name
    else
      logger.debug { "No cookie set" }
    end
  end

  def render_404
   respond_to do |format|
     format.html { render :file => "#{Rails.root}/public/404.html", :status => :not_found }
     #format.json { :status => :not_found}
     format.any  { head :not_found }
   end
  end

  def render_403
   respond_to do |format|
     format.html { render :file => "#{Rails.root}/public/403.html", :status => :forbidden }
     #format.json { :status => :not_found}
     format.any  { head :not_found }
   end
  end

end
