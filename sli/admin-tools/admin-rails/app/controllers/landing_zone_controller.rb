class LandingZoneController < ApplicationController
  def provision
    params.each do |key, value|
      logger.fatal "XXXXX    #{key}  #{value}"
    end
    redirect_to '/'
  end

  def index
    @edOrgs = ["High-Level Ed-Org from Sample Dataset 1", "High-Level Ed-Org from Sample Dataset 2"]
  end
end
