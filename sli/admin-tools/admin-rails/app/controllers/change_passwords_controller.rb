class ChangePasswordsController < ApplicationController
    before_filter :handle_oauth, :check_allowed_user
    
  # GET /change_passwords
  # GET /change_passwords.json
  def index
    @change_passwords = ChangePassword.all

    respond_to do |format|
      format.html # index.html.erb
      format.json { render json: @change_passwords }
    end
  end

  # GET /change_passwords/new
  # GET /change_passwords/new.json
  def new
    @change_password = ChangePassword.new

    respond_to do |format|
      format.html # new.html.erb
      format.json { render json: @change_password }
    end
  end

  # POST /change_passwords
  # POST /change_passwords.json
  def create
    @change_password = ChangePassword.new(params[:change_password])
    @change_password.errors.clear
    respond_to do |format|
      if @change_password.valid? == true
          check = Check.get("")
          email = check["external_id"]

          if !APP_LDAP_CLIENT.authenticate(email, @change_password.old_pass) && !APP_LDAP_CLIENT.authenticate_secure(email, @change_password.old_pass)
            @change_password.errors.add(:base, "Unable to verify old password, please try again.")
            format.html { render action: "new" }
            format.json { render json: @change_password.errors, status: :unprocessable_entity }
          else
            begin
              update_info = {
                :email => "#{email}",
                :password   => "#{@change_password.new_pass}"
              }
              response =  APP_LDAP_CLIENT.update_user_info(update_info)

              fullName = check["full_name"]
              ApplicationMailer.notify_password_change(email, fullName)

              format.html { redirect_to new_change_password_path, notice: 'Your password has been modified successfully.' }
              format.json { render :json => @change_password, status: :created, location: @change_password }

            rescue InvalidPasswordException => e
              APP_CONFIG['password_policy'].each { |msg| @user_account_registration.errors.add(:new_pass, msg) }
              format.html { render action: "new" }
              format.json { render json: @change_password.errors, status: :unprocessable_entity }

            rescue Exception => e
              @change_password.errors.add(:base, "Unable to change password, please try again.")
              format.html { render action: "new" }
              format.json { render json: @change_password.errors, status: :unprocessable_entity }
            end
          end
      else
        format.html { render action: "new" }
        format.json { render json: @change_password.errors, status: :unprocessable_entity }
      end
    end
  end

      
  def check_allowed_user
    if $check_user==nil || $check_user == true
      check = Check.get("")
      roles = check["sliRoles"] 
      if roles == nil || !(roles.include?("SLC Operator")==true || roles.include?("SLI Administrator")==true || roles.include?("SEA Administrator")==true || roles.include?("LEA Administrator")==true || roles.include?("Realm Administrator")==true || roles.include?("Application Developer")==true || roles.include?("Ingestion User")==true || roles.include?("IT Administrator")==true) 
        render :file=> "#{Rails.root}/public/403.html"
      end
    end
  end
end
