class LandingZone
  
  def self.possible_edorgs
    if APP_CONFIG["is_sandbox"]
      edOrgs = []
      edOrgs << EducationOrganization.new(:stateUniqueId => 'IL', :nameOfInstitution => "High-Level Ed-Org from Sample Dataset 1")
      edOrgs << EducationOrganization.new(:stateUniqueId => 'IL-SUNSET', :nameOfInstitution => "High-Level Ed-Org from Sample Dataset 2")
      return edOrgs
    else
      []
    end
  end
  
  def self.provision(edorg_id, tenant)
    provision = OnBoarding.new(:stateOrganizationId => edorg_id, :tenantId => tenant)
    saved = provision.save()
    Rails.logger.info "Provisioning Request: #{edorg_id}, successful? #{saved}"
    
    if (saved == false)
      raise ProvisioningError.new "Could not provision landing zone"
    end
  end
  
end

class ProvisioningError < StandardError
  
  def initialize(message)
    @message = message
  end
  
  def to_s
    @message
  end
end
