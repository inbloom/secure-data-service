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


require 'rest-client'
require 'json'
class UserAccountRegistrationsController < ApplicationController
  include ReCaptcha::AppHelper

  skip_before_filter :handle_oauth
  before_filter :enabled_only_in_sandbox
  before_filter :check_for_cancel, :only => [:create, :update]

  # GET /user_account_registrations/new
  # GET /user_account_registrations/new.json
  def new
    if (user_limit_reached?)
      redirect_to(:controller => "waitlist_users", :action => "new")
    else
      @user_account_registration = UserAccountRegistration.new
      respond_to do |format|
        format.html # new.html.erb
        format.json { render json: @user_account_registration }
      end
    end
  end

  # POST /user_account_registrations
  # POST /user_account_registrations.json
  def create
    if (user_limit_reached?)
      redirect_to(:controller => "waitlist_users", :action => "new")
    else
      @user_account_registration = UserAccountRegistration.new(params[:user_account_registration])
      logger.debug "User Account Registration = #{@user_account_registration}"
      captcha_valid = validate_recap(params, @user_account_registration.errors)
      @user_account_registration.errors.clear

      if @user_account_registration.valid? == false || captcha_valid == false
        # perform validation on fields even if captcha response is invalid.  Otherwise, user has to solve captcha just to get form feedback, which is super annoying.
        redirectPage=false
        render500=false
        @user_account_registration.errors.add :recaptcha, "Invalid Captcha Response" unless captcha_valid
        if @user_account_registration.errors[:email].include?("An account with this email already exists") && captcha_valid == false
          # to avoid bots being able to check for valid emails, only display existing email message if captcha is valid
          @user_account_registration.errors[:email].reject! { |x| x == "An account with this email already exists" }
        end
      else
        begin
          @user_account_registration.register
          redirectPage= true
          render500 = false
          session[:guuid] = @user_account_registration.email
        rescue InvalidPasswordException => e
          logger.error e.message
          logger.error e.backtrace.join("\n")

          APP_CONFIG['password_policy'].each { |msg| @user_account_registration.errors.add(:password, msg) }
          redirectPage = false
          render500 = false
        rescue Exception => e
          logger.error e.message
          logger.error e.backtrace.join("\n")

          logger.info { e.message }
          render500=true
        end
      end
      respond_to do |format|
        if redirectPage==true
          format.html  { redirect_to("/eula")}
          format.json  { render :json => @user_account_registration,action: "/eulas"}
        elsif render500==true
          format.html { render :noframe_500, :status => 500 }
          #format.json { :status => :not_found}
          format.any  { head :not_found }
        else
          format.html { render action: "new" }
          format.json { render json: @user_account_registration.errors, status: :unprocessable_entity }
        end
      end
    end
  end

  private

  def user_limit_reached?
    max_user = APP_CONFIG['maximum_user_count']
    if max_user
      begin
        user_count = ApprovalEngine.get_sandbox_admin_count
        logger.debug "max user = #{APP_CONFIG['maximum_user_count']}, user count = #{user_count}"
        user_count >= max_user
      rescue Exception => e
        logger.error e.message
        logger.error e.backtrace.join("\n")

        logger.fatal "An exception occured when retrieving existing user counts."
        logger.fatal "Exception:  #{e}"
        logger.fatal "Backtrace:  #{e.backtrace.join("\n")}"
        return true
      end
    else
      false
    end
  end

  #redirect cancel
  def check_for_cancel
    if params[:commit] == "Cancel"
      redirect_to  APP_CONFIG['redirect_slc_url']
    end
  end

  def enabled_only_in_sandbox
    unless APP_CONFIG['is_sandbox']
      redirect_to APP_CONFIG['app_dev_documentation_link']
    end
  end
end
