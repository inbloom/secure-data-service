class LandingZone
  
  def self.possible_edorgs
    edOrgs = []
    edOrgs << EducationOrganization.new(:stateUniqueId => 'sample-dataset-1-ed-org', :nameOfInstitution => "High-Level Ed-Org from Sample Dataset 1")
    edOrgs << EducationOrganization.new(:stateUniqueId => 'sample-dataset-2-ed-org', :nameOfInstitution => "High-Level Ed-Org from Sample Dataset 2")
    return edOrgs
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
    saved = edOrg.save()
    Rails.logger.info "Provisioning Request: #{edorg_id}, successful? #{saved}"
    return saved
  end
  
end