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
    userRealm = session[:edOrg]
    realmToRedirectTo = GeneralRealmHelper.get_realm_to_redirect_to(userRealm)
    custom_roles = CustomRole.find(:all)
    custom_roles.each do |role|
      if role.realmId == realmToRedirectTo.id
        redirect_to  :action => "show", :id => role.id
        return
      end
    end
    
  end


  # # GET /realms/1/
   def show
     @custom_roles = CustomRole.find(params[:id])
     @default_roles = CustomRole.defaults()
   end

  # # PUT /realms/1
   def update
     @custom_roles = CustomRole.find(params[:id])
     respond_to do |format|
       success = false
       errorMsg = ""
       begin
         @custom_roles.roles = params[:json]
         success =  @custom_roles.save()
       rescue ActiveResource::BadRequest => error
         errorMsg = error.response.body
         logger.debug("Error: #{errorMsg}")
       end

       if success
         format.json { render json: @custom_roles, status: :created, location: @custom_roles }
       else
         #errorJson = JSON.parse(errorMsg)
         flash[:error] = errorMsg
         format.json { render json: errorMsg, status: :unprocessable_entity }
       end
	
     end
   end

private

  def get_user_realm
    return session[:edOrg]
  end

end
