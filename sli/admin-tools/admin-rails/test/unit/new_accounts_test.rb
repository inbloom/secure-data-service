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

class NewAccountsTest < ActiveSupport::TestCase
  def setup

  end

  def testNewAccountRequireNewConfirmation
    new_account = makeBasicPassword
    assert new_account.valid?
    new_account.tou_required = true
    assert !new_account.valid?
    new_account.terms_and_conditions = "1"
    assert new_account.valid?
  end

  def testPasswordDontMatch
    new_account = makeBasicPassword
    new_account.confirmation = "somethingDifferent"
    assert !new_account.valid?
  end

  def makeBasicPassword
    new_account = NewAccountPassword.new
    new_account.token = "1234"
    new_account.new_pass = "2bV!2b"
    new_account.confirmation = "2bV!2b"
    return new_account
  end

end
