=begin

Copyright 2012 Shared Learning Collaborative, LLC

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

class CustomRolesController < ApplicationController
  # GET /realms
  # GET /realms.json
  def index
    custom_role = CustomRole.all
    if !custom_role.nil? and custom_role.size == 1
      redirect_to  :action => "show", :id => custom_role[0].realmId
    else
      redirect_to realm_management_index_path
    end
  end


  # # GET /realms/1/
  def show
    @custom_roles = CustomRole.find(:first, :params => {"realmId" => params[:id]})
    logger.debug {"Custom Roles: #{@custom_roles.to_json}"}
    @default_roles = CustomRole.defaults()
  end

  # # PUT /realms/1
  def update
    @custom_roles = CustomRole.find(:first, :params => {"realmId" => params[:id]})
    respond_to do |format|
      success = false
      errorMsg = ""
      begin
        @custom_roles.roles = params[:json]
        success =  @custom_roles.save()
      rescue ActiveResource::BadRequest => error
        logger.error error.message
        logger.error error.backtrace.join("\n")

        errorMsg = error.response.body
        logger.debug("Error: #{errorMsg}")
      end

      if success
        format.json { render json: @custom_roles, status: :created, location: @custom_roles }
      else
        #errorJson = JSON.parse(errorMsg)
        if /ValidationError.*groupTitle/.match(errorMsg)
          flash[:error] = "Group name contains invalid characters."
        elsif /ValidationError.*names/.match(errorMsg)
          flash[:error] = "Role name contains invalid characters."
        else
          flash[:error] = "Changes could not be saved."
        end
        format.json { render json: errorMsg, status: :unprocessable_entity }
      end

    end
  end

  private

  def get_user_realm
    return session[:edOrg]
  end

end
