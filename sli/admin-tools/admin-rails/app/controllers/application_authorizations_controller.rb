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

  # NOTE this controller allows ed org super admins to enable/disable apps for their LEA(s)
  # It allows LEA(s) to see (but not change) their app authorizations
  def check_rights
    unless is_lea_admin? or is_sea_admin?
      logger.warn {'User is not lea or sea admin and cannot access application authorizations'}
      raise ActiveResource::ForbiddenAccess, caller
    end
  end

  # GET /application_authorizations
  # GET /application_authorizations.json
  def index

    load_apps()

    # Use this in the template to enable buttons
    @isSEAAdmin = is_sea_admin?

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
      # Some apps such as dashboard and databrowser may have allowed_for_all_edorgs set
      if app.allowed_for_all_edorgs
        if ! @edorg_apps.has_key?(session[:edOrgId])
          @edorg_apps[session[:edOrgId]] = { appId => true }
        else
          @edorg_apps[session[:edOrgId]][appId] = true
        end
      end
    end
    
    # We used to support a mode where the SEA saw edOrgs for which is
    # was delegated admin access by the edOrgs (usu. an LEA)
    legacy_sea_delegation_support = false
    
    @application_authorizations = {}
    if legacy_sea_delegation_support and is_sea_admin?
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

    load_apps()

    # Only allow update by SEA admin.  Should not really trigger this since the
    # buttons are grayed out and non-SEAadmin use is not invited to get here
    unless is_sea_admin?
      logger.warn {'User is not sea admin and cannot update application authorizations'}
      raise ActiveResource::ForbiddenAccess, caller
    end

    # Top level edOrg to expand
    edorg = params[:application_authorization][:edorg]

    # ID of app
    appId = params[:application_authorization][:appId]

    # Will affect a different set of affected edOrgs depending on whether app is Bulk Extract or not
    isBulkExtract = @apps_map[appId].isBulkExtract

    # Get all descendants of edorg and grant or revoke all of them for this app
    if isBulkExtract
      all_edorgs = EducationOrganization.get_edorg_children(edorg).map { |edOrg| edOrg.id }
      all_edorgs.push(edorg)
    else
      all_edorgs = EducationOrganization.get_edorg_descendants(edorg)
    end

    # Action is approve/deny based on waht button was used
    approve = true
    if(params[:commit] == "Deny")
      approve = false
    end

    # Loop through affected edorgs and update authorizations
    success = true
    updates = {"appId" =>  appId, "authorized" => approve}
    all_edorgs.sort().each do |affected_edorg|
      ApplicationAuthorization.cur_edorg = affected_edorg
      @application_authorization = ApplicationAuthorization.find(params[:id], :params => {:edorg => affected_edorg})
      success = success and @application_authorization.update_attributes(updates)
      raise "error" if ! success
      break if ! success
    end
    
    # Redirect to response page
    ApplicationAuthorization.cur_edorg = edorg
    respond_to do |format|
      if success
        format.html { redirect_to application_authorizations_path, notice: edorg}
        #format.html {redirect_to :action => 'index', notice: 'Application authorization was succesfully updated.'}
        format.json { head :ok }
      else
        format.html { render action: "edit" }
        format.json { render json: @application_authorization.errors, status: :unprocessable_entity }
      end
    end
  end

  # Load up all apps into @apps_map
  def load_apps()
    # Slurp all apps into @apps_map = a map of appId -> info
    @apps_map = {}
    allApps = App.findAllInChunks({})
    allApps.each { |app| @apps_map[app.id] = app }
  end

end
