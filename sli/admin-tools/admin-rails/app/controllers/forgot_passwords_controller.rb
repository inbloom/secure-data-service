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


require 'active_support/secure_random'
require 'digest'
require 'ldapstorage'
require 'time'
require 'date'

class ForgotPasswordsController < ApplicationController
  
  skip_filter :handle_oauth
  # GET /forgot_passwords
  # GET /forgot_passwords.json
  def index
    respond_to do |format|
      format.html # index.html.erb
      format.json { render json: @forgot_passwords }
    end
  end
  
  # POST /forgot_passwords
  # POST /forgot_passwords.json
  def create
    @forgot_password = ForgotPassword.new(params[:forgot_password])
    @forgot_password.errors.clear
    respond_to do |format|
      user = APP_LDAP_CLIENT.read_user_resetkey(@forgot_password.token)
           
      if !!user && @forgot_password.valid? == true
         begin
           update_info = {
                :email => "#{user[:email]}",
                :password   => "#{@forgot_password.new_pass}",
                :resetKey => ""
           }
           response =  APP_LDAP_CLIENT.update_user_info(update_info)
           
           emailAddress = user[:emailAddress]
           fullName = user[:first] + " " + user[:last]
           ApplicationMailer.notify_password_change(emailAddress, fullName).deliver
           puts "after mailer"

           format.html { redirect_to "/forgotPassword/notify", notice: 'Your password has been successfully modified.'}
           format.json { render :json => @forgot_password, status: :created, location: @forgot_password }

         rescue InvalidPasswordException => e
           APP_CONFIG['password_policy'].each { |msg|  @forgot_password.errors.add(:new_pass, msg) }
           format.html { render action: "update" }
           format.json { render json: @forgot_password.errors, status: :unprocessable_entity }

         rescue Exception => e
           @forgot_password.errors.add(:base, "Unable to change password, please try again.")
           format.html { render action: "update" }
           format.json { render json: @forgot_password.errors, status: :unprocessable_entity }
         end
      else
        format.html { render action: "update" }
        format.json { render json: @forgot_password.errors, status: :unprocessable_entity }
      end
    end
  end

  # PUT /forgot_passwords/1
  # PUT /forgot_passwords/1.json
  def update
    @forgot_password = ForgotPassword.new
    key = params[:key]
    @forgot_password.set_token(key)
    respond_to do |format|
      user = APP_LDAP_CLIENT.read_user_resetkey(key)
      if (!!user)
        resetKey = user[:resetKey]
        currentTimestamp = DateTime.current.utc.to_i
        difference = currentTimestamp - Integer(resetKey.sub(key + "@", ""))
        puts difference
        if difference >= 0 && difference < 86400
          format.html { render action: "update" }
          format.json { render json: @forgot_password, status: :created, location: @forgot_password }
        else 
          @forgot_password.errors.add(:base, "Password reset request expired!")
          format.html { render action: "show" }
          format.json { render json: @forgot_password.errors, status: :unprocessable_entity }
        end
      else
        @forgot_password.errors.add(:base, "Unable to verify user, Please contact SLC Operator")
        format.html { render action: "show" }
        format.json { render json: @forgot_password.errors, status: :unprocessable_entity }
      end
    end
  end
  
  # PUT /forgot_passwords/1
  # PUT /forgot_passwords/1.json
  def reset
    @forgot_password = ForgotPassword.new
    user_id = params[:user_id]
    
    respond_to do |format|
      if ApplicationHelper.user_exists?(user_id)
        begin
          currentTimestamp = DateTime.current.utc.to_i.to_s
          key = Digest::MD5.hexdigest(SecureRandom.base64(10) + currentTimestamp + user_id)
          token = key + "@" + currentTimestamp
          update_info = {
            :email    => "#{user_id}",
            :resetKey => "#{token}"
          }
          response =  APP_LDAP_CLIENT.update_user_info(update_info)
          
          ApplicationMailer.notify_reset_password(user_id, key).deliver
          
          format.html { redirect_to "/forgotPassword/notify", notice: 'Your password reset instructions are sent to your email. Please follow the instructions in the email' }
          format.json { render :json => @forgot_password, status: :created, location: @forgot_password }
        rescue Exception => e
          puts e
          @forgot_password.errors.add(:base, "Unable to reset your password. Please contact, The Shared Learning Collaborative")
          format.html { render action: "reset" }
          format.json { render json: @forgot_password.errors, status: :unprocessable_entity }
        end
      else
        @forgot_password.errors.add(:base, "Unable to verify your user ID.")
        format.html { render action: "reset" }
        format.json { render json: @forgot_password.errors, status: :unprocessable_entity }
      end
    end
    #redirect_to(:controller => "reset_passwords", :action => "show")
  end

  # DELETE /forgot_passwords/1
  # DELETE /forgot_passwords/1.json
  def destroy
    @forgot_password = ForgotPassword.find(params[:id])
    @forgot_password.destroy

    respond_to do |format|
      format.html { redirect_to forgot_passwords_url }
      format.json { head :ok }
    end
  end
end
