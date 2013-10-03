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
    raise "Edorg '" + edOrgId + "' not found in educationOrganization collection" if @edOrg.nil?

    # Get app data
    load_apps()
    appId = params[:application_authorization][:appId]
    @app = @apps_map[appId]
    raise "Application '" + appId + "' not found in sli.application" if @app.nil?
    @app_description = app_description(@app)

    # Get developer-enabled edorgsfor the app.  NOTE: Even though the field is
    # sli.application.authorized_ed_orgs[] these edorgs are called "developer-
    # enabled" or just "enabled" edorgs.
    @enabled_ed_orgs = array_to_hash(@app.authorized_ed_orgs)

    # Get edOrgs already authorized in <tenant>.applicationAuthorization.edorgs[]
    # The are the "authorized" (by the edOrg admin) edorgs for the app
    @appAuth = ApplicationAuthorization.find(appId)
    @authorized_ed_orgs = array_to_hash(@appAuth.edorgs)

    # Load up edOrg data
    load_edorgs()

    roots_info = ""
    @edinf[ROOT_ID][:children].each do |cid|
      roots_info += cid + ": " + @edinf[cid].to_s + "\n"
    end
    
#-#     @debug = "edorgId=" + edOrgId \
#-#               + "\n" + "params:\n" + params.to_s() \
#-#               + "\n" + "@app is:\n" + @app.to_s() \
#-#               + "\n" + "@app.authorized_ed_orgs=" + @app.authorized_ed_orgs.to_s() \
#-#               + "\n" + "@appAuth.appid:" + @appAuth.appId.to_s() \
#-#               + "\n" + "@appAuth.edorgs:" + @appAuth.edorgs.to_s() \
#-#               + "\n" + "@edinf.keys.length: " + @edinf.keys.length.to_s() \
#-#               + "\n" + "Roots children:\n" + roots_info \
#-#               + "\n" + "@edinf[ROOT_ID]:\n" + @edinf[ROOT_ID].to_s \
#-#               + ""
    @debug = ""

    @edorg_tree_html = "<ul>\n" + render_html(ROOT_ID, 0) + "</ul>\n"

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
      updates[:edorgs] = [affected_edorg]
      success = success && @application_authorization.update_attributes(updates)
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

  # Load up all the edOrgs.  Creates:
  #    @edinf - Map of edOrg ID to the tree
  #    @enabled_ed_orgs - Map of IDs enabled
  #    @authorized_ed_orgs - Map of IDs authorized
  #
  def load_edorgs()

    @edinf = {}
    root_ids = []
    
    # Get all edOrgs, include only needed fields
    allEdOrgs = EducationOrganization.findAllInChunks({"includeFields" => "parentEducationAgencyReference,nameOfInstitution,stateOrganizationId,organizationCategories"})
    allEdOrgs.each do |eo|

      @edinf[eo.id] = { :edOrg => eo, :id => eo.id, :name => eo.nameOfInstitution, :children => [],
                        :enabled => @enabled_ed_orgs.has_key?(eo.id), :authorized => @authorized_ed_orgs.has_key?(eo.id)
                      }

      parents = eo.parentEducationAgencyReference
      if parents.nil?
        parents = []
      else
        # Handle arrays of arrays due to migration script issues
        parents = parents.flatten(1)
      end

      @edinf[eo.id][:parents] = parents
      
      # Track root edorgs (with no parents)
      if parents.empty?
        root_ids.push(eo.id)
      end

    end

    # Init immediate children of each edorg by inverting parent relationship
    @edinf.keys.each do |id|
      @edinf[id][:parents].each do |pid|
        if !@edinf.has_key?(pid)
          # Dangling reference to nonexistent parent
          raise "Edorg '" + id + "' parents to nonexistent '" + pid.to_s() + "'"
        else
          @edinf[pid][:children].push(id)
        end
      end
    end

    # Create fake root edOrg and parent all top level nodes to it
    root_edorg = { :id => ROOT_ID, :parents => [], :children => root_ids,
                   :enabled => true, :authorized => true, :name => "All EdOrgs", 
                   :edOrg => { :id => ROOT_ID, :parentEducationAgencyReference  => [], }
                 }
    @edinf[ROOT_ID] = root_edorg
    root_ids.each do |id|
      @edinf[id][:parents] = [ ROOT_ID ]
    end

    # Compile counts across whole tree and build "by-type" category nodes
    @id_counter = 0
    count_children(ROOT_ID, {})
    
  end

  # Traverse graph and count children and descendants and put into @edinf map
  # Sets :nchild and :ndesc in the node with given ID
  # Replaces :children array with array of nodes by type
  def count_children(id, seen)
    # Trap cycles
    if seen.has_key?(id)
      raise "CYCLE in EdOrg hierarchy includes EdOrg id '" + id + "'"
    else
      seen[id] = true
    end
    
    nchild = @edinf[id][:children].length
    @edinf[id][:nchild] = nchild
    ndesc = nchild
    by_type = {}

    # Sort children by name
    compare_name = ->(a,b) { @edinf[a][:name] <=> @edinf[b][:name] }
    @edinf[id][:children].sort!( & compare_name )

    @edinf[id][:children].each do |cid|
      count_children(cid, seen.clone)
      ndesc += @edinf[cid][:ndesc]
      ctype = get_edorg_type(cid)
      if by_type.has_key?(ctype)
        by_type[ctype][:children].push(cid)
        by_type[ctype][:nchild] += 1
        by_type[ctype][:ndesc] += @edinf[cid][:ndesc] + 1
        # Aggregate the enabled/authorized status
        by_type[ctype][:enabled] = by_type[ctype][:enabled] && @edinf[cid][:enabled]
        by_type[ctype][:authorized] = by_type[ctype][:authorized] && @edinf[cid][:authorized]
      else
        new_id = CATEGORY_NODE_PREFIX + @id_counter.to_s
        @id_counter += 1
        by_type[ctype] = { :id => new_id, :name => ctype, :parents => [ id ], :children => [ cid ], :nchild => 1, :ndesc => @edinf[cid][:ndesc] + 1,
                           :enabled => @edinf[cid][:enabled], :authorized => @edinf[cid][:authorized]
                         }
      end
    end

    # Save accumulated descendant info for whole edorg
    @edinf[id][:ndesc] = ndesc

    # Replace children array with arrays of children by type
    new_children = []
    by_type.each do |ctype, cinf|
      new_children.push(cinf[:id])
      @edinf[cinf[:id]] = cinf
    end
    @edinf[id][:children] = new_children
    
  end

  # Format app description
  def app_description(a)
    s = ""
    s += a.name
    s += " (id " + a.client_id + ")" if !is_empty(a.client_id)
    s += ", v. " + a.version if !is_empty(a.version)
    if a.isBulkExtract
      s += " -- Bulk Extract"
    else
      s += " -- non-Bulk Extract"
    end
    return s
  end

  # String is neither nil nor empty?
  def is_empty(s)
    return true if s.nil?
    return true if s.length == 0
    return false
  end

  # render_html - Render HTML markup for the node and its children
  # Render a <li> element for the node, and if children exist, render
  # a <ul> with <li>'s for the children.
  # Each <li> will have a checkbox with the edOrg ID, with "checked"
  # set if the edOrg is currently selected (or if it is a "category" node
  # and all it's children are selected).  The <li> will be given
  # a class="collapsed" if it should be collapsed initially.  This
  # will be determined by number of children being fewer than some
  # threshold.
  # Example <li>:
  #    <li class="collapsed"><input type="checkbox" id="edorg_1">EdOrg 1
  def render_html(id, level)
    # <LI> part
    indent = "  " * level
    result = indent + "<li"
    maxchild = 10
    expand_level = 3
    eo = @edinf[id]
    nchildren = eo[:children].length
    # Auto-collapse nodes with "large" (per maxchild) number of children, or if they
    # are deeply nexted (per expand_level), but don't collapse a single child,
    # regardless of depth
    collapsed = nchildren > 0 && (level >= expand_level && nchildren >= 2 || nchildren > maxchild)
    result += " class=\"collapsed\"" if collapsed
    result += ">"

    # <input>
    is_category = id.start_with?(CATEGORY_NODE_PREFIX) || id == ROOT_ID
    if eo[:enabled]
      result += "<input type=\"checkbox\""
      if !id.start_with?(CATEGORY_NODE_PREFIX) && !is_empty(id)
        result += " id=\"" + id + "\""
      end
      result += " checked" if eo[:authorized]
      result += ">"
    end

    # Text of the node.  Italicize if not enabled
    result += "<span"
    result += " class=\"categorynode\"" if is_category
    result += ">"
    result += "<i>" if !eo[:enabled]
    result += eo[:name]
    result += "</i>" if !eo[:enabled]
    # Add counts.  Note that eo[:nchild] is the number of direct child EdOrgs not the number of child display nodes
    nchild = eo[:nchild]
    ndesc = eo[:ndesc]
    if ndesc > 1
      if ndesc == nchild
        countstr = nchild.to_s
      else
        countstr = nchild.to_s + "/" + ndesc.to_s
      end
      result += "<span class=\"treecounts\"> (" + countstr + ")</span>"
    end
    result += "</span>\n"
    
    # Children
    if nchildren > 0
      result += indent + "<ul>\n"
      eo[:children].each do |cid|
        result += render_html(cid, level + 1)
      end
      result += indent + "</ul>\n"
    end

    return result
  end
  
  # Convert array to map
  def array_to_hash(a)
    result = {}
    a.each do|elt|
      result[elt] = true
    end
    return result
  end

  # Get best guess at edorg type
  def get_edorg_type(id)
    cats = @edinf[id][:edOrg].organizationCategories
    if cats.nil? || cats.empty?
      return "Unknown"
    end
    cats.uniq!
    if cats.length == 1
      return cats[0]
    end
    # Return whole list joined together
    return cats.sort.join("/")
  end
   
end
