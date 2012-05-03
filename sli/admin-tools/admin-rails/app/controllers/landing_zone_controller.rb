class LandingZoneController < ApplicationController
  
  rescue_from ActiveResource::ForbiddenAccess, :with => :render_403
  
  def provision
    ed_org_id = params[:ed_org]
    ed_org_id = params[:custom_ed_org] if ed_org_id == 'custom'
    result = LandingZone.provision ed_org_id
    if (result)
      render_400
    else
      
    end
  end

  def index
    @edOrgs = LandingZone.possible_edorgs
  end
end
