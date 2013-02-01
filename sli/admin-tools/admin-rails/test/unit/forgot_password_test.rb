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

class ForgotPasswordTest < ActiveSupport::TestCase
  def setup

  end
  def test_initialise
    forgot_password=ForgotPassword.new(
        :token => 'asd6s0asod8asaisfd9af87ysad==' ,
        :new_pass => 'test4321',
        :confirmation => 'test4321'
    )
    assert_equal('test4321',forgot_password.new_pass)
    assert_equal('test4321',forgot_password.confirmation)
    assert(forgot_password.valid?)
  end

  def test_confirmation_password
    forgot_password=ForgotPassword.new(
        :token => 'asd6s0asod8asaisfd9af87ysad==' ,
        :new_pass => 'test4321',
        :confirmation => 'test431'
    )
    assert(!forgot_password.valid?)
  end

end
