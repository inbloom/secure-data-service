=begin

Copyright 2012 Shared Learning Collaborative, LLC

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

=end


require "erb"

class LandingZone
  def self.possible_edorgs
    if APP_CONFIG["is_sandbox"]
      edOrgs = []
      edOrgs << EducationOrganization.new(:stateUniqueId => 'STANDARD-SEA', :nameOfInstitution => "Use a SLC sample dataset")
      return edOrgs
    else
      []
    end
  end

  def self.possible_sample_data
    if APP_CONFIG["is_sandbox"]
      sample_data=[]
      sample_data << ["Small Dataset (4k Records)","small"]
      sample_data << ["Medium Dataset (40k Records)","medium"]
      return sample_data
    else
      return []
    end
  end

  SAMPLE_DATA_SET_TO_LOGIN_USER = {
      "small" => "linda.kim",
      "medium" => "lroslin"
  }

  def self.provision(edorg_id, tenant, uid, sample_data_select = nil, public_key = nil)
    hasPublicKey = !public_key.nil? && !public_key.empty?
    logger.debug "entered provision: edorg_id = #{edorg_id}, tenant = #{tenant}, uid = #{uid}, hasPublicKey = #{hasPublicKey}"

    user_info = APP_LDAP_CLIENT.read_user(uid)
    if(!user_info)
      raise ProvisioningError.new "User does not exist in LDAP"
    end

    if (hasPublicKey)
      logger.debug("Doing something with this public key: #{public_key}")
      if (!LandingZoneHelper.valid_rsa_key?(public_key))
        public_key = LandingZoneHelper.convert_key(public_key, uid)
        if (public_key.nil? || !LandingZoneHelper.valid_rsa_key?(public_key))
          logger.debug("Invalid key")
          raise KeyValidationError.new "Received invalid public key"
        end
      end
      logger.debug("Valid key")
      LandingZoneHelper.create_key(public_key, uid)
    end

    isDuplicate = false
    if(APP_CONFIG['is_sandbox'] && (sample_data_select == nil || sample_data_select == ""))
      isDuplicate = (user_info[:edorg] == edorg_id && user_info[:tenant] == tenant)
    elsif APP_CONFIG['is_sandbox'] == false
      isDuplicate = user_info[:homedir] != "/dev/null"
    end
    result = OnBoarding.create(:stateOrganizationId => edorg_id)

    if !result.valid?
      raise ProvisioningError.new "Could not provision landing zone"
    end

    @landingzone = result.attributes[:landingZone]
    @server = result.attributes[:serverName]
    logger.info "landing zone is #{@landingzone}, server is #{@server}"
    logger.info "the tenant uuid is: #{result.attributes[:tenantUuid]}"

    if(APP_CONFIG['is_sandbox'] && sample_data_select != nil && sample_data_select != "")
      begin
        logger.info("start preload data to tenant uuid: #{result.attributes[:tenantUuid]}, with #{sample_data_select} sample data")
        preload_result = OnBoarding.new.preload(result.attributes[:tenantUuid], sample_data_select)
        logger.info("preload status code is: #{preload_result.code}")
        if preload_result.code == 409
          isDuplicate = true
        end
      rescue ActiveResource::ResourceConflict
        isDuplicate = true
      end
    end

    user_info[:homedir] = result.attributes[:landingZone]
    user_info[:edorg] = edorg_id
    # if APP_CONFIG["is_sandbox"]
    #   user_info[:tenant] = tenant
    # end

    begin
      APP_LDAP_CLIENT.update_user_info(user_info)
    rescue => e
        logger.error e.message
        logger.error e.backtrace.join("\n")

        logger.error "Could not update ldap for user #{uid} with #{user_info}.\nError: #{e.message}."
    end

    if(user_info[:emailAddress] != nil && user_info[:emailAddress].length != 0)
      begin
        if sample_data_select !=nil && sample_data_select != "" && isDuplicate == false
          ApplicationMailer.auto_provision_email(user_info[:emailAddress], user_info[:first], SAMPLE_DATA_SET_TO_LOGIN_USER[sample_data_select]).deliver
        elsif (sample_data_select == nil || sample_data_select == "")
          ApplicationMailer.provision_email(user_info[:emailAddress], user_info[:first], @server,edorg_id).deliver
        end
      rescue => e
        logger.error e.message
        logger.error e.backtrace.join("\n")
        logger.error "Could not send email to #{user_info[:emailAddress]}."
      end

      @emailWarningMessage = ""
    else
      @emailWarningMessage = "Unfortunately, your account does not have an email address and " <<
          "therefore we cannot send an email to you.  Please use this page " <<
          "as reference or contact the SLC Operator for your landing zone details."
    end

    {:landingzone => @landingzone, :server => @server, :emailWarning => @emailWarningMessage, :edOrg => user_info[:edorg], :isDuplicate => isDuplicate}
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
class ErbBinding < OpenStruct
  def get_binding
    return binding()
  end
end
