class LandingZone < Ldap
  
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
    # TODO: catch exception because we still want to create user on LDAP
    saved = provision.save()
    Rails.logger.info "Provisioning Request: #{edorg_id}, successful? #{saved}"
    
    if (saved == false)
      raise ProvisioningError.new "Could not provision landing zone"
    end
  end

  # user_info = {
  #     :first => "John",
  #     :last => "Doe", 
  #     :email => "jdoe@example.com",
  #     :password => "secret", 
  #     :vendor => "Acme Inc."
  #     :emailtoken ... hash string 
  #     :updated ... datetime
  #     :status  ... "submitted"
  #     :homedirectory ... string
  # }
  # @@ldap.create_user(user_info)

end

class ProvisioningError < StandardError
  
  def initialize(message)
    @message = message
  end
  
  def to_s
    @message
  end
end
