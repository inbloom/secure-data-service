class ApplicationAuthorizationsController < ApplicationController
  rescue_from ActiveResource::ForbiddenAccess, :with => :render_403
  rescue_from ActiveResource::ResourceNotFound, :with => :render_404
  before_filter :check_rights

  # Let us add some docs to this confusing controller.
  # NOTE this controller allows ed org super admins to 
  # enable/disable apps for their LEA.
  # SEA admin authorization not implemented yet.
  def check_rights
    unless is_lea_admin?
      render_403
    end
  end

  # GET /application_authorizations
  # GET /application_authorizations.json
  def index
    @application_authorizations = ApplicationAuthorization.all
    if @application_authorizations.length == 0
      newAppAuthorization = ApplicationAuthorization.new({"authId" => session[:edOrg], "authType" => "EDUCATION_ORGANIZATION", "appIds" => []})
      @application_authorizations = [newAppAuthorization]
    end
    @apps = @application_authorizations.first.apps_for_auths
    respond_to do |format|
      format.html # index.html.erb
      format.json { render json: @application_authorizations }
    end
  end

  # GET /application_authorizations/1
  # GET /application_authorizations/1.json
  def show
    @application_authorization = ApplicationAuthorization.find(params[:id])

    respond_to do |format|
      format.html # show.html.erb
      format.json { render json: @application_authorization }
    end
  end

  ## GET /application_authorizations/new
  ## GET /application_authorizations/new.json
  #def new
  #  @application_authorization = ApplicationAuthorization.new
  #
  #  respond_to do |format|
  #    format.html # new.html.erb
  #    format.json { render json: @application_authorization }
  #  end
  #end
  #
  ## GET /application_authorizations/1/edit
  #def edit
  #  @application_authorization = ApplicationAuthorization.find(params[:id])
  #end
  #
  # POST /application_authorizations
  # POST /application_authorizations.json
  def create
    appId = params[:application_authorization][:appId]

    @application_authorization = ApplicationAuthorization.new({"authId" => Check.get("")["edOrg"], "authType" => "EDUCATION_ORGANIZATION", "appIds" => [appId]})

    respond_to do |format|
      if @application_authorization.save
        format.html { redirect_to @application_authorization, notice: 'Application authorization was successfully created.' }
        format.json { render json: @application_authorization, status: :created, location: @application_authorization }
      else
        format.html { render action: "new" }
        format.json { render json: @application_authorization.errors, status: :unprocessable_entity }
      end
    end
  end

  # PUT /application_authorizations/1
  # PUT /application_authorizations/1.json
  def update
    @application_authorization = ApplicationAuthorization.find(params[:id])
    appId = params[:application_authorization][:appId]
    idArray = @application_authorization.appIds

    if(params[:commit] == "Deny")
      idArray.delete(appId)
    else
      idArray.unshift(appId)
    end
    updates = {"appIds" =>  idArray}
    respond_to do |format|
      if @application_authorization.update_attributes(updates)
        format.html { redirect_to @application_authorization, notice: 'Application authorization was successfully updated.' }
        #format.html {redirect_to :action => 'index', notice: 'Application authorization was succesfully updated.'}
        format.json { head :ok }
      else
        format.html { render action: "edit" }
        format.json { render json: @application_authorization.errors, status: :unprocessable_entity }
      end
    end
  end

  ## DELETE /application_authorizations/1
  ## DELETE /application_authorizations/1.json
  #def destroy
  #  @application_authorization = ApplicationAuthorization.find(params[:id])
  #  @application_authorization.destroy
  #
  #  respond_to do |format|
  #    format.html { redirect_to application_authorizations_url }
  #    format.json { head :ok }
  #  end
  #end
end
