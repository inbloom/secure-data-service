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
      if @forgot_password.valid? == true
         begin
           str = (Base64.decode64(@forgot_password.token)).split(" ");
           email = str[0]
           resetKey = str[1]
           update_info = {
                :email => "#{email}",
                :password   => "#{@forgot_password.new_pass}",
                :resetKey => ""
           }
           puts "before response" + email
           response =  APP_LDAP_CLIENT.update_user_info(update_info)
           puts "after response"
           user = APP_LDAP_CLIENT.read_user(email)
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
    token = params[:key]
    puts @forgot_password
    @forgot_password.set_token(token)
    str = (Base64.decode64(@forgot_password.token)).split(" ");
    user_id = str[0]
    resetKey = str[1]
    
    user =  APP_LDAP_CLIENT.read_user(user_id)
    puts user
    currentTime = DateTime.current
    difference = ((currentTime - user[:updated]) * 24 * 60 * 60).to_i
    respond_to do |format|
      if difference < 86400
       if resetKey == user[:resetKey]
          format.html { render action: "update" }
          format.json { render json: @forgot_password, status: :created, location: @forgot_password }
        else
          puts "I am here"
          @forgot_password.errors.add(:base, "Unable to verify user, Please contact SLC Operator")
          format.html { render action: "show" }
          format.json { render json: @forgot_password.errors, status: :unprocessable_entity }
        end
      else 
        @forgot_password.errors.add(:base, "Password reset request expired!")
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
          key = SecureRandom.base64(10)
          update_info = {
            :email    => "#{user_id}",
            :resetKey => "#{key}"
          }
          
          token = Base64.encode64("#{user_id}" + " " + "#{key}")
          response =  APP_LDAP_CLIENT.update_user_info(update_info)
          
          ApplicationMailer.notify_reset_password(user_id, token).deliver
          
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
