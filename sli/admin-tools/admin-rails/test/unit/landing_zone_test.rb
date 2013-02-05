=begin

Copyright 2012-2013 inBloom, Inc. and its affiliates.

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


require 'test_helper'
require 'approval'

class UserAccountValidationTest < ActiveSupport::TestCase

  def test_possible_sample_data_prod
    assert_equal [], LandingZone.possible_sample_data
  end

  def test_possible_sample_data_sandbox
    APP_CONFIG["is_sandbox"] = true
    possible = LandingZone.possible_sample_data
    assert_not_equal [], possible
    assert_equal "small", possible[0][1]
    assert_equal "medium", possible[1][1]
  end

  def test_edorg_to_sample
    assert_equal "STANDARD-SEA", LandingZone.edorg_for_sample_dataset('small')
    assert_equal "CAP0", LandingZone.edorg_for_sample_dataset('medium')
  end

  class OnboardingResponse
    def initialize(valid, attributes)
      @attributes = attributes;
      @valid = valid
    end

    def attributes
      @attributes
    end

    def valid?
      @valid
    end
  end

  def test_provision_email_sb
    APP_CONFIG["is_sandbox"] = true
    uid = 'test@test.com'
    tenant = 'test'
    new_edorg = "new-edorg"
    APP_LDAP_CLIENT.stubs(:read_user).with(uid).returns({ :emailAddress => uid, :edorg => nil, :tenant => tenant, :first => 'first'})
    APP_LDAP_CLIENT.stubs(:update_user_info)
    res = OnboardingResponse.new true, { :landingZone => 'lz', :serverName => 'server' }
    OnBoarding.expects(:create).with(:stateOrganizationId => new_edorg).returns(res)

    mock = mock('mailer_mock')
    ApplicationMailer.expects(:provision_email).once().returns(mock)
    mock.expects(:deliver).once().with()

    LandingZone.provision new_edorg, tenant, uid
  end

  def test_provision_email_sb_duplicate
    APP_CONFIG["is_sandbox"] = true
    uid = 'test@test.com'
    tenant = 'test'
    new_edorg = "existing-edorg"
    APP_LDAP_CLIENT.stubs(:read_user).with(uid).returns({ :emailAddress => uid, :edorg => 'existing-edorg', :tenant => tenant, :first => 'first'})
    APP_LDAP_CLIENT.stubs(:update_user_info)
    res = OnboardingResponse.new true, { :landingZone => 'lz', :serverName => 'server' }
    OnBoarding.expects(:create).with(:stateOrganizationId => new_edorg).returns(res)

    ApplicationMailer.expects(:provision_email).never()

    LandingZone.provision new_edorg, tenant, uid
  end

end
