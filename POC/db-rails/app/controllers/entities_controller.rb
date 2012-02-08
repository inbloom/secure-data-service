require "active_resource/base"

class EntitiesController < ApplicationController
  before_filter :set_url
  after_filter :fix_urls
  
  def set_url
    Entity.url_type = params[:type]
  end
  
  def fix_urls
      response.body = response.body.gsub!("https://devapp1.slidev.org/api/rest", "http://#{request.host_with_port}/entities")
  end
  # rescue_from ActiveResource::ResourceNotFound do |exception|
  #   render :file => "404.html"
  # end
  
  
  # GET /entities
  # GET /entities.json
  def index

    @entities = Entity.all

    respond_to do |format|
      format.html # index.html.erb
      format.json { render json: @entities }
    end
  end

  # GET /entities/1
  # GET /entities/1.json
  def show
    if(params[:targets])
      logger.debug("Building targets link")
      @entity = Entity.get("#{params[:id]}/#{params[:targets]}")
    else
      @entity = Entity.get(params[:id])
    end

    raise ActiveResource::ResourceNotFound.new "Couldn't load resource with id: #{params[:id]}" unless !@entity.empty?

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
