include GeneralRealmHelper

class RealmManagementController < ApplicationController
  # GET /realm_management
  # GET /realm_management.json
  def index
    userRealm = session[:edOrg]
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
  def show
   @realm = Realm.find(params[:id])
  
   respond_to do |format|
     format.html # show.html.erb
     format.json { render json: @realm }
   end
  end

  # GET /realm_management/new
  # GET /realm_management/new.json
  def new
    @realm = Realm.new
    @realm.idp = Realm::Idp.new
  end

  # GET /realm_management/1/edit
  def edit
    @realm = Realm.find(params[:id])
  end

  # POST /realm_management
  # POST /realm_management.json
  def create
    @realm = Realm.new(params[:realm])
    @realm.edOrg = session[:edOrg]
    respond_to do |format|
      success = false
      begin
        @realm.save
        success = true if @realm.valid? and @realm.idp.valid?
        flash[:notice] = 'Realm was successfully created.'
      rescue ActiveResource::BadRequest => error
        @realm.errors.add(:uniqueIdentifier, "must be unique") if error.response.body.include? "unique"
      end
      if success
        @realm = Realm.find(@realm.id)
        format.html { redirect_to edit_realm_management_path(@realm),  notice: 'Realm was successfully created.' }
        format.json { render json: @realm, status: :created, location: @realm }
      else
        format.html { render action: "new" }
        format.json { render json: @realm.errors, status: :unprocessable_entity }
      end
    end
  end

  # PUT /realm_management/1
  # PUT /realm_management/1.json
  def update
   @realm = Realm.find(params[:id])
   params[:realm] = {} if params[:realm] == nil
   params[:realm][:mappings] = params[:mappings] if params[:mappings] != nil
  
   respond_to do |format|
     success = false
     begin
       @realm.update_attributes(params[:realm])
       success = true if @realm.valid? and @realm.idp.valid?
     rescue ActiveResource::BadRequest => error
       @realm.errors.add(:uniqueIdentifier, "must be unique") if error.response.body.include? "unique"
     end
     if success
       format.html { redirect_to edit_realm_management_path(@realm), notice: 'Realm was successfully updated.' }
       format.json { head :ok }
     else
       format.html { render action: "edit" }
       format.json { render json: @realm.errors, status: :unprocessable_entity }
     end
   end
  end
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
