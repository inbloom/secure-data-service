include ActiveSupport::Rescuable

class RealmsController < ApplicationController

  rescue_from ActiveResource::ForbiddenAccess, :with => :render_403
  rescue_from ActiveResource::ResourceNotFound, :with => :render_404

  # GET /realms
  # GET /realms.json
  def index
    #figure out the realm this user has access to
    userRealm = get_user_realm
    realmToRedirectTo = nil
    realms = Realm.all
    logger.debug {"User Realm: #{userRealm}"}
    realms.each do |realm|
        realmToRedirectTo = realm if realm.id.eql? userRealm
    end
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
         format.html { redirect_to realm_editors_path, notice: 'Realm was successfully updated.' }
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
     @realm.mappings = {} if @realm.mappings == nil
     @realm.admin = false
     logger.debug{"Creating realm #{@realm}"}

     respond_to do |format|
       if @realm.save
         format.html { redirect_to realm_editors_path, notice: 'Realm was successfully created.' }
         format.json { render json: @realm, status: :created, location: @realm }
       else
         format.html { render action: "new" }
         format.json { render json: @realm.errors, status: :unprocessable_entity }
       end
       puts("Responded")
     end
  end

  # DELETE /realms/1
  # DELETE /realms/1.json
  def destroy
    @realm = Realm.find(params[:id])
    @realm.destroy

    respond_to do |format|
      format.html { redirect_to realm_editors_url }
      format.json { head :ok }
    end
  end

private

  # Uses the /role api to get the list of roles
  def get_roles()
    roles = Role.all
    toReturn = []
    roles.each do |role|
      toReturn.push role.name unless role.name == "SLI Administrator"
    end
    toReturn
  end

  #TODO:  current we're just checking the realm the user authenticated to,
  # but ultimately we need to get that somewhere else since the user will
  # always be authenticated to the SLI realm
  def get_user_realm
    return session[:adminRealm]
  end

end
