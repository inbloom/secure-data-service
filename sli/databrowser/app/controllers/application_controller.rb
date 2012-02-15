require "active_resource/base"
class ApplicationController < ActionController::Base
  protect_from_forgery
  before_filter :check_login
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


  private 
  def not_found
    logger.debug {"Not found"}
    flash[:alert] = "No resource found with id: #{params[:id]}"
    redirect_to :back
  end
  
  def begin_authenticate(authentication)
    redirect_to authentication + "?RelayState=" + current_url
  end
  
  def current_url
    "http://" + request.host_with_port + request.fullpath
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
      logger.debug { "No cookie set" }
    end
  end
end
