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

    @redirectPage=true
    @gUID="0"
    if @user_account_registration.valid? ==false
       redirectPage=false
      else

        res = RestClient.get(URL+"?userName="+@user_account_registration.email+"&environment="+@currEnvironment, URL_HEADER){|response, request, result| response }

        if (res.code==200)
            jsonDocument = JSON.parse(res.body)
            if(jsonDocument[INDEX].nil?)
                persist_record(true)
            elsif (jsonDocument[INDEX]["validated"] == "true")
                @user_account_registration.errors.add(:email, "User name already exists in record")
                @redirectPage=false
            else
                @gUID= jsonDocument[INDEX]["id"]
                persist_record(false)
            end
        else
            puts("new user")
            persist_record(true)
        end
      end
    respond_to do |format|
        if @redirectPage==true
            session[:guuid]= @gUID
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

   def persist_record(isPost)
        post_data={
                "userName" =>    @user_account_registration.email,
                "firstName" => @user_account_registration.firstName,
                "lastName" => @user_account_registration.lastName,
                "validated" => "false",
                "environment" =>  @currEnvironment
            }
        if isPost == true
            commitResult= RestClient.post(URL,post_data.to_json,URL_HEADER){|response, request, result| response }
            headers = commitResult.raw_headers
            s = headers['location'][0]
            @gUID = s[s.rindex('/')+1..-1]
        else
            commitResult= RestClient.put(URL+"/"+@gUID,post_data.to_json,URL_HEADER){|response, request, result| response }
        end
       if response.code != "200"
           @redirectPage=false
           @user_account_registration.errors.add(:email, "Error occurred while storing record")
       else
        if isPost == true
            ApplicationHelper.add_user(@user_account_registration)
        else
            ApplicationHelper.update_user_info(@user_account_registration)
        end
       end
      end
    #redirect cancel
   def check_for_cancel
       if params[:commit] == "Cancel"
         redirect_to  APP_CONFIG['redirect_slc_url']
       end
     end

end
