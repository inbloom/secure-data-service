require "active_resource/base"
class ApplicationController < ActionController::Base
  protect_from_forgery
  before_filter :check_login
  
  rescue_from ActiveResource::UnauthorizedAccess do |exception|
    logger.info { "Unauthorized Access: Redirecting..." }
    redirect_to exception.response['WWW-Authenticate'] + "?RelayState=#{current_url}"
  end
  
  rescue_from ActiveResource::ForbiddenAccess do |exception|
    logger.info { "Forbidden access."}
    super
  end
  
  rescue_from ActiveResource::ServerError do |exception|
    logger.error {"Exception on server, clearing your session."}
    SessionResource.auth_id = nil
  end

  
  def begin_authenticate(authentication)
    redirect_to authentication + "?RelayState=" + current_url
  end
  
  def current_url
    "http://" + request.host_with_port + request.fullpath
  end
  
  def check_login
    # Check our session for a valid api key, if not, redirect out
    if cookies['iPlanetDirectoryPro'] != nil
      logger.debug 'We have a cookie set.'
      SessionResource.auth_id = cookies['iPlanetDirectoryPro']
      Rails.logger.debug { "SessionResource.auth_id set to #{SessionResource.auth_id}" }
    else
      logger.debug { "No cookie set" }
    end
  end
end
