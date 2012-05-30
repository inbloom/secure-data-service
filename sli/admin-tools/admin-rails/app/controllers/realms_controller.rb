include ActiveSupport::Rescuable
include GeneralRealmHelper

class RealmsController < ApplicationController

  rescue_from ActiveResource::ForbiddenAccess, :with => :render_403
  rescue_from ActiveResource::ResourceNotFound, :with => :render_404

  # GET /realms
  # GET /realms.json
  def index
    userRealm = get_user_realm
    realmToRedirectTo = GeneralRealmHelper.get_realm_to_redirect_to(userRealm)
    logger.debug("Redirecting to #{realmToRedirectTo}")
    if realmToRedirectTo.nil?
      render_404
    else
      redirect_to realmToRedirectTo
    end
  end

  # # GET /realms/1
  # # GET /realms/1.json
   def show
     @realm = Realm.find(params[:id])
     respond_to do |format|
       format.html # show.html.erb
       format.json { render json: @realm }
     end
   end

  # # GET /realms/1/edit
   def edit
     @realm = Realm.find(params[:id])
     @sli_roles = get_roles
   end

  # # PUT /realms/1
   def update
     @realm = Realm.find(params[:id])
     params[:realm] = {} if params[:realm] == nil
     params[:realm][:mappings] = params[:mappings] if params[:mappings] != nil
     respond_to do |format|
       success = false
       errorMsg = ""
       begin
         success =  @realm.update_attributes(params[:realm])
       rescue ActiveResource::BadRequest => error
         errorMsg = error.response.body
         logger.debug("Error: #{errorMsg}")
       end

       if success
         format.html { redirect_to edit_realm_management_path, notice: 'Realm was successfully updated.' }
         format.json { render json: @realm, status: :created, location: @realm }
       else
         format.json { render json: errorMsg, status: :unprocessable_entity }
       end
	
     end
   end

   # POST /roles
  # POST /roles.json
  def create
     logger.debug("Creating a new realm")
     @realm = Realm.new(params[:realm])
     @realm.saml = {} if @realm.saml == nil
     if @realm.mappings == nil
      @realm.mappings = {}
      @realm.mappings["role"] = [{"sliRoleName" => "IT Administrator", "clientRoleName" => [ "IT Administrator" ] }, {"sliRoleName" => "Educator", "clientRoleName" => [ "Educator" ] }, {"sliRoleName" => "Aggregate Viewer", "clientRoleName" => [ "Aggregate Viewer" ] }, {"sliRoleName" => "Leader", "clientRoleName" => [ "Leader" ] }]
     end
     @realm.admin = false
     @realm.edOrg = session[:edOrg]
     #Default saml mapping
     @realm.saml =  { "field" => [ { "clientName" => "roles", "sliName" => "roles", "transform" => "(.+)" }, { "clientName" => "userId", "sliName" => "userId", "transform" => "(.+)" }, { "clientName" => "userName", "sliName" => "userName", "transform" => "(.+)" } ] }
     logger.debug{"Creating realm #{@realm}"}

     respond_to do |format|
       if @realm.save
         format.html { redirect_to realm_management_index_path, notice: 'Realm was successfully created.' }
         format.json { render json: @realm, status: :created, location: @realm }
       else
         format.html { render action: "new" }
         format.json { render json: @realm.errors, status: :unprocessable_entity }
       end
     end
  end

  # DELETE /realms/1
  # DELETE /realms/1.json
  def destroy
    @realm = Realm.find(params[:id])
    @realm.destroy

    respond_to do |format|
      format.html { redirect_to new_realm_management_path, notice: "Realm was successfully deleted." }
      format.json { head :ok }
    end
  end

private

  # Uses the /role api to get the list of roles
  def get_roles()
    roles = Role.all

    toReturn = []
    roles.each do |role|
      toReturn.push role.name unless role.admin
    end
    toReturn
  end

  def get_user_realm
    return session[:edOrg]
  end

end
