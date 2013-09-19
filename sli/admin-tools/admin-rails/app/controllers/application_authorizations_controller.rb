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


class ApplicationAuthorizationsController < ApplicationController
  before_filter :check_rights

  # Let us add some docs to this confusing controller.
  # NOTE this controller allows ed org super admins to 
  # enable/disable apps for their LEA.
  # SEA admin authorization not implemented yet.
  def check_rights
    unless is_sea_admin?
      logger.warn {'User is not sea admin and cannot access application authorizations'}
      raise ActiveResource::ForbiddenAccess, caller
    end
  end

  # GET /application_authorizations
  # GET /application_authorizations.json
  def index

    # Slurp all apps into a map of appId -> info
    @apps_map = {}
    App.all.each { |app| @apps_map[app.id] = app }

    # Invert apps map to get set of enabled apps by edOrg for filtering
    @edorg_apps = {}
    @apps_map.each do |appId, app|
      authorized_ed_orgs = app.authorized_ed_orgs
      if ! authorized_ed_orgs.nil?
        authorized_ed_orgs.each do |edOrg|
          if ! @edorg_apps.has_key?(edOrg)
            @edorg_apps[edOrg] = { appId => true }
          else
            @edorg_apps[edOrg][appId] = true
          end
        end
      end
    end
    
    @application_authorizations = {}
    if false and is_sea_admin?
      my_delegations = AdminDelegation.all
      @edorgs = (my_delegations.select{|delegation| delegation.appApprovalEnabled}).map{|cur| cur.localEdOrgId}
      @edorgs = @edorgs.sort{|a,b| a <=> b}
      @edorgs.each { |edorg|
        @application_authorizations[edorg] = ApplicationAuthorization.find(:all, :params => {'edorg' => edorg})
      }
    else
      @edorgs = [session[:edOrgId]]
      ApplicationAuthorization.cur_edorg = @edorgs[0]
      @application_authorizations[@edorgs[0]] = ApplicationAuthorization.all
    end
    # Get EDORGS for the authId
    respond_to do |format|
      format.html # index.html.erb
    end
  end

  # PUT /application_authorizations/1
  # PUT /application_authorizations/1.json
  def update
    edorg = params[:application_authorization][:edorg]
    ApplicationAuthorization.cur_edorg = edorg
    @application_authorization = ApplicationAuthorization.find(params[:id], :params => {:edorg => edorg})
    appId = params[:application_authorization][:appId]
    approve = true

    # Get all descendants of edorg and grant or revoke all of them for this app
    all_edorgs = EducationOrganization.get_edorg_descendants(edorg)

    if(params[:commit] == "Deny")
      approve = false
    end
    updates = {"appId" =>  appId, "authorized" => approve}

    respond_to do |format|
      if @application_authorization.update_attributes(updates)
        format.html { redirect_to application_authorizations_path, notice: edorg}
        #format.html {redirect_to :action => 'index', notice: 'Application authorization was succesfully updated.'}
        format.json { head :ok }
      else
        format.html { render action: "edit" }
        format.json { render json: @application_authorization.errors, status: :unprocessable_entity }
      end
    end
  end
end
