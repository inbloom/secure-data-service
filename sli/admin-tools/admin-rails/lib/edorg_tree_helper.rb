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


module EdorgTreeHelper

  class EdorgTree

    ROOT_ID = 'root'
    CATEGORY_NODE_PREFIX = 'cat_'

    # render a tree where enabled & authorized edOrgs are checked(selected) and enabled & unauthorized edOrgs can be checked(selected)
    # If is_sea_admin is false, the top most nodes of the tree are 'userEdOrgs'. If true, all the parentless edOrgs become the topmost nodes of the tree.
    def get_authorization_tree_html(userEdOrgs, appId, is_sea_admin, authorizedEdOrgs)
      get_tree_html(userEdOrgs, appId, is_sea_admin, true, authorizedEdOrgs)
    end

    # render a tree where enabled edOrgs are checked(selected) and all edOrgs can be checked(selected)
    # If is_sea_admin is false, the top most nodes of the tree are 'userEdOrgs'. If true, all the parentless edOrgs become the topmost nodes of the tree.
    def get_enablement_tree_html(userEdOrgs, appId, is_sea_admin)
      get_tree_html(userEdOrgs, appId, is_sea_admin, false, [])
    end

    def get_tree_html(userEdOrgs, appId, is_sea_admin, forAAuthorization, authorizedEdOrgs)
      @userEdOrgs           = userEdOrgs || []
      @authorized_ed_orgs   = array_to_hash(authorizedEdOrgs)
      @forAppAuthorization  = forAAuthorization

      # Get app data
      @app = App.find(appId)
      raise "Application #{appId} not found in sli.application" if @app.nil?

      # Get developer-enabled edorgsfor the app.  NOTE: Even though the field is
      # sli.application.authorized_ed_orgs[] these edorgs are called "developer-
      # enabled" or just "enabled" edorgs.
      @enabled_ed_orgs = array_to_hash(@app.authorized_ed_orgs)

      # Load up edOrg data
      load_edorgs(is_sea_admin)
      @id_counter = 0
      # Compile counts across whole tree and build "by-type" category nodes
      build_tree(ROOT_ID, {}, {})
      edorg_tree_html = "<ul>\n  #{render_html(ROOT_ID, ROOT_ID, 0)} </ul>\n"
    end

    # Load up all the edOrgs.  Creates:
    #    @edinf - Map of edOrg ID to the tree
    #    @enabled_ed_orgs - Map of IDs enabled
    #    @authorized_ed_orgs - Map of IDs authorized
    #
    def load_edorgs(is_sea_admin)

      @edinf = {}
      root_ids = []
      allEdOrgs = EducationOrganization.findAllInChunks({'includeFields' => 'parentEducationAgencyReference,nameOfInstitution,stateOrganizationId,organizationCategories'})

      allEdOrgs.each do |eo|

        # Enable edorgs if the "allowed_for_all_edorgs" flag is set for the application OR
        # the edorg is listed in the application's "authorized" (actually enabled) list
        app_enabled = @app.allowed_for_all_edorgs || @enabled_ed_orgs.has_key?(eo.id)
        @edinf[eo.id] = { :edOrg => eo, :id => eo.id, :name => eo.nameOfInstitution, :children => [], :enabled => app_enabled, :authorized => @authorized_ed_orgs.has_key?(eo.id)}

        parents = eo.parentEducationAgencyReference || []
        parents.flatten!(1)
        @edinf[eo.id][:parents] = parents

        # Track root edorgs (with no parents)
        root_ids.push(eo.id) if parents.empty?
      end

      # Init immediate children of each edorg by inverting parent relationship
      @edinf.keys.each do |id|
        @edinf[id][:parents].each do |pid|
          if !@edinf.has_key?(pid)
            # Dangling reference to nonexistent parent
            raise "EdOrg #{id} parents to nonexistent #{pid.to_s()}"
          else
            @edinf[pid][:children].push(id)
          end
        end
      end

      # Create fake root edOrg and parent all top level nodes to it
      root_children = if is_sea_admin then root_ids else @userEdOrgs end
      # Because ":enabled" aggregates on an "or" basis, and ":authorized" aggregates on an "and" basis, init
      # them to "false" and "true" respectively, so that the logic aggregates appropriately.
      root_edorg = { :id => ROOT_ID, :parents => [], :children => root_children, :enabled => ! @forAppAuthorization, :authorized => true,
                     :name => 'All EdOrgs', :edOrg => { :id => ROOT_ID, :parentEducationAgencyReference  => []}}
      @edinf[ROOT_ID] = root_edorg

      # Allow SEA admin to see everything, including edOrgs not parented
      # up to SEA.  LEA admin just his own edorg
      root_children.each do |id|
        @edinf[id][:parents] = [ ROOT_ID ]
      end

      # Cleanse parents and children of dangling IDs that point outside subtree of ROOT_ID
      ids_in_scope = {}
      get_descendants(ROOT_ID, ids_in_scope, {})
      cleanse_refs(ROOT_ID, ids_in_scope)
    end

    # Get descendant nodes, recursively
    def get_descendants(id, result, seen)
      raise "CYCLE in EdOrg hierarchy includes EdOrg id #{id}" if seen.has_key?(id)
      seen[id] = true
      result[id] = true
      @edinf[id][:children].each do |cid|
        get_descendants(cid, result, seen.clone)
      end
    end

    # Cleanse parent/child refs that point outside subtree at ROOT_ID
    def cleanse_refs(id, ids_in_scope)
      @edinf[id][:parents].select! { |id| ids_in_scope.has_key?(id) }
      @edinf[id][:children].select! { |id| ids_in_scope.has_key?(id) }
      @edinf[id][:children].each do |cid|
        cleanse_refs(cid, ids_in_scope)
      end
    end 

    # Traverse graph and count children and descendants and put into @edinf map
    # Sets :nchild and :ndesc in the node with given ID
    # Replaces :children array with array of nodes by type

    def build_tree(id, seen, all_seen)
      all_seen[id] = true
      # Trap cycles
      if seen.has_key?(id)
        raise "CYCLE in EdOrg hierarchy includes EdOrg id #{id}"
      else
        seen[id] = true
      end

      nchild = @edinf[id][:children].length
      @edinf[id][:nchild] = nchild
      ndesc = nchild
      by_type = {}

      # Sort children by name
      compare_name = ->(a,b) { @edinf[a][:name].casecmp(@edinf[b][:name]) }
      @edinf[id][:children].sort!( & compare_name )

      # Also sort parents by name so that we can identify the "canonical" parent
      # predictably and in a way that matches the rendering traversal
      @edinf[id][:parents].sort!( & compare_name )

      @edinf[id][:children].each do |cid|
        build_tree(cid, seen.clone, all_seen) if !all_seen.has_key?(cid)
        ndesc += @edinf[cid][:ndesc]
        ctype = get_edorg_type(cid)
        if by_type.has_key?(ctype)
          by_type[ctype][:children].push(cid)
          by_type[ctype][:nchild] += 1
          by_type[ctype][:ndesc] += @edinf[cid][:ndesc] + 1
          # Aggregate the enabled/authorized status.  For authoriztation, enabled aggregates on an "or" basis,
          # and authorized aggregates on an "and" basis (and then only for enabled children).
          # For enablement screen, aggregate by "and" everywhere.
          if @forAppAuthorization
            agg_enabled = by_type[ctype][:enabled] || @edinf[cid][:enabled]
          else
            agg_enabled = by_type[ctype][:enabled] && @edinf[cid][:enabled]
          end
          by_type[ctype][:enabled] = agg_enabled
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
      # and authorized aggregates on an "and" basis, and only for enabled children.
      # Init aggregate status so that the and/or logic accumulates correctly.
      agg_enabled = false
      agg_authorized = true
      by_type.each do |ctype, cinf|
        new_children.push(cinf[:id])
        @edinf[cinf[:id]] = cinf
        if @forAppAuthorization
          agg_enabled = agg_enabled || cinf[:enabled]
        else
          agg_enabled = agg_enabled && cinf[:enabled]
        end
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
    def render_html(parent_id, id, level)
      # <LI> part
      indent = '  ' * level
      result = indent + '<li'
      maxchild = 10
      expand_level = 3
      eo = @edinf[id]

      # Detect repeat subtrees: if a node has multiple parents (and is
      # thus reachable by multiple paths from parents to children),
      # consider all but the first ID in the parents list as "aliases".
      # Using the first ID as the "canonical" copy will have the effect
      # of the "real" node being rendered first in the recursion, so
      # that, in turn, we can label the "alias" nodes with a notice to
      # consult the real node "above" it.  This in turn means that the
      # real node will appear first in a depth first traversal, i.e.,
      # as the user scans a fully expanded tree from top to bottom.
      parents = eo[:parents]
      is_repeat_subtree = id != ROOT_ID && (parents[0] == ROOT_ID || parents.length > 1) && parent_id != parents[0]
      is_anchored = parents.length > 1 && parent_id == parents[0]
      if is_repeat_subtree
        anc_id = parents[0]
        ppath = []
        while !is_empty(anc_id) && anc_id != ROOT_ID
          ppath.unshift(@edinf[anc_id][:name])
          new_parents = @edinf[anc_id][:parents]
          if new_parents.empty?
            anc_id = nil
          else
            anc_id = new_parents[0]
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

      isCheckable = if @forAppAuthorization then eo[:enabled]    else true end
      isChecked   = if @forAppAuthorization then eo[:authorized] else eo[:enabled] end

      # <input>
      is_category = id.start_with?(CATEGORY_NODE_PREFIX) || id == ROOT_ID
      if !is_repeat_subtree && isCheckable
        result += "<input type=\"checkbox\""
        if !is_category
          result += " class=\"edorgId\""
        end
        if !id.start_with?(CATEGORY_NODE_PREFIX) && !is_empty(id)
          result += " id=\"" + id + "\""
        end
        result += " checked" if isChecked
        result += "> "
      end

      # Text of the node.  Italicize if not enabled
      result += "<span"
      result += " class=\"categorynode\"" if is_category
      result += " class=\"repeatsubtree\"" if is_repeat_subtree
      result += ">"
      result += "<i>" if !isCheckable
      result += "(&rArr; see <a style=\"color: #0000ff; text-decoration:underline\" href=\"#" + id + "\">" if is_repeat_subtree
      result += "<a name=\"" + id + "\"></a>" if is_anchored
      result += eo[:name]
      result += "</a>" if is_repeat_subtree

      # Uncomment below for debugging: add ID, show enabled/authorized status, show subtree status
      # result += " [" + eo[:id][0,8] + "]"
      # result += " parents.length=" + parents.length.to_s() + " parent_id=[" + parent_id.to_s()[0,8] + "]"
      # result += " enabled=" + eo[:enabled].to_s + " authorized=" + eo[:authorized].to_s
      # result += " is_anchored=" + is_anchored.to_s + " is_repeat_subtree=" + is_repeat_subtree.to_s
      # result += " parents[0]=[" + parents[0][0,8] + "]" if !parents.empty?

      result += ", under \"" + path_to_root + "\"" if is_repeat_subtree && !path_to_root.empty?
      result += " above)" if is_repeat_subtree
      result += "</i>" if !isCheckable
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
          result += render_html(parent_to_use, cid, level + 1)
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
      begin
        cats = @edinf[id][:edOrg].organizationCategories
      rescue
        return "Unknown"
      end
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

    # String is neither nil nor empty?
    def is_empty(s)
      return true if s.nil?
      return true if s.length == 0
      return false
    end
  end #end class definition
end #end module definition
