
class UserAccountRegistrationsController < ApplicationController
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

    respond_to do |format|

      if @user_account_registration.valid? ==false
        format.html { render action: "new" }
        format.json { render json: @user_account_registration.errors, status: :unprocessable_entity }
      else
         @user_account_registration.save
      end
    end
  end
end
