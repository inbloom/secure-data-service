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
      html << "<p>Click on the # to display the counts of associated entities (Total/Current)</p>"
      hash.sort_by{|link| t(link["rel"]).downcase}.each do |link|
        html << '<li>' << link_to(t(link["rel"]), localize_url(link["href"]))
        
        url = link['href']
        if (url.include? "?")
          url = "#{url}&countOnly=true"
        else
          url = "#{url}?countOnly=true"
        end

        # Adds the count span to the page for use with getting the counts by ajax request.
        html << " (" << "<span class=count_link data-url=#{localize_url(url)}>#</span>" << ")"
        
        html << '</li>'

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
  def display_edorg_table(entity = nil)
    ed_orgs = []
    html ||= ""


    if (entity.is_a?(Array))
      entities = get_user_edorg(entity)
      entities.each do |ent|
        ed_orgs.push(ent)
      end
      html << "<table id=\"edorgcounts_home\" class=\"edOrg_home\"><thead><tr><th>Entity/Role</th><th>Total</th><th>Current</th></tr></thead><tbody>"
    else
      ed_orgs << entity  
      html << "<table id=\"edorgcounts_#{entity['id']}\" class=\"edOrg\"><thead><tr><th>Entity/Role</th><th>Total</th><th>Current</th></tr></thead><tbody>"
    end
    
    # Build the list of EdOrgs and it's children

    ed_orgs.each do |ed_org|
      get_feeder_edorgs(ed_org['id'], ed_orgs)
    end
      
    student_counts = get_student_counts(ed_orgs)
    staff_counts = get_staff_counts(ed_orgs)
      
    # Add all of the counts to the table
    html << "<tr><td>Staff</td><td>#{staff_counts['total']}</td><td>#{staff_counts['current']}</td></tr>"
    html << "<tr><td>Students</td><td>#{student_counts['total']}</td><td>#{student_counts['current']}</td></tr>"
    html << "<tr><td>Teachers</td><td>#{staff_counts['total_teachers']}</td><td>#{staff_counts['current_teachers']}</td></tr>"
    html << "<tr><td>Non-Teachers</td><td>#{staff_counts['total_non_teachers']}</td><td>#{staff_counts['current_non_teachers']}</td></tr>"

    # End the table
    html << "</tbody></table>"
    
  end

  # This is recursive function that retrieves all of the edorgs below the current edorg
  # and their children and so on. It uses the organizationalCategory of School to break
  # out of the recursiveness as schools should not have an children
  def get_feeder_edorgs(entity_id, destination = nil)
    begin
      url = "#{APP_CONFIG['api_base']}/educationOrganizations?parentEducationAgencyReference=#{entity_id}"
      entities = RestClient.get(url, get_header)
      entities = JSON.parse(entities)
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
    rescue => e
      logger.info("Could not get feeder Ed Org because of #{e.message}")
    end
    destination
  end

  # Used by the rest client to set up some basic header information
  def get_header
    header = Hash.new
    header[:Authorization] = "Bearer #{Entity.access_token}"
    header[:content_type] = :json
    header[:accept] = :json
    header
  end

  # Retrieves the counts from the api for students and staff. This takes the entity_type as a variable so that
  # the appropriate url can be chosen based on the count needed. If total = true, this returns all of the entities
  # and if total is false, the currentOnly parameter is passed to the api.
  def get_student_counts(ed_orgs)
    total = Hash.new
    current = Hash.new
    #UseForTotaltotal = 0
    #UseForTotalcurrent = 0
    ed_orgs.each do | ed_org |
      url = "#{APP_CONFIG['api_base']}/educationOrganizations/#{ed_org['id']}/studentSchoolAssociations?limit=0"
      begin
        entities = RestClient.get(url, get_header)
        entities = JSON.parse(entities)
        entities.each do | entity |
          total[entity['studentId']] = entity['studentId']
          #UseForTotaltotal += 1
          if (is_current(entity))
            current[entity['studentId']] = entity['studentId']
            #UseForTotalcurrent += 1
          end
        end
      rescue => e
        logger.info("Could not get student counts for #{ed_org['id']} because of #{e.message}")
      end
    end
    result = Hash.new
    #UseForTotalresult['total'] = total
    #UseForTotalresult['current'] = current
    result['total'] = total.size()
    result['current'] = current.size()
    
    result
  end

  # This function is used to retrieve the teacher and non-teacher counts for the edOrg that is passed
  # in. It builds the link and pulls back all of the associations. Then looks for the Educator 
  # staffClassification. If the classification is Educator then it is a teacher and the teachers
  # count is incremented, otherwise, the non-teacher count is incremented. This returns a Hash
  # of the two counts
  def get_staff_counts(ed_orgs)
