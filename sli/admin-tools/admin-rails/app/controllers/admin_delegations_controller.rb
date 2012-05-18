class AdminDelegationsController < ApplicationController

  before_filter :check_rights

  def check_rights
    unless is_lea_admin?
      render_403
    end
  end

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

  # POST /admin_delegations
  # POST /admin_delegations.json
  def create
    params[:admin_delegation][:appApprovalEnabled] = boolean_fix(params[:admin_delegation][:appApprovalEnabled])
    params[:admin_delegation][:viewSecurityEventsEnabled] = boolean_fix(params[:admin_delegation][:viewSecurityEventsEnabled])
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
    params[:admin_delegation][:appApprovalEnabled] = boolean_fix(params[:admin_delegation][:appApprovalEnabled])
    params[:admin_delegation][:viewSecurityEventsEnabled] = boolean_fix(params[:admin_delegation][:viewSecurityEventsEnabled])


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

  def boolean_fix (parameter)
    case parameter
    when "1"
      parameter = true
    when "0"
      parameter = false
    end
  end


end
