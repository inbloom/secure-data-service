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

class UsersController < ApplicationController

  # GET /users
  # GET /users.json
  def index
    check = Check.get ""
    @loginUserId=check["external_id"]
    @users = User.all
    respond_to do |format|
      format.html # index.html.erb
      #format.json { render json: @users }
    end
  end
 
 
  # DELETE /users/1
  # DELETE /users/1.json
  def destroy 
    @users = User.all
    @users.each do |user|
      if user.uid == params[:id]
        user.id = user.uid
        user.destroy
       @user_id = user.uid.gsub(/\@/,"\\\\\\\\@").gsub(/\./, "\\\\\\\\."); #escape dot in uid for javascript
        logger.info("user id after escape is #{@user_id}")
      end
    end
    
    respond_to do |format|
      format.js
    end
  end
  
  # GET /apps/new
  # GET /apps/new.json
  def new
    check = Check.get ""
    @user = User.new
     @edorgs = {"" => "", check["edOrg"] => check["edOrg"]} 
     @sandbox_roles ={"Sandbox Administrator" => "Sandbox Administrator", "Application Developer" => "Application Developer", "Ingestion User" => "Ingestion User"}
    respond_to do |format|
      format.html # new.html.erb
      format.json { render json: @user }
    end
  end
  
  # POST /users
  # POST /users.json
  def create
    check = Check.get ""
    logger.info{"running create new user"}
    groups = []
    groups << params[:user][:primary_role]
    groups << params[:user][:optional_role_1] if params[:user][:optional_role_1]!="0" && !groups.include?(params[:user][:optional_role_1])
    groups << params[:user][:optional_role_2] if params[:user][:optional_role_2]!="0" && !groups.include?(params[:user][:optional_role_2])
    params[:user][:firstName]= params[:user][:fullName].split(" ")[0]
    params[:user][:lastName] = params[:user][:fullName].gsub(params[:user][:firstName],"").lstrip
    
    @user = User.new()

    @user.firstName = params[:user][:firstName]
    @user.lastName = params[:user][:lastName]
    @user.email = params[:user][:email]
    @user.tenant = check["tenantId"]
    @user.edorg = params[:user][:edorg]
    @user.uid = params[:user][:email]
    @user.groups = groups
    @user.homeDir = "/dev/null"
    
    logger.info("the new user is #{@user.to_json}")

    @user.save
    @users=User.all
     respond_to do |format|
        format.html { render "index"}
     end
    
  end
  
  # GET /users/1/edit
  # GET /users/1/edit.json
  def edit
    @users = User.all
    respond_to do |format|
      format.html # index.html.erb
      #format.json { render json: @users }
    end
  end
end