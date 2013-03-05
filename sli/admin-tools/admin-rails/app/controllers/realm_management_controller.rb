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


include GeneralRealmHelper

class RealmManagementController < ApplicationController
  # GET /realm_management
  # GET /realm_management.json
  def index
    userRealm = session[:edOrg]
    @realms = GeneralRealmHelper.get_realm_to_redirect_to(userRealm)
    logger.debug {"Realms #{@realms.to_json}"}
    if @realms.nil? or @realms.empty?
      redirect_to new_realm_management_path and return
    end
    edorg_entity = EducationOrganization.get("?stateOrganizationId=#{userRealm}")
    puts edorg_entity.inspect 
    if edorg_entity != nil && !edorg_entity.empty?
      @edorg = edorg_entity["nameOfInstitution"] + " (#{session[:edOrg]})"
    else
      @edorg = session[:edOrg]       
    end
    respond_to do |format|
      format.html
    end and return
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
      rescue ActiveResource::BadRequest => error
        @realm.errors.add(:uniqueIdentifier, "must be unique") if error.response.body.include? "unique"
        @realm.errors.add(:name, "must be unique") if error.response.body.include? "display"
        @realm.errors[:base].push("IDP URL must be unique")  if error.response.body.include? "idp ids"
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
    params[:realm] = {} if params[:realm] == nil

    respond_to do |format|
      success = false
      begin
        @realm.update_attributes(params[:realm])
        success = true if @realm.valid? and @realm.idp.valid?
      rescue ActiveResource::BadRequest => error
        @realm.errors.add(:uniqueIdentifier, "must be unique") if error.response.body.include? "unique"
        @realm.errors.add(:name, "must be unique") if error.response.body.include? "display"
        @realm.errors[:base].push("IDP URL must be unique") if error.response.body.include? "idp ids"
      end
      if success
        format.html { redirect_to realm_management_index_path, notice: 'Realm was successfully updated.' }
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
  def destroy
   @realm = Realm.find(params[:id])
   @realm.destroy
  end
end
