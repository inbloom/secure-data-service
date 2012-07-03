=begin

Copyright 2012 Shared Learning Collaborative, LLC

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
require "active_resource/base"

class EntitiesController < ApplicationController
  before_filter :set_url

  rescue_from ActiveResource::ForbiddenAccess do |exception|
    flash[:notice] = "No accessible entries found."
    if !request.headers['referer'].nil? and !request.headers['referer'].include?(request.host)  
      redirect_to :back
    else
      raise exception
    end
  end

  def set_url
    @search_field = nil
    case params[:search_type]
    when /teachers/
      @search_field = "teacherUniqueStateId"
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
    Entity.url_type = params[:other]
    params.delete(:search_type)
    Entity.format = ActiveResource::Formats::JsonLinkFormat
  end

  # GET /entities/1
  # GET /entities/1.json
  def show
    @@LIMIT = 50
    @page = Page.new
    if params[:search_id] && @search_field
      @entities = Entity.get("", @search_field => params[:search_id]) if params[:search_id]
      flash.now[:notice] = "There were no entries matching your search" if @entities.size == 0 || @entities.nil?  
      return
    else
      if params[:offset]
        @entities = Entity.get("", {:offset => params[:offset], :limit => @@LIMIT})
      else
        @entities = Entity.get("")
      end
      @page = Page.new(@entities.http_response)
    end
    if @entities.is_a?(Hash)
      tmp = Array.new()
      tmp.push(@entities)
      @entities = tmp
    end

    if params[:other] == 'home'
      render :index
      return
    end

    respond_to do |format|
      format.html # show.html.erb
      format.json { render json: @entities }
      format.js #show.js.erb
    end
  end

  # GET /entities/new
  # GET /entities/new.json
  # def new
  #   @entity = Entity.new
  # 
  #   respond_to do |format|
  #     format.html # new.html.erb
  #     format.json { render json: @entity }
  #   end
  # end
  # 
  # # GET /entities/1/edit
  # def edit
  #   @entity = Entity.find(params[:id])
  # end

  # # POST /entities
  # # POST /entities.json
  # def create
  #   @entity = Entity.new(params[:entity])
  # 
  #   respond_to do |format|
  #     if @entity.save
  #       format.html { redirect_to @entity, notice: 'Entity was successfully created.' }
  #       format.json { render json: @entity, status: :created, location: @entity }
  #     else
  #       format.html { render action: "new" }
  #       format.json { render json: @entity.errors, status: :unprocessable_entity }
  #     end
  #   end
  # end
  # 
  # # PUT /entities/1
  # # PUT /entities/1.json
  # def update
  #   @entity = Entity.find(params[:id])
  # 
  #   respond_to do |format|
  #     if @entity.update_attributes(params[:entity])
  #       format.html { redirect_to @entity, notice: 'Entity was successfully updated.' }
  #       format.json { head :ok }
  #     else
  #       format.html { render action: "edit" }
  #       format.json { render json: @entity.errors, status: :unprocessable_entity }
  #     end
  #   end
  # end
  # 
  # # DELETE /entities/1
  # # DELETE /entities/1.json
  # def destroy
  #   @entity = Entity.find(params[:id])
  #   @entity.destroy
  # 
  #   respond_to do |format|
  #     format.html { redirect_to entities_url }
  #     format.json { head :ok }
  #   end
  # end
end
