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

require 'approval'

class AccountManagementsController < ApplicationController
  before_filter :check_slc_operator
  before_filter :enabled_only_in_sandbox

  # GET /account_managements
  # GET /account_managements.json
  def index
    begin
      @account_managements=get_all()
      @account_managements=sort(@account_managements)
    rescue => e
      logger.error e.message
      logger.error e.backtrace.join("\n")
    end

    respond_to do |format|
      #    if @forbidden == true
      #   format.html { render :file=> "#{Rails.root}/public/403.html" }
      #   end
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

  def sort(account_managements)
    order = ["pending", "approved", "rejected", "disabled"]
    account_managements.select{|x| order.include? x.status.downcase}.sort{|x, y| order.index(x.status.downcase) <=> order.index(y.status.downcase)}
  end

  def get_all()
    account_managements = []
    accounts = ApprovalEngine.get_users()
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
    if $check_slc.nil? || $check_slc == true
      check = Check.get("")
      roles = check["sliRoles"]
      if roles.nil? || !roles.include?("SLC Operator")
        render :file => "#{Rails.root}/public/403.html"
      end
    end
  end

  def enabled_only_in_sandbox
    unless APP_CONFIG['is_sandbox']
      render :file => "#{Rails.root}/public/404.html"
    end
  end
end
