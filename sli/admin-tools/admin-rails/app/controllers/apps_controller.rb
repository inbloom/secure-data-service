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

  # # GET /apps/new
  # # GET /apps/new.json
  # def new
  #   @App = App.new
  # 
  #   respond_to do |format|
  #     format.html # new.html.erb
  #     format.json { render json: @App }
  #   end
  # end

  # GET /apps/1/edit
  # def edit
  #   @App = App.find(params[:id])
  # end

  # POST /apps
  # POST /apps.json
  # def create
  #   @App = App.new(params[:App])
  # 
  #   respond_to do |format|
  #     if @App.save
  #       format.html { redirect_to @App, notice: 'App was successfully created.' }
  #       format.json { render json: @App, status: :created, location: @App }
  #     else
  #       format.html { render action: "new" }
  #       format.json { render json: @App.errors, status: :unprocessable_entity }
  #     end
  #   end
  # end

  # PUT /apps/1
  # PUT /apps/1.json
  # def update
  #   @App = App.find(params[:id])
  #   puts "App found (Update): #{@App.attributes}"
  #   respond_to do |format|
  #     if @App.update_attributes(params[:App])
  #       format.html { redirect_to @App.id, notice: 'App was successfully updated.' }
  #       format.json { head :ok }
  #     else
  #       format.html { render action: "edit" }
  #       format.json { render json: @App.errors, status: :unprocessable_entity }
  #     end
  #   end
  # end

  # DELETE /apps/1
  # DELETE /apps/1.json
  # def destroy
  #   @App = App.find(params[:id])
  #   @App.destroy
  # 
  #   respond_to do |format|
  #     format.html { redirect_to apps_url }
  #     format.json { head :ok }
  #   end
  # end
end
