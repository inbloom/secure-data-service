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
    session[:oauth] = nil
    handle_oauth
  end
  
  rescue_from ActiveResource::ForbiddenAccess do |exception|
    logger.info { "Forbidden access."}
    flash[:error] = "You do not have access to view this."

    # If 403 happened during login, we don't have a valid :back, so render 403 page.
    # Otherwise redirect back with the flash set
    if request.headers['referer'].nil? or !request.headers['referer'].include?(request.host)  
        return render :status => :forbidden, :layout=> false, :file => "#{Rails.root}/public/403.html"
    end
    redirect_to :back
  end
  
  rescue_from ActiveResource::ServerError do |exception|
    logger.error {"Exception on server, clearing your session."}
    reset_session
    SessionResource.auth_id = nil
    flash[:error] = "There was a problem in the API."
    #redirect_to :back
  end

  rescue_from OAuth2::Error do |exception|
    logger.error {"Oauth invalid, clearing your session."}
    reset_session
    SessionResource.auth_id = nil
    render :auth_error_page
    #redirect_to :back
  end
  
  def callback
    #TODO: disable redirects to other domains
    redirect_to session[:entry_url] unless session[:entry_url].include? '/callback'
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
      session[:entry_url] = current_url
      session[:oauth] = oauth 
    end
    if oauth.enabled?
      if oauth.token != nil
        SessionResource.access_token = oauth.token
        Check.url_type = "check"
        session[:full_name] ||= Check.get("")["full_name"]    
      elsif params[:code] && !oauth.has_code
        SessionResource.access_token = oauth.get_token(params[:code])
      else
        redirect_to oauth.authorize_url + "&state=" + CGI::escape(form_authenticity_token)
      end
    else
      logger.info { "OAuth disabled."}
    end

  end

end
