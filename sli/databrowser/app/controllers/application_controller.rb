require "active_resource/base"
require "oauth_helper"

class ApplicationController < ActionController::Base
  protect_from_forgery
  ActionController::Base.request_forgery_protection_token = 'state'

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
        Check.url_type = "check"
        session[:full_name] ||= Check.get("")["full_name"]    
      elsif params[:code] && !oauth.has_code
        logger.info { "Requesting access token for  #{params[:code]}"}
        SessionResource.access_token = oauth.get_token(params[:code])
      else
        logger.info { "Redirecting to oauth auth URL:  #{oauth.authorize_url}"}
        redirect_to oauth.authorize_url + "&state=" + CGI::escape(form_authenticity_token)
      end
    else
      logger.info { "OAuth disabled."}
    end

  end
end
