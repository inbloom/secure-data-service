include ActiveSupport::Rescuable

class AppsController < ApplicationController

  rescue_from ActiveResource::ForbiddenAccess, :with => :render_403
  rescue_from ActiveResource::ResourceNotFound, :with => :render_404

  # GET /apps
  # GET /apps.json
  def index
   @apps = App.all
    respond_to do |format|
      format.html # index.html.erb
      format.json { render json: @apps }
    end
  end

  # GET /apps/1
  # GET /apps/1.json
  def show
    @app = App.find(params[:id])
  
    respond_to do |format|
      format.html # show.html.erb
      format.json { render json: @app }
    end
  end

  # GET /apps/new
  # GET /apps/new.json
  def new
    @app = App.new
    @app.developer_info = App::DeveloperInfo.new
  
    respond_to do |format|
      format.html # new.html.erb
      format.json { render json: @app }
    end
  end

  # # GET /apps/1/edit
  # def edit
  #   @app = App.find(params[:id])
  # end

  # POST /apps
  # POST /apps.json
  def create
    @app = App.new(params[:app])
    logger.debug {"#{@app.to_s}"}
    case @app.is_admin
    when "1"
      @app.is_admin = true
    when "0"
      @app.is_admin = false
    end
    
    case @app.developer_info.license_acceptance
    when "1"
      @app.developer_info.license_acceptance = true
    when "0"
      @app.developer_info.license_acceptance = false
    end
    
    respond_to do |format|
      if @app.save
        format.html { redirect_to apps_path, notice: 'App was successfully created.' }
        format.json { render json: @app, status: :created, location: @app }
        # format.js
      else
        format.html { render action: "new" }
        format.json { render json: @app.errors, status: :unprocessable_entity }
        # format.js
      end
    end
  end

  # PUT /apps/1
  # PUT /apps/1.json
  def update
    @app = App.find(params[:id])
    puts "App found (Update): #{@app.attributes}"
    respond_to do |format|
      if @app.update_attributes(params[:App])
        format.html { redirect_to @app.id, notice: 'App was successfully updated.' }
        format.json { head :ok }
      else
        format.html { render action: "edit" }
        format.json { render json: @app.errors, status: :unprocessable_entity }
      end
    end
  end

  # DELETE /apps/1
  # DELETE /apps/1.json
  def destroy
    @app = App.find(params[:id])
    @app.destroy
  
    respond_to do |format|
      format.js
      # format.html { redirect_to apps_url }
      # format.json { head :ok }
    end
  end
end
