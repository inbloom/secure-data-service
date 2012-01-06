class ApplicationController < ActionController::Base
  protect_from_forgery
  before_filter :check_login
  
  
  def begin_authenticate(authentication)
    redirect_to authentication + "?RelayState=" + request.url
  end
  
  def check_login
    # Check our session for a valid api key, if not, redirect out
    logger.info "Cookies: #{cookies['iPlanetDirectoryPro']}"
    if cookies['iPlanetDirectoryPro'] != nil
      logger.debug 'We have a cookie set.'
      session['ADMIN_SESSION_ID'] = request.headers['iPlanetDirectoryPro']
    end
  end
end
