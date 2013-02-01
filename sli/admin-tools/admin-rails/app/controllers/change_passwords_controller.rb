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

class ChangePasswordsController < ApplicationController
  before_filter :check_allowed_user

  # GET /change_passwords
  # GET /change_passwords.json
  def index
    @change_passwords = ChangePassword.all

    respond_to do |format|
      format.html # index.html.erb
      format.json { render json: @change_passwords }
    end
  end

  # GET /change_passwords/new
  # GET /change_passwords/new.json
  def new
    @prod_developer = is_developer? && !APP_CONFIG['is_sandbox']
    @change_password = ChangePassword.new

    respond_to do |format|
      format.html # new.html.erb
      format.json { render json: @change_password }
    end
  end

  # POST /change_passwords
  # POST /change_passwords.json
  def create
    @change_password = ChangePassword.new(params[:change_password])
    @change_password.errors.clear
    respond_to do |format|
      if @change_password.valid? == true
        email = session[:external_id]

        if !APP_LDAP_CLIENT.authenticate(email, @change_password.old_pass)
          @change_password.errors.add(:base, "Unable to verify old password, please try again.")
          format.html { render action: "new" }
          format.json { render json: @change_password.errors, status: :unprocessable_entity }
        else
          begin
            update_info = {
                :email => "#{email}",
                :password   => "#{@change_password.new_pass}"
            }
            response =  APP_LDAP_CLIENT.update_user_info(update_info)

            fullName = session[:full_name]
            emailAddress = APP_LDAP_CLIENT.read_user(email)[:emailAddress]
            ApplicationMailer.notify_password_change(emailAddress, fullName).deliver

            format.html { redirect_to new_change_password_path, notice: 'Your password has been successfully modified.'}
            format.json { render :json => @change_password, status: :created, location: @change_password }

          rescue InvalidPasswordException => e
            logger.error e.message
            logger.error e.backtrace.join("\n")
            
            APP_CONFIG['password_policy'].each { |msg|  @change_password.errors.add(:new_pass, msg) }
            format.html { render action: "new" }
            format.json { render json: @change_password.errors, status: :unprocessable_entity }

          rescue Exception => e
            logger.error e.message
            logger.error e.backtrace.join("\n")


            @change_password.errors.add(:base, "Unable to change password, please try again.")
            format.html { render action: "new" }
            format.json { render json: @change_password.errors, status: :unprocessable_entity }
          end
        end
      else
        format.html { render action: "new" }
        format.json { render json: @change_password.errors, status: :unprocessable_entity }
      end
    end
  end


  def check_allowed_user
    roles = session[:roles]
    if roles == nil || !(is_operator? || is_sea_admin? || is_lea_admin? || is_developer? || is_realm_admin? || is_ingestion_user?)
      raise ActiveResource::ForbiddenAccess, caller
    end
  end
end
