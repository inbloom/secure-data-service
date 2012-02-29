require "active_resource/base"

class ApplicationController < ActionController::Base
 # protect_from_forgery
  before_filter :handle_oauth
  
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
    SessionResource.access_token = nil
  end


  def callback
    #TODO: disable redirects to other domains
    redirect_to session[:oauth].entry_url unless session[:oauth].entry_url.include? '/callback'
    return
    if params[:state]
      redirectUrl = CGI::unescape(params[:state])
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

  def handle_oauth
    SessionResource.access_token = nil
    oauth = session[:oauth]
    if oauth == nil 
      oauth = Oauth.new()
      oauth.entry_url = current_url
      session[:oauth] = oauth 
    end
    if oauth.enabled
      if oauth.token != nil
        logger.info { "OAuth access token is #{oauth.token}"}
        SessionResource.access_token = oauth.token
      elsif params[:code] && !oauth.has_code
        logger.info { "Requesting access token for  #{params[:code]}"}
        SessionResource.access_token = oauth.get_token(params[:code])
      else
        logger.info { "Redirecting to oauth auth URL:  #{oauth.authorize_url}"}
        redirect_to oauth.authorize_url  
      end
    else
      logger.info { "OAuth disabled."}
      check_login
    end

  end
  
  def check_login
    # Check our session for a valid api key, if not, redirect out
    if cookies['iPlanetDirectoryPro'] != nil
      logger.debug 'We have a cookie set.'
      SessionResource.auth_id = cookies['iPlanetDirectoryPro']
      Rails.logger.debug { "SessionResource.auth_id set to #{SessionResource.auth_id}" }
      # Get the state unique id and state to identify and key logging
      session[:full_name] = Check.new(SessionResource.auth_id, nil).full_name
    else
      logger.debug { "No cookie set" }
      SessionResource.auth_id = nil
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
