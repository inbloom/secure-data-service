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

class RealmsController < ApplicationController

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
    @sli_roles = get_roles
  end

  # POST /roles.json
  def create
    logger.debug("Creating a new realm")
    @realm = Realm.new(params[:realm])
    @realm.edOrg = session[:edOrg]
    logger.debug{"Creating realm #{@realm}"}
    respond_to do |format|
      if @realm.save
        format.html { redirect_to realm_management_index_path, notice: 'Realm was successfully created.' }
        format.json { render json: @realm, status: :created, location: @realm }
      else
        format.html { render action: "new" }
        format.json { render json: @realm.errors, status: :unprocessable_entity }
      end
    end
  end

  # DELETE /realms/1
  # DELETE /realms/1.json
  def destroy
    @realm = Realm.find(params[:id])
    @realm.destroy

    respond_to do |format|
      format.html { redirect_to new_realm_management_path, notice: "Realm was successfully deleted." }
      format.json { head :ok }
    end
  end

  private

  # Uses the /role api to get the list of roles
  def get_roles()
    roles = Role.all

    toReturn = []
    roles.each do |role|
      toReturn.push role.name unless role.admin
    end
    toReturn
  end

  def get_user_realm
    return session[:edOrg]
  end

end
