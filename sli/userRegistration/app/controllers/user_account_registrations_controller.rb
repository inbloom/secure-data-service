require 'rest-client'
class UserAccountRegistrationsController < ApplicationController
    before_filter :check_for_cancel, :only => [:create, :update]

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
    redirectPage=true
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
        puts(response.to_json)

        if (res.code==200)
            jsonDocument = JSON.parse(res.body)
            if(jsonDocument!=nil)
                if (jsonDocument["validated"] == "true")
                    @user_account_registration.errors.add(:email, "User name already exists in record")
                    redirectPage=false
                else
                 @commitResult= RestClient.put(url+"/userAccountId="+jsonDocument["id"],@user_account_registration.to_json,urlHeader){|response, request, result| response }
                end
            else
                @commitResult= RestClient.post(url,@user_account_registration.to_json,urlHeader)
            end
        else
            @commitResult= RestClient.post(url,@user_account_registration.to_json,urlHeader)
        end
        if @commitResult.code != 200
            redirectPage=false
            @user_account_registration.errors.add(:email, "Error occurred storing user name in record")
        end

      end
    respond_to do |format|
        if redirectPage==true
            format.html  { redirect_to("/eulas/#@user_account_registration.email")}
            format.json  { render :json => @user_account_registration,
                                   action: "/eulas/"}
        else
            format.html { render action: "new" }
            format.json { render json: @user_account_registration.errors, status: :unprocessable_entity }
        end
    end
  end

  #redirect cancel
  def check_for_cancel
      if params[:commit] == "Cancel"
        redirect_to  APP_CONFIG['api_base']
      end
    end
end
