require "active_resource/base"
require "oauth_helper"

class ApplicationController < ActionController::Base
  # protect_from_forgery
  before_filter :handle_oauth
  rescue_from ActiveResource::ResourceNotFound, :with => :not_found
  
  rescue_from ActiveResource::UnauthorizedAccess do |exception|
    logger.debug {"401 detected"}
    logger.info { "Unauthorized Access: Redirecting..." }
    redirect_to exception.response['WWW-Authenticate'] + "?RelayState=#{current_url}"
  end
  
  rescue_from ActiveResource::ForbiddenAccess do |exception|
    logger.info { "Forbidden access."}
    flash[:error] = "You do not have access to view this."
    redirect_to :back
    # raise exception
  end
  
  rescue_from ActiveResource::ServerError do |exception|
    logger.error {"Exception on server, clearing your session."}
    reset_session
    SessionResource.auth_id = nil
    flash[:error] = "There was a problem in the API."
    redirect_to :back
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


  private 
  def not_found
    logger.debug {"Not found"}
    flash[:alert] = "No resource found with id: #{params[:id]}"
    redirect_to :back
  end

  def check_login
    # Check our session for a valid api key, if not, redirect out
    if cookies.has_key? 'iPlanetDirectoryPro'
      logger.debug 'We have a cookie set.'
      SessionResource.auth_id = cookies['iPlanetDirectoryPro']
      Rails.logger.debug { "SessionResource.auth_id set to #{SessionResource.auth_id}" }
      Check.url_type = "check"
      # Get the state unique id and state to identify and key logging
      session[:full_name] ||= Check.get("")["full_name"]
    else
      SessionResource.auth_id = nil
      logger.debug { "No cookie set" }
    end
  end
  

  
  def current_url
    request.url
  end

  def handle_oauth
    SessionResource.access_token = nil
    oauth = session[:oauth]
    if oauth.nil?
      oauth = OauthHelper::Oauth.new()
      oauth.entry_url = current_url
      session[:oauth] = oauth 
    end
    if oauth.enabled?
      if oauth.token != nil
        logger.info { "OAuth access token is #{oauth.token}"}
        SessionResource.access_token = oauth.token
      elsif params[:code] && !oauth.has_code
        logger.info { "Requesting access token for  #{params[:code]}"}
        SessionResource.access_token = oauth.get_token(params[:code])
      else
        logger.info { "Redirecting to oauth auth URL:  #{oauth.authorize_url}"}
        redirect_to oauth.authorize_url + "&RealmName=Shared%20Learning%20Infrastructure" 
      end
    else
      logger.info { "OAuth disabled."}
      check_login
    end

  end
end
