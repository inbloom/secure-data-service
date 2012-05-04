class LandingZone
  
  def self.possible_edorgs
    if APP_CONFIG["is_sandbox"]
      edOrgs = []
      edOrgs << EducationOrganization.new(:stateUniqueId => 'sample-dataset-1-ed-org', :nameOfInstitution => "High-Level Ed-Org from Sample Dataset 1")
      edOrgs << EducationOrganization.new(:stateUniqueId => 'sample-dataset-2-ed-org', :nameOfInstitution => "High-Level Ed-Org from Sample Dataset 2")
      return edOrgs
    else
      []
    end
  end
  
  def self.provision(edorg_id)
    edOrg = EducationOrganization.new(:stateUniqueId => edorg_id, 
                              :nameOfInstitution => 'TEMPORARY_PLACEHOLDER', 
                              :organizationCategories => 'State Education Agency', 
                              :address => { 
                              :streetNumberName => 'TEMPORARY_PLACEHOLDER', 
                              :city => 'TEMPORARY_PLACEHOLDER', 
                              :stateAbbreviation => 'TX', 
                              :postalCode => 'TEMPORARY_PLACEHOLDER'})
    # TODO:  implement how to save this in the db
    Rails.logger.warn "Provisioning logic not implemented yet!"
    # saved = edOrg.save()
    saved = false
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
