require 'base64'
require 'rexml/document'
require 'IDP'

class IdpController < ApplicationController
  
  def index
    
    if params[:SAMLRequest]
      begin
        saml = IDP::SAMLRequest.new params[:SAMLRequest]
        destination_map = {'https://devopenam1.slidev.org:80/idp2/SSORedirect/metaAlias/idp' => 'NY'}
        tenant = destination_map[saml.destination]
        @idp_name = tenant
        session[:tenant] = tenant
      rescue
        @error = "Error parsing SAMLRequest!"
      end
    else
      @error = "Warning.  No SAMLRequest parameter found"
    end
    
    @users = Teacher.all() + Staff.all()
    @roles = ['IT Administrator', 'Leader', 'Educator', 'Aggregator']
    @idp_name = '<Unknown Realm>' unless tenant
  end

  def login
    @staffUniqueStateId = params[:selected_user]
    @roles = params[:selected_roles].join ','
    @tenant = session[:tenant]
  end

  def logout
    # do something to trigger logout
  end
end
