require 'approval'

class AccountManagementsController < ApplicationController
  
  before_filter :check_slc_operator
  # GET /account_managements
  # GET /account_managements.json
  def index
    begin
    @account_managements=get_all()
    
    @account_managements=sort(@account_managements)
    rescue
    ensure
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
      ApprovalEngine.change_user_status(email,commit.downcase)
    rescue Exception => e
      @error_notice=e.message
    ensure 
    end
    
    @account_managements=get_all()
    @account_managements=sort(@account_managements)

    # may need to figure out how to send error message if the status change failed
    @notice="Account was successfully updated."
    

    respond_to do |format|
      
        format.html { render "index"}
    #format.json { head :ok }

    end
  end

  def sort(account_managements)
    pending_account_managements,approved_account_managements,rejected_account_managements,disabled_account_managements=Array.new(),Array.new(),Array.new(),Array.new()
    account_managements.each do |account_management|
      if account_management.status.downcase =="pending"
      pending_account_managements.push(account_management)
      elsif account_management.status.downcase =="approved"
      approved_account_managements.push(account_management)
      elsif account_management.status.downcase =="rejected"
      rejected_account_managements.push(account_management)
      elsif account_management.status.downcase =="disabled"
      disabled_account_managements.push(account_management)
      end
    end
    account_managements=pending_account_managements.concat(approved_account_managements).concat(disabled_account_managements).concat(rejected_account_managements)
  end

  def get_all()
    account_managements=Array.new()
    accounts=ApprovalEngine.get_users()
    if accounts!=nil && accounts.length>0
      accounts.each do |account|
        account_management = AccountManagement.new()
        account_management.name=account[:first]+" "+account[:last]
        account_management.vendor=account[:vendor]
        account_management.email=account[:email]
        
        account_management.lastUpdate=account[:updated].strftime("%Y-%m-%d %I:%M:%S%p")
        account_management.status=account[:status]
        account_management.transitions=account[:transitions]
        account_managements.push(account_management)
      end
    end
    account_managements
  end
  
  def check_slc_operator
    if $check_slc==nil || $check_slc == true
    check = Check.get("")
    roles = check["sliRoles"] 
    if roles == nil || roles.include?("SLC Operator")==false
     render :file=> "#{Rails.root}/public/403.html"
    end
    end
  end
  
  
  #mock test data for interface testing only
=begin
def get_mock_all()
account_managements=$account_managements
if account_managements==nil||account_managements.length==0
counters = (0...20).to_a
account_managements=Array.new()
counters.each do |counter|
account_management = AccountManagement.new()
account_management.name="Loraine Plyler " +String(counter+1)
account_management.vendor="Macro Corp "+String(counter+1)
if counter<=4
account_management.lastUpdate="2012-01-01"
account_management.status="Pending"
account_management.transitions=["reject","approve"]
elsif counter<=9
account_management.lastUpdate="2012-01-01"
account_management.status="Approved"
account_management.transitions=["disable"]
elsif counter<=14
account_management.lastUpdate="2012-01-01"
account_management.status="Rejected"
account_management.transitions=[]
else
account_management.lastUpdate="2012-01-01"
account_management.status="Disabled"
account_management.transitions=["enable"]
end
account_management.email="Lplyer"+String(counter+1)+"@macrocorp.com"
account_managements.push(account_management)
end
$account_managements=account_managements
end
account_managements
end

def update_mock(email,commit)
account_managements=$account_managements
account_managements.each do |account_management|
if account_management.email==email
account_managements.delete(account_management)
if commit=="Approve"
account_management.status="Approved"
account_management.lastUpdate=Time.now.strftime("%Y-%m-%d")
account_management.transitions=["disable"]
elsif commit=="Reject"
account_management.status="Rejected"
account_management.lastUpdate=Time.now.strftime("%Y-%m-%d")
account_management.transitions=[]
elsif commit =="Disable"
account_management.status="Disabled"
account_management.lastUpdate=Time.now.strftime("%Y-%m-%d")
account_management.transitions=["enable"]
elsif commit =="Enable"
account_management.status="Approved"
account_management.lastUpdate=Time.now.strftime("%Y-%m-%d")
account_management.transitions=["disable"]
end
account_managements.push(account_management)
end
end
$account_managements=account_managements
$account_managements
end

def reset
if $account_managements!=nil
$account_managements.clear
end
end

def reset_sandbox
if $account_managements!=nil
$account_managements.clear
end
counters = (0...20).to_a
account_managements=Array.new()
counters.each do |counter|
account_management = AccountManagement.new()
account_management.name="Loraine Plyler "+String(counter+1)
account_management.vendor="Macro Corp "+String(counter+1)
account_management.lastUpdate="2012-01-01"
account_management.status="Approved"
account_management.transitions=["disable"]
account_management.email="Lplyer"+String(counter+1)+"@macrocorp.com"
account_managements.push(account_management)
end
$account_managements=account_managements
end
=end
end
