require "active_resource/base"
require "oauth_helper"

class ApplicationController < ActionController::Base
  protect_from_forgery
  before_filter :handle_oauth
  ActionController::Base.request_forgery_protection_token = 'state'
  
  rescue_from ActiveResource::UnauthorizedAccess do |exception|
    logger.info { "Unauthorized Access: Redirecting..." }
    session[:oauth] = nil
    handle_oauth
  end
  
  rescue_from ActiveResource::ForbiddenAccess do |exception|
    logger.info { "Forbidden access."}
    raise exception
  end
  
  rescue_from ActiveResource::ServerError do |exception|
    logger.error {"Exception on server, clearing your session."}
    SessionResource.access_token = nil
  end

  def callback
    #TODO: disable redirects to other domains
    redirect_to session[:oauth].entry_url unless session[:oauth].entry_url.include? '/callback'
    return
    #render :nothing => true
  end
  
  def current_url
    request.url
  end

  def handle_oauth
    SessionResource.access_token = nil
    oauth = session[:oauth]
    if oauth == nil 
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
        check = Check.get("")
        session[:full_name] ||= check["full_name"]   
        session[:adminRealm] = check["adminRealm"]
        session[:roles] = check["sliRoles"]
        session[:edOrg] = check["edOrg"]
      else
        logger.info { "Redirecting to oauth auth URL:  #{oauth.authorize_url}"}
        redirect_to oauth.authorize_url + "&Realm=Shared%20Learning%20Infrastructure&state=" + CGI::escape(form_authenticity_token)
      end
    else
      logger.info { "OAuth disabled."}
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
