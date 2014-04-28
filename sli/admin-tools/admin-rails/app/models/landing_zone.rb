class LandingZone

  def self.possible_sample_data
    if APP_CONFIG['is_sandbox']
      [
        ["Small Dataset (4k Records)", "small"],
        ["Medium Dataset (40k Records)", "medium"]
      ]
    else
      []
    end
  end

  SAMPLE_DATA_SET_TO_LOGIN_USER = {
    'small' => 'linda.kim',
    'medium' => 'lroslin'
  }

  SAMPLE_DATA_SET_TO_ED_ORG = {
    'small' => 'STANDARD-SEA',
    'medium' => 'CAP0'
  }

  def self.edorg_for_sample_dataset(sample)
    sample_data_set = SAMPLE_DATA_SET_TO_ED_ORG[sample]
    raise "Cannot determine EdOrg for sample dataset #{sample}" unless sample_data_set
    return sample_data_set
  end

  def self.provision(edorg_id, tenant, uid, sample_data_select = nil, public_key = nil)

    hasPublicKey = !public_key.blank?
    Rails.logger.debug "entered provision: edorg_id = #{edorg_id}, tenant = #{tenant}, uid = #{uid}, hasPublicKey = #{hasPublicKey}"

    user_info = APP_LDAP_CLIENT.read_user(uid)
    raise ProvisioningError.new('User does not exist in LDAP') unless user_info

    if user_info[:emailAddress].blank?
      Rails.logger.fatal "Cannot provision landing zone: No email address for account with uid #{uid}"
      raise ProvisioningError.new "No email address for account with uid #{uid}"
    end

    if hasPublicKey
      Rails.logger.debug "Doing something with this public key: #{public_key}"
      if !LandingZoneHelper.valid_rsa_key?(public_key)
        public_key = LandingZoneHelper.convert_key(public_key, uid)
        if public_key.nil? || !LandingZoneHelper.valid_rsa_key?(public_key)
          Rails.logger.debug 'Invalid key'
          raise KeyValidationError.new 'Received invalid public key'
        end
      end
      Rails.logger.debug 'Valid key'
      LandingZoneHelper.create_key(public_key, uid)
    end

    isDuplicate = false
    if APP_CONFIG['is_sandbox'] && !sample_data_select
      isDuplicate = (user_info[:edorg] == edorg_id && user_info[:tenant] == tenant)
    elsif APP_CONFIG['is_sandbox'] == false
      isDuplicate = user_info[:homedir] != "/dev/null"
    end
    result = OnBoarding.create(:stateOrganizationId => edorg_id)

    raise ProvisioningError.new('Could not provision landing zone') unless result.valid?

    @landingzone = result.attributes[:landingZone]
    @server = result.attributes[:serverName]

    Rails.logger.info "landing zone is #{@landingzone}, server is #{@server}"
    Rails.logger.info "the tenant uuid is: #{result.attributes[:tenantUuid]}"

    if APP_CONFIG['is_sandbox'] && sample_data_select
      Rails.logger.info("start preload data to tenant uuid: #{result.attributes[:tenantUuid]}, with #{sample_data_select} sample data")
      preload_result = nil
      begin
        preload_result = OnBoarding.new.preload(result.attributes[:tenantUuid], sample_data_select)
      rescue ActiveResource::ResourceConflict
        isDuplicate = true
      end

      unless preload_result.nil? && preload_result.code.nil?
        Rails.logger.info "preload status code is: #{preload_result.code}"
        if preload_result.code == 409
          isDuplicate = true
        elsif preload_result.code >= 400
          raise ProvisioningError.new 'Problem occurred preloading dataset'
        end
      end
    end

    begin
      user_info[:homedir] = result.attributes[:landingZone]
      user_info[:edorg] = edorg_id
      APP_LDAP_CLIENT.update_user_info(user_info)
    rescue => e
      Rails.logger.error e.message
      Rails.logger.error e.backtrace.join("\n")
      Rails.logger.error "Could not update ldap for user #{uid} with #{user_info}.\nError: #{e.message}."
    end


    begin
      # don't send an email if it's a duplicate.
      if !isDuplicate
        if sample_data_select
          ApplicationMailer.auto_provision_email(user_info[:emailAddress], user_info[:first], SAMPLE_DATA_SET_TO_LOGIN_USER[sample_data_select]).deliver
        else
          ApplicationMailer.provision_email(user_info[:emailAddress], user_info[:first], @server, edorg_id).deliver
        end
      end
    rescue => e
      Rails.logger.error e.message
      Rails.logger.error e.backtrace.join("\n")
      Rails.logger.error "Could not send email to #{user_info[:emailAddress]}."
    end

    {:server => @server, :edOrg => user_info[:edorg], :isDuplicate => isDuplicate}
  end

end

class KeyValidationError < StandardError
  def initialize(message)
    @message = message
  end

  def to_s
    @message
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