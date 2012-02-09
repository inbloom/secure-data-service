include ActiveSupport::Rescuable
class RealmsController < ApplicationController

  rescue_from ActiveResource::ResourceNotFound, :with => :render_404

  # GET /realms
  # GET /realms.json
  def index
    @realms = Realm.all

    #figure out the realm this user has access to
    userRealm = get_user_realm
    @realms.each do |realm|
      if realm.respond_to?(:realm)
        if realm.realm == userRealm
          redirect_to realm
          return
        end
      end
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

  # Uses the /role api to get the list of roles
  def get_roles()
    roles = Role.all
    toReturn = []
    roles.each do |role|
      toReturn.push role.name
    end
    toReturn
  end

  #TODO:  current we're just checking the realm the user authenticated to,
  # but ultimately we need to get that somewhere else since the user will
  # always be authenticated to the SLI realm
  def get_user_realm
    return Check.new(SessionResource.auth_id).realm
  end

  def render_404
   respond_to do |format|
     format.html { render :file => "#{Rails.root}/public/404.html", :status => :not_found }
     #format.json { :status => :not_found}
     format.any  { head :not_found }
   end
  end
end

