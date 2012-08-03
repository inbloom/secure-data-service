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

 @@EXISTING_EMAIL_MSG = "An account with this email already exists"
  
  SANDBOX_ADMINISTRATOR = "Sandbox Administrator"
  APPLICATION_DEVELOPER = "Application Developer"
  INGESTION_USER = "Ingestion User"
  SLC_OPERATOR = "SLC Operator"
  SEA_ADMINISTRATOR = "SEA Administrator"
  LEA_ADMINISTRATOR = "LEA Administrator"
  REALM_ADMINISTRATOR ="Realm Administrator"
  SANDBOX_ALLOWED_ROLES = [SANDBOX_ADMINISTRATOR]
  PRODUCTION_ALLOWED_ROLES = [SLC_OPERATOR, SEA_ADMINISTRATOR, LEA_ADMINISTRATOR]

  before_filter :check_rights
  
  def check_rights
  if APP_CONFIG['is_sandbox']
  allowed_roles =SANDBOX_ALLOWED_ROLES
  else
  allowed_roles = PRODUCTION_ALLOWED_ROLES
  end
  overlap_roles = allowed_roles & session[:roles]
  if not overlap_roles.length>0
  render_403
  end
  
  
  end

  # GET /users
  # GET /users.json
  def index
    get_login_id
    @users = User.all
    respond_to do |format|
      format.html
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
        @users.delete(user)
        
     # @user_id = user.uid.gsub(/\@/,"\\\\\\\\@").gsub(/\./, "\\\\\\\\."); #escape dot in uid for javascript
      #  logger.info("user id after escape is #{@user_id}")
      end
    end
    get_login_id
    respond_to do |format|
    #format.js
    flash[:notice]="Success! You have deleted the user"
    format.html {render "index" }
    end
  end
  
  # GET /users/new
  # GET /users/new.json
  def new
    check = Check.get ""
    @user = User.new
    @is_operator = is_operator?
    @is_lea = is_lea_admin?
   set_edorg_options
   set_role_options
   get_login_tenant
    respond_to do |format|
      format.html # new.html.erb
      format.json { render json: @user }
    end
  end
  
  # POST /users
  # POST /users.json
  def create
    logger.info{"running create new user"}
    @user = User.new()
    create_update 
    @user.uid = params[:user][:email]   
    logger.info("the new user is #{@user.to_json}")
    logger.info("the new user validation is #{@user.valid?}")
    logger.info("the new user validation error is #{@user.errors.to_json}")
    @user.errors.clear
    if @user.valid? == false || validate_email==false || validate_tenant_edorg==false
      validate_tenant_edorg
     resend = true 
     else
    begin  
    @user.save
    rescue ActiveResource::ResourceConflict
      resend = true
      @user.errors[:email] << @@EXISTING_EMAIL_MSG
     rescue ActiveResource::BadRequest
     resend =true
     @user.errors[:tenant] << "tenant and edorg mismatch"
     @user.errors[:edorg] << "tenant and edorg mismatch"
    end
    
    end
    
     respond_to do |format|
       if resend
         set_edorg_options
         set_role_options
         set_roles
         get_login_tenant
         @is_operator = is_operator?
         @is_lea = is_lea_admin?
         format.html {render "new"}
       else
         flash[:notice]= 'Success! You have added a new user'
        format.html { redirect_to "/users" } 
       end
     end
    
  end
  
  # GET /users/1/edit
  # GET /users/1/edit.json
  def edit
    @users = User.all
    check = Check.get ""
    set_edorg_options
    set_role_options
    @is_operator = is_operator?
    @is_lea = is_lea_admin?
   @users.each do |user|
      if user.uid == params[:id]
        @user = user
        @user.id = user.uid
        logger.info{"find updated user #{user.to_json}"}
      end
    
    end
    
    set_roles
    @user.errors.clear
    @user.edorg = "" if @user.edorg==nil || @user.edorg == "null"
     
     logger.info{"find updated user #{@user.to_json}"}
    
  #  respond_to do |format|
      
   #   format.html {render "users/edit"}
      #format.json { render json: @users }
  #  end
  end
  
  # PUT /users/1/
  def update
    
    logger.info{"running the update user now"}
    @is_lea = is_lea_admin?
    @users = User.all
    @users.each do |user|
      if user.uid = params[:id]
        @user = user
      end
    end
    create_update
    
    @user.createTime="2000-01-01"
    @user.modifyTime="2000-01-01"
    
    logger.info{"the updated user is #{@user.to_json}"}
    @user.errors.clear
    logger.info{"the updated user validate is #{@user.valid?}"}
    logger.info{"the updated user validation errors is #{@user.errors.to_json}"}
    if @user.valid? == false || validate_email==false || validate_tenant_edorg==false
      validate_tenant_edorg
     resend = true 
     else
     begin
      @user.save
     rescue ActiveResource::BadRequest
     resend =true
     @user.errors[:tenant] << "tenant and edorg mismatch"
     @user.errors[:edorg] << "tenant and edorg mismatch"
     end
    end

     respond_to do |format|
       if resend
         @user.id = @user.uid
         set_edorg_options
         set_role_options
         set_roles
         @is_operator = is_operator?
         format.html { render "edit"}
       else
         flash[:notice]='Success! You have updated the user'
        format.html { redirect_to "/users" }
        end
     end
  end
  
  def create_update
    check = Check.get ""
    groups = []
    groups << params[:user][:primary_role]
    groups << params[:user][:optional_role_1] if params[:user][:optional_role_1]!="0" && !groups.include?(params[:user][:optional_role_1])
    groups << params[:user][:optional_role_2] if params[:user][:optional_role_2]!="0" && !groups.include?(params[:user][:optional_role_2])
    params[:user][:firstName]= params[:user][:fullName].split(" ")[0]
    params[:user][:lastName] = params[:user][:fullName].gsub(params[:user][:firstName],"").lstrip if params[:user][:fullName] !=nil && params[:user][:fullName]!=""
    @user.fullName = params[:user][:fullName]
    @user.fullName = nil if @user.fullName == ""
    @user.firstName = params[:user][:firstName]
    @user.lastName = params[:user][:lastName]
    @user.lastName = " " if @user.lastName==""
    @user.email = params[:user][:email]
    if APP_CONFIG['is_sandbox'] ||  !is_operator?
    @user.tenant = check["tenantId"]
    else
      @user.tenant = params[:user][:tenant]
    end
    @user.edorg = params[:user][:edorg]
   
    @user.groups = groups
    @user.homeDir = "/dev/null"
    return @user
    
  end
  
  # GET /users/1
  # GET /users/1.json
  def show
     respond_to do |format|
        format.html { redirect_to "/users" }
     end
     end
  
  def get_login_id
    check = Check.get ""
    @loginUserId=check["external_id"]
  end
  
  def get_login_tenant
    check = Check.get""
    @loginUserTenant = check["tenantId"]
  end
  
  def set_edorg_options
    if is_sea_admin? || is_lea_admin?
    check = Check.get ""
    @login_user_edorg_name = check['edOrg']
    @edorgs={check['edOrg']=> check ['edOrg']}
    if @login_user_edorg_name !=nil
    current_edorgs = EducationOrganization.find(:all, :params => {"stateOrganizationId" => @login_user_edorg_name})
    end
      while current_edorgs !=nil && current_edorgs.length>0
        
        child_edorgs=[]
        current_edorgs.each do |edorg|
          edorgs = EducationOrganization.find(:all, :params => {"parentEducationAgencyReference" => edorg.id } )
           if edorgs!=nil && edorgs.length>0
             edorgs.each do |temp_edorg|
               if temp_edorg.organizationCategories.index("State Education Agency") != nil || temp_edorg.organizationCategories.index("Local Education Agency")!=nil
                 child_edorgs.push temp_edorg
               end
             end
           end
         end
      if child_edorgs !=nil && child_edorgs.length>0
        child_edorgs.each do |child_edorg|
         @edorgs.merge!({child_edorg.stateOrganizationId => child_edorg.stateOrganizationId}) 
         logger.info("add education organization #{child_edorg.stateOrganizationId} to select options")
        end
      end
      current_edorgs = child_edorgs
     end
    
     logger.info("the edorg options are #{@edorgs.to_json}")
     end
  end
  
  
  def set_role_options
    if APP_CONFIG['is_sandbox']
    @sandbox_roles ={SANDBOX_ADMINISTRATOR => SANDBOX_ADMINISTRATOR, APPLICATION_DEVELOPER => APPLICATION_DEVELOPER, INGESTION_USER => INGESTION_USER } 
    elsif is_operator?
    @production_roles={SLC_OPERATOR => SLC_OPERATOR, SEA_ADMINISTRATOR => SEA_ADMINISTRATOR, LEA_ADMINISTRATOR => LEA_ADMINISTRATOR, INGESTION_USER => INGESTION_USER, REALM_ADMINISTRATOR => REALM_ADMINISTRATOR }
    
    elsif is_sea_admin?
       @production_roles={SEA_ADMINISTRATOR => SEA_ADMINISTRATOR, LEA_ADMINISTRATOR => LEA_ADMINISTRATOR, INGESTION_USER => INGESTION_USER, REALM_ADMINISTRATOR => REALM_ADMINISTRATOR }
    elsif is_lea_admin?
       @production_roles={LEA_ADMINISTRATOR => LEA_ADMINISTRATOR, INGESTION_USER => INGESTION_USER, REALM_ADMINISTRATOR => REALM_ADMINISTRATOR }

    end 
  end
  
  def set_roles
    if APP_CONFIG['is_sandbox']
    if @user.groups.include?(SANDBOX_ADMINISTRATOR)
       @user.primary_role = SANDBOX_ADMINISTRATOR
       if @user.groups.include?(APPLICATION_DEVELOPER)
         @user.optional_role_1 = APPLICATION_DEVELOPER
       end
        if @user.groups.include?(INGESTION_USER)
         @user.optional_role_2 = INGESTION_USER
       end
     elsif @user.groups.include?(APPLICATION_DEVELOPER)
       @user.primary_role = APPLICATION_DEVELOPER
        if @user.groups.include?(INGESTION_USER)
         @user.optional_role_2 = INGESTION_USER
        end
     elsif @user.groups.include?(INGESTION_USER)
       @user.primary_role = INGESTION_USER
     end
     else
       overlap_group = @user.groups & [SLC_OPERATOR, SEA_ADMINISTRATOR, LEA_ADMINISTRATOR]
       if overlap_group.length == 0 && @user.groups.include?(INGESTION_USER)
         @user.primary_role = INGESTION_USER
         if@user.groups.include?(REALM_ADMINISTRATOR)
           @user.optional_role_2 = REALM_ADMINISTRATOR
         end
        elsif overlap_group.length == 0 && @user.groups.include?(REALM_ADMINISTRATOR)
          @user.primary_role = REALM_ADMINISTRATOR
        elsif overlap_group.length == 1 && @user.groups.include?(SLC_OPERATOR)
          @user.primary_role = SLC_OPERATOR
           if@user.groups.include?(INGESTION_USER)
           @user.optional_role_1 = INGESTION_USER
           end
          if@user.groups.include?(REALM_ADMINISTRATOR)
           @user.optional_role_2 = REALM_ADMINISTRATOR
           end
         elsif overlap_group.length == 1 && @user.groups.include?(SEA_ADMINISTRATOR)
          @user.primary_role = SEA_ADMINISTRATOR
           if@user.groups.include?(INGESTION_USER)
           @user.optional_role_1 = INGESTION_USER
           end
          if@user.groups.include?(REALM_ADMINISTRATOR)
           @user.optional_role_2 = REALM_ADMINISTRATOR
           end
          elsif overlap_group.length == 1 && @user.groups.include?(LEA_ADMINISTRATOR)
          @user.primary_role = LEA_ADMINISTRATOR
           if@user.groups.include?(INGESTION_USER)
           @user.optional_role_1 = INGESTION_USER
           end
          if@user.groups.include?(REALM_ADMINISTRATOR)
           @user.optional_role_2 = REALM_ADMINISTRATOR
           end
          
       end
       end
    
  end
  
  def validate_email
    # don't validate empty values here, otherwise we get duplicate error messages
    valid=true
    if not @user.email =~ /^[-a-z0-9_]+([\.]{0,1}[-a-z0-9_]+)*\@([a-z0-9]+([-]*[a-z0-9]+)*\.)*([a-z0-9]+([-]*[a-z0-9]+))+$/i
      @user.errors[:email] << "Please enter a valid email address"
      valid =false
    elsif @user.email.length > 160
      @user.errors[:email] << "Email address is too long"
      valid=false
      end
      return valid
      end
      
      def validate_tenant_edorg
        valid =true
        if (is_operator? && !@user.groups.include?("SLC Operator")) && (@user.edorg==nil || @user.edorg=="")
          @user.errors[:edorg] << "can't be blank"
          valid=false
        end
        
        if (is_operator? && !@user.groups.include?("SLC Operator")) && (@user.tenant == nil || @user.tenant=="")
          @user.errors[:tenant] << "can't be blank"
          valid=false
        end
        return valid
      end
  
end
