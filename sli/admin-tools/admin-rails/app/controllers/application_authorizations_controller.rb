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


class ApplicationAuthorizationsController < ApplicationController
  before_filter :check_rights

  # Let us add some docs to this confusing controller.
  # NOTE this controller allows ed org super admins to 
  # enable/disable apps for their LEA.
  # SEA admin authorization not implemented yet.
  def check_rights
    unless is_lea_admin? or is_sea_admin?
      logger.warn {'User is not lea or sea admin and cannot access application authorizations'}
      raise ActiveResource::ForbiddenAccess, caller
    end
  end

  # GET /application_authorizations
  # GET /application_authorizations.json
  def index
    @application_authorizations = ApplicationAuthorization.all
    existing_authorizations = @application_authorizations.map{|cur| cur.authId}
    if @application_authorizations.length == 0 and is_lea_admin?
      newAppAuthorization = ApplicationAuthorization.new({"authId" => session[:edOrgId], "authType" => "EDUCATION_ORGANIZATION", "appIds" => []})
      @application_authorizations = [newAppAuthorization]
    elsif is_sea_admin?
      my_delegations = AdminDelegation.all
      my_authorizations = (my_delegations.select{|delegation| delegation.appApprovalEnabled}).map{|cur| cur.localEdOrgId}
      missing_authorizations = my_authorizations - existing_authorizations
      missing_authorizations.each do |edOrg|
        newAppAuthorization = ApplicationAuthorization.new({"authId" => edOrg, "authType" => "EDUCATION_ORGANIZATION", "appIds" => []})
        @application_authorizations.push(newAppAuthorization)
      end
      @application_authorizations = @application_authorizations.sort {|a, b| a.authId <=> b.authId}
    end
    #Get EDORGS for the authId
    @edorgs = {}
    @application_authorizations.each do |auth|
      raise OutOfOrder unless EducationOrganization.exists? auth.authId
      @edorgs[auth.authId] = EducationOrganization.find(auth.authId).nameOfInstitution
    end
    respond_to do |format|
      format.html # index.html.erb
      format.json { render json: @application_authorizations }
    end
  end

  # POST /application_authorizations
  # POST /application_authorizations.json
  def create
    appId = params[:application_authorization][:appId]

    @application_authorization = ApplicationAuthorization.new({"authId" => params[:application_authorization][:authId], "authType" => "EDUCATION_ORGANIZATION", "appIds" => [appId]})

    respond_to do |format|
      if @application_authorization.save
        format.html { redirect_to application_authorizations_path, notice: 'Application authorization was successfully created.' }
        format.json { render json: @application_authorization, status: :created, location: @application_authorization }
      else
        format.html { render action: "new" }
        format.json { render json: @application_authorization.errors, status: :unprocessable_entity }
      end
    end
  end

  # PUT /application_authorizations/1
  # PUT /application_authorizations/1.json
  def update
    @application_authorization = ApplicationAuthorization.find(params[:id])
    appId = params[:application_authorization][:appId]
    idArray = @application_authorization.appIds

    if(params[:commit] == "Deny")
      idArray.delete(appId)
    else
      idArray.unshift(appId)
    end
    updates = {"appIds" =>  idArray}
    respond_to do |format|
      if @application_authorization.update_attributes(updates)
        format.html { redirect_to application_authorizations_path, notice: @application_authorization.authId}
        #format.html {redirect_to :action => 'index', notice: 'Application authorization was succesfully updated.'}
        format.json { head :ok }
      else
        format.html { render action: "edit" }
        format.json { render json: @application_authorization.errors, status: :unprocessable_entity }
      end
    end
  end
end