# Can be used for unique staff
    total = Hash.new
    current = Hash.new
    total_teachers = Hash.new
    total_non_teachers = Hash.new
    current_teachers = Hash.new
    current_non_teachers = Hash.new

#UseForTotal    total = 0
#UseForTotal    current = 0
#UseForTotal    total_teachers = 0
#UseForTotal    total_non_teachers = 0
#UseForTotal    current_teachers = 0
#UseForTotal    current_non_teachers = 0

    ed_orgs.each do | ed_org |
      url = "#{APP_CONFIG['api_base']}/educationOrganizations/#{ed_org['id']}/staffEducationOrgAssignmentAssociations?limit=0"

      begin
        associations = RestClient.get(url, get_header)
        associations = JSON.parse(associations)
        associations.each do |association|
          # Increment the total
          total[association['staffReference']] = association['staffReference']
          #UseForTotaltotal += 1
          
          # Increment totals for teachers and non-teachers as necessary for totals
          if (!association['staffClassification'].nil?)
            if (association['staffClassification'].include? "Educator")
              total_teachers[association['staffReference']] = association['staffReference']
              #UseForTotaltotal_teachers += 1
            else
              total_non_teachers[association['staffReference']] = association['staffReference']
              #UseForTotaltotal_non_teachers += 1
            end
          end
          
          # Increment current for staff, teachers and non-teachers
          if (is_current(association))
            current[association['staffReference']] = association['staffReference']
            #UseForTotalcurrent += 1
            if (!association['staffClassification'].nil?)
              if (association['staffClassification'].include? "Educator")
                #UseForTotalcurrent_teachers += 1
                current_teachers[association['staffReference']] = association['staffReference']
              else
                #UseForTotalcurrent_non_teachers += 1
                current_non_teachers[association['staffReference']] = association['staffReference']
              end
            end
          end
          
        end
      rescue => e
        logger.info("Could not get staff counts for #{ed_org['id']} because of #{e.message}")
      end
    end
    
    result = Hash.new
    result['total'] = total.size()
    result['current'] = current.size()
    result['total_teachers'] = total_teachers.size()
    result['total_non_teachers'] = total_non_teachers.size()
    result['current_teachers'] = current_teachers.size()
    result['current_non_teachers'] = current_non_teachers.size()
    result    
  end

  private
  def is_current(entity)
    result = false

    now = Date.today
    
    begin_date_field = "beginDate"
    end_date_field = "endDate"
    
    begin_date = nil
    end_date = nil
    
    if (entity['entityType'] == "studentSchoolAssociation")
      begin_date_field = "entryDate"
      end_date_field = "exitWithdrawDate"
    end
    
    if (entity[begin_date_field].nil?)
      begin_date = now - 1
    else
      begin_date = Date.strptime(entity[begin_date_field], "%F") 
    end

    if (entity[end_date_field].nil?)
      end_date = now + 1
    else 
      end_date = Date.strptime(entity[end_date_field], "%F")
    end

    if (begin_date <= now) and (end_date >= now)
      result = true
    end
    result
   
  end

  def get_user_edorg(entities)
    user = getUserEntityIdAndCollection(entities)
    url = "#{APP_CONFIG['api_base']}/staff/#{user['entid']}/staffEducationOrgAssignmentAssociations/educationOrganizations"
    begin
      entities = RestClient.get(url, get_header)
      entities = JSON.parse(entities)
    rescue => e
      logger.info("Could not get ed orgs for #{entities} because of #{e.message}")
    end
    entities
  end

  # Stolen from DS-1005, will need some merging when these two branches are merged.
  def getUserEntityIdAndCollection(entities)
    logger.info("Entities: #{entities}")
     bodyparts = JSON.parse(entities.http_response.body)
     #get one link
     linkstring = bodyparts['links'][0]
     #split the link on "rest/", as that always comes before the version number, user's entity collection, and entityId
     linksplit = linkstring['href'].split("rest/")
     # then take the part of the string that comes after
     entidPlus = linksplit[1]
     # drop everything after "/" to isolate the id from other url parts
     #break the string into version number, user's entity collection, entityId, and other parts that follow
     entidplussplit = entidPlus.split("/")
     collection = entidplussplit[1]
     entid = entidplussplit[2]
     entidAndCollection = {"entid"=>entid,"collection"=>collection}
     entidAndCollection
  end
  def userIsAStudent
      entidAndCollection = getUserEntityIdAndCollection(@entities)
      if entidAndCollection['collection'] == "students"
         true
      else
         false
      end
  end
end
