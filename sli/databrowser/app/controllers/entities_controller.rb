require "active_resource/base"

class EntitiesController < ApplicationController
  before_filter :set_params, :set_url
  skip_before_filter :set_params, :only => [:index]  
  
  def set_params
    split_path = params[:other].split('/')
    redirect_to "entities/#{split_path.first}" if split_path.size == 1
    id_index = split_path.index {|path| /[0-9]/ =~ path}
    params[:type] = split_path.slice(0...id_index).join('/')
    params[:id] = split_path.slice(id_index...split_path.size).join('/')
  end
  
  def set_url
      Entity.url_type = params[:type]
      case params[:type]
      when /association/
        logger.debug {"Full json support on"}
        Entity.format = ActiveResource::Formats::JsonFullFormat
      else
        logger.debug {"Full json support off"}
        Entity.format = ActiveResource::Formats::JsonFormat
      end
    end
  
  # rescue_from ActiveResource::ResourceNotFound do |exception|
  #   render :file => "404.html"
  # end
  
  
  # GET /entities
  # GET /entities.json
  def index
    Entity.url_type = params[:type]
    @entities = Entity.all

    respond_to do |format|
      format.html # index.html.erb
      format.json { render json: @entities }
    end
  end

  # GET /entities/1
  # GET /entities/1.json
  def show
    @entity = Entity.get_simple_and_complex(params[:id])
    # @entity = Entity.find(:all, :from => "/api/rest/#{params[:other]}")
    
    respond_to do |format|
      format.html # show.html.erb
      format.json { render json: @entity }
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
