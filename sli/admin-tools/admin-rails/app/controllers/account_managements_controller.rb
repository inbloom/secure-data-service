class AccountManagementsController < ApplicationController
  # GET /account_managements
  # GET /account_managements.json
  def index
    reset=params["reset"]
    if reset =="true"
      reset()
    end
    env=params["env"]
    if env=="sandbox"
      reset_sandbox()
    end
   # @account_managements=get_all()
  #  if @account_managements.length==0
    @account_managements=get_mock_all()
   # end

    # sort the @account_managements based on status
    @account_managements=sort(@account_managements)

    respond_to do |format|
      format.html # index.html.erb
      format.json { render json: @account_managements }
    end
  end

  # GET /account_managements/1
  # GET /account_managements/1.json
  #  def show
  #   @account_management = AccountManagement.find(params[:id])

  #   respond_to do |format|
  #     format.html # show.html.erb
  #     format.json { render json: @account_management }
  #   end
  # end

  # GET /account_managements/new
  # GET /account_managements/new.json
  #  def new
  #   @account_management = AccountManagement.new

  #  respond_to do |format|
  #    format.html # new.html.erb
  #    format.json { render json: @account_management }
  # end
  #end

  # GET /account_managements/1/edit
  # def edit
  #   @account_management = AccountManagement.find(params[:id])
  # end

  # POST /account_managements
  # POST /account_managements.json
  def create
    commit = params["commit"]
    email = params["email"]
   # account=AccountManagement.change_user_status(email,commit.downcase)
   # @account_managements=get_all()
   # if@account_managements.length==0
    @account_managements= update_mock(email,commit)
  #  end
    @account_managements=sort(@account_managements)
    @notice='Account was successfully updated.'

    respond_to do |format|

      format.html { render "index"}
    #format.json { head :ok }

    end
  end

  # PUT /account_managements/1
  # PUT /account_managements/1.json
  #  def update
  #    @account_management = AccountManagement.find(params[:id])

  #   respond_to do |format|
  #     if @account_management.update_attributes(params[:account_management])
  #       format.html { redirect_to @account_management, notice: 'Account management was successfully updated.' }
  #       format.json { head :ok }
  #     else
  #       format.html { render action: "edit" }
  #      format.json { render json: @account_management.errors, status: :unprocessable_entity }
  #      end
  #   end
  # end

  # DELETE /account_managements/1
  # DELETE /account_managements/1.json
  #  def destroy
  #   @account_management = AccountManagement.find(params[:id])
  #   @account_management.destroy

  #  respond_to do |format|
  #    format.html { redirect_to account_managements_url }
  #    format.json { head :ok }
  #  end
  #end

  def sort(account_managements)
    pending_account_managements,approved_account_managements,rejected_account_managements,disabled_account_managements=Array.new(),Array.new(),Array.new(),Array.new()
    account_managements.each do |account_management|
      if account_management.status =="Pending".downcase ||account_management.status =="Pending"
      pending_account_managements.push(account_management)
      elsif account_management.status =="Approved".downcase || account_management.status =="Approved"
      approved_account_managements.push(account_management)
      elsif account_management.status =="Rejected".downcase || account_management.status =="Rejected"
      rejected_account_managements.push(account_management)
      elsif account_management.status =="Disabled".downcase || account_management.status =="Disabled"
      disabled_account_managements.push(account_management)
      end
    end
    account_managements=pending_account_managements.concat(approved_account_managements).concat(disabled_account_managements).concat(rejected_account_managements)
  end

  def get_all()
    account_managements=Array.new()
    accounts=AccountManagement.get_users()
    if accounts!=nil
      accounts.each do |account|
        account_management = AccountManagement.new()
        account_management.name=account["first"]+" "+account["last"]
        account_management.vendor=account["vendor"]
        account_management.email=account["email"]
        account_management.lastUpdated=account["updated"]
        account_management.status=account["status"]
        account_management.transitions=account["transitions"]
        account_managements.push(account_management)
      end
    end
    account_managements
  end

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

end
