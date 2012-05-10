require 'rest-client'
require 'json'
class UserAccountRegistrationsController < ApplicationController
    before_filter :check_for_cancel, :only => [:create, :update]
  INDEX=0

  URL=APP_CONFIG['api_base']
  URL_HEADER = {
      "Content-Type" => "application/json",
      "content_type" => "json",
      "accept" => "application/json"
      }
  # GET /user_account_registrations/new
  # GET /user_account_registrations/new.json
  def new
    @user_account_registration = UserAccountRegistration.new

    respond_to do |format|
      format.html # new.html.erb
      format.json { render json: @user_account_registration }
    end
  end


  # POST /user_account_registrations
  # POST /user_account_registrations.json
  def create
    @user_account_registration = UserAccountRegistration.new(params[:user_account_registration])
    @currEnvironment=APP_CONFIG["is_sandbox"]? "Sandbox":"Production"

    redirectPage=true
    if @user_account_registration.valid? ==false
       redirectPage=false
      else
        redirectPage=register_user
      end
    respond_to do |format|
        if redirectPage==true
            
            format.html  { redirect_to("/eula")}
            format.json  { render :json => @user_account_registration,
                                   action: "/eulas"}
        else
            format.html { render action: "new" }
            format.json { render json: @user_account_registration.errors, status: :unprocessable_entity }
        end
    end
  end

private
  def register_user
    res = RestClient.get(URL+"?userName="+@user_account_registration.email+"&environment="+@currEnvironment, URL_HEADER){|response, request, result| response }

        if (res.code==200)
            jsonDocument = JSON.parse(res.body)
            if(jsonDocument[INDEX].nil?)
                return persist_record(true)
            elsif (jsonDocument[INDEX]["validated"] == "true")
                @user_account_registration.errors.add(:email, "User name already exists in record")
                return false
            else
                session[:guuid]= jsonDocument[INDEX]["id"]
                return persist_record(false,jsonDocument[INDEX]["id"])
            end
        else
            puts("new user")
            return persist_record(true)
        end
      
  end

   def persist_record(isPost,gUID="0")
        post_data={
                "userName" =>    @user_account_registration.email,
                "firstName" => @user_account_registration.firstName,
                "lastName" => @user_account_registration.lastName,
                "validated" => "false",
                "environment" =>  @currEnvironment
            }
        success=true
        if isPost == true
            commitResult= RestClient.post(URL,post_data.to_json,URL_HEADER){|response, request, result| response }
            headers = commitResult.raw_headers
            s = headers['location'][0]
            session[:guuid]= s[s.rindex('/')+1..-1]
        else
            commitResult= RestClient.put(URL+"/"+gUID,post_data.to_json,URL_HEADER){|response, request, result| response }
        end
       if response.code != "200"
           @user_account_registration.errors.add(:email, "Error occurred while storing record")
           success = false
       else
        if isPost == true
            emailToken=ApplicationHelper.add_user(@user_account_registration)
        else
            ApplicationHelper.update_user_info(@user_account_registration)
        end
       end
       return success
      end
    #redirect cancel
   def check_for_cancel
       if params[:commit] == "Cancel"
         redirect_to  APP_CONFIG['redirect_slc_url']
       end
     end

end
