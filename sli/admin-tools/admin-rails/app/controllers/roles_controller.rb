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


class RolesController < ApplicationController
  # GET /roles
  # GET /roles.json
  def index
    @roles = Role.get_static_information
    respond_to do |format|
      format.html # index.html.erb
      format.json { render json: @roles }
    end
  end

  # GET /roles/1
  # GET /roles/1.json
  def show
    @role = Role.find(params[:id])
  
    respond_to do |format|
      format.html # show.html.erb
      format.json { render json: @role }
    end
  end

  # # GET /roles/new
  # # GET /roles/new.json
  # def new
  #   @role = Role.new
  # 
  #   respond_to do |format|
  #     format.html # new.html.erb
  #     format.json { render json: @role }
  #   end
  # end

  # GET /roles/1/edit
  # def edit
  #   @role = Role.find(params[:id])
  # end

  # POST /roles
  # POST /roles.json
  # def create
  #   @role = Role.new(params[:role])
  # 
  #   respond_to do |format|
  #     if @role.save
  #       format.html { redirect_to @role, notice: 'Role was successfully created.' }
  #       format.json { render json: @role, status: :created, location: @role }
  #     else
  #       format.html { render action: "new" }
  #       format.json { render json: @role.errors, status: :unprocessable_entity }
  #     end
  #   end
  # end

  # PUT /roles/1
  # PUT /roles/1.json
  # def update
  #   @role = Role.find(params[:id])
  #   puts "Role found (Update): #{@role.attributes}"
  #   respond_to do |format|
  #     if @role.update_attributes(params[:role])
  #       format.html { redirect_to @role.id, notice: 'Role was successfully updated.' }
  #       format.json { head :ok }
  #     else
  #       format.html { render action: "edit" }
  #       format.json { render json: @role.errors, status: :unprocessable_entity }
  #     end
  #   end
  # end

  # DELETE /roles/1
  # DELETE /roles/1.json
  # def destroy
  #   @role = Role.find(params[:id])
  #   @role.destroy
  # 
  #   respond_to do |format|
  #     format.html { redirect_to roles_url }
  #     format.json { head :ok }
  #   end
  # end
end
