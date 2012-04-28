class AccountManagementsController < ApplicationController
  # GET /account_managements
  # GET /account_managements.json
  def index
    counters = [0,1,2,3,4,5]
    @account_managements=Array.new()
    counters.each do |counter|
    account_management = AccountManagement.new()
    account_management.name="test name " +String(counter+1)
    account_management.vendor="test vendor "+String(counter+1)
    account_management.approvalDate="2012-01-01"
    account_management.status="approved"
    account_management.email="test"+String(counter+1)+"@test.com"
     @account_managements[counter]=account_management
end
    respond_to do |format|
      format.html # index.html.erb
      format.json { render json: @account_managements }
    end
  end

  # GET /account_managements/1
  # GET /account_managements/1.json
  def show
    @account_management = AccountManagement.find(params[:id])

    respond_to do |format|
      format.html # show.html.erb
      format.json { render json: @account_management }
    end
  end

  # GET /account_managements/new
  # GET /account_managements/new.json
  def new
    @account_management = AccountManagement.new

    respond_to do |format|
      format.html # new.html.erb
      format.json { render json: @account_management }
    end
  end

  # GET /account_managements/1/edit
  def edit
    @account_management = AccountManagement.find(params[:id])
  end

  # POST /account_managements
  # POST /account_managements.json
  def create
    @account_management = AccountManagement.new(params[:account_management])

    respond_to do |format|
      if @account_management.save
        format.html { redirect_to @account_management, notice: 'Account management was successfully created.' }
        format.json { render json: @account_management, status: :created, location: @account_management }
      else
        format.html { render action: "new" }
        format.json { render json: @account_management.errors, status: :unprocessable_entity }
      end
    end
  end

  # PUT /account_managements/1
  # PUT /account_managements/1.json
  def update
    @account_management = AccountManagement.find(params[:id])

    respond_to do |format|
      if @account_management.update_attributes(params[:account_management])
        format.html { redirect_to @account_management, notice: 'Account management was successfully updated.' }
        format.json { head :ok }
      else
        format.html { render action: "edit" }
        format.json { render json: @account_management.errors, status: :unprocessable_entity }
      end
    end
  end

  # DELETE /account_managements/1
  # DELETE /account_managements/1.json
  def destroy
    @account_management = AccountManagement.find(params[:id])
    @account_management.destroy

    respond_to do |format|
      format.html { redirect_to account_managements_url }
      format.json { head :ok }
    end
  end
end
