class DistrictAuthorizationsController < ApplicationController
  # GET /district_authorizations
  # GET /district_authorizations.json
  def index
    @apps = App.all

    respond_to do |format|
      format.html # index.html.erb
      format.json { render json: @apps }
    end
  end

  # GET /district_authorizations/1
  ## GET /district_authorizations/1.json
  #def show
  #  @district_authorization = DistrictAuthorization.find(params[:id])
  #
  #  respond_to do |format|
  #    format.html # show.html.erb
  #    format.json { render json: @district_authorization }
  #  end
  #end
  #
  ## GET /district_authorizations/new
  ## GET /district_authorizations/new.json
  #def new
  #  @district_authorization = DistrictAuthorization.new
  #
  #  respond_to do |format|
  #    format.html # new.html.erb
  #    format.json { render json: @district_authorization }
  #  end
  #end

  # GET /district_authorizations/1/edit
  def edit
    @app = App.find(params[:id])
  end

  ## POST /district_authorizations
  ## POST /district_authorizations.json
  #def create
  #  @district_authorization = DistrictAuthorization.new(params[:district_authorization])
  #
  #  respond_to do |format|
  #    if @district_authorization.save
  #      format.html { redirect_to @district_authorization, notice: 'District authorization was successfully created.' }
  #      format.json { render json: @district_authorization, status: :created, location: @district_authorization }
  #    else
  #      format.html { render action: "new" }
  #      format.json { render json: @district_authorization.errors, status: :unprocessable_entity }
  #    end
  #  end
  #end
  #
  ## PUT /district_authorizations/1
  ## PUT /district_authorizations/1.json
  #def update
  #  @district_authorization = DistrictAuthorization.find(params[:id])
  #
  #  respond_to do |format|
  #    if @district_authorization.update_attributes(params[:district_authorization])
  #      format.html { redirect_to @district_authorization, notice: 'District authorization was successfully updated.' }
  #      format.json { head :ok }
  #    else
  #      format.html { render action: "edit" }
  #      format.json { render json: @district_authorization.errors, status: :unprocessable_entity }
  #    end
  #  end
  #end
  #
  ## DELETE /district_authorizations/1
  ## DELETE /district_authorizations/1.json
  #def destroy
  #  @district_authorization = DistrictAuthorization.find(params[:id])
  #  @district_authorization.destroy
  #
  #  respond_to do |format|
  #    format.html { redirect_to district_authorizations_url }
  #    format.json { head :ok }
  #  end
  #end
end
