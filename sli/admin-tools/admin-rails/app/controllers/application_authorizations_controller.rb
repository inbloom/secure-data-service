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

include EdorgTreeHelper

class ApplicationAuthorizationsController < ApplicationController
  before_filter :check_rights

  ROOT_ID = "root"
  CATEGORY_NODE_PREFIX = "cat_"
  
  #
  # Edit app authorizations
  #
  # Render a tree of all available edOrgs, w/ checkboxes
  # Each edOrg will be in one of three states:
  #      - disabled (grayed) -- not enabled by developer for the edOrg per the @apps "authorized_ed_orgs"
  #      - not authorized (unchecked) -- doesn't appear in the app authorizations' edorg list
  #      - authorized (checked) -- appears in app authorizaiton's edorg list
  #
  def edit

    # Get input objects
    edOrgId = session[:edOrgId]
    @edOrg = EducationOrganization.find(edOrgId)
    raise NoUserEdOrgError.new "Educational organization '" + edOrgId + "' not found in educationOrganization collection" if @edOrg.nil?

    # Get app data
    load_apps()
    appId = params[:application_authorization][:appId]
    @app = @apps_map[appId]
    raise "Application '" + appId + "' not found in sli.application" if @app.nil?

    @app_description = app_description(@app)
    @app_bulk_extract_description = if @app.isBulkExtract then "Bulk Extract" else "Non-Bulk Extract" end
    @app_version_description = if is_empty(@app.version) then "Unknown" else "v" + @app.version.to_s() end

    # Get developer-enabled edorgsfor the app.  NOTE: Even though the field is
    # sli.application.authorized_ed_orgs[] these edorgs are called "developer-
    # enabled" or just "enabled" edorgs.
    @enabled_ed_orgs = array_to_hash(@app.authorized_ed_orgs)

    # Get edOrgs already authorized in <tenant>.applicationAuthorization.edorgs[]
    # The are the "authorized" (by the edOrg admin) edorgs for the app
    @appAuth = ApplicationAuthorization.find(appId)
    edOrgTree = EdorgTree.new()
    @appAuth_edorgs = []
        @appAuth.edorgs.each do |edorg_entry|
          @appAuth_edorgs.push(edorg_entry.authorizedEdorg)
        end
    @edorg_tree_html = edOrgTree.get_authorization_tree_html([edOrgId], appId, is_sea_admin?, @appAuth_edorgs || [])
  end
  

  # NOTE this controller allows ed org super admins to enable/disable apps for their LEA(s)
  # It allows LEA(s) to see (but not change) their app authorizations
  def check_rights
    unless is_lea_admin? || is_sea_admin?
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
    @isLEAAdmin = is_lea_admin?

    # Get counts of apps ... have to look up each individually
    # For non-SEA admin apply a filter of the edOrgs in scope for the user
    @app_counts = {}
    if !@isSEAAdmin
      edorgs_in_scope = get_edorgs_in_scope()
    end
    
    allAuth = ApplicationAuthorization.findAllInChunks({})
    allAuth.each do |auth|
      auth2 = ApplicationAuthorization.find(auth.id)
      if !auth2.edorgs.nil?
        if @isSEAAdmin
          count = auth2.edorgs.length
        else
          count = 0
          auth2.edorgs.each do |id|
            count +=1 if edorgs_in_scope.has_key?(id.authorizedEdorg)
          end
        end
        @app_counts[auth.id] = count
      end
    end

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
    if legacy_sea_delegation_support && is_sea_admin?
      my_delegations = AdminDelegation.all
      @edorgs = (my_delegations.select{|delegation| delegation.appApprovalEnabled}).map{|cur| cur.localEdOrgId}
      @edorgs = @edorgs.sort{|a,b| a.casecmp(b)}
      @edorgs.each { |edorg|
        @application_authorizations[edorg] = ApplicationAuthorization.find(:all, :params => {'edorg' => edorg})
      }
    else
      eo = session[:edOrgId]
      raise NoUserEdOrgError.new "No education organization in session -- The user\'s educational organization may not exist. Please confirm that realms are set up properly and relevant educational organizations have been ingested." if !eo
      @edorgs = [eo]
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

    # Only allow update by SEA  or LEA admin.
    unless is_sea_admin? || is_lea_admin?
      logger.warn {'User is not SEA or LEA admin and cannot update application authorizations'}
      raise ActiveResource::ForbiddenAccess, caller
    end

    # Top level edOrg to expand
    edorg = session[:edOrgId]
    # EdOrgs selected using Tree Control
    added = params[:application_authorization][:edorgsAdded]
    added = "" if added.nil?
    added.strip!
    addedEdOrgs = added.split(/,/)

    removed = params[:application_authorization][:edorgsRemoved]
    removed = "" if removed.nil?
    removed.strip!
    removedEdOrgs = removed.split(/,/)

    # ID of app
    appId = params[:application_authorization][:appId]

    updates = {"appId" =>  appId, "authorized" => true, :edorgs => addedEdOrgs}
    @application_authorization = ApplicationAuthorization.find(params[:id])
    success = @application_authorization.update_attributes(updates)

    updates = {"appId" =>  appId, "authorized" => false, :edorgs => removedEdOrgs}
    @application_authorization = ApplicationAuthorization.find(params[:id])
    success = @application_authorization.update_attributes(updates)

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

  # Convert array to map
  def array_to_hash(a)
    result = {}
    if !a.nil?
      a.each do|elt|
        result[elt] = true
      end
    end
    return result
  end

   
  # Get edOrgs in user's scope (descendants of user's edOrg). This is optimized just
  # to get a map of the IDs for the purpose of filtering the "index" list
  def get_edorgs_in_scope()
    edinf = {}
    userEdOrg = session[:edOrgId]
    
    # Get all edOrgs, include only needed fields
    allEdOrgs = EducationOrganization.findAllInChunks({"includeFields" => "parentEducationAgencyReference"})
    allEdOrgs.each do |eo|
      edinf[eo.id] = { :id => eo.id, :children => [], }
      parents = eo.parentEducationAgencyReference
      if parents.nil?
        parents = []
      else
        # Handle arrays of arrays due to migration script issues
        parents = parents.flatten(1)
      end
      edinf[eo.id][:parents] = parents
    end

    # Init immediate children of each edorg by inverting parent relationship
    edinf.keys.each do |id|
      edinf[id][:parents].each do |pid|
        if !edinf.has_key?(pid)
          # Dangling reference to nonexistent parent
          raise "Edorg '" + id + "' parents to nonexistent '" + pid.to_s() + "'"
        else
          edinf[pid][:children].push(id)
        end
      end
    end

    # Now traverse it
    result = {}
    get_edorgs_in_scope_recursive(edinf, userEdOrg, result)
    return result
  end

  # Recursive traversal to get edOrg IDs in scope of user
  def get_edorgs_in_scope_recursive(edinf, id, result)
    result[id] = true
    return if !edinf.has_key?(id)
    edinf[id][:children].each do |cid|
      get_edorgs_in_scope_recursive(edinf, cid, result)
    end
  end

  # Format app description
  def app_description(a)
    s = ""
    s += a.name
    s += " (id " + a.client_id + ")" if !is_empty(a.client_id)
    return s
  end

  # String is neither nil nor empty?
  def is_empty(s)
    return true if s.nil?
    return true if s.length == 0
    return false
  end
end
