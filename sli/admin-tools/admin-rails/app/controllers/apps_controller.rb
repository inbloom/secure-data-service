class AppsController < ApplicationController
  before_filter :check_rights

  # Let us add some docs to this confusing controller.
  # NOTE this controller is performing two actions:
  # It allows developers to create new apps.
  # It also allows slc operators approve an app for use in the SLC.
  def check_rights
    unless is_developer? or is_operator?
      raise ActiveResource::ForbiddenAccess, caller
    end
  end

  # GET /apps
  # GET /apps.json
  def index
    @title = "Application Registration Tool"
    @apps = App.all.sort { |a,b| b.metaData.updated <=> a.metaData.updated }
    respond_to do |format|
      format.html # index.html.erb
      format.json { render json: @apps }
    end
  end

  # GET /apps/1
  # GET /apps/1.json
  def show
    @app = App.find(params[:id])
  
    respond_to do |format|
      format.html # show.html.erb
      format.json { render json: @app }
    end
  end

  # GET /apps/new
  # GET /apps/new.json
  def new
    @title = "New Application"
    @app = App.new
      
    respond_to do |format|
      format.html # new.html.erb
      format.json { render json: @app }
    end
  end
  
  # GET /apps/1/edit
  # GET /apps/1/edit.json
  def edit
    @title = "Edit Application"
    @district_hierarchy = get_district_hierarchy
    @app = App.find(params[:id])
  end

  def approve
    @app = App.find(params[:id])
    respond_to do |format|
      reg = @app.attributes["registration"]
      reg.status = "APPROVED"
      if @app.update_attribute("registration", reg)
        user_info = APP_LDAP_CLIENT.read_user(@app.metaData.createdBy)
        ApplicationMailer.notify_developer(@app, user_info[:first]).deliver
        format.html { redirect_to apps_path, notice: 'App was successfully updated.' }
        format.json { head :ok }
      else
        format.html { render action: "edit" }
        format.json { render json: @app.errors, status: :unprocessable_entity }
      end
    end
  end

  def unregister
    @app = App.find(params[:id])
    respond_to do |format|
      reg = @app.attributes["registration"]
      if reg.status == 'PENDING'
        reg.status = 'DENIED'
      else
        reg.status = "UNREGISTERED"
      end
      if @app.update_attribute("registration", reg)
        #ApplicationMailer.notify_operator(@app).deliver
        format.html { redirect_to apps_path, notice: 'App was successfully updated.' }
        format.json { head :ok }
      else
        format.html { render action: "edit" }
        format.json { render json: @app.errors, status: :unprocessable_entity }
      end
    end
  end

  # # GET /apps/1/edit
  # def edit
  #   @app = App.find(params[:id])
  # end

  # POST /apps
  # POST /apps.json
  def create
    # if operator?
    #       redirect_to apps_path, notice: "Only developers can create new applications" and return
    #     end
    #ugg...can't figure out why rails nests the app_behavior attribute outside the rest of the app
    params[:app][:behavior] = params[:app_behavior]
    params[:app][:authorized_ed_orgs] = params[:authorized_ed_orgs]
    params[:app][:authorized_ed_orgs] = [] if params[:app][:authorized_ed_orgs] == nil
    params[:app].delete_if {|key, value| ["administration_url", "image_url"].include? key and value.length == 0 }
    
    logger.debug {params[:app].inspect}

    @app = App.new(params[:app])
    logger.debug{"Application is valid? #{@app.valid?}"}
    @app.is_admin = boolean_fix @app.is_admin
    @app.installed = boolean_fix @app.installed

    respond_to do |format|
      if @app.save
        logger.debug {"Redirecting to #{apps_path}"}
        if !APP_CONFIG["is_sandbox"]
            # Want to read the created_by on the @app, which is stamped during the created.
            # Tried @app.reload and it didn't work
            creator_email = App.find(@app.id).created_by
            user_info = APP_LDAP_CLIENT.read_user(session[:support_email])
            dev_info = APP_LDAP_CLIENT.read_user(creator_email)
            ApplicationMailer.notify_operator(session[:support_email], @app, user_info[:first], "#{dev_info[:first]} #{dev_info[:last]}").deliver
        end
        format.html { redirect_to apps_path, notice: 'App was successfully created.' }
        format.json { render json: @app, status: :created, location: @app }
        # format.js
      else
        format.html { render action: "new" }
        format.json { render json: @app.errors, status: :unprocessable_entity }
        # format.js
      end
    end
  end

  # PUT /apps/1
  # PUT /apps/1.json
  def update
    @app = App.find(params[:id])
    logger.debug {"App found (Update): #{@app.attributes}"}

    params[:app][:is_admin] = boolean_fix params[:app][:is_admin]
    params[:app][:installed] = boolean_fix params[:app][:installed]
    params[:app][:authorized_ed_orgs] = params[@app.name.gsub(" ", "_") + "_authorized_ed_orgs"]
    params[:app][:authorized_ed_orgs] = [] if params[:app][:authorized_ed_orgs] == nil

    #ugg...can't figure out why rails nests the app_behavior attribute outside the rest of the app
    params[:app][:behavior] = params[:app_behavior]

    respond_to do |format|
      if @app.update_attributes(params[:app])
          format.html { redirect_to apps_path, notice: 'App was successfully updated.' }
          format.json { head :ok }
      else
        format.html { render action: "edit" }
        format.json { render json: @app.errors, status: :unprocessable_entity }
      end
    end
  end

  # DELETE /apps/1
  # DELETE /apps/1.json
  def destroy
    @app = App.find(params[:id])
    @app.destroy
  
    respond_to do |format|
      format.js
      # format.html { redirect_to apps_url }
      # format.json { head :ok }
    end
  end

  private
  def boolean_fix (parameter)
    case parameter
    when "1"
      parameter = true
    when "0"
      parameter = false
    end
  end

  def get_district_hierarchy
    state_ed_orgs = EducationOrganization.all
    result = {}
    user_tenant = get_tenant
    state_ed_orgs.each do |ed_org|
      # In sandbox mode, only show edorgs for the current user's tenant
      filter_tenant = APP_CONFIG["is_sandbox"] && (!ed_org.metaData.attributes.has_key?("tenantId") || ed_org.metaData.tenantId != user_tenant)

      next if ed_org.organizationCategories == nil or ed_org.organizationCategories.index("State Education Agency") == nil or filter_tenant
      current_parent = {"id" => ed_org.id, "name" => ed_org.nameOfInstitution, "stateOrganizationId" => ed_org.stateOrganizationId}
      child_ed_orgs = EducationOrganization.find(:all, :params => {"parentEducationAgencyReference" => ed_org.id})
      child_ed_orgs.each do |child_ed_org|
        current_child = {"id" => child_ed_org.id, "name" => child_ed_org.nameOfInstitution, "stateOrganizationId" => child_ed_org.stateOrganizationId}
        if result.keys.include?(current_parent)
          result[current_parent].push(current_child)
        else
          result[current_parent] = [current_child]
        end
      end
    end
    result
  end

end
