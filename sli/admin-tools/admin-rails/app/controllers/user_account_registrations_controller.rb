require 'rest-client'
require 'json'
class UserAccountRegistrationsController < ApplicationController
  include ReCaptcha::AppHelper
  
  skip_before_filter :handle_oauth
  before_filter :check_for_cancel, :only => [:create, :update]
  
  # GET /user_account_registrations/new
  # GET /user_account_registrations/new.json
  def new
    if (APP_CONFIG["is_sandbox"] && user_limit_reached?)
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
    @user_account_registration = UserAccountRegistration.new(params[:user_account_registration])
    Rails.logger.debug "User Account Registration = #{@user_account_registration}"
    captcha_valid = validate_recap(params, @user_account_registration.errors)
    @user_account_registration.errors.clear
    
    if @user_account_registration.valid? == false || captcha_valid == false
     redirectPage=false
     render500=false
     @user_account_registration.errors.add :recaptcha, "Invalid Captcha Response" unless captcha_valid
    else
      begin
        response=UserAccountRegistrationsHelper.register_user(@user_account_registration)
        redirectPage=response["redirect"]
        @user_account_registration.errors.add(:email,response["error"])
        session[:guuid]=response["guuid"]
      rescue Exception => e
        logger.info { e.message }
        render500=true
      end
      
    end
    respond_to do |format|
        if redirectPage==true
            format.html  { redirect_to("/eula")}
            format.json  { render :json => @user_account_registration,action: "/eulas"}
        elsif render500==true
          format.html { render :file => "#{Rails.root}/public/500.html", :status => 500 }
           #format.json { :status => :not_found}
           format.any  { head :not_found }
        else
            format.html { render action: "new" }
            format.json { render json: @user_account_registration.errors, status: :unprocessable_entity }
        end
    end
  end

private

  URL_HEADER = {
    "Content-Type" => "application/json",
    "content_type" => "json",
    "accept" => "application/json"
  }
      
  def user_limit_reached?
    res = RestClient.get(APP_CONFIG['api_base']+"/v1/userAccounts/createCheck", URL_HEADER){|response, request, result| response }
    "false" == JSON.parse(res)['canCreate']
  end

    #redirect cancel
   def check_for_cancel
       if params[:commit] == "Cancel"
         redirect_to  APP_CONFIG['redirect_slc_url']
       end
     end

end
