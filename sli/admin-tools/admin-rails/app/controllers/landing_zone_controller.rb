class LandingZoneController < ApplicationController
  def provision
    ed_org = params[:ed_org]
    ed_org = params[:custom_ed_org] if ed_org == 'custom'
    logger.fatal "XXXX  #{ed_org}"
    redirect_to '/'
  end

  def index
    @edOrgs = ["High-Level Ed-Org from Sample Dataset 1", "High-Level Ed-Org from Sample Dataset 2"]
  end
end
