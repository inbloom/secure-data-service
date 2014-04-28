class RealmManagementController < ApplicationController

  before_filter :check_rights

  # GET /realm_management
  # GET /realm_management.json
  def index
    user_realm = session[:edOrg]
    @realms = Realm.get_realm_to_redirect_to(user_realm)
    redirect_to new_realm_management_path and return if @realms.empty?

    edorg_entity = EducationOrganization.get('', headers = {:stateOrganizationId => user_realm})
    if edorg_entity && !edorg_entity.empty?
      @edorg = "#{edorg_entity['nameOfInstitution']} (#{session[:edOrg]})"
    else
      @edorg = session[:edOrg]       
    end
    respond_to do |format|
      format.html
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
        success = true if @realm.valid? && @realm.idp.valid?
      rescue ActiveResource::BadRequest => error
        @realm.errors.add(:uniqueIdentifier, 'must be unique') if error.response.body.include?('Cannot have duplicate unique identifiers')
        @realm.errors.add(:name, 'must be unique') if error.response.body.include?('Cannot have duplicate display names')
      end
      if success
        @realm = Realm.find(@realm.id)
        format.html { redirect_to realm_management_index_path,  notice: 'Realm was successfully created.' }
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
    params[:realm] ||= {}

    respond_to do |format|
      success = false
      begin
        @realm.update_attributes(params[:realm])
        success = true if @realm.valid? && @realm.idp.valid?
      rescue ActiveResource::BadRequest => error
        @realm.errors.add(:uniqueIdentifier, 'must be unique') if error.response.body.include? 'unique'
        @realm.errors.add(:name, 'must be unique') if error.response.body.include? 'display'
        @realm.errors[:base].push('IDP URL must be unique') if error.response.body.include? 'idp ids'
      end
      if success
        format.html { redirect_to realm_management_index_path, notice: 'Realm was successfully updated.' }
        format.json { head :ok }
      else
        format.html { render action: 'edit' }
        format.json { render json: @realm.errors, status: :unprocessable_entity }
      end
    end
  end

  ## DELETE /realm_management/1
  ## DELETE /realm_management/1.json
  def destroy
   @realm = Realm.find(params[:id])
   @realm.destroy
   flash.now[:notice] = 'Realm was successfully deleted'
  end

  private

  def check_rights
    unless session[:rights].include?('CRUD_REALM')
      logger.warn 'User does not have CRUD_REALM right and cannot create/delete/modify realms'
      raise ActiveResource::ForbiddenAccess, caller
    end
  end

end
