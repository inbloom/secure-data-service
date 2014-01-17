=begin
#--

Copyright 2012-2013 inBloom, Inc. and its affiliates.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

=end


require "active_resource/base"
require "oauth_helper"
require "breadcrumb"

#
# This is the standard base controller class that all others in the databrowser
# (and Rails) inherits from. We take advantage and put some common
# functionality here
#
# Namely we have a before_filter that will handle the basic oauth functionality
# to ensure you have a valid API token
#
# We also catch most of the common ActiveResource exceptions and force them to 
# display flash messages, clear your rails session, etc.
class ApplicationController < ActionController::Base
  protect_from_forgery
  ActionController::Base.request_forgery_protection_token = 'state'
  before_filter :handle_oauth
  before_filter :handle_breadcrumb
  
  rescue_from ActiveResource::ResourceNotFound, :with => :not_found
  
  rescue_from ActiveResource::UnauthorizedAccess do |exception|
    logger.debug {"401 detected"}
    logger.info { "Unauthorized Access: Redirecting..." }
    reset_session
    handle_oauth
  end
  
  rescue_from ActiveResource::ForbiddenAccess do |exception|
    logger.info { "Forbidden access."}
    flash[:error] = "Sorry, you don't have access to this page. If you feel like you are getting this page in error, please contact your administrator."

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
  
  #This method is where OAuth will finish. Basically after you send your credentials to the Api
  #it will redirect to this method to try and finish the OAuth process.
  #
  #From here we bounce you back internally to the page you originally wanted to see.
  def callback
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
    flash[:alert] = "No resource found with id: #{params[:id] || params[:search_id]}"
    redirect_to :back
  end
  
  def current_url
    request.url
  end

  #This method is critical.
  #This method is a before_filter that gets run on every request to ensure that
  #you have an OAuth token with the Api, and starts the process if you don't have
  #one.
  #
  #After you've successfully logged in, it makes a call to system/session/check to
  #get your name and display it in the header of the databrowser. It represents the
  #first successful call to the Api.
  def handle_oauth
    @header = nil
    @footer = nil
    SessionResource.access_token = nil
    oauth = session[:oauth]
    if oauth.nil?
      oauth = OauthHelper::Oauth.new()
      session[:entry_url] = current_url
      session[:oauth] = oauth 
    end
    if oauth.enabled?
      if oauth.token != nil
        begin
          @header = PortalHeader.get("", :isAdmin => true)
          @footer = PortalFooter.get("", :isAdmin => true)
        rescue Exception => e
          logger.warn {"We couldn't load the portal header and footer #{e.message}"}
        end
        SessionResource.access_token = oauth.token
        logger.info {"*********  Token is #{oauth.token}"}
        Check.url_type = "check"
        check = Check.get("")
        session[:full_name] = check["full_name"]
        session[:is_admin] = check["isAdminUser"]
        if !session[:is_admin] && !session[:is_admin].nil?
          logger.warn {"User is not an administrator, they cannot use this application"}
          render :auth_error_page
        end

      elsif params[:code] && !oauth.has_code
        SessionResource.access_token = oauth.get_token(params[:code])
      else
        redirect_to oauth.authorize_url + "&state=" + CGI::escape(form_authenticity_token)
      end
    else
      logger.info { "OAuth disabled."}
    end

  end

  # this routine handles adjustment of the session breadcrumbtrail, which
  # is an array of breadcrumb objects.  Each such object has a name (for
  # display) and a link; the list is presented at the top of each
  # databrowser page so the user knows what s/he clicked on to get to
  # the current page and has a way to return to a previous page 
  # with a single click.
  def handle_breadcrumb
    logger.debug("===================")
    # logger.debug("handling breadcrumb for <" + current_url + ">")

    trail = session[:breadcrumbtrail]

    # remove any parameters from end of string for breadcrumb trail
    urlNoParams = current_url
    qIndex = current_url.index('?')
    if not qIndex.nil? then
      urlNoParams = current_url.slice(0,qIndex)
      # logger.debug("breadcrumb handling, trimmed " + current_url + " to " + urlNoParams)
    end
    
    # if this url ends with "/callback", we don't adjust the breadcrumb trail at all.
    logger.debug("checking <" + urlNoParams + "> for /callback")
    if urlNoParams.end_with? "/callback" then 
      return 
    end

    if trail.nil? then
      # we must have just started a session; create the
      # first breadcrumb and the array that holds the breadcrumbs in the session
      logger.debug("creating breadcrumb trail")
      bc = Breadcrumbhelper::Breadcrumb.new "home", urlNoParams, current_url
      trail = [ bc ]
    else
      # look through our array of breadcrumbs to see if this URL is already in it.
      matchedIndex = -1		# set to real index if we find a match
      trail.each_with_index do |crumb, current_index|
        # logger.debug("matching against <" + crumb.strippedLink + ">")
        if crumb.strippedLink.eql? urlNoParams then
          # we've found the URL in our list -- save the index to trim the array
          matchedIndex = current_index
	  # logger.debug("matched URL at " + matchedIndex.to_s)
	  break
        end
      end

      if matchedIndex >= 0 then
        # we have this URL in our list, so trim the list.
        trail = trail.slice(0..matchedIndex)
	# logger.debug("slicing urlArray")
      else
        # this URL is not in our list; we determine a user-friendly name for the URL, 
        # create a breadcrumb, and add it to the end of the list
	name = getUserFriendlyUrlName(urlNoParams)
        trail.push Breadcrumbhelper::Breadcrumb.new name, urlNoParams, current_url
	# logger.debug("pushing new link onto array")
      end
    end

    # logger.debug("current_url = " + current_url)
    # trail.each do |bc|
      # logger.debug("  " + bc.name + "  " + bc.link)
    # end

    session[:breadcrumbtrail] = trail
    # logger.debug("==========================")
  end

  # split the given URL on its slash chars; for each element starting from the 
  # last, discard any elements that are made up of hex chars (plus '-') or 
  # are "entities"
  private
  def getUserFriendlyUrlName(urlNoParams)
    urlArray = urlNoParams.split("\/")
    name = ""
    urlArray.reverse_each do |urlPart|
      name = urlPart
      if urlPart.eql? "entities" then next end
      if hex?(urlPart) then next end
      break  # if we get here, we've found some other string, and we're done
    end
    return name
  end

  private
  def hex? str
    # return true if the given string has only hex characters and/or "-", "_" or "i" 
    # (that last to handle URLs which have _id); this returns false otherwise
    str.each_char do |ch|
      if "0123456789abcdefABCDEF-_i".index(ch).nil? then return false end
    end
    return true
  end

end
