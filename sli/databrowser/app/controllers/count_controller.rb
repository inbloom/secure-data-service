require 'rest-client'

class CountController < ApplicationController

  #
  # Used to get the associations for the counts for teachers. This is for
  #   the AJAX counts next to the teachers and teacherAssociation link
  #
  def associations
    url = drop_url_version + "/count/teacherAssociations/#{params['edorg_id']}"
    begin
      entities = RestClient.get(url, get_header)
      entities = JSON.parse(entities)
    rescue => e
      logger.info("Could not get ed orgs for #{entities} because of #{e.message}")
    end
    
    respond_to do |format|
      format.json { render json: entities }
    end
  end

  #
  # Used to get the unique counts for teachers. This is for
  #   the AJAX counts next to the teachers and teacherAssociation link
  #  
  def teachers
    url = drop_url_version + "/count/teacherAssociations/#{params['edorg_id']}/teachers"
    begin
      entities = RestClient.get(url, get_header)
      entities = JSON.parse(entities)
    rescue => e
      logger.info("Could not get ed orgs for #{entities} because of #{e.message}")
    end
    
    respond_to do |format|
      format.json { render json: entities }
    end
  end

  # Used by the rest client to set up some basic header information
  private
  def get_header
    header = Hash.new
    header[:Authorization] = "Bearer #{Entity.access_token}"
    header[:content_type] = :json
    header[:accept] = :json
    header
  end

  # Drops the version number off of the base url for unversioned resources
  private
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
