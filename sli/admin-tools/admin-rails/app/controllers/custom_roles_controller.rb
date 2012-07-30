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
    userRealm = get_user_realm
    realmToRedirectTo = GeneralRealmHelper.get_realm_to_redirect_to(userRealm)
    logger.debug("Redirecting to #{realmToRedirectTo}")
    if realmToRedirectTo.nil?
      render_404
    else
      redirect_to  :action => "show", :id => CustomRole.find(:first).id
    end
  end


  # # GET /realms/1/
   def show
     @custom_roles = CustomRole.find(params[:id])
   end

  # # PUT /realms/1
   def update
     @custom_roles = CustomRole.find(params[:id])
     respond_to do |format|
       success = false
       errorMsg = ""
       begin
         @custom_roles.roles = params[:json]
puts params[:json]
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
