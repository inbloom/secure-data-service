require 'rest-client'
require 'json'
class UserAccountRegistrationsController < ApplicationController
    before_filter :check_for_cancel, :only => [:create, :update]
  INDEX=0
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

    post_data={
        "userName" =>    @user_account_registration.email,
        "firstName" => @user_account_registration.firstName,
        "lastName" => @user_account_registration.lastName,
        "validated" => "false",
        "environment" => APP_CONFIG["is_sandbox"]? "Sandbox":"Production"
    }
    @redirectPage=true
    if @user_account_registration.valid? ==false
       redirectPage=false
      else
        url=APP_CONFIG['api_base']
        urlHeader = {
            "Content-Type" => "application/json",
            "content_type" => "json",
            "accept" => "application/json"
            }
        res = RestClient.get(url+"?userName="+@user_account_registration.email, urlHeader){|response, request, result| response }
        puts(res.code)
        puts(res.body)

        if (res.code==200)
            jsonDocument = JSON.parse(res.body)
            if(jsonDocument[INDEX].nil?)
                puts("No record for user")
                commitResult=  commitResult= RestClient.post(url,post_data.to_json,urlHeader){|response, request, result| response }
                handle_commit(response)
            elsif (jsonDocument[INDEX]["validated"] == "true")
                @user_account_registration.errors.add(:email, "User name already exists in record")
                @redirectPage=false
            else
                puts("user name exists but not validated")
                commitResult= RestClient.put(url+"/"+jsonDocument[INDEX]["id"],post_data.to_json,urlHeader){|response, request, result| response }

                handle_commit(response)
            end
        else
            puts("new user")
            commitResult= RestClient.post(url,post_data.to_json,urlHeader)
            handle_commit(response)
        end
      end
      puts(@redirectPage)
    respond_to do |format|
        if @redirectPage==true
            format.html  { redirect_to("/eulas/#@user_account_registration.email")}
            format.json  { render :json => @user_account_registration,
                                   action: "/eulas/"}
        else
            format.html { render action: "new" }
            format.json { render json: @user_account_registration.errors, status: :unprocessable_entity }
        end
    end
  end

private
   def handle_commit(commitResult)
    if commitResult.code != "200"
        puts("cannot handle not equal")
        @redirectPage=false
        @user_account_registration.errors.add(:email, "Error occurred while storing record")
    end
   end
    #redirect cancel
   def check_for_cancel
       if params[:commit] == "Cancel"
         redirect_to  APP_CONFIG['redirect_cancel_handler']
       end
     end

end
