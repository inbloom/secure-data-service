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
  def test_validate_account_ok
    ApplicationHelper.expects(:verify_email).with("1234567890").once()
    assert_equal UserAccountValidation::ACCOUNT_VERIFICATION_COMPLETE, UserAccountValidation.validate_account("1234567890")
  end

  def test_account_duplicate
    ApplicationHelper.expects(:verify_email).with("1234567890").raises(ApprovalEngine::ApprovalException.new(ApprovalEngine::ERR_UNKNOWN_USER, "Could not find user for email id 1234567890."))
    assert_equal UserAccountValidation::INVALID_VERIFICATION_CODE, UserAccountValidation.validate_account("1234567890")
  end

  def test_account_already_verified
    ApplicationHelper.expects(:verify_email).with("1234567890").raises(ApprovalEngine::ApprovalException.new(ApprovalEngine::ERR_INVALID_TRANSITION, "Current status 'approved' does not allow transition 'verify_email'."))
    assert_equal UserAccountValidation::ACCOUNT_PREVIOUSLY_VERIFIED, UserAccountValidation.validate_account("1234567890")
  end

  def test_account_already_misc_exception
    ApplicationHelper.expects(:verify_email).with("1234567890").raises(ApprovalEngine::ApprovalException.new(ApprovalEngine::ERR_UNKNOWN_TRANSITION, "La Marche des Hobos"))
    assert_equal UserAccountValidation::UNEXPECTED_VERIFICATION_ERROR, UserAccountValidation.validate_account("1234567890")
  end

end


