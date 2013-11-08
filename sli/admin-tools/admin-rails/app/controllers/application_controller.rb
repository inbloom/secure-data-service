=begin

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

class ApplicationController < ActionController::Base
  protect_from_forgery
  before_filter :handle_oauth
  ActionController::Base.request_forgery_protection_token = 'state'

  class OutOfOrder < StandardError
  end

  class NoUserEdOrgError < StandardError
    def initialize(message)
      @message = message
    end

    def to_s
      @message
    end

  end

  unless Rails.application.config.consider_all_requests_local
    rescue_from NoUserEdOrgError do |exception|
      logger.error("Error occurred processing application authorizations for #{uid()}: #{exception}")
      render_412(exception.message)
    end
  end

  rescue_from ActiveResource::UnauthorizedAccess do |exception|
    logger.info { "Unauthorized Access: Redirecting..." }
    reset_session
    handle_oauth
  end

  rescue_from ActiveResource::ResourceNotFound do |exception|
    logger.info {"Resource not found."}
    render_404
  end

  rescue_from ActiveResource::ForbiddenAccess do |exception|
    logger.info { "Forbidden access."}
    reset_session
    render_403
  end

  rescue_from ActiveResource::ServerError do |exception|
    logger.error {"Exception on server"}
    render_500
    reset_session
    SessionResource.access_token = nil
  end

  rescue_from OutOfOrder do |exception|
    logger.error {"User has tried to access a resource before the proper steps have been followed"}
    render_realm_help
  end

  def callback
    redirect_to session[:entry_url] unless session[:entry_url].include? '/callback'
    return
  end

  def error
    logger.error {"Internal server error"}
    if params[:msg]
      logger.error {"Message is: " + params[:msg]}
    end
    render_500
  end
  
  def current_url
    request.url
  end

  def handle_oauth
    SessionResource.access_token = nil
    oauth = session[:oauth]
    if oauth == nil
      oauth = OauthHelper::Oauth.new()
      session[:entry_url] = current_url
      session[:oauth] = oauth
    end
    if oauth.enabled?
      if oauth.token != nil
        SessionResource.access_token = oauth.token
        logger.debug "TOKEN = #{oauth.token}"
        set_session
      elsif params[:code] && !oauth.has_code
        SessionResource.access_token = oauth.get_token(params[:code])
        set_session
      else
        @url = oauth.authorize_url + "&state=" + CGI::escape(form_authenticity_token)
        respond_to do |format|
          format.html {redirect_to @url}
          format.js { render 'layouts/redirect_to_login', :layout => false}
        end
      end
    else
      logger.info { "OAuth disabled."}
    end
  end

  def render_realm_help
    respond_to do |format|
      format.html { render :file => "#{Rails.root}/public/realm_help.html", :status => :not_found }
      #format.json { :status => :not_found}
      format.any  { head :not_found }
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

  def render_412(message=nil)
    if message.nil? || message.empty?
      @errorMessage = "No additional information available."
    else
      @errorMessage = message
    end
    respond_to do |format|
      format.html { render "#{Rails.root}/app/views/apps/errors/412", :formats => [:html], :status => :precondition_failed }
      format.any  { head :precondition_failed }
    end
  end

  def render_500
    respond_to do |format|
      format.html { render :file => "#{Rails.root}/public/500.html", :status => :internal_server_error }
      format.any  { head :not_found }
    end
  end

  def is_developer?
    session[:roles].include? "Application Developer"
  end

  def is_operator?
    session[:roles].include? "SLC Operator"
  end

  def is_lea_admin?
    session[:roles].include? "LEA Administrator"
  end

  def is_sea_admin?
    session[:roles].include? "SEA Administrator"
  end

  def is_realm_admin?
    session[:roles].include?("Realm Administrator")
  end

  def is_ingestion_user?
    session[:roles].include?("Ingestion User")
  end

  def is_it_admin?
    session[:roles].include?("IT Administrator")
  end

  # (1) hosted user with global EDORG_APP_AUTHZ right
  # (2) federated user with the right APP_AUTHORIZE for at least one edOrg (see edOrgRights in session check)
  def is_app_authorizer?
    if is_admin_realm_authenticated?
      return session[:rights].include?('EDORG_APP_AUTHZ')
    elsif ! session[:edOrgRights].nil? && ! session[:edOrgRights].empty?
      anyAppAuth =  session[:edOrgRights].any? do |edorg, rights|
        rights.include?('APP_AUTHORIZE')
      end
      return anyAppAuth
    end
    return false
  end

   def get_app_authorizer_edOrgs
     if is_admin_realm_authenticated?
         return [session[:edOrgId]] if session[:rights].include?('EDORG_APP_AUTHZ')
     end
     if session[:edOrgRights]
       edOrgsWithAppAuth = session[:edOrgRights].select do |edorg, rights|
         rights.include?('APP_AUTHORIZE')
       end
       return edOrgsWithAppAuth.keys
     end
     return []
   end

  def is_admin_realm_authenticated?
    session[:adminRealmAuthenticated]
  end

  def get_tenant
    check = Check.get ""
    return check["tenantId"]
  end


  def not_found
    raise ActionController::RoutingError.new('Not Found')
  end

  def set_session
    check = Check.get("")
    if check['authenticated'] == false
      raise ActiveResource::UnauthorizedAccess, caller
    end
    email = SupportEmail.get("")
    logger.debug { "Email #{email}"}
    session[:support_email] = email
    session[:full_name] ||= check["full_name"]
    session[:email] ||= check["email"]
    session[:adminRealmAuthenticated] = check["adminRealmAuthenticated"]
    session[:adminRealm] = check["adminRealm"]
    session[:roles] = check["sliRoles"]
    session[:edOrg] = check["edOrg"]
    session[:edOrgId] = check["edOrgId"]
    session[:edOrgRoles] = check["edOrgRoles"]
    session[:edOrgRights] = check["edOrgRights"]
    session[:external_id] = check["external_id"]
    session[:first_name] = check["first_name"]
    session[:last_name] = check["last_name"]
    session[:vendor] = check["vendor"]
    session[:tenant_id] = get_tenant()
    session[:rights] = check["rights"]
  end

  def uid
    check = Check.get ''
    return check["external_id"]
  end

end
