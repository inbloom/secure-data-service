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
#include EdorgTreeHelper
#require "edorg_tree_helper"
require "active_resource/base"
#require "entities_helper"
# This is the main controller of the Databrowser.
# We try to "Wrap" all api requests in this one single point
# and do some clever work with filters and routing to make this work.
# The basic flow goes like this:
# * The Api request is routed as parameters to this controller
# * The set_url field deals with that parameter as well as search parameters
# * The show action creates the new model with the url, searches, and pages.
# We make heavy use of params which is everything that comes into
# this controller after /entities/
class EntitiesController < ApplicationController
  helper EntitiesHelper
  before_filter :set_url

  # What we see mostly here is that we are looking for searh parameters.
  # Now, we also try to simply set up the search field and then remove it
  # from the parameters so that we don't confuse the API by passing it
  # through later.
  #
  # Here we tell the Entity model that it's url is the thing that was passed
  # through in params. Which is how we are able to wrap the entire
  # api through one place.
  def set_url
    @search_field = nil
    case params[:search_type]
    when /studentByName/
      @search_field = "q"
    when /staffByName/
      @search_field = "q"
    when /edOrgByName/
      @search_field = "q"
    when /students/
      @search_field = "studentUniqueStateId"
    when /staff/
      @search_field = "staffUniqueStateId"
    when /parents/
      @search_field = "parentUniqueStateId"
    when /educationOrganizations/
      @search_field = "stateOrganizationId"
    end
    params[:other] = params[:search_type] if @search_field
    if params[:search_type] == "studentByName"
      Entity.url_type = "search/students"
    elsif params[:search_type] == "staffByName"
        Entity.url_type = "search/staff,teachers"
    elsif params[:search_type] == "edOrgByName"
        Entity.url_type = "search/educationOrganizations"
    else
      Entity.url_type = params[:other]
    end
    params.delete(:search_type)
    Entity.format = ActiveResource::Formats::JsonLinkFormat
  end

  # Ignoring some of the complicated parts, is we use the configured
  # model from set_url to make the Api call to get the data from the Api.
  #
  # Because we are trying to be generic with the data we get back, we handle
  # two special cases. The first is if params is 'home' which is a 
  # special home page in the Api. So if we call that we, render the index
  # page instead of the normal 'show'.
  # 
  # Second, if we only got one entity back, like the data for a single student
  # we go ahead and wrap that up into an array with that as the only element so
  # that our view logic can be simpler.
  #
  # As for the complicated parts, we do a few things, first is we detect if we
  # were passed any search parameters, and augment the Api call to deal with that
  # instead.
  #
  # Second, if we see any offset in params then we make the call to
  # grab the next page of data from the Api.
  def show
	 logger.debug {"Parameters are:#{params.inspect}"}
    @@LIMIT = 50
    @page = Page.new
    if params[:search_id] && @search_field
      @entities = []
      @entities = Entity.get("", @search_field => params[:search_id].strip) if params[:search_id] && !params[:search_id].strip.empty?
      @entities = clean_up_results(@entities)
      flash.now[:notice] = "There were no entries matching your search" if @entities.size == 0 || @entities.nil?
    else
      #Clean up the parameters to pass through to the API.
      if params[:offset]
        params[:limit] == @@LIMIT
      end
      query = params.reject {|k, v| k == 'controller' || k == 'action' || k == 'other' || k == 'search_id'}
      logger.debug {"Keeping query parameters #{query.inspect}"}
      @entities = Entity.get("", query)


      @page = Page.new(@entities.http_response)
      @entities= clean_up_results(@entities)
    end
    if params[:other] == 'home'
=begin
      entidAndCollection = EntitiesHelper::getUserEntityIdAndCollection(@entities)
      if entidAndCollection['collection'] != "students"
      	#getStudentAndStaffCounts(entidAndCollection)
        @isAStudent = false
      else
        @isAStudent = true
      end
=end
      render :index
      return
    end

    respond_to do |format|
      format.html # show.html.erb
      format.json { render json: @entities }
      format.js #show.js.erb
    end
  end
  
  private
  def clean_up_results(entities)
    tmp = entities
    if entities.is_a?(Hash)
      tmp = Array.new()
      tmp.push(entities)
    end
    tmp
  end
=begin
  private
  def getStudentAndStaffCounts(entidAndCollection)
    # for @entities.each |e|
       # logger.debug{""}
     
     userEdOrgsString = getUserEdOrgsString(entidAndCollection['entid'])
     @allStudentCount = -1
     @allStaffCount = -1
     @currentStudentCount = -1
     @currentStaffCount = -1
     @allStudentCount = getCount("educationOrganizations", userEdOrgsString, "studentSchoolAssociations", "students", false)
     @allStaffCount = getCount("educationOrganizations", userEdOrgsString, "staffEducationOrgAssignmentAssociations", "staff", false) 
     @currentStudentCount = getCount("educationOrganizations", userEdOrgsString, "studentSchoolAssociations", "students", true)  
     @currentStaffCount = getCount("educationOrganizations", userEdOrgsString, "staffEducationOrgAssignmentAssociations", "staff", true)
     
   end

=begin
   private
   def getUserEntityIdAndCollection
      logger.debug {"Try to print Response:"}
      bodyparts = JSON.parse(@entities.http_response.body)
      #bodyparts['links'].each do |e|
      bodyparts.each do |e|
           logger.debug {"#{e} \n"}
      end
      logger.debug {"After Try to print Response:"}
      #get one link
      linkstring = bodyparts['links'][0]
      logger.debug {"linkstring = #{linkstring['href']}"}
      #split the link on "rest/", as that always comes before the version number, user's entity collection, and entityId
      linksplit = linkstring['href'].split("rest/")
      # then take the part of the string that comes after
      entidPlus = linksplit[1]
      # drop everything after "/" to isolate the id from other url parts
      #break the string into version number, user's entity collection, entityId, and other parts that follow
      entidplussplit = entidPlus.split("/")
      collection = entidplussplit[1]
      logger.debug {"User entity collection: #{collection}"}
      entid = entidplussplit[2]
      logger.debug {"User entity ID: #{entid}"}
      entidAndCollection = {"entid"=>entid,"collection"=>collection}
      entidAndCollection
   end
=end
=begin
   private
   def getUserEdOrgsString(entid)
      
      Entity.url_type = "staff/#{entid}/staffEducationOrgAssignmentAssociations/educationOrganizations"
      begin
         userEdOrgs = Entity.get("")
      rescue
         logger.debug {"Caught Exception on getting edorgs"}
         return
      end
      userEdOrgs = clean_up_results(userEdOrgs)
      userEdOrgsString = ""
      userEdOrgs.each do |e|
         userEdOrgsString = "#{userEdOrgsString},#{e['id']}"
         logger.debug {"User edorgs : #{e}"}
      end
      # drop leading comma
      userEdOrgsString = userEdOrgsString[1..-1]
      logger.debug {"User edorg string: #{userEdOrgsString}"}
      userEdOrgsString
   end
=end
=begin
   private
   def  getCount(associationRoot, userEdOrgsString, associationType, targetCollection, currentOnly)
      Entity.url_type = "#{associationRoot}/#{userEdOrgsString}/#{associationType}/#{targetCollection}"
      if !currentOnly.nil? && currentOnly == true
         params = {"countOnly"=>"true","currentOnly"=>"true"}
      else
         params = {"countOnly"=>"true"}
      end
      begin
         count = Entity.get("",params).http_response['TotalCount']
      rescue
         count = "N/A"
         logger.debug {"Caught Exceptionon count"}
      end
      count
   end
=end
end
