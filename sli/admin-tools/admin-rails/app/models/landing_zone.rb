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
  
  def self.provision(edorg_id, tenant, uid)
    Rails.logger.debug "entered provision: edorg_id = #{edorg_id}, tenant = #{tenant}, uid = #{uid}"

    user_info = @@ldap.read_user(uid)
    if(!user_info)
      raise ProvisioningError.new "User does not exist in LDAP"
    end

    result = OnBoarding.create(:stateOrganizationId => edorg_id, :tenantId => tenant)
    if !result.valid?
      raise ProvisioningError.new "Could not provision landing zone"
    end
    @landingzone = result.attributes[:landingZone]
    @server = result.attributes[:serverName]
    Rails.logger.info "landing zone is #{@landingzone}, server is #{@server}"
    
    user_info[:homedir] = result.attributes[@landingzone]
    @@ldap.update_user_info(user_info)
    {:landingzone => @landingzone, :server => @server}
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
