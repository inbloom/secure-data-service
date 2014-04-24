require 'approval'

class AccountManagementsController < ApplicationController
  before_filter :check_slc_operator
  before_filter :check_if_sandbox

  # GET /account_managements
  # GET /account_managements.json
  def index
    begin
      @account_managements = get_all
      @account_managements = sort(@account_managements)
    rescue => e
      logger.error e.message
      logger.error e.backtrace.join("\n")
    end

    respond_to do |format|
      format.html # index.html.erb
      format.json { render json: @account_managements }
    end
  end

  # POST /account_managements
  # POST /account_managements.json
  def create
    commit = params["commit"]
    email = params["email"]

    # may need to figure out better way to handle exception
    begin
      ApprovalEngine.change_user_status(email, commit.downcase)
    rescue Exception => e
      @error_notice = e.message
      logger.error e.message
      logger.error e.backtrace.join("\n")      
    end

    @account_managements = get_all()
    @account_managements = sort(@account_managements)

    # may need to figure out how to send error message if the status change failed
    @notice = "Account was successfully updated."

    respond_to do |format|
      format.html { render "index"}
      #format.json { head :ok }
    end
  end

  private

  def sort(account_managements)
    order = ["pending", "approved", "rejected", "disabled"]
    account_managements.select{|x| order.include? x.status.downcase}.sort{|x, y| order.index(x.status.downcase) <=> order.index(y.status.downcase)}
  end

  def get_all
    account_managements = []
    accounts = ApprovalEngine.get_users
    unless accounts.nil?
      accounts.each do |account|
        account_management = AccountManagement.new()
        account_management.name = account[:first] + " " + account[:last]
        account_management.vendor = account[:vendor]
        account_management.email = account[:email]

        account_management.lastUpdate = account[:updated].in_time_zone().strftime("%c %Z")
        account_management.status = account[:status]
        account_management.transitions = account[:transitions]
        account_managements.push(account_management)
      end
    end
    account_managements
  end

  def check_slc_operator
    check = Check.get('')
    roles = check['sliRoles']
    render(file:'public/403.html') unless roles && roles.include?('SLC Operator')
  end

  def check_if_sandbox
    render(file:'public/404.html', status: :not_found, layout: false) unless APP_CONFIG['is_sandbox']
  end
end
