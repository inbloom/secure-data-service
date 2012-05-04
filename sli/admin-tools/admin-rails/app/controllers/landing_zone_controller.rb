class LandingZoneController < ApplicationController
  
  rescue_from ActiveResource::ForbiddenAccess, :with => :render_403
  rescue_from ProvisioningError, :with => :handle_error
  
  def provision
    ed_org_id = params[:ed_org]
    ed_org_id = params[:custom_ed_org] if ed_org_id == 'custom'
    LandingZone.provision ed_org_id
  end

  def index
    @edOrgs = LandingZone.possible_edorgs
  end
  
  def handle_error
    render :status => 500, :text => "An error occured when provisioning the landing zone"
  end
end