include ActiveSupport::Rescuable
class RealmsController < ApplicationController

  rescue_from ActiveResource::ResourceNotFound, :with => :render_404

  # GET /realms
  # GET /realms.json
  def index
    @realms = Realm.all

    #SessionResource.auth_id = cookies['iPlanetDirectoryPro']
     
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
     @mapping = get_mappings_from_realm(params[:id])
     @sli_roles = []
     @mapping.each do |mapping|
       @sli_roles.push mapping[:name]
     end
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
         #format.html { redirect_to @realm, notice: 'Realm was successfully updated.' }
         format.json { render json: @realm }
       else
         #format.html { render action: "edit" }
         #format.json { render json: @realm.errors, status: :unprocessable_entity }
         format.json { render json: errorMsg, status: :unprocessable_entity }
       end
	
     end
   end

  def get_mappings_from_realm(realm)
    roles = Role.all
    mapping = []
    roles.each do |role|
      map = {}
      map[:name] = role.name
      mapping.push map
    end
    mapping
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

