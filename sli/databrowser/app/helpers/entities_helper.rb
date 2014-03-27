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
  def build_links(hash, entity)
    html = ""
    if hash.is_a?(Array)
      html << "<ul class='values'>"
      html << "<p>Click on the # to display the counts of associated entities (Total/Current)</p>"
      hash.sort_by{|link| t(link["rel"]).downcase}.each do |link|
        url = link['href']
        
        if (url.include? "student")
          if url.include? "?"
            url = url + "&showAll=true"
          else
            url = url + "?showAll=true"
          end
        end

        if (link['rel'] == "getTeachers")
          html << '<li>' << link_to(t(link["rel"]), localize_url(url))
          link_url = request.protocol + request.host_with_port + "/count/teacherAssociations/#{entity['id']}/teachers"
        elsif (link['rel'] == "getTeacherSchoolAssociations")
          html << '<li>' << link_to(t(link["rel"]), localize_url(url))
          link_url = request.protocol + request.host_with_port + "/count/teacherAssociations/#{entity['id']}"
        else
          html << '<li>' << link_to(t(link["rel"]), localize_url(url))
          if url.include? "?"
            url = url + "&countOnly=true"
          else
            url = url + "?countOnly=true"
          end
          link_url = localize_url(url)
        end
        
        # Adds the count span to the page for use with getting the counts by ajax request.
        if (COUNT_CONFIG['include_current'].include? get_last_url_part(url))
          html << " (" << "<span class=count_link data-url=#{link_url} data-include_current=true>#</span>" << ")"
        else
          html << " (" << "<span class=count_link data-url=#{link_url} data-include_current=false>#</span>" << ")"
        end
        
        html << '</li>'

      end
      html << '</ul>'
    else
      html << link_to(t(hash["rel"]), localize_url(hash["href"]))
    end
    html
  end
  
  private
  def get_last_url_part(url)
    return URI(url).path.split("/").last
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
        val_text = (key == 'links' || key == 'link') ? build_links(value, entity) : display_entity(value)
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
    html ||= ""
    
    if (entity.nil?)
      html << "<table id=\"edorgcounts_home\" class=\"home_table\"><thead><tr><th>Entity/Role</th><th>Total</th><th>Current</th></tr></thead><tbody>"
    else
      html << "<table id=\"edorgcounts_#{entity['id']}\" class=\"edOrg\"><thead><tr><th>Entity/Role</th><th>Total</th><th>Current</th></tr></thead><tbody>"
    end

    begin
      if (entity.nil?)
        url = drop_url_version + "/count/educationOrganizations"
      else
        url = drop_url_version + "/count/educationOrganizations/#{entity['id']}"
      end
      counts = RestClient.get(url, get_header)
      counts = JSON.parse(counts)
    rescue => e
      logger.info("Could not get staff counts for because of #{e.message}")
    end
      
      
    # Add all of the counts to the table
    if (!counts.nil?)
      html << "<tr><td>Staff</td><td>#{counts['totalStaff']}</td><td>#{counts['currentStaff']}</td></tr>"
      html << "<tr><td>Students</td><td>#{counts['totalStudent']}</td><td>#{counts['currentStudent']}</td></tr>"
      html << "<tr><td>Teachers</td><td>#{counts['totalTeacher']}</td><td>#{counts['currentTeacher']}</td></tr>"
      html << "<tr><td>Non-Teachers</td><td>#{counts['totalNonTeacher']}</td><td>#{counts['currentNonTeacher']}</td></tr>"
    end

    # End the table
    html << "</tbody></table>"
    
  end

  # Used by the rest client to set up some basic header information
  def get_header
    header = Hash.new
    header[:Authorization] = "Bearer #{Entity.access_token}"
    header[:content_type] = :json
    header[:accept] = :json
    header
  end

  # Method to get the Ingestion data needed for the ingestion pages and tables
  def get_ingestion_data(limit = 0)
    url = drop_url_version + "/ingestionJobs" + "?limit=" + limit.to_s + "&sortBy=jobStartTimestamp&sortOrder=DESC"
    begin
      entities = RestClient.get(url, get_header)
      entities = JSON.parse(entities)
    rescue => e
      logger.info("Could not get ed orgs for #{entities} because of #{e.message}")
    end
    entities
  end

  # Drops the version number off of the base url for unversioned resources
  def drop_url_version
    
    # Convert api_base to URI
    url = URI(APP_CONFIG['api_base'])
    
    # Start building back the new url with scheme and host
    new_url = url.scheme + "://" + url.host
    
    # Check if there is a port, if so add it
    if url.port
      new_url += ":" + url.port.to_s
    end
    
    # Split the parts from the url. The first part will be blank since url.path starts with a slash
    # Skip that one, and if it is the last part, then don't add it as it will be the version.
    url_path_parts = url.path.split("/")
    url_path_parts.each do |part|
      if part == url_path_parts.first
        next
      end
      if !(part == url_path_parts.last)
        new_url += "/" + part
      end
    end
    
    # return the new url
    new_url
  end
end
