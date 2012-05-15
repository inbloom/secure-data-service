class AdminDelegationsController < ApplicationController
  # GET /admin_delegations
  # GET /admin_delegations.json
  def index
    admin_delegations = AdminDelegation.all
    if admin_delegations == nil
      @admin_delegation = AdminDelegation.new
      edOrgId = Check.get("")["edOrg"]
      @admin_delegation.localEdOrgId = edOrgId
    else
      @admin_delegation = admin_delegations[0]
    end

    respond_to do |format|
      format.html # index.html.erb
      format.json { render json: @admin_delegations }
    end
  end

  # GET /admin_delegations/1
  # GET /admin_delegations/1.json
  def show
    @admin_delegation = AdminDelegation.find(params[:id])

    respond_to do |format|
      format.html # show.html.erb
      format.json { render json: @admin_delegation }
    end
  end

  # GET /admin_delegations/new
  # GET /admin_delegations/new.json
  def new
    @admin_delegation = AdminDelegation.new

    respond_to do |format|
      format.html # new.html.erb
      format.json { render json: @admin_delegation }
    end
  end

  # GET /admin_delegations/1/edit
  def edit
    @admin_delegation = AdminDelegation.find(params[:id])
  end

  # POST /admin_delegations
  # POST /admin_delegations.json
  def create
    @admin_delegation = AdminDelegation.new(params[:admin_delegation])

    respond_to do |format|
      if @admin_delegation.save
        format.html { redirect_to admin_delegations_path, notice: 'Admin delegation was successfully created.' }
        format.json { render json: @admin_delegation, status: :created, location: @admin_delegation }
      else
        format.html { render action: "new" }
        format.json { render json: @admin_delegation.errors, status: :unprocessable_entity }
      end
    end
  end

  # PUT /admin_delegations/1
  # PUT /admin_delegations/1.json
  def update
    @admin_delegation = AdminDelegation.all[0]
    @admin_delegation.id = "myEdOrg"

    respond_to do |format|
      if @admin_delegation.update_attributes(params[:admin_delegation])
        format.html { redirect_to admin_delegations_path, notice: 'Admin delegation was successfully updated.' }
        format.json { head :ok }
      else
        format.html { render action: "edit" }
        format.json { render json: @admin_delegation.errors, status: :unprocessable_entity }
      end
    end
  end

  # DELETE /admin_delegations/1
  # DELETE /admin_delegations/1.json
  def destroy
    @admin_delegation = AdminDelegation.find(params[:id])
    @admin_delegation.destroy

    respond_to do |format|
      format.html { redirect_to admin_delegations_url }
      format.json { head :ok }
    end
  end


end
