include GeneralRealmHelper

class RealmManagementController < ApplicationController
  # GET /realm_management
  # GET /realm_management.json
  def index
    userRealm = session[:edOrg]
    Check.get("")
    realmToRedirectTo = GeneralRealmHelper.get_realm_to_redirect_to(userRealm)
    logger.debug("Redirecting to #{realmToRedirectTo}")
    if realmToRedirectTo.nil? and session[:roles] != nil and session[:roles].member?("Realm Administrator")
      redirect_to new_realm_management_path, notice: notice
    elsif realmToRedirectTo.nil?
      render_404
    else
      redirect_to edit_realm_management_path(realmToRedirectTo), notice: notice
    end
  end

  ## GET /realm_management/1
  ## GET /realm_management/1.json
  #def show
  #  @realm = Realm.find(params[:id])
  #
  #  respond_to do |format|
  #    format.html # show.html.erb
  #    format.json { render json: @realm }
  #  end
  #end

  # GET /realm_management/new
  # GET /realm_management/new.json
  def new
    @realm = Realm.new

    respond_to do |format|
      format.html # new.html.erb
      format.json { render json: @realm }
    end
  end

  # GET /realm_management/1/edit
  def edit
    @realm = Realm.find(params[:id])
  end

  # POST /realm_management
  # POST /realm_management.json
  #def create
  #  @realm = Realm.new(params[:realm_editor])
  #
  #  respond_to do |format|
  #    if @realm.save
  #      format.html { redirect_to @realm, notice: 'Realm editor was successfully created.' }
  #      format.json { render json: @realm, status: :created, location: @realm }
  #    else
  #      format.html { render action: "new" }
  #      format.json { render json: @realm.errors, status: :unprocessable_entity }
  #    end
  #  end
  #end

  # PUT /realm_management/1
  # PUT /realm_management/1.json
  #def update
  #  @realm = Realm.find(params[:id])
  #
  #  respond_to do |format|
  #    if @realm.update_attributes(params[:realm_editor])
  #      format.html { redirect_to @realm, notice: 'Realm editor was successfully updated.' }
  #      format.json { head :ok }
  #    else
  #      format.html { render action: "edit" }
  #      format.json { render json: @realm.errors, status: :unprocessable_entity }
  #    end
  #  end
  #end
  #
  ## DELETE /realm_management/1
  ## DELETE /realm_management/1.json
  #def destroy
  #  @realm = Realm.find(params[:id])
  #  @realm.destroy
  #
  #  respond_to do |format|
  #    format.html { redirect_to realm_editors_url }
  #    format.json { head :ok }
  #  end
  #end
end
