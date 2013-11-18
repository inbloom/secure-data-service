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


require 'securerandom'
require 'digest'
require 'ldapstorage'
require 'time'
require 'date'

class ForgotPasswordsController < ApplicationController
  include ReCaptcha::AppHelper

  skip_filter :handle_oauth
  before_filter :get_user, :only => [:new_account, :update]
  before_filter :token_still_valid, :only => [:new_account, :update]

  def get_user
    @user = APP_LDAP_CLIENT.read_user_resetkey(params[:key])
    if !@user
      flash[:error] = "Unable to verify user. Please contact inBloom"
      redirect_to forgot_password_notify_path
    end
  end

  # GET /forgot_passwords
  # GET /forgot_passwords.json
  def index
    @forgot_password = ForgotPassword.new
    @forgot_password.errors.clear
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
      if @forgot_password.set_password {|emailAddress, fullName| ApplicationMailer.notify_password_change(emailAddress, fullName).deliver}
        format.html { redirect_to "/forgotPassword/success", notice: 'Your password has been successfully modified.'}
        format.json { render :json => @forgot_password, status: :created, location: @forgot_password }
      else
        format.html { render action: "update" }
        format.json { render json: @forgot_password.errors, status: :unprocessable_entity }
      end
    end
  end

  def update
    @forgot_password = ForgotPassword.new
    key = params[:key]
    @forgot_password.token = key
    respond_to do |format|
      format.html { render action: "update" }
      format.json { render json: @forgot_password, status: :created, location: @forgot_password }
    end
  end

  def new_account
    @forgot_password = NewAccountPassword.new
    @forgot_password.token = params[:key]
    render action: 'update'
  end

  def new_account_set
  end

  def token_still_valid
    key = params[:key]
    resetKey = @user[:resetKey]
    currentTimestamp = DateTime.current.utc.to_i
    difference = currentTimestamp - Integer(resetKey.sub(key + "@", ""))
    puts resetKey, currentTimestamp, difference, resetKey.sub(key + "@", "")
    if difference > Integer(APP_CONFIG['reset_password_lifespan'])
      flash[:error] = "Password reset request expired. Please make a new request."
      redirect_to forgot_password_notify_path
    end
  end

  def reset
    @forgot_password = ForgotPassword.new
    user_id = params[:user_id]
    captcha_valid = validate_recap(params, @forgot_password.errors)

    respond_to do |format|
      if captcha_valid == false
        @forgot_password.errors.clear
        @forgot_password.errors.add :base, "Invalid Captcha Response"
        format.html { render action: "reset" }
        format.json { render json: @forgot_password.errors, status: :unprocessable_entity }
      elsif ApplicationHelper.user_exists?(user_id)
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

          format.html { redirect_to "/forgotPassword/notify", notice: 'Password reset instructions have been emailed to you. Please follow the instructions in the email.' }
          format.json { render :json => @forgot_password, status: :created, location: @forgot_password }
        rescue Exception => e
          logger.error e.message
          logger.error e.backtrace.join("\n")

          @forgot_password.errors.add(:base, "Unable to reset your password. Please contact inBloom.")
          format.html { render action: "reset" }
          format.json { render json: @forgot_password.errors, status: :unprocessable_entity }
        end
      else
        @forgot_password.errors.add(:base, "Unable to verify your user ID. Please contact inBloom.")
        format.html { render action: "reset" }
        format.json { render json: @forgot_password.errors, status: :unprocessable_entity }
      end
    end
  end
end
