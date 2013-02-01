=begin

Copyright 2012-2013 inBloom, Inc. and its affiliates.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

=end


class AppsController < ApplicationController
  before_filter :check_rights

  $column_names = ["name", "vendor", "version", "metaData.created", "metaData.updated", "registration.approval_date", "registration.request_date"]

  # Let us add some docs to this confusing controller.
  # NOTE this controller is performing two actions:
  # It allows developers to create new apps.
  # It also allows inBloom operators approve an app for use in inBloom.
  def check_rights
    logger.debug {"Roles: #{session[:roles]}"}
    unless is_developer? or is_operator?
      raise ActiveResource::ForbiddenAccess, caller
    end
  end

  # GET /apps
  # GET /apps.json
  def index
    @title = "Application Registration Tool"

    @apps = App.all
    @apps = sort(@apps)

    respond_to do |format|
      format.html # index.html.erb
      format.json { render json: @apps }
    end
  end

  # GET /apps/1
  # GET /apps/1.json
  def show
    redirect_to apps_url
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
    @sea = get_state_edorgs
    @app = App.find(params[:id])
  end

  def approve
    @app = App.find(params[:id])
    respond_to do |format|
      reg = @app.attributes["registration"]
      reg.status = "APPROVED"
      if @app.update_attribute("registration", reg)
        dev_name = @app.author_first_name != nil ? @app.author_first_name : @app.metaData.createdBy
        ApplicationMailer.notify_developer(@app, dev_name).deliver
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
    params[:app].delete_if {|key, value| ["administration_url", "image_url", "application_url", "redirect_uri"].include? key and value.length == 0 }

    logger.debug {params[:app].inspect}

    @app = App.new(params[:app])
    # Want to read the created_by on the @app, which is stamped during the created.
    # Tried @app.reload and it didn't work
    dev_info = get_user_info(session[:external_id])
    @app.vendor = dev_info[:vendor]
    @app.is_admin = boolean_fix @app.is_admin
    @app.installed = boolean_fix @app.installed
    logger.debug{"Application is valid? #{@app.valid?}"}
    respond_to do |format|
      if @app.save
        logger.debug {"Redirecting to #{apps_path}"}
        if !APP_CONFIG["is_sandbox"]
          ApplicationMailer.notify_operator(session[:support_email], @app, "#{dev_info[:first]} #{dev_info[:last]}").deliver
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
    logger.debug("App params are #{params[:app].inspect}")
    params[:app][:is_admin] = boolean_fix params[:app][:is_admin]
    params[:app][:installed] = boolean_fix params[:app][:installed]
    params[:app][:authorized_ed_orgs] = [] if params[:app][:authorized_ed_orgs] == nil
    params[:app].delete_if {|key, value| ["administration_url", "image_url", "application_url", "redirect_uri"].include? key and value.length == 0 }
    #ugg...can't figure out why rails nests the app_behavior attribute outside the rest of the app
    params[:app][:behavior] = params[:app_behavior]
    @app.load(params[:app])
    # Want to read the created_by on the @app, which is stamped during the created.
    # Tried @app.reload and it didn't work
    dev_info = get_user_info(session[:external_id])
    @app.vendor = dev_info[:vendor]
    @app.attributes.delete :image_url unless params[:app].include? :image_url
    @app.attributes.delete :administration_url unless params[:app].include? :administration_url
    @app.attributes.delete :application_url unless params[:app].include? :application_url
    @app.attributes.delete :redirect_uri unless params[:app].include? :redirect_uri


    logger.debug {"App found (Update): #{@app.to_json}"}

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

  def get_state_edorgs
    state_ed_orgs = EducationOrganization.find(:all, :params => {"organizationCategories" => "State Education Agency", "limit" => 100})
    @results = []

    state_ed_orgs.each do |ed_org|
      current = {"name" => ed_org.nameOfInstitution, "state" => ed_org.address[0].stateAbbreviation }
      @results.push current
    end
    @results.sort! {|x, y| x["state"] <=> y["state"]}
  end

  def get_local_edorgs
    state = params[:state]
    @results = []
    count = 0
    local = EducationOrganization.find(:all, :params => {"organizationCategories" => "Local Education Agency", "address.stateAbbreviation" => state, "limit" => 0})
    until local.count == 0
      count += local.count
      local.each do |lea|
        temp = {"name" => lea.nameOfInstitution, "id" => lea.id}
        @results.push temp
      end
      local = EducationOrganization.find(:all, :params => {"organizationCategories" => "Local Education Agency", "address.stateAbbreviation" => state, "offset" => count, "limit" => 0})
    end
    @results.sort! {|x, y| x["name"] <=> y["name"]}
    render :partial => "lea_list", :locals => {:results => @results, :sea => params[:state]}
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


  def sort_column
    $column_names.include?(params[:sort]) ? params[:sort] : "metaData.updated"
  end

  def sort_direction
    %w[asc desc].include?(params[:direction]) ?  params[:direction] : "desc"
  end

  def sort(app_array)
    columns = sort_column().split(".")
    puts("The sort_column is #{sort_column()}")
    if sort_direction == "desc"
      app_array = app_array.sort { |a, b| getAttribute(b, columns) <=> getAttribute(a, columns)}
    else
      app_array = app_array.sort { |a, b| getAttribute(a, columns) <=> getAttribute(b, columns)}
    end
    app_array
  end

  def getAttribute(model, column_array)
    cur = model
    column_array.each do |col|
      cur = cur.attributes[col]
    end
    return cur

  end

  def get_user_info(uid)
    dev_info = APP_LDAP_CLIENT.read_user(uid)
    if dev_info == nil
        dev_info = Hash.new
        dev_info[:first] = session[:first_name]
        dev_info[:last] = session[:last_name]
        dev_info[:vendor] = session[:vendor]
    end
    dev_info[:vendor] = (dev_info.has_key?(:vendor) and dev_info[:vendor]) || (APP_CONFIG['is_sandbox'] ? "Sandbox" : "Unknown")
    return dev_info
  end
end
