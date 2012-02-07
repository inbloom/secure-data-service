class RealmsController < ApplicationController
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
  
    #User's realm didn't match any known realm  
    respond_to do |format|
      format.html { render :status => :not_found, :text => "No valid realm found for #{userRealm}" }
      format.json { render json: @realms, :status => :not_found }
    end
  end

  # # GET /realms/1
  # # GET /realms/1.json
   def show
     begin
       @realm = Realm.find(params[:id])
     rescue ActiveResource::ResourceNotFound
       render :status => :not_found, :text => "Not found" #TODO: redirect to proper 404 page
       return
     end
     respond_to do |format|
       format.html # show.html.erb
       format.json { render json: @realm }
     end
   end
  # 
  # # GET /realms/new
  # # GET /realms/new.json
  # def new
  #   @realm = Realm.new
  # 
  #   respond_to do |format|
  #     format.html # new.html.erb
  #     format.json { render json: @realm }
  #   end
  # end
  # 
  # # GET /realms/1/edit
   def edit
     begin
       @realm = Realm.find(params[:id])
     rescue ActiveResource::ResourceNotFound
       render :status => :not_found, :text => "Not found" #TODO: redirect to proper 404 page
       return
     end
     @mapping = get_mappings_from_realm(params[:id])
     @sli_roles = []
     @mapping.each do |mapping|
       @sli_roles.push mapping[:name]
     end
   end
  # 
  # # POST /realms
  # # POST /realms.json
  # def create
  #   @realm = Realm.new(params[:realm])
  # 
  #   respond_to do |format|
  #     if @realm.save
  #       format.html { redirect_to @realm, notice: 'Realm was successfully created.' }
  #       format.json { render json: @realm, status: :created, location: @realm }
  #     else
  #       format.html { render action: "new" }
  #       format.json { render json: @realm.errors, status: :unprocessable_entity }
  #     end
  #   end
  # end
  # 
  # # PUT /realms/1
  # # PUT /realms/1.json
   def update
     begin
       @realm = Realm.find(params[:id])
     rescue ActiveResource::ResourceNotFound
       render json: {"response" => "Not found"}, :status => :not_found
       return
     end

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
  # 
  # # DELETE /realms/1
  # # DELETE /realms/1.json
  # def destroy
  #   @realm = Realm.find(params[:id])
  #   @realm.destroy
  # 
  #   respond_to do |format|
  #     format.html { redirect_to realms_url }
  #     format.json { head :ok }
  #   end
  # end
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
end
