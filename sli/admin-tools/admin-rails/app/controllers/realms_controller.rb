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
    realms.each do |realm|
      realmToRedirectTo = realm if realm.idp.id == userRealm
    end
    if realmToRedirectTo != nil
      redirect_to realmToRedirectTo
      return
    end
    render_404
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

     @realm.mappings = params[:mappings];
     respond_to do |format|
       success = false
       errorMsg = ""

       begin
         success =  @realm.save()
       rescue ActiveResource::BadRequest => error
         errorMsg = error.response.body
       end

       if success && params[:mappings] != nil
         format.json { render json: @realm }
       else
         format.json { render json: errorMsg, status: :unprocessable_entity }
       end
	
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
    return Check.new(SessionResource.auth_id).realm
  end

end
