class ApplicationController < ActionController::Base
  protect_from_forgery
  before_filter :check_login
  
  rescue_from ActiveResource::UnauthorizedAccess do |exception|
    logger.info { "Unauthorized Access: Redirecting..." }
    begin_authenticate(exception.response['WWW-Authenticate'])    
  end
  
  def begin_authenticate(authentication)
    redirect_to authentication + "?RelayState=" + request.url
  end
  
  def check_login
    # Check our session for a valid api key, if not, redirect out
    if cookies['iPlanetDirectoryPro'] != nil
      logger.debug 'We have a cookie set.'
      SessionModel.id = cookies['iPlanetDirectoryPro']
      Rails.logger.debug { "SessionModel.id set to #{SessionModel.id}" }
    else
      logger.debug { "No cookie set" }
    end
  end
end
