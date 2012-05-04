class LandingZoneController < ApplicationController
  
  rescue_from ActiveResource::ForbiddenAccess, :with => :render_403
  rescue_from ProvisioningError, :with => :handle_error
  
  def provision
    # TODO check the user has the correct role to be doing this.
    if (params[:cancel] == "Cancel")
      redirect_to "/"
      return
    end
    
    ed_org_id = params[:ed_org]
    ed_org_id = params[:custom_ed_org] if ed_org_id == 'custom'
    if (ed_org_id == nil || ed_org_id.gsub(/\s/, '').length == 0)
      redirect_to :action => 'index', :controller => 'landing_zone'
    else
      LandingZone.provision ed_org_id
    end
  end

  def index
    # TODO check the user has the correct role to be doing this.
    @edOrgs = LandingZone.possible_edorgs
  end
  
  def handle_error
    render :status => 500, :text => "An error occured when provisioning the landing zone"
  end
end