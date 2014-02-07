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
require 'rest-client'
#++
# This module contains a lot of the recursive view logic needed to display
# items correctly in the databrowser views.
#
# The basic process is that the data gets split into the simple view and
# a complex view which represent the full entity and the tabular list of
# entities.
#
# To build and display them we go through a number of different methods to
# pull out the configured table columns based on the entity type and move
# the data into those columns
#
# Some of the columns are are also based on what we have configured in
# config/views.yml
#
module EntitiesHelper

  # Here we go through the entities the API has returned and figure out what
  # type it is and then get the table columns out of set up.
  def get_table_fields(entities)
    tableFields = Array.new
    return tableFields if entities.nil? || (entities.is_a?(Array) && entities.empty?)

    entity = entities.first
    if (entity.has_key?('entityType') && VIEW_CONFIG.has_key?(entity['entityType']))
      tableFields = VIEW_CONFIG[entity['entityType']]
    end

    if tableFields.empty?
      entity.each do |key, val|
        next if val.is_a?(Array) || val.is_a?(Hash)
        tableFields.push(key)
        break if tableFields.length >= 5
      end
    end

    return tableFields
  end

  # Because entities are complex json objects this method will look at the
  # "path" we have described to extract the data to show in the table, and then
  # dig into that data to pull out the individual items
  #
  # At this point it doesn't support Arrays in complex types, it will just take
  # the first item in that array. Some example of where this will happen are in
  # addresses and names.
  def value_for_table_view (type, entity)
    return nil if entity.nil? or type.nil?
    return entity[type] unless type.include? '/'
    temp_hash = entity
    type_split = type.split '/'
    type_split.each do |split|
      if temp_hash.is_a?(Array)
        temp_hash = temp_hash.first[split] unless temp_hash.first.nil?
      elsif temp_hash.has_key? split
        temp_hash = temp_hash[split]
      else
        return nil
      end
    end
    temp_hash
  end

  # This method takes an API URL and then does a simple substitution to change
  # it to point to the databrowser instead, this is how we make the HATEOS
  # links work.
  def localize_url(url)
    url.gsub(%r(#{APP_CONFIG['api_base']}\.?\d+?), "#{request.protocol}#{request.host_with_port}/entities") unless url.nil?
  end

  # Here we detect links and turn it into an alphabetized list of links that
  # reference the databrowser instead of the API. We also use the i18n
  # translation file to make some of the links more readable.
  def build_links(hash)
    html = ""
    if hash.is_a?(Array)
      html << "<ul class='values'>"
      hash.sort_by{|link| t(link["rel"]).downcase}.each do |link|
        html << '<li>' << link_to(t(link["rel"]), localize_url(link["href"])) << '</li>'
      end
      html << '</ul>'
    else
      html << link_to(t(hash["rel"]), localize_url(hash["href"]))
    end
    html
  end

  # This method sorts the entity into the order determined by ./config/order.yml
  # It assumes that the entity object is a Hash. If the entity has the key entityType,
  # then it will use that configuration from ./config/order.yml. Otherwise, it will use
  # the 'default' configuration from that file. It sorts the hash into the desired order,
  # appends the keys that were not given in the configuration and then appends "links" as
  # it was desired that "links" be the very last component displayed.
  def sort_entity (entity)
    if (entity.has_key?('entityType') && SORT_CONFIG.has_key?(entity['entityType']))
      order = SORT_CONFIG[entity['entityType']].clone.to_a
    else
      order = SORT_CONFIG["default"].clone.to_a
    end
    entity.each { |key, value|
      order << key if order.index(key).nil? && key != "links"
    }
    if !order.include?('links')
      order << "links"
    end
    Hash[entity.sort_by {|k, v| order.index(k)}]
  end

  # This method is pretty complex, it's recursive, so what it does is it digs
  # through the hashmap of data we have available and builds a definition list
  # of the data to all be displayed. It makes use of the other methods in this
  # module to accomplish this
  #
  # We would like to support nested lists, but at this point everything winds
  # up flattened in one single definition list.
  def display_entity (entity)
    html ||= ""
    if entity.is_a?(Hash)
      entity = sort_entity(entity)
      entity.each { |key, value|
        val_text = (key == 'links' || key == 'link') ? build_links(value) : display_entity(value)
        val_text = "&nbsp" if val_text.nil? || val_text.empty?
        address_text = (key == 'address') ? " address" : ""
        html << "<div class='row'><div class='key left'>#{t(key)}:</div><div class='value#{address_text}'>#{val_text}</div></div>"
      }
    elsif entity.is_a?(Array)
      # The following is so that a list a values (like Links) can be displayed in a
      # different way than a list of hashes (like address)
      if entity[0].is_a?(Hash)
        html << "<ul class='hashes'>"
      else
        html << "<ul class='values'>"
      end
      entity.each do |item|
        html << '<li>' << display_entity(item) << '</li>'
      end
      html << '</ul>'
    else
      html << entity.to_s
    end
    html
  end

  # This is for displaying the table that is attached to the EdOrg page. This will only display when the entityType
  # is educationOrganization. This is a table that shows the number of staff, students, teachers and non-teachers
  # associated with the EdOrg and it's children
  def display_edorg_table(entity)
    
    # Set up the header of the table
    html ||= ""
    html << "<table class=\"edOrg\"><thead><tr><th>Entity/Role</th><th>Total</th><th>Current</th></tr></thead><tbody>"

    # Get the entity ID so that it can be used for passing to other functions instead of the full entity
    entity_id = entity['id']

    counts = Hash.new

    # Get the relevent counts for the top level entity
    counts["staff_total"] = get_counts(entity_id, "staff")
    counts["staff_current"] = get_counts(entity_id, "staff", false)
    counts["students_total"] = get_counts(entity_id, "students")
    counts["students_current"] = get_counts(entity_id, "students", false)
    counts = get_teacher_and_non_counts(entity_id, counts)
    counts = get_teacher_and_non_counts(entity_id, counts, false)
    
    # Retrieve the Organizations that this is the parent of and their children and so on
    # from the function below
    feederEdOrgs = []
    feederEdOrgs = get_feeder_edorgs(entity_id)

    # For each of the children EdOrgs, retrieve their relevent counts and add them to the total
    feederEdOrgs.each do |ed_org|
      ed_org_id = ed_org['id']
      #counts["staff_total"] += get_counts(ed_org_id, "staff")
      #counts["staff_current"] += get_counts(ed_org_id, "staff", false)
      #counts["students_total"] += get_counts(ed_org_id, "students")
      #counts["students_current"] += get_counts(ed_org_id, "students", false)
      
      # Since teacher and non-teacher come back as a hash a little more work is required
      counts = get_teacher_and_non_counts(entity_id, counts)
      counts = get_teacher_and_non_counts(entity_id, counts, false)
    end

    # Add all of the counts to the table
    html << "<tr><td>Staff</td><td>#{counts['staff_total']}</td><td>#{counts['staff_current']}</td></tr>"
    html << "<tr><td>Students</td><td>#{counts['students_total']}</td><td>#{counts['students_current']}</td></tr>"
    html << "<tr><td>Teachers</td><td>#{counts['teachers_total']}</td><td>#{counts['teachers_current']}</td></tr>"
    html << "<tr><td>Non-Teachers</td><td>#{counts['non-teachers_total']}</td><td>#{counts['non-teachers_current']}</td></tr>"

    # End the table
    html << "</tbody></table>"
  end

  # This is recursive function that retrieves all of the edorgs below the current edorg
  # and their children and so on. It uses the organizationalCategory of School to break
  # out of the recursiveness as schools should not have an children
  def get_feeder_edorgs(entity_id, destination = nil)
    Entity.url_type = "educationOrganizations"
    entities = Entity.get("", {:parentEducationAgencyReference => "#{entity_id}"})
    if destination.nil?
      destination = []
    end
    entities.each do |ed_org|
      if ed_org['organizationCategories'].include? "School"
        destination.push(ed_org) unless destination.include? ed_org
      else
        get_feeder_edorgs(ed_org['id'], destination)
      end
    end
    destination
  end

  # Used by the rest client to set up some basic header information
  def get_basic_params
    params = Hash.new
    params[:Authorization] = "Bearer #{Entity.access_token}"
    params[:content_type] = :json
    params[:accept] = :json
    params
  end

  # Retrieves the counts from the api for students and staff. This takes the entity_type as a variable so that
  # the appropriate url can be chosen based on the count needed. If total = true, this returns all of the entities
  # and if total is false, the currentOnly parameter is passed to the api.
  def get_counts(entity_id, entity_type, total = true)
    if (entity_type == "students")
      url = "#{APP_CONFIG['api_base']}/educationOrganizations/#{entity_id}/studentSchoolAssociations/students"
    elsif (entity_type == "staff")
      url = "#{APP_CONFIG['api_base']}/educationOrganizations/#{entity_id}/staffEducationOrgAssignmentAssociations/staff"
    else
      logger.info("Invalid Entity For counts")
      return
    end
    params = get_basic_params
    params[:countOnly] = "true"
    if !total
      params[:currentOnly] = "true"
    end
    count = RestClient.get(url, params).headers[:totalcount].to_i
    count
  end

  # This function is used to retrieve the teacher and non-teacher counts for the edOrg that is passed
  # in. It builds the link and pulls back all of the associations. Then looks for the Educator 
  # staffClassification. If the classification is Educator then it is a teacher and the teachers
  # count is incremented, otherwise, the non-teacher count is incremented. This returns a Hash
  # of the two counts
  def get_teacher_and_non_counts(entity_id, counts = nil, total = true)
    if counts.nil?
      counts = Hash.new
    end
    Entity.url_type = "educationOrganizations/#{entity_id}/staffEducationOrgAssignmentAssociations"
    params = Hash.new

    teacher_key = "teachers_total"
    non_teacher_key = "non-teachers_total"
    if !total
      params[:currentOnly] = "true"
      teacher_key = "teachers_current"
      non_teacher_key = "non-teachers_current"
    end

    # Populate default counts with 0 if they do not have value
    if counts[teacher_key].nil?
      counts[teacher_key] = 0
    end
    if counts[non_teacher_key].nil?
      counts[non_teacher_key] = 0
    end

    associations = Entity.get("", params)
    associations.each do |association|
      if (association['staffClassification'].include? "Educator")
        counts[teacher_key] += 1
      else
        counts[non_teacher_key] += 1
      end
    end
    counts
  end
end
