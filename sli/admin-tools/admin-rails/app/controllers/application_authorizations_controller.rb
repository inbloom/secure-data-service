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
    
    @debug = ""

    @edorg_tree_html = "<ul>\n" + render_html(nil, ROOT_ID, 0, {}) + "</ul>\n"

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
            count +=1 if edorgs_in_scope.has_key?(id)
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

    # Only allow update by SEA  or LEA admin.
    unless is_sea_admin? || is_lea_admin?
      logger.warn {'User is not SEA or LEA admin and cannot update application authorizations'}
      raise ActiveResource::ForbiddenAccess, caller
    end

    # Top level edOrg to expand
    edorg = session[:edOrgId]
    # EdOrgs selected using Tree Control
    added = params[:application_authorization][:edorgsAdded]
    added.strip!
    addedEdOrgs = added.split(/,/)

    removed = params[:application_authorization][:edorgsRemoved]
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

  # Load up all the edOrgs.  Creates:
  #    @edinf - Map of edOrg ID to the tree
  #    @enabled_ed_orgs - Map of IDs enabled
  #    @authorized_ed_orgs - Map of IDs authorized
  #
  def load_edorgs()

    @edinf = {}
    root_ids = []
    userEdOrg = session[:edOrgId]
    
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
    if is_sea_admin?
      root_children = root_ids
    else
      root_children = [ userEdOrg ]
    end
    
    root_edorg = { :id => ROOT_ID, :parents => [], :children => root_children,
                   :enabled => true, :authorized => true, :name => "All EdOrgs", 
                   :edOrg => { :id => ROOT_ID, :parentEducationAgencyReference  => [], }
                 }
    @edinf[ROOT_ID] = root_edorg

    # Allow SEA admin to see everything, including edOrgs not parented
    # up to SEA.  LEA admin just his own edorg
    if is_sea_admin?
      root_ids.each do |id|
        @edinf[id][:parents] = [ ROOT_ID ]
      end
    else
      @edinf[userEdOrg][:parents] = [ ROOT_ID ]
    end

    # Compile counts across whole tree and build "by-type" category nodes
    @id_counter = 0
    build_tree(ROOT_ID, {})
    
  end

  # Traverse graph and count children and descendants and put into @edinf map
  # Sets :nchild and :ndesc in the node with given ID
  # Replaces :children array with array of nodes by type

  def build_tree(id, seen)
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
      build_tree(cid, seen.clone)
      ndesc += @edinf[cid][:ndesc]
      ctype = get_edorg_type(cid)
      if by_type.has_key?(ctype)
        by_type[ctype][:children].push(cid)
        by_type[ctype][:nchild] += 1
        by_type[ctype][:ndesc] += @edinf[cid][:ndesc] + 1
        # Aggregate the enabled/authorized status.  Enabled aggregates on an "or" basis,
        # and authorized aggregates on an "and" basis, and only for enabled children
        by_type[ctype][:enabled] = by_type[ctype][:enabled] || @edinf[cid][:enabled]
        if @edinf[cid][:enabled]
          by_type[ctype][:authorized] = by_type[ctype][:authorized]  && @edinf[cid][:authorized]
        end
      else
        new_id = CATEGORY_NODE_PREFIX + @id_counter.to_s
        @id_counter += 1
        by_type[ctype] = { :id => new_id, :name => ctype, :parents => [ id ], :children => [ cid ], :nchild => 1, :ndesc => @edinf[cid][:ndesc] + 1,
                           :enabled => @edinf[cid][:enabled], :authorized => !@edinf[cid][:enabled] || @edinf[cid][:authorized]
                         }
      end
    end

    # Save accumulated descendant info for whole edorg
    @edinf[id][:ndesc] = ndesc

    # Replace children array with arrays of children by type.
    # Also aggregate authorized status of children
    new_children = []

    # Aggregate the enabled/authorized status.  Enabled aggregates on an "or" basis,
    # and authorized aggregates on an "and" basis, and only for enabled children
    agg_enabled = true
    agg_authorized = true
    by_type.each do |ctype, cinf|
      new_children.push(cinf[:id])
      @edinf[cinf[:id]] = cinf
      agg_enabled = agg_enabled || cinf[:enabled]
      if cinf[:enabled]
        agg_authorized = agg_authorized && cinf[:authorized]
      end
    end
    @edinf[id][:children] = new_children
    is_category = id.start_with?(CATEGORY_NODE_PREFIX) || id == ROOT_ID
    if is_category
      @edinf[id][:enabled] = agg_enabled
      @edinf[id][:authorized] = agg_authorized
    end
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
  def render_html(parent_id, id, level, seen)
    # <LI> part
    indent = "  " * level
    result = indent + "<li"
    maxchild = 10
    expand_level = 3
    eo = @edinf[id]

    # Detect repeat subtrees: if a node has multiple parents (and is
    # thus reachable by multiple paths from parents to children),
    # consider all but the last ID in the parents list as "aliases".
    # Using the last ID, as opposed to the first, will have the effect
    # of the "real" node being rendered first in the recursion, so
    # that, in turn, we can label the "alias" nodes with a notice to
    # consult the real node "above" it.  This in turn means that the
    # real node will appear first as the user scans the tree from top
    # to bottom.
    parents = eo[:parents]
    is_repeat_subtree = parents.length > 1 && parent_id != parents.last && seen.has_key?(parents.last)
    is_anchored = parents.length > 1 && parent_id == parents.last && seen.has_key?(parents.last)
    if is_repeat_subtree
      anc_id = parents.last
      ppath = []
      while !is_empty(anc_id) && anc_id != ROOT_ID
        ppath.unshift(@edinf[anc_id][:name])
        new_parents = @edinf[anc_id][:parents]
        if new_parents.empty?
          anc_id = nil
        else
          anc_id = new_parents.last
        end
      end
      path_to_root = ppath.join(" &rarr; ")
    end

    nchildren = eo[:children].length
    # Auto-collapse nodes with "large" (per maxchild) number of children, or if they
    # are deeply nexted (per expand_level), but don't collapse a single child,
    # regardless of depth
    collapsed = nchildren > 0 && (level >= expand_level && nchildren >= 2 || nchildren > maxchild)
    result += " class=\"collapsed\"" if collapsed
    result += ">"

    # <input>
    is_category = id.start_with?(CATEGORY_NODE_PREFIX) || id == ROOT_ID
    if !is_repeat_subtree && eo[:enabled]
      result += "<input type=\"checkbox\""
      if !is_category
        result += " class=\"edorgId\""
      end
      if !id.start_with?(CATEGORY_NODE_PREFIX) && !is_empty(id)
        result += " id=\"" + id + "\""
      end
      result += " checked" if eo[:authorized]
      result += "> "
    end

    # Text of the node.  Italicize if not enabled
    result += "<span"
    result += " class=\"categorynode\"" if is_category
    result += " class=\"repeatsubtree\"" if is_repeat_subtree
    result += ">"
    result += "<i>" if !eo[:enabled]
    result += "(&rArr; <a href=\"#" + parents.last + "\">see</a> " if is_repeat_subtree
    result += "<a name=\"" + id + "\"></a>" if is_anchored
    result += eo[:name]
    # result += " [" + eo[:id][0,8] + "]"
    # result += " (" + eo[:enabled].to_s + " / " + eo[:authorized].to_s + ")"
    result += ", under \"" + path_to_root + "\" above)" if is_repeat_subtree
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
    # For purpose of telling child whose parent is rendering it (i.e. to
    # detect repeat sub-trees) use a "real" edOrg node, not a category node
    parent_to_use = if is_category then parent_id  else id end
    if !is_repeat_subtree && nchildren > 0
      result += indent + "<ul>\n"
      eo[:children].each do |cid|
        seen[id] = true
        result += render_html(parent_to_use, cid, level + 1, seen)
      end
      result += indent + "</ul>\n"
    end

    return result
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
   
  # Get edOrgs in user's scope (descendants of user's edOrg). This is optimized just
  # to get a map of the IDs for the purpose of filtering the "index" list
  def get_edorgs_in_scope()
    raise "foo"
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
    edinf[id][:children].each do |cid|
      get_edorgs_in_scope_recursive(edinf, cid, result)
    end
  end
    
end
