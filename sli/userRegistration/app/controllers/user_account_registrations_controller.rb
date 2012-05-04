class UserAccountRegistrationsController < ApplicationController
  # GET /user_account_registrations
  # GET /user_account_registrations.json
  def index
    @user_account_registrations = UserAccountRegistration

    respond_to do |format|
      format.html # index.html.erb
      format.json { render json: @user_account_registrations }
    end
  end

  # GET /user_account_registrations/1
  # GET /user_account_registrations/1.json
  def show
    @user_account_registration = UserAccountRegistration.find(params[:id])

    respond_to do |format|
      format.html # show.html.erb
      format.json { render json: @user_account_registration }
    end
  end

  # GET /user_account_registrations/new
  # GET /user_account_registrations/new.json
  def new
    @user_account_registration = UserAccountRegistration.new

    respond_to do |format|
      format.html # new.html.erb
      format.json { render json: @user_account_registration }
    end
  end

  # GET /user_account_registrations/1/edit
  def edit
    @user_account_registration = UserAccountRegistration.find(params[:id])
  end

  # POST /user_account_registrations
  # POST /user_account_registrations.json
  def create
    @user_account_registration = UserAccountRegistration.new(params[:user_account_registration])

    respond_to do |format|
      if @user_account_registration.save
        format.html { redirect_to @user_account_registration, notice: 'User account registration was successfully created.' }
        format.json { render json: @user_account_registration, status: :created, location: @user_account_registration }
      else
        format.html { render action: "new" }
        format.json { render json: @user_account_registration.errors, status: :unprocessable_entity }
      end
    end
  end

  # PUT /user_account_registrations/1
  # PUT /user_account_registrations/1.json
  def update
    @user_account_registration = UserAccountRegistration.find(params[:id])

    respond_to do |format|
      if @user_account_registration.update_attributes(params[:user_account_registration])
        format.html { redirect_to @user_account_registration, notice: 'User account registration was successfully updated.' }
        format.json { head :no_content }
      else
        format.html { render action: "edit" }
        format.json { render json: @user_account_registration.errors, status: :unprocessable_entity }
      end
    end
  end

  # DELETE /user_account_registrations/1
  # DELETE /user_account_registrations/1.json
  def destroy
    @user_account_registration = UserAccountRegistration.find(params[:id])
    @user_account_registration.destroy

    respond_to do |format|
      format.html { redirect_to user_account_registrations_url }
      format.json { head :no_content }
    end
  end
end
